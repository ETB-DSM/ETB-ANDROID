package com.aicane.app.domain.usecase.destination

import com.aicane.app.domain.repository.DestinationRepository
import javax.inject.Inject

class DeleteDestinationUseCase @Inject constructor(private val repository: DestinationRepository) {
    suspend operator fun invoke(destinationId: String): Result<Unit> =
        repository.deleteDestination(destinationId)
}
