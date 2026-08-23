package com.aicane.app.domain.usecase.device

import com.aicane.app.domain.repository.DeviceRepository
import javax.inject.Inject

class DeleteDeviceUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(deviceId: String): Result<Unit> =
        repository.deleteDevice(deviceId)
}
