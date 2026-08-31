package com.aicane.app.domain.model

data class NavigationInstruction(
    val sessionId: String,
    val action: NavigationAction,
    val distanceMeters: Double,
    val message: String,
    val latitude: Double,
    val longitude: Double,
    // 아래 두 필드는 화면 표시용이며 서버로는 전송하지 않는다.
    val nextTurnType: TurnType? = null,
    val distanceToNextTurn: Double? = null,
)
