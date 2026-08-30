package com.aicane.app.domain.repository

import com.aicane.app.domain.model.Device
import com.aicane.app.domain.model.DeviceStatus

interface DeviceRepository {
    suspend fun registerDevice(deviceId: String): Result<Device>
    suspend fun getDevices(): Result<List<Device>>
    suspend fun deleteDevice(deviceId: String): Result<Unit>
    suspend fun getDeviceStatus(deviceId: String): Result<DeviceStatus>
    fun getPairedDeviceId(): String?
}
