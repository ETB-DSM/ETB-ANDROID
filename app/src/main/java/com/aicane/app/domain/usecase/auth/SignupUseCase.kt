package com.aicane.app.domain.usecase.auth

import com.aicane.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, name: String, password: String): Result<Unit> =
        repository.signup(email, name, password)
}
