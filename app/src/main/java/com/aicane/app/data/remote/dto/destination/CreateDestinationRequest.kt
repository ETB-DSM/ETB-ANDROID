package com.aicane.app.data.remote.dto.destination

import kotlinx.serialization.Serializable

@Serializable
data class CreateDestinationRequest(
    val name: String,
    val targetText: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
)
