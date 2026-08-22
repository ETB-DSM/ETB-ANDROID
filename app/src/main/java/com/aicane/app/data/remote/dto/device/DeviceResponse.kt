package com.aicane.app.data.remote.dto.device

import kotlinx.serialization.Serializable

@Serializable
data class DeviceResponse(
    val deviceId: String,
    val name: String,
)
