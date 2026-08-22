package com.aicane.app.domain.model

data class Destination(
    val destinationId: String,
    val name: String,
    val targetText: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
)
