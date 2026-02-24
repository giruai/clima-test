package com.giruai.climatest.domain.model

data class DailyForecast(
    val date: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val weatherCondition: WeatherCondition
)
