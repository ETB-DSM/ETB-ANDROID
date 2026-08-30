package com.aicane.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.auth.ResendCodeUseCase
import com.aicane.app.domain.usecase.auth.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val RESEND_COOLDOWN_SEC = 30

sealed class VerifyEvent {
    object NavigateToLogin : VerifyEvent()
}

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
        val infoMessage: String = "",
        val isResending: Boolean = false,
        val resendCooldownSec: Int = 0,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<VerifyEvent>()
    val events: Flow<VerifyEvent> = _events.receiveAsFlow()

    fun verifyEmail(email: String, code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "", infoMessage = "") }
            verifyEmailUseCase(email, code)
                .onSuccess { _events.send(VerifyEvent.NavigateToLogin) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "인증에 실패했습니다.") }
                }
        }
    }

    fun resendCode(email: String) {
        if (_uiState.value.isResending || _uiState.value.resendCooldownSec > 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, errorMessage = "", infoMessage = "") }
            resendCodeUseCase(email)
                .onSuccess {
                    _uiState.update { it.copy(isResending = false, infoMessage = "인증 코드를 다시 보냈어요.") }
                    startCooldown()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isResending = false, errorMessage = error.message ?: "재전송에 실패했습니다.")
                    }
                }
        }
    }

    private fun startCooldown() {
        viewModelScope.launch {
            for (remaining in RESEND_COOLDOWN_SEC downTo 1) {
                _uiState.update { it.copy(resendCooldownSec = remaining) }
                delay(1000L)
            }
            _uiState.update { it.copy(resendCooldownSec = 0) }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = "") }
}
