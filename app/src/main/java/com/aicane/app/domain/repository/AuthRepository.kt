package com.aicane.app.domain.repository

interface AuthRepository {
    suspend fun signup(email: String, name: String, password: String): Result<Unit>
    suspend fun verifyEmail(email: String, code: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun logout(): Result<Unit>
}
