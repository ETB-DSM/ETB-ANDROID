package com.aicane.app.domain.model

data class DeviceStatus(
    val deviceId: String,
    val battery: Int,
    val connected: Boolean,
)
