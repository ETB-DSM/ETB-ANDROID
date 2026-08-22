package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.navigation.CreateSessionRequest
import com.aicane.app.data.remote.dto.navigation.CreateSessionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface NavigationApi {
    @POST("api/v1/navigation/sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): CreateSessionResponse
}
