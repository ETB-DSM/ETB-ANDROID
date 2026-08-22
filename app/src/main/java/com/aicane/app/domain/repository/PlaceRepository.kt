package com.aicane.app.domain.repository

import com.aicane.app.domain.model.Place

interface PlaceRepository {
    suspend fun searchPlaces(query: String): Result<List<Place>>
}
