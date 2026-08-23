package com.aicane.app.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    @SerialName("nickname") val name: String,
    val password: String,
)
