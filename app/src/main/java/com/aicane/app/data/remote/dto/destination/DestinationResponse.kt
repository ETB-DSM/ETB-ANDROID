package com.aicane.app.data.remote.dto.destination

import kotlinx.serialization.Serializable

@Serializable
data class DestinationResponse(
    val destinationId: String,
    val name: String,
    val targetText: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
)
