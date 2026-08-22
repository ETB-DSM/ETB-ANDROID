package com.aicane.app.domain.usecase.place

import com.aicane.app.domain.model.Place
import com.aicane.app.domain.repository.PlaceRepository
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(private val repository: PlaceRepository) {
    suspend operator fun invoke(query: String): Result<List<Place>> =
        repository.searchPlaces(query)
}
