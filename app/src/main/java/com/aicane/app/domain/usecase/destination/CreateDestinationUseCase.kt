package com.aicane.app.domain.usecase.destination

import com.aicane.app.domain.model.Destination
import com.aicane.app.domain.repository.DestinationRepository
import javax.inject.Inject

class CreateDestinationUseCase @Inject constructor(private val repository: DestinationRepository) {
    suspend operator fun invoke(
        name: String,
        targetText: String,
        latitude: Double,
        longitude: Double,
        radius: Double,
    ): Result<Destination> = repository.createDestination(name, targetText, latitude, longitude, radius)
}
