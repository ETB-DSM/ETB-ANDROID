package com.aicane.app.data.repository

import com.aicane.app.data.remote.api.TmapPoiApi
import com.aicane.app.domain.model.Place
import com.aicane.app.domain.repository.PlaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepositoryImpl @Inject constructor(
    private val tmapPoiApi: TmapPoiApi,
) : PlaceRepository {
    override suspend fun searchPlaces(query: String): Result<List<Place>> = runCatching {
        tmapPoiApi.searchPois(keyword = query).searchPoiInfo.pois.poi.map { poi ->
            val address = listOf(poi.upperAddrName, poi.middleAddrName, poi.roadName, poi.firstBuildNo)
                .filter { it.isNotBlank() }
                .joinToString(" ")
            Place(
                name = poi.name,
                address = address,
                latitude = poi.frontLat.toDouble(),
                longitude = poi.frontLon.toDouble(),
            )
        }
    }
}
