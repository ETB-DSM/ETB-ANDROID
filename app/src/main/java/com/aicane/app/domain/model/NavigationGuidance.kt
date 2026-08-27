package com.aicane.app.domain.model

/**
 * 한 번의 위치 계산 결과. 현재 안내 동작과 함께, 화면 표시용 "다음 회전" 정보를 담는다.
 *
 * @property action            현재 안내 동작
 * @property distanceToDest    목적지까지 직선거리(m)
 * @property nextTurnType      다음 회전 방향 (예정된 회전이 없으면 null)
 * @property distanceToNextTurn 경로를 따라 다음 회전점까지 거리(m) (없으면 null)
 */
data class NavigationGuidance(
    val action: NavigationAction,
    val distanceToDest: Double,
    val nextTurnType: TurnType?,
    val distanceToNextTurn: Double?,
)
