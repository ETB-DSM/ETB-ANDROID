package com.aicane.app.data.repository

import com.aicane.app.data.local.TokenStorage
import com.aicane.app.data.remote.api.DeviceApi
import com.aicane.app.data.remote.api.EmbeddedApi
import com.aicane.app.data.remote.dto.device.RegisterDeviceRequest
import com.aicane.app.domain.model.Device
import com.aicane.app.domain.model.DeviceStatus
import com.aicane.app.domain.repository.DeviceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val deviceApi: DeviceApi,
    private val embeddedApi: EmbeddedApi,
    private val tokenStorage: TokenStorage,
) : DeviceRepository {

    override suspend fun registerDevice(deviceId: String): Result<Device> = runCatching {
        val response = deviceApi.registerDevice(RegisterDeviceRequest(deviceId))
        tokenStorage.deviceId = response.deviceId
        Device(response.deviceId, response.name)
    }

    override suspend fun getDevices(): Result<List<Device>> = runCatching {
        deviceApi.getDevices().map { Device(it.deviceId, it.name) }
    }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> =
        runCatching { deviceApi.deleteDevice(deviceId) }

    override suspend fun getDeviceStatus(deviceId: String): Result<DeviceStatus> = runCatching {
        val envelope = embeddedApi.getDeviceStatus(deviceId)
        val payload = envelope.data ?: error(envelope.message ?: "디바이스 상태를 불러오지 못했습니다.")
        DeviceStatus(
            deviceId  = payload.deviceId,
            battery   = payload.battery,
            connected = payload.networkStatus.equals("ok", ignoreCase = true),
        )
    }

    override fun getPairedDeviceId(): String? = tokenStorage.deviceId
}
