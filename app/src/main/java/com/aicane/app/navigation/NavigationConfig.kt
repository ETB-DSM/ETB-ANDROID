package com.aicane.app.navigation

import com.aicane.app.domain.model.RouteStep
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationConfig @Inject constructor() {
    @Volatile var sessionId: String = ""
    @Volatile var steps: List<RouteStep> = emptyList()
    @Volatile var destinationLat: Double = 0.0
    @Volatile var destinationLng: Double = 0.0
    @Volatile var destinationRadius: Double = 30.0
}
