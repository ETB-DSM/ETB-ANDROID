package com.aicane.app.domain.usecase.guardian

import com.aicane.app.domain.model.Guardian
import com.aicane.app.domain.repository.GuardianRepository
import javax.inject.Inject

class RegisterGuardianUseCase @Inject constructor(private val repository: GuardianRepository) {
    suspend operator fun invoke(name: String, phone: String): Result<Guardian> =
        repository.registerGuardian(name, phone)
}
