package com.aicane.app.presentation.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.device.RegisterDeviceUseCase
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

sealed class DeviceEvent {
    object Registered : DeviceEvent()
}

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<DeviceEvent>()
    val events: Flow<DeviceEvent> = _events.receiveAsFlow()

    fun register(deviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            registerDeviceUseCase(deviceId)
                .onSuccess { _events.send(DeviceEvent.Registered) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "기기 등록에 실패했습니다.") }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = "") }
}
