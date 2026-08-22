package com.aicane.app.data.repository

import com.aicane.app.data.remote.api.NavigationApi
import com.aicane.app.data.remote.api.TmapPedestrianApi
import com.aicane.app.data.remote.dto.navigation.CreateInstructionRequest
import com.aicane.app.data.remote.dto.navigation.CreateSessionRequest
import com.aicane.app.data.remote.dto.navigation.UpdateStatusRequest
import com.aicane.app.data.remote.dto.tmap.TmapPedestrianRequest
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationSession
import com.aicane.app.domain.model.RouteStep
import com.aicane.app.domain.model.TurnType
import com.aicane.app.domain.repository.NavigationRepository
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.double
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepositoryImpl @Inject constructor(
    private val navigationApi: NavigationApi,
    private val tmapPedestrianApi: TmapPedestrianApi,
) : NavigationRepository {

    override suspend fun createSession(destinationId: String): Result<NavigationSession> = runCatching {
        val response = navigationApi.createSession(CreateSessionRequest(destinationId))
        NavigationSession(response.sessionId)
    }

    override suspend fun fetchRoute(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double,
    ): Result<List<RouteStep>> = runCatching {
        val response = tmapPedestrianApi.getPedestrianRoute(
            request = TmapPedestrianRequest(
                startX = startLng.toString(), startY = startLat.toString(),
                endX   = endLng.toString(),   endY   = endLat.toString(),
            )
        )
        response.features
            .filter { it.geometry.type == "Point" }
            .map { feature ->
                val coords = feature.geometry.coordinates.jsonArray
                val longitude = coords[0].jsonPrimitive.double
                val latitude  = coords[1].jsonPrimitive.double
                val turnType = when (feature.properties.turnType) {
                    12, 16, 18, 212 -> TurnType.LEFT
                    13, 17, 19, 213 -> TurnType.RIGHT
                    else            -> TurnType.STRAIGHT
                }
                RouteStep(
                    latitude      = latitude,
                    longitude     = longitude,
                    distanceMeters = feature.properties.distance,
                    turnType      = turnType,
                    description   = feature.properties.description,
                )
            }
    }

    override suspend fun createInstruction(
        sessionId: String,
        action: NavigationAction,
        distanceMeters: Double,
        message: String,
    ): Result<Unit> = runCatching {
        navigationApi.createInstruction(
            sessionId = sessionId,
            request   = CreateInstructionRequest(action.name.lowercase(), distanceMeters, message),
        )
    }

    override suspend fun updateStatus(sessionId: String, status: String): Result<Unit> = runCatching {
        navigationApi.updateStatus(sessionId, UpdateStatusRequest(status))
    }
}
