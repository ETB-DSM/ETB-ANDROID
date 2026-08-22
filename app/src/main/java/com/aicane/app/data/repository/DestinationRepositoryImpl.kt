package com.aicane.app.data.repository

import com.aicane.app.data.remote.api.DestinationApi
import com.aicane.app.data.remote.dto.destination.CreateDestinationRequest
import com.aicane.app.data.remote.dto.destination.DestinationResponse
import com.aicane.app.domain.model.Destination
import com.aicane.app.domain.repository.DestinationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestinationRepositoryImpl @Inject constructor(
    private val destinationApi: DestinationApi,
) : DestinationRepository {
    override suspend fun getDestinations(): Result<List<Destination>> = runCatching {
        destinationApi.getDestinations().map { it.toDomain() }
    }

    override suspend fun createDestination(
        name: String,
        targetText: String,
        latitude: Double,
        longitude: Double,
        radius: Double,
    ): Result<Destination> = runCatching {
        destinationApi.createDestination(
            CreateDestinationRequest(name, targetText, latitude, longitude, radius)
        ).toDomain()
    }

    private fun DestinationResponse.toDomain() =
        Destination(destinationId, name, targetText, latitude, longitude, radius)
}
