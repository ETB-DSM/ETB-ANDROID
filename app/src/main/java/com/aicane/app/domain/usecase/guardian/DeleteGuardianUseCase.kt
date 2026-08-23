package com.aicane.app.domain.usecase.guardian

import com.aicane.app.domain.repository.GuardianRepository
import javax.inject.Inject

class DeleteGuardianUseCase @Inject constructor(private val repository: GuardianRepository) {
    suspend operator fun invoke(guardianId: String): Result<Unit> =
        repository.deleteGuardian(guardianId)
}
