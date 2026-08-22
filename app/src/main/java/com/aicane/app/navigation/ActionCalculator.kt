package com.aicane.app.navigation

import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.RouteStep
import com.aicane.app.domain.model.TurnType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class ActionCalculator @Inject constructor() {

    fun calculate(
        lat: Double,
        lng: Double,
        steps: List<RouteStep>,
        destLat: Double,
        destLng: Double,
        destRadius: Double,
    ): Pair<NavigationAction, Double> {
        val distToDest = haversine(lat, lng, destLat, destLng)
        if (distToDest <= destRadius) return NavigationAction.ARRIVED to 0.0

        if (steps.isEmpty()) return NavigationAction.STRAIGHT to distToDest

        val minDistToRoute = steps.minOf { haversine(lat, lng, it.latitude, it.longitude) }
        if (minDistToRoute > 30.0) return NavigationAction.REROUTE to 0.0

        val currentIdx = steps.indices.minByOrNull { haversine(lat, lng, steps[it].latitude, steps[it].longitude) }
            ?: return NavigationAction.STRAIGHT to distToDest

        val nextTurn = steps.drop(currentIdx + 1).firstOrNull { it.turnType != TurnType.STRAIGHT }
            ?: return NavigationAction.STRAIGHT to distToDest

        val distToTurn = haversine(lat, lng, nextTurn.latitude, nextTurn.longitude)

        return when {
            distToTurn <= 5.0 -> when (nextTurn.turnType) {
                TurnType.LEFT  -> NavigationAction.LEFT
                TurnType.RIGHT -> NavigationAction.RIGHT
                else           -> NavigationAction.STRAIGHT
            } to distToTurn
            distToTurn <= 20.0 -> when (nextTurn.turnType) {
                TurnType.LEFT  -> NavigationAction.PREPARE_LEFT
                TurnType.RIGHT -> NavigationAction.PREPARE_RIGHT
                else           -> NavigationAction.STRAIGHT
            } to distToTurn
            else -> NavigationAction.STRAIGHT to distToTurn
        }
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
