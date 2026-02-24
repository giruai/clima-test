package com.giruai.climatest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: CurrentDto,
    val daily: DailyDto
)

data class CurrentDto(
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("apparent_temperature")
    val apparentTemperature: Double,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double,
    @SerializedName("relative_humidity_2m")
    val humidity: Int
)

data class DailyDto(
    val time: List<String>,
    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>
)
