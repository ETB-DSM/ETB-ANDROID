package com.aicane.app.data.repository

import com.aicane.app.data.local.TokenStorage
import com.aicane.app.data.remote.api.AuthApi
import com.aicane.app.data.remote.dto.auth.GoogleLoginRequest
import com.aicane.app.data.remote.dto.auth.LoginRequest
import com.aicane.app.data.remote.dto.auth.SignupRequest
import com.aicane.app.data.remote.dto.auth.VerifyEmailRequest
import com.aicane.app.domain.repository.AuthRepository
import com.aicane.app.domain.repository.DeviceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val deviceRepository: DeviceRepository,
) : AuthRepository {

    override suspend fun signup(email: String, name: String, password: String): Result<Unit> =
        runCatching {
            authApi.signup(SignupRequest(email, name, password))
            tokenStorage.userName  = name
            tokenStorage.userEmail = email
        }

    override suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        runCatching { authApi.verifyEmail(VerifyEmailRequest(email, code)) }

    override suspend fun login(email: String, password: String): Result<Boolean> =
        runCatching {
            val response = authApi.login(LoginRequest(email, password))
            tokenStorage.accessToken  = response.accessToken
            tokenStorage.refreshToken = response.refreshToken
            tokenStorage.userId       = response.userId
            tokenStorage.userEmail    = email
            resolveIsFirst()
        }

    override suspend fun loginWithGoogle(idToken: String): Result<Boolean> =
        runCatching {
            val response = authApi.loginWithGoogle(GoogleLoginRequest(idToken))
            tokenStorage.accessToken  = response.accessToken
            tokenStorage.refreshToken = response.refreshToken
            tokenStorage.userId       = response.userId
            resolveIsFirst()
        }

    // 재설치 시에도 로컬이 아닌 서버 등록 상태로 온보딩 필요 여부를 판단한다.
    private suspend fun resolveIsFirst(): Boolean =
        deviceRepository.getDevices().fold(
            onSuccess = { devices ->
                val existing = devices.firstOrNull()
                if (existing != null) {
                    tokenStorage.deviceId = existing.deviceId
                    false
                } else {
                    true
                }
            },
            onFailure = { tokenStorage.deviceId == null },
        )

    override suspend fun logout(): Result<Unit> = runCatching {
        runCatching { authApi.logout() }
        tokenStorage.clearAll()
    }

    override fun getUserInfo(): Pair<String, String> =
        Pair(tokenStorage.userName ?: "", tokenStorage.userEmail ?: "")
}
