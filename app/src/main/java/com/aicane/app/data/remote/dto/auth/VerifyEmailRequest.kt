package com.aicane.app.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String,
)
