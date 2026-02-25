package com.giruai.climatest.data.local.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsManagerTest {

    private lateinit var context: Context
    private lateinit var settingsManager: SettingsManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        settingsManager = SettingsManager(context)
    }

    @After
    fun tearDown() {
        // Clear preferences after each test
        context.getSharedPreferences("clima_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun `settings default to Celsius and KMH`() = runTest {
        val settings = settingsManager.settings.first()
        
        assertEquals(TemperatureUnit.CELSIUS, settings.temperatureUnit)
        assertEquals(WindUnit.KMH, settings.windUnit)
    }

    @Test
    fun `setTemperatureUnit updates settings flow`() = runTest {
        settingsManager.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        
        val settings = settingsManager.settings.first()
        assertEquals(TemperatureUnit.FAHRENHEIT, settings.temperatureUnit)
    }

    @Test
    fun `setWindUnit updates settings flow`() = runTest {
        settingsManager.setWindUnit(WindUnit.MPH)
        
        val settings = settingsManager.settings.first()
        assertEquals(WindUnit.MPH, settings.windUnit)
    }

    @Test
    fun `settings persist across manager instances`() = runTest {
        // Set preferences
        settingsManager.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        settingsManager.setWindUnit(WindUnit.MPH)
        
        // Create new instance (simulates app restart)
        val newManager = SettingsManager(context)
        val settings = newManager.settings.first()
        
        assertEquals(TemperatureUnit.FAHRENHEIT, settings.temperatureUnit)
        assertEquals(WindUnit.MPH, settings.windUnit)
    }

    @Test
    fun `settings flow emits updated values on change`() = runTest {
        val initialSettings = settingsManager.settings.first()
        assertEquals(TemperatureUnit.CELSIUS, initialSettings.temperatureUnit)
        
        settingsManager.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        
        val updatedSettings = settingsManager.settings.first()
        assertEquals(TemperatureUnit.FAHRENHEIT, updatedSettings.temperatureUnit)
    }

    @Test
    fun `temperature unit enum has correct symbols`() {
        assertEquals("°C", TemperatureUnit.CELSIUS.symbol)
        assertEquals("°F", TemperatureUnit.FAHRENHEIT.symbol)
    }

    @Test
    fun `wind unit enum has correct symbols`() {
        assertEquals("km/h", WindUnit.KMH.symbol)
        assertEquals("mph", WindUnit.MPH.symbol)
    }
}
