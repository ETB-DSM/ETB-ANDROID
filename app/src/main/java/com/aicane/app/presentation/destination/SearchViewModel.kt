package com.aicane.app.presentation.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicane.app.domain.usecase.place.SearchPlaceUseCase
import com.aicane.app.ui.screen.destination.PlaceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchPlaceUseCase: SearchPlaceUseCase,
) : ViewModel() {

    data class UiState(
        val results: List<PlaceResult> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _query
                .debounce(500L)
                .filter { it.isNotBlank() }
                .collectLatest { query ->
                    _uiState.update { it.copy(isLoading = true, errorMessage = "") }
                    searchPlaceUseCase(query)
                        .onSuccess { places ->
                            _uiState.update {
                                it.copy(
                                    results = places.map { p ->
                                        PlaceResult(p.name, p.address, p.latitude, p.longitude)
                                    },
                                    isLoading = false,
                                )
                            }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = error.message ?: "검색에 실패했습니다.")
                            }
                        }
                }
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, errorMessage = "") }
        }
    }
}
