package com.giruai.climatest.presentation.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.data.location.ReverseGeocoder
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.usecase.GetFavoritesUseCase
import com.giruai.climatest.domain.usecase.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val reverseGeocoder: ReverseGeocoder
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
                if (favorites.isEmpty()) {
                    _uiState.value = FavoritesUiState.Empty
                } else {
                    // Show favorites immediately, then resolve names in background
                    _uiState.value = FavoritesUiState.Success(favorites)
                    
                    // Check if any favorites need name resolution
                    val needsResolution = favorites.any { 
                        it.name.contains("°") || it.country == "Unknown" 
                    }
                    if (needsResolution) {
                        resolveNames(favorites)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun resolveNames(favorites: List<City>) {
        viewModelScope.launch {
            try {
                val resolved = withContext(Dispatchers.IO) {
                    favorites.map { city ->
                        if (city.name.contains("°") || city.country == "Unknown") {
                            try {
                                val cityName = reverseGeocoder.getCityName(city.latitude, city.longitude)
                                val parts = cityName.split(", ")
                                val name = parts.firstOrNull() ?: city.name
                                val country = if (parts.size > 1) parts.last() else city.country
                                City(id = city.id, name = name, country = country,
                                     latitude = city.latitude, longitude = city.longitude)
                            } catch (e: Exception) {
                                Timber.w(e, "Failed to resolve name for ${city.latitude}, ${city.longitude}")
                                city
                            }
                        } else {
                            city
                        }
                    }
                }
                _uiState.value = FavoritesUiState.Success(resolved)
            } catch (e: Exception) {
                Timber.e(e, "Failed to resolve favorite names")
            }
        }
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
