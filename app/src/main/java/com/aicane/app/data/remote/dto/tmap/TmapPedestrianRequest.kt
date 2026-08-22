package com.aicane.app.data.remote.dto.tmap

import kotlinx.serialization.Serializable

@Serializable
data class TmapPedestrianRequest(
    val startX: String,
    val startY: String,
    val endX: String,
    val endY: String,
    val reqCoordType: String = "WGS84GEO",
    val resCoordType: String = "WGS84GEO",
    val startName: String = "출발지",
    val endName: String = "목적지",
)
