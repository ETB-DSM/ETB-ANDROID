package com.aicane.app.domain.repository

import com.aicane.app.domain.model.NavigationSession

interface NavigationRepository {
    suspend fun createSession(destinationId: String): Result<NavigationSession>
}
