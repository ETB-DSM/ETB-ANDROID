package com.aicane.app.presentation.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationInstruction
import com.aicane.app.domain.usecase.navigation.FetchRouteUseCase
import com.aicane.app.domain.usecase.navigation.UpdateSessionStatusUseCase
import com.aicane.app.navigation.NavigationConfig
import com.aicane.app.navigation.NavigationStateHolder
import com.aicane.app.service.NavigationService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

sealed class NavigationEvent {
    object NavigateToEnd : NavigationEvent()
}

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val fetchRouteUseCase: FetchRouteUseCase,
    private val updateStatusUseCase: UpdateSessionStatusUseCase,
    private val stateHolder: NavigationStateHolder,
    private val config: NavigationConfig,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    data class UiState(
        val instruction: NavigationInstruction? = null,
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<NavigationEvent>()
    val events: Flow<NavigationEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            stateHolder.currentInstruction.collect { instruction ->
                _uiState.update { it.copy(instruction = instruction, isLoading = false) }
                if (instruction?.action == NavigationAction.ARRIVED) {
                    _events.send(NavigationEvent.NavigateToEnd)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startNavigation(sessionId: String, destLat: Double, destLng: Double, destRadius: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val startLocation = suspendCancellableCoroutine { cont ->
                fusedClient.lastLocation
                    .addOnSuccessListener { loc -> cont.resume(loc) }
                    .addOnFailureListener { cont.resume(null) }
            }
            val startLat = startLocation?.latitude ?: destLat
            val startLng = startLocation?.longitude ?: destLng

            fetchRouteUseCase(startLat, startLng, destLat, destLng)
                .onSuccess { steps ->
                    config.sessionId         = sessionId
                    config.steps             = steps
                    config.destinationLat    = destLat
                    config.destinationLng    = destLng
                    config.destinationRadius = destRadius
                    stateHolder.reset()
                    context.startForegroundService(Intent(context, NavigationService::class.java))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "경로를 불러오지 못했습니다.")
                    }
                }
        }
    }

    fun cancelNavigation(sessionId: String) {
        viewModelScope.launch {
            runCatching { updateStatusUseCase(sessionId, "canceled") }
            context.stopService(Intent(context, NavigationService::class.java))
            stateHolder.reset()
            _events.send(NavigationEvent.NavigateToEnd)
        }
    }
}
