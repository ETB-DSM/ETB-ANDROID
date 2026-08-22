package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.navigation.CreateInstructionRequest
import com.aicane.app.data.remote.dto.navigation.CreateSessionRequest
import com.aicane.app.data.remote.dto.navigation.CreateSessionResponse
import com.aicane.app.data.remote.dto.navigation.UpdateStatusRequest
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NavigationApi {
    @POST("api/v1/navigation/sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): CreateSessionResponse

    @POST("api/v1/navigation/sessions/{sessionId}/instructions")
    suspend fun createInstruction(
        @Path("sessionId") sessionId: String,
        @Body request: CreateInstructionRequest,
    )

    @PATCH("api/v1/navigation/sessions/{sessionId}/status")
    suspend fun updateStatus(
        @Path("sessionId") sessionId: String,
        @Body request: UpdateStatusRequest,
    )
}
