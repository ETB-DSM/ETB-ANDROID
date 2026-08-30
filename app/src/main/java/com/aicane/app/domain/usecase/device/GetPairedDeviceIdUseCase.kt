package com.aicane.app.domain.usecase.device

import com.aicane.app.domain.repository.DeviceRepository
import javax.inject.Inject

class GetPairedDeviceIdUseCase @Inject constructor(private val repository: DeviceRepository) {
    operator fun invoke(): String? = repository.getPairedDeviceId()
}
