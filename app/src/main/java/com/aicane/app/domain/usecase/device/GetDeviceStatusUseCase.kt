package com.aicane.app.domain.usecase.device

import com.aicane.app.domain.model.DeviceStatus
import com.aicane.app.domain.repository.DeviceRepository
import javax.inject.Inject

class GetDeviceStatusUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(deviceId: String): Result<DeviceStatus> = repository.getDeviceStatus(deviceId)
}
