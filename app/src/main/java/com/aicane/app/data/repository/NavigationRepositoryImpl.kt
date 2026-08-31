package com.aicane.app.data.repository

import android.util.Log
import com.aicane.app.data.local.TokenStorage
import com.aicane.app.data.remote.api.EmbeddedApi
import com.aicane.app.data.remote.api.TmapPedestrianApi
import com.aicane.app.data.remote.dto.embedded.CreateNavigationInstructionRequest
import com.aicane.app.data.remote.dto.embedded.CreateNavigationSessionRequest
import com.aicane.app.data.remote.dto.embedded.UpdateNavigationSessionStatusRequest
import com.aicane.app.data.remote.dto.tmap.TmapPedestrianRequest
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationSession
import com.aicane.app.domain.model.RouteStep
import com.aicane.app.domain.model.TurnType
import com.aicane.app.domain.repository.NavigationRepository
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.double
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepositoryImpl @Inject constructor(
    private val embeddedApi: EmbeddedApi,
    private val tmapPedestrianApi: TmapPedestrianApi,
    private val tokenStorage: TokenStorage,
) : NavigationRepository {

    override suspend fun createSession(
        destinationId: String,
        startLatitude: Double,
        startLongitude: Double,
    ): Result<NavigationSession> = runCatching {
        val deviceId = tokenStorage.deviceId
            ?: throw IllegalStateException("등록된 지팡이가 없어 길안내를 시작할 수 없습니다.")
        val userId = tokenStorage.userId
            ?: throw IllegalStateException("사용자 정보를 확인할 수 없습니다. 다시 로그인해주세요.")

        val envelope = embeddedApi.createSession(
            CreateNavigationSessionRequest(
                userId          = userId,
                deviceId        = deviceId,
                destinationId   = destinationId,
                startLatitude   = startLatitude,
                startLongitude  = startLongitude,
                timestamp       = Instant.now().toString(),
            )
        )
        val data = envelope.data ?: error(envelope.message ?: "길안내 세션을 생성하지 못했습니다.")
        NavigationSession(data.navigationSessionId)
    }

    override suspend fun fetchRoute(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double,
    ): Result<List<RouteStep>> = runCatching {
        val response = tmapPedestrianApi.getPedestrianRoute(
            request = TmapPedestrianRequest(
                startX    = startLng, startY = startLat,
                endX      = endLng,   endY   = endLat,
                startName = java.net.URLEncoder.encode("출발지", "UTF-8"),
                endName   = java.net.URLEncoder.encode("목적지", "UTF-8"),
            )
        )
        val steps = response.features.flatMap { feature ->
            when (feature.geometry.type) {
                "Point" -> {
                    val coords = feature.geometry.coordinates.jsonArray
                    val longitude = coords[0].jsonPrimitive.double
                    val latitude  = coords[1].jsonPrimitive.double
                    val turnType = when (feature.properties.turnType) {
                        12, 16, 18, 212 -> TurnType.LEFT
                        13, 17, 19, 213 -> TurnType.RIGHT
                        else            -> TurnType.STRAIGHT
                    }
                    Log.d(
                        "NavRoute",
                        "Point rawTurnType=${feature.properties.turnType} -> $turnType, desc=\"${feature.properties.description}\"",
                    )
                    listOf(
                        RouteStep(
                            latitude       = latitude,
                            longitude      = longitude,
                            distanceMeters = feature.properties.distance,
                            turnType       = turnType,
                            description    = feature.properties.description,
                        )
                    )
                }
                "LineString" -> {
                    feature.geometry.coordinates.jsonArray.map { pair ->
                        val point     = pair.jsonArray
                        val longitude = point[0].jsonPrimitive.double
                        val latitude  = point[1].jsonPrimitive.double
                        RouteStep(
                            latitude       = latitude,
                            longitude      = longitude,
                            distanceMeters = feature.properties.distance,
                            turnType       = TurnType.STRAIGHT,
                            description    = feature.properties.description,
                        )
                    }
                }
                else -> emptyList()
            }
        }
        val turnCount = steps.count { it.turnType != TurnType.STRAIGHT }
        Log.d("NavRoute", "steps=${steps.size}, features=${response.features.size}, 회전 노드=$turnCount")
        steps
    }

    override suspend fun createInstruction(
        sessionId: String,
        action: NavigationAction,
        distanceMeters: Double,
        message: String,
        latitude: Double,
        longitude: Double,
    ): Result<Unit> = runCatching {
        embeddedApi.createInstruction(
            navigationSessionId = sessionId,
            request = CreateNavigationInstructionRequest(
                action         = action.name.lowercase(),
                distanceMeters = distanceMeters.toInt(),
                message        = message,
                latitude       = latitude,
                longitude      = longitude,
                timestamp      = Instant.now().toString(),
            ),
        )
    }

    override suspend fun updateStatus(sessionId: String, status: String, reason: String?): Result<Unit> = runCatching {
        embeddedApi.updateSessionStatus(
            navigationSessionId = sessionId,
            request = UpdateNavigationSessionStatusRequest(
                status    = status,
                reason    = reason,
                timestamp = Instant.now().toString(),
            ),
        )
    }
}
