package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.auth.GoogleLoginRequest
import com.aicane.app.data.remote.dto.auth.LoginRequest
import com.aicane.app.data.remote.dto.auth.LoginResponse
import com.aicane.app.data.remote.dto.auth.SignupRequest
import com.aicane.app.data.remote.dto.auth.VerifyEmailRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest)

    @POST("api/v1/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest)

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/v1/auth/login/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): LoginResponse

    @POST("api/v1/auth/logout")
    suspend fun logout()
}
