package com.aicane.app.domain.usecase.auth

import com.aicane.app.domain.repository.AuthRepository
import javax.inject.Inject

class ResendCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> = repository.resendCode(email)
}
