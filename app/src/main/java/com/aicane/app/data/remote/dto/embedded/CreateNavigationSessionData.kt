package com.aicane.app.data.remote.dto.embedded

import kotlinx.serialization.Serializable

@Serializable
data class CreateNavigationSessionData(
    val navigationSessionId: String,
)
