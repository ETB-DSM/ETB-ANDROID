package com.aicane.app.domain.usecase.device

import com.aicane.app.domain.model.Device
import com.aicane.app.domain.repository.DeviceRepository
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(): Result<List<Device>> = repository.getDevices()
}
