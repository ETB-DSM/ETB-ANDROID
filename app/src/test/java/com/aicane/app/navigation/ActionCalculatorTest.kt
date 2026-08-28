package com.aicane.app.navigation

import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.RouteStep
import com.aicane.app.domain.model.TurnType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PR #16(직진 중 다음 회전까지 거리/방향 안내)이 실제로 계산되는지 검증한다.
 * 좌표는 위도 1도 ≈ 110,540m 근사를 이용해 "북쪽으로 Nm" 지점을 생성한다.
 */
class ActionCalculatorTest {

    private val baseLat = 37.0
    private val lng = 127.0

    private fun northOf(meters: Double) = baseLat + meters / 110_540.0

    /**
     * 0m 지점부터 10m 간격으로 190m까지 직진 후, 200m 지점에서 좌회전.
     * 좌회전 이후 다시 10m 간격으로 300m까지 직진 후, 310m 지점에서 우회전.
     */
    private fun buildSteps(): List<RouteStep> = buildList {
        for (i in 0..19) {
            add(RouteStep(northOf(i * 10.0), lng, 10.0, TurnType.STRAIGHT, "직진"))
        }
        add(RouteStep(northOf(200.0), lng, 10.0, TurnType.LEFT, "좌회전")) // idx 20
        for (i in 21..30) {
            add(RouteStep(northOf(200.0 + (i - 20) * 10.0), lng, 10.0, TurnType.STRAIGHT, "직진"))
        }
        add(RouteStep(northOf(310.0), lng, 10.0, TurnType.RIGHT, "우회전")) // idx 31
        // 마지막 회전 이후 목적지까지 이어지는 직진 구간(실제 경로엔 항상 존재).
        for (i in 32..39) {
            add(RouteStep(northOf(310.0 + (i - 31) * 10.0), lng, 10.0, TurnType.STRAIGHT, "직진"))
        }
    }

    @Test
    fun `긴 직진 구간에서도 다음 회전 방향과 거리를 계산한다`() {
        val calculator = ActionCalculator()
        val steps = buildSteps()

        // 현재 위치: idx 5 (50m 지점) — 다음 좌회전(200m 지점)까지 아직 150m, PREPARE 임계값(20m) 밖.
        val guidance = calculator.calculate(
            lat = northOf(50.0), lng = lng,
            steps = steps,
            destLat = northOf(1000.0), destLng = lng,
            destRadius = 30.0,
        )

        assertEquals(NavigationAction.STRAIGHT, guidance.action)
        assertEquals(TurnType.LEFT, guidance.nextTurnType)
        assertEquals(150.0, guidance.distanceToNextTurn!!, 1.0)
    }

    @Test
    fun `회전 20m 이내로 접근하면 PREPARE 상태와 함께 거리가 줄어든다`() {
        val calculator = ActionCalculator()
        val steps = buildSteps()

        // 현재 위치: 194m 지점 — 좌회전(200m)까지 약 6~14m.
        val guidance = calculator.calculate(
            lat = northOf(194.0), lng = lng,
            steps = steps,
            destLat = northOf(1000.0), destLng = lng,
            destRadius = 30.0,
        )

        assertEquals(NavigationAction.PREPARE_LEFT, guidance.action)
        assertEquals(TurnType.LEFT, guidance.nextTurnType)
        assertTrue("distanceToNextTurn=${guidance.distanceToNextTurn}", guidance.distanceToNextTurn!! <= 20.0)
    }

    /**
     * 버그 재현: GPS로 가장 가까운 경로점(currentIdx)이 회전점 그 자체일 때,
     * nextTurnIdx 탐색이 currentIdx+1부터 시작해 코앞의 회전을 건너뛰고
     * 훨씬 뒤에 있는 다음 회전(우회전, 310m)을 "다음 회전"으로 잘못 계산한다.
     * 기대 동작(action=LEFT, 거리 ~1m)과 달리 실제로는 STRAIGHT/RIGHT/~101m가 나와 실패해야 정상이다.
     */
    @Test
    fun `회전점 바로 위에서는 눈앞의 회전을 건너뛰지 않아야 한다`() {
        val calculator = ActionCalculator()
        val steps = buildSteps()

        // 현재 위치: 199m 지점 — 가장 가까운 경로점은 idx 20(좌회전, 200m 지점, 거리 1m).
        val guidance = calculator.calculate(
            lat = northOf(199.0), lng = lng,
            steps = steps,
            destLat = northOf(1000.0), destLng = lng,
            destRadius = 30.0,
        )

        assertEquals("눈앞의 좌회전을 인지해야 한다", NavigationAction.LEFT, guidance.action)
        assertEquals(TurnType.LEFT, guidance.nextTurnType)
        assertTrue("distanceToNextTurn=${guidance.distanceToNextTurn}", guidance.distanceToNextTurn!! <= 5.0)
    }

    @Test
    fun `마지막 회전 이후에는 다음 회전 정보가 없다`() {
        val calculator = ActionCalculator()
        val steps = buildSteps()

        // 현재 위치: 350m 지점 — 마지막 회전(우회전, 310m)을 이미 지나 남은 회전이 없음.
        val guidance = calculator.calculate(
            lat = northOf(350.0), lng = lng,
            steps = steps,
            destLat = northOf(1000.0), destLng = lng,
            destRadius = 30.0,
        )

        assertEquals(NavigationAction.STRAIGHT, guidance.action)
        assertNull(guidance.nextTurnType)
        assertNull(guidance.distanceToNextTurn)
    }
}
