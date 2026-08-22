package com.aicane.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.auth.VerifyEmailUseCase
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

sealed class VerifyEvent {
    object NavigateToLogin : VerifyEvent()
}

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<VerifyEvent>()
    val events: Flow<VerifyEvent> = _events.receiveAsFlow()

    fun verifyEmail(email: String, code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            verifyEmailUseCase(email, code)
                .onSuccess { _events.send(VerifyEvent.NavigateToLogin) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "인증에 실패했습니다.") }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = "") }
}
