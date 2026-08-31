package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.embedded.CreateNavigationInstructionRequest
import com.aicane.app.data.remote.dto.embedded.CreateNavigationSessionData
import com.aicane.app.data.remote.dto.embedded.CreateNavigationSessionRequest
import com.aicane.app.data.remote.dto.embedded.EmbeddedDeviceStatusData
import com.aicane.app.data.remote.dto.embedded.EmbeddedEnvelope
import com.aicane.app.data.remote.dto.embedded.UpdateNavigationSessionStatusRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface EmbeddedApi {
    @GET("api/devices/{deviceId}/status")
    suspend fun getDeviceStatus(@Path("deviceId") deviceId: String): EmbeddedEnvelope<EmbeddedDeviceStatusData>

    @POST("api/navigation/sessions")
    suspend fun createSession(@Body request: CreateNavigationSessionRequest): EmbeddedEnvelope<CreateNavigationSessionData>

    @POST("api/navigation/sessions/{navigationSessionId}/instruction")
    suspend fun createInstruction(
        @Path("navigationSessionId") navigationSessionId: String,
        @Body request: CreateNavigationInstructionRequest,
    )

    @PATCH("api/navigation/sessions/{navigationSessionId}/status")
    suspend fun updateSessionStatus(
        @Path("navigationSessionId") navigationSessionId: String,
        @Body request: UpdateNavigationSessionStatusRequest,
    )
}
