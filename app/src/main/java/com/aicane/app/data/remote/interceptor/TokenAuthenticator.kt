package com.aicane.app.data.remote.interceptor

import com.aicane.app.BuildConfig
import com.aicane.app.data.local.TokenStorage
import com.aicane.app.di.qualifier.RefreshClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    @RefreshClient private val refreshClient: OkHttpClient,
    private val json: Json,
) : okhttp3.Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            // refreshToken을 synchronized 블록 안에서 읽어 clearAll() 경쟁 조건 방지
            val refreshToken = tokenStorage.refreshToken ?: return null

            // 다른 스레드가 이미 갱신했으면 그 토큰으로 재시도
            val currentToken = tokenStorage.accessToken
            val requestToken  = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (currentToken != null && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val newTokens = runCatching { callRefreshApi(refreshToken) }.getOrNull()
                ?: run {
                    tokenStorage.clearAll()
                    return null
                }

            tokenStorage.accessToken  = newTokens.accessToken
            tokenStorage.refreshToken = newTokens.refreshToken

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        }
    }

    private fun callRefreshApi(refreshToken: String): RefreshResponse {
        val body = """{"refreshToken":"$refreshToken"}"""
            .toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/v1/auth/refresh")
            .post(body)
            .build()

        val responseBody = refreshClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("Refresh failed: ${resp.code}")
            resp.body?.string() ?: throw IOException("Empty refresh response")
        }
        return json.decodeFromString(responseBody)
    }

    @Serializable
    private data class RefreshResponse(
        val accessToken: String,
        val refreshToken: String,
    )
}
