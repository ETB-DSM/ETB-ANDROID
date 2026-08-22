package com.aicane.app.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val name: String,
    val password: String,
)
