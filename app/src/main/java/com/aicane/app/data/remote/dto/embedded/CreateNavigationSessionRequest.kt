package com.aicane.app.data.remote.dto.embedded

import kotlinx.serialization.Serializable

@Serializable
data class CreateNavigationSessionRequest(
    val userId: String,
    val deviceId: String,
    val destinationId: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val timestamp: String,
)
