package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<CurrentWeather> =
        repository.getCurrentWeather(latitude, longitude)
}
