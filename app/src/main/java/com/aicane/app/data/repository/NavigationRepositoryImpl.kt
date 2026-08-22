package com.aicane.app.data.repository

import com.aicane.app.data.remote.api.NavigationApi
import com.aicane.app.data.remote.dto.navigation.CreateSessionRequest
import com.aicane.app.domain.model.NavigationSession
import com.aicane.app.domain.repository.NavigationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepositoryImpl @Inject constructor(
    private val navigationApi: NavigationApi,
) : NavigationRepository {
    override suspend fun createSession(destinationId: String): Result<NavigationSession> = runCatching {
        val response = navigationApi.createSession(CreateSessionRequest(destinationId))
        NavigationSession(response.sessionId)
    }
}
