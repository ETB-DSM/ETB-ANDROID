package com.aicane.app.domain.usecase.auth

import com.aicane.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Pair<String, String> = authRepository.getUserInfo()
}
