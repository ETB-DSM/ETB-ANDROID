package com.aicane.app.data.remote.dto.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceRequest(@SerialName("name") val deviceId: String)
