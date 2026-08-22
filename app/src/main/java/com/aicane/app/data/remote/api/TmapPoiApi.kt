package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.tmap.TmapPoiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmapPoiApi {
    @GET("tmap/pois")
    suspend fun searchPois(
        @Query("version") version: Int = 1,
        @Query("searchKeyword") keyword: String,
        @Query("resCoordType") resCoordType: String = "WGS84GEO",
        @Query("reqCoordType") reqCoordType: String = "WGS84GEO",
        @Query("count") count: Int = 10,
    ): TmapPoiResponse
}
