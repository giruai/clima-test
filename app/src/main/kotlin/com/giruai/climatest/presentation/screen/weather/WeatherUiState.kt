package com.giruai.climatest.presentation.screen.weather

import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.model.DailyForecast

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(
        val currentWeather: CurrentWeather,
        val forecast: List<DailyForecast>,
        val cityName: String = "",
        val lastUpdated: Long = System.currentTimeMillis()
    ) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
    object PermissionRequired : WeatherUiState
}
