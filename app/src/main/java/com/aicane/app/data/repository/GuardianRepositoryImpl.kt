package com.aicane.app.data.repository

import com.aicane.app.data.remote.api.GuardianApi
import com.aicane.app.data.remote.dto.guardian.RegisterGuardianRequest
import com.aicane.app.domain.model.Guardian
import com.aicane.app.domain.repository.GuardianRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuardianRepositoryImpl @Inject constructor(
    private val guardianApi: GuardianApi,
) : GuardianRepository {

    override suspend fun registerGuardian(name: String, phone: String): Result<Guardian> =
        runCatching {
            val response = guardianApi.registerGuardian(RegisterGuardianRequest(name, phone))
            Guardian(response.guardianId, response.name, response.phone)
        }

    override suspend fun getGuardians(): Result<List<Guardian>> = runCatching {
        guardianApi.getGuardians().map { Guardian(it.guardianId, it.name, it.phone) }
    }
}
