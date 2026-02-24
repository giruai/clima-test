package com.giruai.climatest.presentation.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.domain.location.LocationProvider
import com.giruai.climatest.domain.usecase.GetCurrentWeatherUseCase
import com.giruai.climatest.domain.usecase.GetForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val getForecast: GetForecastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

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
                    Timber.d("Got location: ${location.latitude}, ${location.longitude}")
                    
                    // Fetch current weather
                    val weatherResult = getCurrentWeather(location.latitude, location.longitude)
                    weatherResult.onSuccess { currentWeather ->
                        
                        // Fetch forecast
                        val forecastResult = getForecast(location.latitude, location.longitude)
                        forecastResult.onSuccess { forecast ->
                            _uiState.value = WeatherUiState.Success(
                                currentWeather = currentWeather,
                                forecast = forecast,
                                lastUpdated = System.currentTimeMillis()
                            )
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

    fun refresh() {
        Timber.d("Refreshing weather data")
        loadWeather()
    }
}
