package com.aicane.app.domain.repository

import com.aicane.app.domain.model.Destination

interface DestinationRepository {
    suspend fun getDestinations(): Result<List<Destination>>
    suspend fun createDestination(
        name: String,
        targetText: String,
        latitude: Double,
        longitude: Double,
        radius: Double,
    ): Result<Destination>
    suspend fun deleteDestination(destinationId: String): Result<Unit>
}
