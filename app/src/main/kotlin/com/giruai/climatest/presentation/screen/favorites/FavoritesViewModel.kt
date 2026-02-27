package com.giruai.climatest.presentation.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.data.local.preferences.SettingsManager
import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.data.local.preferences.UserSettings
import com.giruai.climatest.data.location.ReverseGeocoder
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.domain.usecase.GetCurrentWeatherUseCase
import com.giruai.climatest.domain.usecase.GetFavoritesUseCase
import com.giruai.climatest.domain.usecase.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Rich favorite data with weather preview.
 */
data class RichFavorite(
    val city: City,
    val temperature: Double? = null,
    val condition: WeatherCondition = WeatherCondition.UNKNOWN
)

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val favorites: List<RichFavorite>) : FavoritesUiState()
    object Empty : FavoritesUiState()
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val reverseGeocoder: ReverseGeocoder,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    settingsManager: SettingsManager
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settingsManager.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow<Pair<Long, String>?>(null)
    val showDeleteDialog: StateFlow<Pair<Long, String>?> = _showDeleteDialog.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        getFavorites()
            .onEach { cities ->
                if (cities.isEmpty()) {
                    _uiState.value = FavoritesUiState.Empty
                } else {
                    // Create rich favorites with empty weather initially
                    val richFavorites = cities.map { RichFavorite(city = it) }
                    _uiState.value = FavoritesUiState.Success(richFavorites)

                    // Resolve names and fetch weather in background
                    resolveNamesAndWeather(cities)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun resolveNamesAndWeather(cities: List<City>) {
        viewModelScope.launch {
            try {
                val enriched = withContext(Dispatchers.IO) {
                    cities.map { city ->
                        async {
                            var enrichedCity = city

                            // Resolve name if needed
                            if (city.name.contains("°") || city.country == "Unknown") {
                                try {
                                    val cityName = reverseGeocoder.getCityName(city.latitude, city.longitude)
                                    val parts = cityName.split(", ")
                                    val name = parts.firstOrNull() ?: city.name
                                    val country = if (parts.size > 1) parts.last() else city.country
                                    enrichedCity = City(
                                        id = city.id,
                                        name = name,
                                        country = country,
                                        latitude = city.latitude,
                                        longitude = city.longitude
                                    )
                                } catch (e: Exception) {
                                    Timber.w(e, "Failed to resolve name for ${city.latitude}, ${city.longitude}")
                                }
                            }

                            // Fetch weather
                            val weather = try {
                                getCurrentWeather(enrichedCity.latitude, enrichedCity.longitude).getOrNull()
                            } catch (e: Exception) {
                                Timber.w(e, "Failed to fetch weather for ${enrichedCity.name}")
                                null
                            }

                            RichFavorite(
                                city = enrichedCity,
                                temperature = weather?.temperature,
                                condition = weather?.weatherCondition ?: WeatherCondition.UNKNOWN
                            )
                        }
                    }.awaitAll()
                }
                _uiState.value = FavoritesUiState.Success(enriched)
            } catch (e: Exception) {
                Timber.e(e, "Failed to enrich favorites")
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
        // Trigger re-fetch of weather data
        val currentState = _uiState.value
        if (currentState is FavoritesUiState.Success) {
            val cities = currentState.favorites.map { it.city }
            resolveNamesAndWeather(cities)
        }
    }
}
