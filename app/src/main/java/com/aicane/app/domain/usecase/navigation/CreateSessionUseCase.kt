package com.aicane.app.domain.usecase.navigation

import com.aicane.app.domain.model.NavigationSession
import com.aicane.app.domain.repository.NavigationRepository
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(private val repository: NavigationRepository) {
    suspend operator fun invoke(
        destinationId: String,
        startLatitude: Double,
        startLongitude: Double,
    ): Result<NavigationSession> = repository.createSession(destinationId, startLatitude, startLongitude)
}
