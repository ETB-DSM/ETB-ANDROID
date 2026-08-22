package com.aicane.app.data.remote.dto.guardian

import kotlinx.serialization.Serializable

@Serializable
data class GuardianResponse(
    val guardianId: String,
    val name: String,
    val phone: String,
)
