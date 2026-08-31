package com.aicane.app.data.remote.dto.embedded

import kotlinx.serialization.Serializable

@Serializable
data class EmbeddedDeviceStatusData(
    val userId: String,
    val deviceId: String,
    val battery: Int,
    val lidarStatus: String,
    val cameraStatus: String,
    val gpsStatus: String,
    val networkStatus: String,
    val timestamp: String,
)
