package com.aicane.app.domain.usecase.destination

import com.aicane.app.domain.model.Destination
import com.aicane.app.domain.repository.DestinationRepository
import javax.inject.Inject

class GetDestinationsUseCase @Inject constructor(private val repository: DestinationRepository) {
    suspend operator fun invoke(): Result<List<Destination>> =
        repository.getDestinations()
}
