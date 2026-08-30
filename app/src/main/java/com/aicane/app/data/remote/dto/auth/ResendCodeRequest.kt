package com.aicane.app.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ResendCodeRequest(
    val email: String,
)
