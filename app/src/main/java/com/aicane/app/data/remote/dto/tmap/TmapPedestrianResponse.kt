package com.aicane.app.data.remote.dto.tmap

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TmapPedestrianResponse(val features: List<TmapFeature>)

@Serializable
data class TmapFeature(
    val geometry: TmapGeometry,
    val properties: TmapRouteProperties,
)

@Serializable
data class TmapGeometry(
    val type: String,
    val coordinates: JsonElement,
)

@Serializable
data class TmapRouteProperties(
    val turnType: Int = 11,
    val distance: Double = 0.0,
    val description: String = "",
)
