package com.giruai.climatest.domain.repository

import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.model.DailyForecast

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<CurrentWeather>
    suspend fun getForecast(latitude: Double, longitude: Double): Result<List<DailyForecast>>
    suspend fun searchCities(query: String): Result<List<com.giruai.climatest.domain.model.City>>
}
