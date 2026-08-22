package com.aicane.app.data.remote.dto.guardian

import kotlinx.serialization.Serializable

@Serializable
data class RegisterGuardianRequest(
    val name: String,
    val phone: String,
)
