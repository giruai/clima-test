package com.giruai.climatest.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

enum class WindUnit(val symbol: String) {
    KMH("km/h"),
    MPH("mph")
}

data class UserSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windUnit: WindUnit = WindUnit.KMH
)

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("clima_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_TEMP_UNIT, unit.name).apply()
        _settings.value = _settings.value.copy(temperatureUnit = unit)
    }

    fun setWindUnit(unit: WindUnit) {
        prefs.edit().putString(KEY_WIND_UNIT, unit.name).apply()
        _settings.value = _settings.value.copy(windUnit = unit)
    }

    private fun loadSettings(): UserSettings {
        val tempUnit = prefs.getString(KEY_TEMP_UNIT, TemperatureUnit.CELSIUS.name)
            ?.let { TemperatureUnit.valueOf(it) } ?: TemperatureUnit.CELSIUS
        val windUnit = prefs.getString(KEY_WIND_UNIT, WindUnit.KMH.name)
            ?.let { WindUnit.valueOf(it) } ?: WindUnit.KMH
        return UserSettings(temperatureUnit = tempUnit, windUnit = windUnit)
    }

    companion object {
        private const val KEY_TEMP_UNIT = "temperature_unit"
        private const val KEY_WIND_UNIT = "wind_unit"
    }
}
