package com.aicane.app.presentation.destination

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.model.Destination
import com.aicane.app.domain.usecase.auth.GetUserInfoUseCase
import com.aicane.app.domain.usecase.destination.DeleteDestinationUseCase
import com.aicane.app.domain.usecase.destination.GetDestinationsUseCase
import com.aicane.app.domain.usecase.navigation.CreateSessionUseCase
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

sealed class DestinationListEvent {
    data class NavigateToNavigation(val sessionId: String, val destination: Destination) : DestinationListEvent()
}

@HiltViewModel
class DestinationListViewModel @Inject constructor(
    private val getDestinationsUseCase: GetDestinationsUseCase,
    private val deleteDestinationUseCase: DeleteDestinationUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    data class UiState(
        val destinations: List<Destination> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String = "",
        val destinationToDelete: Destination? = null,
        val userInitial: String = "?",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<DestinationListEvent>()
    val events: Flow<DestinationListEvent> = _events.receiveAsFlow()

    init {
        val (name, _) = getUserInfoUseCase()
        val initial = name.trim().firstOrNull()?.toString() ?: "?"
        _uiState.update { it.copy(userInitial = initial) }
    }

    fun loadDestinations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            getDestinationsUseCase()
                .onSuccess { destinations ->
                    _uiState.update { it.copy(destinations = destinations, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "목적지를 불러오지 못했습니다.")
                    }
                }
        }
    }

    fun confirmDelete(destination: Destination) {
        _uiState.update { it.copy(destinationToDelete = destination) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(destinationToDelete = null) }
    }

    fun deleteDestination() {
        val target = _uiState.value.destinationToDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(destinationToDelete = null) }
            deleteDestinationUseCase(target.destinationId)
                .onSuccess { loadDestinations() }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "삭제에 실패했습니다.")
                    }
                }
        }
    }

    @SuppressLint("MissingPermission")
    fun startNavigation(destinationId: String) {
        val destination = _uiState.value.destinations.find { it.destinationId == destinationId } ?: return
        viewModelScope.launch {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val currentLocation = suspendCancellableCoroutine { cont ->
                fusedClient.lastLocation
                    .addOnSuccessListener { loc -> cont.resume(loc) }
                    .addOnFailureListener { cont.resume(null) }
            }
            val startLat = currentLocation?.latitude ?: destination.latitude
            val startLng = currentLocation?.longitude ?: destination.longitude

            createSessionUseCase(destinationId, startLat, startLng)
                .onSuccess { session ->
                    _events.send(DestinationListEvent.NavigateToNavigation(session.sessionId, destination))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "길안내를 시작할 수 없습니다.")
                    }
                }
        }
    }
}
