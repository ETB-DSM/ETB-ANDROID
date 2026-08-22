package com.aicane.app.domain.model

data class NavigationInstruction(
    val sessionId: String,
    val action: NavigationAction,
    val distanceMeters: Double,
    val message: String,
)
