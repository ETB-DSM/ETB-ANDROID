package com.aicane.app.data.remote.dto.device

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceRequest(val deviceId: String)
