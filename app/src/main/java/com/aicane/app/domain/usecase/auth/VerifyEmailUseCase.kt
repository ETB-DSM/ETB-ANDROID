package com.aicane.app.domain.usecase.auth

import com.aicane.app.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, code: String): Result<Unit> =
        repository.verifyEmail(email, code)
}
