package com.giruai.climatest.domain.model

enum class WeatherCondition(val description: String, val icon: String) {
    CLEAR_SKY("Clear sky", "☀️"),
    PARTLY_CLOUDY("Partly cloudy", "⛅"),
    OVERCAST("Overcast", "☁️"),
    FOG("Fog", "🌫️"),
    DRIZZLE("Drizzle", "🌦️"),
    RAIN("Rain", "🌧️"),
    SNOW("Snow", "❄️"),
    SHOWERS("Showers", "🌦️"),
    THUNDERSTORM("Thunderstorm", "⛈️"),
    UNKNOWN("Unknown", "❓");

    companion object {
        fun fromWeatherCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR_SKY
            1, 2, 3 -> PARTLY_CLOUDY
            45, 48 -> FOG
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 63, 65, 66, 67 -> RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            80, 81, 82 -> SHOWERS
            95, 96, 99 -> THUNDERSTORM
            else -> UNKNOWN
        }
    }
}
