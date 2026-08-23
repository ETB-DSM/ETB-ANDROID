package com.aicane.app.domain.usecase.auth

import com.aicane.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<Boolean> =
        repository.loginWithGoogle(idToken)
}
