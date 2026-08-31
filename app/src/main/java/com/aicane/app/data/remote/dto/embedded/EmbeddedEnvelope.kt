package com.aicane.app.data.remote.dto.embedded

import kotlinx.serialization.Serializable

@Serializable
data class EmbeddedEnvelope<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null,
)
