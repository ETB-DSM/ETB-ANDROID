package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.tmap.TmapPedestrianRequest
import com.aicane.app.data.remote.dto.tmap.TmapPedestrianResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface TmapPedestrianApi {
    @POST("tmap/routes/pedestrian")
    suspend fun getPedestrianRoute(
        @Query("version") version: Int = 1,
        @Body request: TmapPedestrianRequest,
    ): TmapPedestrianResponse
}
