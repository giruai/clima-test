package com.giruai.climatest.presentation.util

import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.data.local.preferences.WindUnit
import org.junit.Assert.*
import org.junit.Test

class UnitConverterTest {

    @Test
    fun `celsius to celsius returns same value`() {
        assertEquals(25.0, UnitConverter.convertTemperature(25.0, TemperatureUnit.CELSIUS), 0.01)
    }

    @Test
    fun `0 celsius to fahrenheit returns 32`() {
        assertEquals(32.0, UnitConverter.convertTemperature(0.0, TemperatureUnit.FAHRENHEIT), 0.01)
    }

    @Test
    fun `100 celsius to fahrenheit returns 212`() {
        assertEquals(212.0, UnitConverter.convertTemperature(100.0, TemperatureUnit.FAHRENHEIT), 0.01)
    }

    @Test
    fun `negative celsius to fahrenheit`() {
        assertEquals(-4.0, UnitConverter.convertTemperature(-20.0, TemperatureUnit.FAHRENHEIT), 0.01)
    }

    @Test
    fun `formatTemperature celsius`() {
        assertEquals("25°C", UnitConverter.formatTemperature(25.3, TemperatureUnit.CELSIUS))
    }

    @Test
    fun `formatTemperature fahrenheit`() {
        assertEquals("77°F", UnitConverter.formatTemperature(25.0, TemperatureUnit.FAHRENHEIT))
    }

    @Test
    fun `kmh to kmh returns same value`() {
        assertEquals(10.0, UnitConverter.convertWindSpeed(10.0, WindUnit.KMH), 0.01)
    }

    @Test
    fun `kmh to mph conversion`() {
        assertEquals(6.21, UnitConverter.convertWindSpeed(10.0, WindUnit.MPH), 0.01)
    }

    @Test
    fun `formatWindSpeed kmh`() {
        assertEquals("10 km/h", UnitConverter.formatWindSpeed(10.0, WindUnit.KMH))
    }

    @Test
    fun `formatWindSpeed mph`() {
        assertEquals("6 mph", UnitConverter.formatWindSpeed(10.0, WindUnit.MPH))
    }
}
