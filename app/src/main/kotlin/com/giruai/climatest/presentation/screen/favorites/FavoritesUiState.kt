package com.giruai.climatest.presentation.screen.favorites

import com.giruai.climatest.domain.model.City

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    data class Success(val favorites: List<City>) : FavoritesUiState
    object Empty : FavoritesUiState
}
