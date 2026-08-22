package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.destination.CreateDestinationRequest
import com.aicane.app.data.remote.dto.destination.DestinationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DestinationApi {
    @GET("api/v1/destinations")
    suspend fun getDestinations(): List<DestinationResponse>

    @POST("api/v1/destinations")
    suspend fun createDestination(@Body request: CreateDestinationRequest): DestinationResponse
}
