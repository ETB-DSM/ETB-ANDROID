package com.aicane.app.domain.repository

import com.aicane.app.domain.model.Guardian

interface GuardianRepository {
    suspend fun registerGuardian(name: String, phone: String): Result<Guardian>
    suspend fun getGuardians(): Result<List<Guardian>>
    suspend fun deleteGuardian(guardianId: String): Result<Unit>
}
