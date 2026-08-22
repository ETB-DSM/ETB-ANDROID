package com.aicane.app.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.auth.LogoutUseCase
import com.aicane.app.domain.usecase.device.GetDevicesUseCase
import com.aicane.app.domain.usecase.guardian.GetGuardiansUseCase
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

sealed class MypageEvent {
    object NavigateToLogin : MypageEvent()
}

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val getGuardiansUseCase: GetGuardiansUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    data class UiState(
        val deviceId: String = "",
        val guardianName: String = "",
        val guardianPhone: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<MypageEvent>()
    val events: Flow<MypageEvent> = _events.receiveAsFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val deviceResult   = getDevicesUseCase()
            val guardianResult = getGuardiansUseCase()

            val deviceId      = deviceResult.getOrNull()?.firstOrNull()?.deviceId ?: ""
            val guardianName  = guardianResult.getOrNull()?.firstOrNull()?.name ?: ""
            val guardianPhone = guardianResult.getOrNull()?.firstOrNull()?.phone ?: ""

            val error = deviceResult.exceptionOrNull() ?: guardianResult.exceptionOrNull()
            _uiState.update {
                it.copy(
                    deviceId      = deviceId,
                    guardianName  = guardianName,
                    guardianPhone = guardianPhone,
                    isLoading     = false,
                    errorMessage  = if (error != null) "정보를 불러오지 못했습니다." else "",
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.send(MypageEvent.NavigateToLogin)
        }
    }
}
