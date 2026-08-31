package com.aicane.app.data.remote.dto.navigation

import kotlinx.serialization.Serializable

@Serializable
data class CreateInstructionRequest(
    val action: String,
    val distanceMeters: Int,
    val message: String,
)
