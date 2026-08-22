package com.aicane.app.presentation.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.model.Destination
import com.aicane.app.domain.usecase.destination.GetDestinationsUseCase
import com.aicane.app.domain.usecase.navigation.CreateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DestinationListEvent {
    data class NavigateToNavigation(val sessionId: String, val destination: Destination) : DestinationListEvent()
}

@HiltViewModel
class DestinationListViewModel @Inject constructor(
    private val getDestinationsUseCase: GetDestinationsUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
) : ViewModel() {

    data class UiState(
        val destinations: List<Destination> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<DestinationListEvent>()
    val events: Flow<DestinationListEvent> = _events.receiveAsFlow()

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

    fun startNavigation(destinationId: String) {
        val destination = _uiState.value.destinations.find { it.destinationId == destinationId } ?: return
        viewModelScope.launch {
            createSessionUseCase(destinationId)
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
