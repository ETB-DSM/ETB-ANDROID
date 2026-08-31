package com.aicane.app.data.remote.dto.navigation

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(val destinationId: String)
