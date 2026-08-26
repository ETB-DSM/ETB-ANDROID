package com.aicane.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.auth.LoginWithGoogleUseCase
import com.aicane.app.domain.usecase.auth.SignupUseCase
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

sealed class SignupEvent {
    data class NavigateToVerify(val email: String) : SignupEvent()
    object NavigateComplete : SignupEvent()
}

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<SignupEvent>()
    val events: Flow<SignupEvent> = _events.receiveAsFlow()

    fun signup(email: String, name: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            signupUseCase(email, name, password)
                .onSuccess { _events.send(SignupEvent.NavigateToVerify(email)) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "회원가입에 실패했습니다.") }
                }
        }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = "") }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            loginWithGoogleUseCase(idToken)
                .onSuccess { _events.send(SignupEvent.NavigateComplete) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "구글 로그인에 실패했습니다.") }
                }
        }
    }
}
