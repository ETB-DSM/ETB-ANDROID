package com.aicane.app.domain.usecase.navigation

import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.repository.NavigationRepository
import javax.inject.Inject

class CreateInstructionUseCase @Inject constructor(private val repository: NavigationRepository) {
    suspend operator fun invoke(
        sessionId: String,
        action: NavigationAction,
        distanceMeters: Double,
        message: String,
        latitude: Double,
        longitude: Double,
    ): Result<Unit> = repository.createInstruction(sessionId, action, distanceMeters, message, latitude, longitude)
}
