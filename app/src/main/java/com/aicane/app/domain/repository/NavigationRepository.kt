package com.aicane.app.domain.repository

import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationSession
import com.aicane.app.domain.model.RouteStep

interface NavigationRepository {
    suspend fun createSession(
        destinationId: String,
        startLatitude: Double,
        startLongitude: Double,
    ): Result<NavigationSession>
    suspend fun fetchRoute(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Result<List<RouteStep>>
    suspend fun createInstruction(
        sessionId: String,
        action: NavigationAction,
        distanceMeters: Double,
        message: String,
        latitude: Double,
        longitude: Double,
    ): Result<Unit>
    suspend fun updateStatus(sessionId: String, status: String, reason: String? = null): Result<Unit>
}
