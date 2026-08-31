package com.aicane.app.data.remote.dto.embedded

import kotlinx.serialization.Serializable

@Serializable
data class CreateNavigationInstructionRequest(
    val action: String,
    val distanceMeters: Int,
    val message: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
)
