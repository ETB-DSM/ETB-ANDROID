package com.aicane.app.domain.usecase.guardian

import com.aicane.app.domain.model.Guardian
import com.aicane.app.domain.repository.GuardianRepository
import javax.inject.Inject

class GetGuardiansUseCase @Inject constructor(private val repository: GuardianRepository) {
    suspend operator fun invoke(): Result<List<Guardian>> = repository.getGuardians()
}
