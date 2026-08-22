package com.aicane.app.data.repository

import com.aicane.app.data.local.TokenStorage
import com.aicane.app.data.remote.api.AuthApi
import com.aicane.app.data.remote.dto.auth.LoginRequest
import com.aicane.app.data.remote.dto.auth.SignupRequest
import com.aicane.app.data.remote.dto.auth.VerifyEmailRequest
import com.aicane.app.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun signup(email: String, name: String, password: String): Result<Unit> =
        runCatching { authApi.signup(SignupRequest(email, name, password)) }

    override suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        runCatching { authApi.verifyEmail(VerifyEmailRequest(email, code)) }

    override suspend fun login(email: String, password: String): Result<Boolean> =
        runCatching {
            val response = authApi.login(LoginRequest(email, password))
            tokenStorage.accessToken  = response.accessToken
            tokenStorage.refreshToken = response.refreshToken
            tokenStorage.userId       = response.userId
            tokenStorage.deviceId == null
        }

    override suspend fun logout(): Result<Unit> = runCatching {
        runCatching { authApi.logout() }
        tokenStorage.clearAll()
    }
}
