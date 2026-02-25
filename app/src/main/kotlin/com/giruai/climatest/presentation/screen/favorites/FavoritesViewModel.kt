package com.giruai.climatest.presentation.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.domain.usecase.GetFavoritesUseCase
import com.giruai.climatest.domain.usecase.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val removeFavorite: RemoveFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow<Pair<Long, String>?>(null)
    val showDeleteDialog: StateFlow<Pair<Long, String>?> = _showDeleteDialog.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        getFavorites()
            .onEach { favorites ->
                _uiState.value = if (favorites.isEmpty()) {
                    FavoritesUiState.Empty
                } else {
                    FavoritesUiState.Success(favorites)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onDeleteClick(cityId: Long, cityName: String) {
        _showDeleteDialog.value = cityId to cityName
    }

    fun onDeleteConfirm() {
        val (cityId, _) = _showDeleteDialog.value ?: return
        viewModelScope.launch {
            val result = removeFavorite(cityId)
            result.onSuccess {
                Timber.d("Removed favorite: cityId=$cityId")
            }.onFailure { error ->
                Timber.e(error, "Failed to remove favorite: cityId=$cityId")
            }
            _showDeleteDialog.value = null
        }
    }

    fun onDeleteCancel() {
        _showDeleteDialog.value = null
    }

    fun refresh() {
        // Favorites auto-refresh via Flow, but could add manual refresh for weather data in future
        Timber.d("Refresh favorites (currently auto-updates via Flow)")
    }
}
