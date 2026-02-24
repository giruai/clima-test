package com.giruai.climatest.domain.model

data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double,
    val weatherCondition: WeatherCondition,
    val windSpeed: Double,
    val humidity: Int,
    val cityName: String = ""
)
