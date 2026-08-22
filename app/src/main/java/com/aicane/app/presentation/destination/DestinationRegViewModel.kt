package com.aicane.app.presentation.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.destination.CreateDestinationUseCase
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

sealed class DestinationRegEvent {
    object Saved : DestinationRegEvent()
}

@HiltViewModel
class DestinationRegViewModel @Inject constructor(
    private val createDestinationUseCase: CreateDestinationUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<DestinationRegEvent>()
    val events: Flow<DestinationRegEvent> = _events.receiveAsFlow()

    fun save(name: String, targetText: String, latitude: Double, longitude: Double, radius: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            createDestinationUseCase(name, targetText, latitude, longitude, radius)
                .onSuccess { _events.send(DestinationRegEvent.Saved) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "목적지 저장에 실패했습니다.")
                    }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = "") }
}
