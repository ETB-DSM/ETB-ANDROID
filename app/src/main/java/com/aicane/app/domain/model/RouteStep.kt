package com.aicane.app.domain.model

data class RouteStep(
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val turnType: TurnType,
    val description: String,
)
