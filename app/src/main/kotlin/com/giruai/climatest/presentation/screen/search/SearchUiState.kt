package com.giruai.climatest.presentation.screen.search

import com.giruai.climatest.domain.model.City

sealed interface SearchUiState {
    object Empty : SearchUiState
    object Loading : SearchUiState
    data class Results(val cities: List<City>) : SearchUiState
    object NoResults : SearchUiState
    data class Error(val message: String) : SearchUiState
}
