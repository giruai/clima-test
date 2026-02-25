package com.giruai.climatest.presentation.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.data.local.preferences.SettingsManager
import com.giruai.climatest.data.local.preferences.UserSettings
import com.giruai.climatest.data.location.ReverseGeocoder
import com.giruai.climatest.domain.location.LocationProvider
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.usecase.AddFavoriteUseCase
import com.giruai.climatest.domain.usecase.GetCurrentWeatherUseCase
import com.giruai.climatest.domain.usecase.GetForecastUseCase
import com.giruai.climatest.domain.usecase.IsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val getForecast: GetForecastUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val reverseGeocoder: ReverseGeocoder,
    settingsManager: SettingsManager
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settingsManager.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var currentCity: City? = null

    init {
        checkPermissionAndLoadWeather()
    }

    fun checkPermissionAndLoadWeather() {
        if (!locationProvider.hasPermission()) {
            Timber.w("Location permission not granted")
            _uiState.value = WeatherUiState.PermissionRequired
        } else {
            loadWeather()
        }
    }

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                val locationResult = locationProvider.getCurrentLocation()
                locationResult.onSuccess { location ->
                    loadWeatherForCoordinates(location.latitude, location.longitude)
                }.onFailure { error ->
                    Timber.e(error, "Failed to get location")
                    _uiState.value = WeatherUiState.Error(
                        "Failed to get location: ${error.message}"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error loading weather")
                _uiState.value = WeatherUiState.Error(
                    "An unexpected error occurred: ${e.message}"
                )
            }
        }
    }

    fun loadWeatherForCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                Timber.d("Loading weather for coordinates: $latitude, $longitude")
                
                // Fetch current weather
                val weatherResult = getCurrentWeather(latitude, longitude)
                weatherResult.onSuccess { currentWeather ->
                    
                    // Fetch forecast
                    val forecastResult = getForecast(latitude, longitude)
                    forecastResult.onSuccess { forecast ->
                        val cityName = reverseGeocoder.getCityName(latitude, longitude)
                        _uiState.value = WeatherUiState.Success(
                            currentWeather = currentWeather,
                            forecast = forecast,
                            cityName = cityName,
                            lastUpdated = System.currentTimeMillis()
                        )
                        
                        // Extract country from cityName (format: "City, CC")
                        val parts = cityName.split(", ")
                        val city = parts.firstOrNull() ?: cityName
                        val country = if (parts.size > 1) parts.last() else "Unknown"
                        
                        // Store current city for favorites
                        currentCity = City(
                            id = generateCityId(latitude, longitude),
                            name = city,
                            country = country,
                            latitude = latitude,
                            longitude = longitude
                        )
                        checkIfFavorite()
                    }.onFailure { error ->
                        Timber.e(error, "Failed to fetch forecast")
                        _uiState.value = WeatherUiState.Error(
                            "Failed to load forecast: ${error.message}"
                        )
                    }
                    
                }.onFailure { error ->
                    Timber.e(error, "Failed to fetch current weather")
                    _uiState.value = WeatherUiState.Error(
                        "Failed to load weather: ${error.message}"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error loading weather for coordinates")
                _uiState.value = WeatherUiState.Error(
                    "An unexpected error occurred: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        Timber.d("Refreshing weather data")
        loadWeather()
    }

    private fun checkIfFavorite() {
        viewModelScope.launch {
            val city = currentCity ?: return@launch
            _isFavorite.value = isFavoriteUseCase(city.id)
        }
    }

    fun addToFavorites() {
        Timber.d("addToFavorites called, currentCity=$currentCity")
        val city = currentCity
        if (city == null) {
            Timber.w("Cannot add favorite: currentCity is null")
            _snackbarMessage.value = "No city loaded"
            return
        }

        Timber.d("Adding city to favorites: id=${city.id}, name=${city.name}, lat=${city.latitude}, lon=${city.longitude}")
        viewModelScope.launch {
            val result = addFavorite(city)
            result.onSuccess {
                Timber.d("Successfully added to favorites: ${city.name}")
                _snackbarMessage.value = "Added to Favorites"
                _isFavorite.value = true
            }.onFailure { error ->
                Timber.e(error, "Failed to add favorite: ${city.name}")
                val message = when {
                    error.message?.contains("10") == true -> "Maximum 10 favorites reached"
                    else -> "Failed to add favorite: ${error.message}"
                }
                _snackbarMessage.value = message
            }
        }
    }

    fun snackbarShown() {
        _snackbarMessage.value = null
    }

    private fun generateCityId(latitude: Double, longitude: Double): Long {
        // Simple hash of coordinates to generate stable positive ID
        val latInt = (latitude * 100000).toLong()
        val lonInt = (longitude * 100000).toLong()
        val combined = latInt * 1000000L + lonInt
        // Ensure positive by taking absolute value
        return Math.abs(combined)
    }
}
