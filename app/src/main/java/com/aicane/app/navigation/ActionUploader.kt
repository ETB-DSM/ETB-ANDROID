package com.aicane.app.navigation

import android.util.Log
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationInstruction
import com.aicane.app.domain.usecase.navigation.CreateInstructionUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionUploader @Inject constructor(
    private val createInstructionUseCase: CreateInstructionUseCase,
) {
    private var lastAction: NavigationAction? = null
    private var lastUploadTime: Long = 0L

    suspend fun upload(instruction: NavigationInstruction) {
        val now = System.currentTimeMillis()
        val actionChanged = instruction.action != lastAction
        val keepAliveExpired = now - lastUploadTime >= 30_000L

        if (actionChanged || keepAliveExpired) {
            createInstructionUseCase(
                sessionId      = instruction.sessionId,
                action         = instruction.action,
                distanceMeters = instruction.distanceMeters,
                message        = instruction.message,
            ).onSuccess {
                lastAction     = instruction.action
                lastUploadTime = now
            }.onFailure { error ->
                Log.w("ActionUploader", "전송 실패: ${error.message}")
            }
        }
    }

    fun reset() {
        lastAction     = null
        lastUploadTime = 0L
    }
}
