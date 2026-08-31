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

        if (!actionChanged && !keepAliveExpired) return

        for (attempt in 1..3) {
            val result = createInstructionUseCase(
                sessionId      = instruction.sessionId,
                action         = instruction.action,
                distanceMeters = instruction.distanceMeters,
                message        = instruction.message,
                latitude       = instruction.latitude,
                longitude      = instruction.longitude,
            )
            if (result.isSuccess) {
                lastAction     = instruction.action
                lastUploadTime = now
                return
            }
            Log.w("ActionUploader", "전송 실패 (시도 $attempt/3): ${result.exceptionOrNull()?.message}")
        }
    }

    fun reset() {
        lastAction     = null
        lastUploadTime = 0L
    }
}
