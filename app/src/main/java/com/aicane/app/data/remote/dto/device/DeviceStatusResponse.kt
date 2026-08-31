package com.aicane.app.data.remote.dto.device

import kotlinx.serialization.Serializable

@Serializable
data class DeviceStatusResponse(
    val deviceId: String,
    val battery: Int,
    val lidarOk: Boolean,
    val cameraOk: Boolean,
    val gpsOk: Boolean,
    val networkOk: Boolean,
    val createdAt: String,
)
