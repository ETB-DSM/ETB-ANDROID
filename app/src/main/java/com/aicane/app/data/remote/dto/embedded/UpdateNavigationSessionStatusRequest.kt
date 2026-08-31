package com.aicane.app.data.remote.dto.embedded

import kotlinx.serialization.Serializable

@Serializable
data class UpdateNavigationSessionStatusRequest(
    val status: String,
    val reason: String? = null,
    val timestamp: String,
)
