package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.destination.CreateDestinationRequest
import com.aicane.app.data.remote.dto.destination.DestinationResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DestinationApi {
    @GET("api/v1/destinations")
    suspend fun getDestinations(): List<DestinationResponse>

    @POST("api/v1/destinations")
    suspend fun createDestination(@Body request: CreateDestinationRequest): DestinationResponse

    @DELETE("api/v1/destinations/{destinationId}")
    suspend fun deleteDestination(@Path("destinationId") destinationId: String)
}
