package com.aicane.app.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.model.Device
import com.aicane.app.domain.model.Guardian
import com.aicane.app.domain.usecase.auth.GetUserInfoUseCase
import com.aicane.app.domain.usecase.auth.LogoutUseCase
import com.aicane.app.domain.usecase.device.DeleteDeviceUseCase
import com.aicane.app.domain.usecase.device.GetDevicesUseCase
import com.aicane.app.domain.usecase.guardian.DeleteGuardianUseCase
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
    private val deleteDeviceUseCase: DeleteDeviceUseCase,
    private val deleteGuardianUseCase: DeleteGuardianUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : ViewModel() {

    data class UiState(
        val userName: String = "",
        val userEmail: String = "",
        val devices: List<Device> = emptyList(),
        val guardians: List<Guardian> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String = "",
        val deviceToDelete: Device? = null,
        val guardianToDelete: Guardian? = null,
    ) {
        // 최대 5개(백엔드 DEVICE_LIMIT_EXCEEDED / GUARDIAN_LIMIT_EXCEEDED 정책과 동일)
        val canAddDevice: Boolean get() = devices.size < 5
        val canAddGuardian: Boolean get() = guardians.size < 5
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<MypageEvent>()
    val events: Flow<MypageEvent> = _events.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        val (name, email) = getUserInfoUseCase()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "", userName = name, userEmail = email) }

            val deviceResult   = getDevicesUseCase()
            val guardianResult = getGuardiansUseCase()

            val error = deviceResult.exceptionOrNull() ?: guardianResult.exceptionOrNull()
            _uiState.update {
                it.copy(
                    devices      = deviceResult.getOrNull() ?: it.devices,
                    guardians    = guardianResult.getOrNull() ?: it.guardians,
                    isLoading    = false,
                    errorMessage = if (error != null) "정보를 불러오지 못했습니다." else "",
                )
            }
        }
    }

    fun confirmDeleteDevice(device: Device)   { _uiState.update { it.copy(deviceToDelete = device) } }
    fun cancelDeleteDevice()                  { _uiState.update { it.copy(deviceToDelete = null) } }
    fun confirmDeleteGuardian(guardian: Guardian) { _uiState.update { it.copy(guardianToDelete = guardian) } }
    fun cancelDeleteGuardian()                { _uiState.update { it.copy(guardianToDelete = null) } }

    fun deleteDevice() {
        val deviceId = _uiState.value.deviceToDelete?.deviceId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(deviceToDelete = null) }
            deleteDeviceUseCase(deviceId)
                .onSuccess { load() }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "삭제에 실패했습니다.") }
                }
        }
    }

    fun deleteGuardian() {
        val guardianId = _uiState.value.guardianToDelete?.guardianId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(guardianToDelete = null) }
            deleteGuardianUseCase(guardianId)
                .onSuccess { load() }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "삭제에 실패했습니다.") }
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
