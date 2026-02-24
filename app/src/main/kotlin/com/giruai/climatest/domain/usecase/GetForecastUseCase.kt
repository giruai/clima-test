package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.DailyForecast
import com.giruai.climatest.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<List<DailyForecast>> =
        repository.getForecast(latitude, longitude)
}
