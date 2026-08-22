package com.aicane.app.presentation.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.guardian.RegisterGuardianUseCase
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

sealed class GuardianEvent {
    object NavigateToDestinationList : GuardianEvent()
}

@HiltViewModel
class GuardianViewModel @Inject constructor(
    private val registerGuardianUseCase: RegisterGuardianUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<GuardianEvent>()
    val events: Flow<GuardianEvent> = _events.receiveAsFlow()

    fun register(name: String, phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            registerGuardianUseCase(name, phone)
                .onSuccess { _events.send(GuardianEvent.NavigateToDestinationList) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "보호자 등록에 실패했습니다.") }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = "") }
}
