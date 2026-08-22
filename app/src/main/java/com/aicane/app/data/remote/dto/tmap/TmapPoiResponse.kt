package com.aicane.app.data.remote.dto.tmap

import kotlinx.serialization.Serializable

@Serializable
data class TmapPoiResponse(val searchPoiInfo: SearchPoiInfo)

@Serializable
data class SearchPoiInfo(val pois: Pois)

@Serializable
data class Pois(val poi: List<Poi> = emptyList())

@Serializable
data class Poi(
    val name: String,
    val frontLat: String,
    val frontLon: String,
    val upperAddrName: String = "",
    val middleAddrName: String = "",
    val roadName: String = "",
    val firstBuildNo: String = "",
)
