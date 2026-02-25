package com.giruai.climatest.presentation.util

import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.data.local.preferences.WindUnit
import kotlin.math.roundToInt

object UnitConverter {

    fun convertTemperature(celsius: Double, unit: TemperatureUnit): Double {
        return when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
        }
    }

    fun formatTemperature(celsius: Double, unit: TemperatureUnit): String {
        val converted = convertTemperature(celsius, unit)
        return "${converted.roundToInt()}${unit.symbol}"
    }

    fun convertWindSpeed(kmh: Double, unit: WindUnit): Double {
        return when (unit) {
            WindUnit.KMH -> kmh
            WindUnit.MPH -> kmh * 0.621371
        }
    }

    fun formatWindSpeed(kmh: Double, unit: WindUnit): String {
        val converted = convertWindSpeed(kmh, unit)
        return "${converted.roundToInt()} ${unit.symbol}"
    }
}
