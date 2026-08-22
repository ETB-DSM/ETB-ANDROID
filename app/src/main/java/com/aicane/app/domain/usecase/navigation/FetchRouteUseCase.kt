package com.aicane.app.domain.usecase.navigation

import com.aicane.app.domain.model.RouteStep
import com.aicane.app.domain.repository.NavigationRepository
import javax.inject.Inject

class FetchRouteUseCase @Inject constructor(private val repository: NavigationRepository) {
    suspend operator fun invoke(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Result<List<RouteStep>> =
        repository.fetchRoute(startLat, startLng, endLat, endLng)
}
