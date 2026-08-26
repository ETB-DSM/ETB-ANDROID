package com.aicane.app.data.remote.dto.tmap

import kotlinx.serialization.Serializable

@Serializable
data class TmapPedestrianRequest(
    val startX: Double,
    val startY: Double,
    val endX: Double,
    val endY: Double,
    val reqCoordType: String = "WGS84GEO",
    val resCoordType: String = "WGS84GEO",
    val startName: String,
    val endName: String,
)
