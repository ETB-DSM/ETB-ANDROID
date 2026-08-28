package com.aicane.app.navigation

import android.util.Log
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationGuidance
import com.aicane.app.domain.model.RouteStep
import com.aicane.app.domain.model.TurnType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val OFF_ROUTE_THRESHOLD = 50.0   // m. 이 거리를 넘어야 경로 이탈로 본다.

@Singleton
class ActionCalculator @Inject constructor() {

    // 안내 시작 후 사용자가 경로에 한 번이라도 진입했는지. 진입 전(출발 스냅으로 멀 때)에는 재탐색을 보류한다.
    @Volatile private var hasEnteredRoute = false

    /** 새 안내 세션 시작 시 호출해 "경로 진입 여부" 상태를 초기화한다. */
    fun reset() {
        hasEnteredRoute = false
    }

    fun calculate(
        lat: Double,
        lng: Double,
        steps: List<RouteStep>,
        destLat: Double,
        destLng: Double,
        destRadius: Double,
    ): NavigationGuidance {
        val distToDest = haversine(lat, lng, destLat, destLng)
        if (distToDest <= destRadius) return NavigationGuidance(NavigationAction.ARRIVED, distToDest, null, null)

        if (steps.isEmpty()) return NavigationGuidance(NavigationAction.STRAIGHT, distToDest, null, null)

        val distToRoute = distanceToRoute(lat, lng, steps)
        if (distToRoute <= OFF_ROUTE_THRESHOLD) {
            hasEnteredRoute = true
        } else {
            // 경로에서 벗어남: 이미 진입한 적 있으면 재탐색, 아직 진입 전(출발 유예)이면 직진 안내만.
            val offRouteAction = if (hasEnteredRoute) NavigationAction.REROUTE else NavigationAction.STRAIGHT
            return NavigationGuidance(offRouteAction, distToDest, null, null)
        }

        val currentIdx = steps.indices.minByOrNull { haversine(lat, lng, steps[it].latitude, steps[it].longitude) }
            ?: return NavigationGuidance(NavigationAction.STRAIGHT, distToDest, null, null)

        // currentIdx 자신이 회전점일 수 있으므로(사용자가 회전 지점 바로 위에 있을 때) currentIdx부터 포함해 탐색한다.
        val nextTurnIdx = (currentIdx until steps.size).firstOrNull { steps[it].turnType != TurnType.STRAIGHT }
        if (nextTurnIdx == null) {
            Log.d("NavCalc", "currentIdx=$currentIdx/${steps.size}, 다음 회전 없음 (destDistance=${distToDest.toInt()}m)")
            return NavigationGuidance(NavigationAction.STRAIGHT, distToDest, null, null)
        }

        val nextTurn = steps[nextTurnIdx]
        val distToTurn = pathDistanceToTurn(lat, lng, steps, currentIdx, nextTurnIdx)
        Log.d(
            "NavCalc",
            "currentIdx=$currentIdx/${steps.size}, nextTurnIdx=$nextTurnIdx(${nextTurn.turnType}), " +
                "distToTurn=${distToTurn.toInt()}m, destDistance=${distToDest.toInt()}m",
        )

        val action = when {
            distToTurn <= 5.0 -> when (nextTurn.turnType) {
                TurnType.LEFT  -> NavigationAction.LEFT
                TurnType.RIGHT -> NavigationAction.RIGHT
                else           -> NavigationAction.STRAIGHT
            }
            distToTurn <= 20.0 -> when (nextTurn.turnType) {
                TurnType.LEFT  -> NavigationAction.PREPARE_LEFT
                TurnType.RIGHT -> NavigationAction.PREPARE_RIGHT
                else           -> NavigationAction.STRAIGHT
            }
            else -> NavigationAction.STRAIGHT
        }
        return NavigationGuidance(action, distToDest, nextTurn.turnType, distToTurn)
    }

    /** 현재 위치에서 경로를 따라 다음 회전점(nextTurnIdx)까지의 누적 거리(m). */
    private fun pathDistanceToTurn(
        lat: Double, lng: Double, steps: List<RouteStep>, currentIdx: Int, nextTurnIdx: Int,
    ): Double {
        var d = haversine(lat, lng, steps[currentIdx].latitude, steps[currentIdx].longitude)
        for (i in currentIdx until nextTurnIdx) {
            d += haversine(steps[i].latitude, steps[i].longitude, steps[i + 1].latitude, steps[i + 1].longitude)
        }
        return d
    }

    /** 경로(연속 선분들)까지의 최단 수직거리(m). */
    private fun distanceToRoute(lat: Double, lng: Double, steps: List<RouteStep>): Double {
        if (steps.size == 1) return haversine(lat, lng, steps[0].latitude, steps[0].longitude)
        var min = Double.MAX_VALUE
        for (i in 0 until steps.size - 1) {
            val d = pointToSegment(lat, lng, steps[i], steps[i + 1])
            if (d < min) min = d
        }
        return min
    }

    /** 점 P에서 선분 A-B까지 최단거리(m). 도보 스케일에서 충분히 정확한 국소 평면 근사. */
    private fun pointToSegment(pLat: Double, pLng: Double, a: RouteStep, b: RouteStep): Double {
        val mPerLat = 110_540.0
        val mPerLng = 111_320.0 * cos(Math.toRadians(pLat))
        val px = pLng * mPerLng; val py = pLat * mPerLat
        val ax = a.longitude * mPerLng; val ay = a.latitude * mPerLat
        val bx = b.longitude * mPerLng; val by = b.latitude * mPerLat
        val dx = bx - ax; val dy = by - ay
        val len2 = dx * dx + dy * dy
        val t = if (len2 == 0.0) 0.0 else (((px - ax) * dx + (py - ay) * dy) / len2).coerceIn(0.0, 1.0)
        val cx = ax + t * dx; val cy = ay + t * dy
        return hypot(px - cx, py - cy)
    }

    fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371000.0
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δφ = Math.toRadians(lat2 - lat1)
        val Δλ = Math.toRadians(lng2 - lng1)
        val a = sin(Δφ / 2).pow(2) + cos(φ1) * cos(φ2) * sin(Δλ / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
