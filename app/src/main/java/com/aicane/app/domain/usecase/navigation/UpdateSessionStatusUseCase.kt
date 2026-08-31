package com.aicane.app.domain.usecase.navigation

import com.aicane.app.domain.repository.NavigationRepository
import javax.inject.Inject

class UpdateSessionStatusUseCase @Inject constructor(private val repository: NavigationRepository) {
    suspend operator fun invoke(sessionId: String, status: String, reason: String? = null): Result<Unit> =
        repository.updateStatus(sessionId, status, reason)
}
