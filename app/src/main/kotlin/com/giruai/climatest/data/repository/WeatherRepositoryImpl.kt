package com.giruai.climatest.data.repository

import com.giruai.climatest.data.remote.api.GeocodingApi
import com.giruai.climatest.data.remote.api.OpenMeteoApi
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.model.DailyForecast
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.domain.repository.WeatherRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: OpenMeteoApi,
    private val geocodingApi: GeocodingApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<CurrentWeather> = try {
        val response = weatherApi.getWeather(latitude, longitude)
        val current = response.current
        Result.success(
            CurrentWeather(
                temperature = current.temperature,
                apparentTemperature = current.apparentTemperature,
                weatherCondition = WeatherCondition.fromWeatherCode(current.weatherCode),
                windSpeed = current.windSpeed,
                humidity = current.humidity
            )
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to fetch current weather")
        Result.failure(e)
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): Result<List<DailyForecast>> = try {
        val response = weatherApi.getWeather(latitude, longitude)
        val daily = response.daily
        val forecasts = daily.time.mapIndexed { index, date ->
            DailyForecast(
                date = date,
                temperatureMax = daily.temperatureMax[index],
                temperatureMin = daily.temperatureMin[index],
                weatherCondition = WeatherCondition.fromWeatherCode(daily.weatherCode[index])
            )
        }
        Result.success(forecasts)
    } catch (e: Exception) {
        Timber.e(e, "Failed to fetch forecast")
        Result.failure(e)
    }

    override suspend fun searchCities(query: String): Result<List<City>> = try {
        val response = geocodingApi.searchCities(query)
        val cities = response.results?.map { dto ->
            City(
                id = dto.id,
                name = dto.name,
                country = dto.country ?: "Unknown",
                latitude = dto.latitude,
                longitude = dto.longitude
            )
        } ?: emptyList()
        Result.success(cities)
    } catch (e: Exception) {
        Timber.e(e, "Failed to search cities")
        Result.failure(e)
    }
}
