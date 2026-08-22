package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.guardian.GuardianResponse
import com.aicane.app.data.remote.dto.guardian.RegisterGuardianRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GuardianApi {
    @POST("api/v1/guardians")
    suspend fun registerGuardian(@Body request: RegisterGuardianRequest): GuardianResponse

    @GET("api/v1/guardians")
    suspend fun getGuardians(): List<GuardianResponse>
}
