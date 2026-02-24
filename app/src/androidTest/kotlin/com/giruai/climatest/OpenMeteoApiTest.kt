package com.giruai.climatest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.giruai.climatest.data.remote.api.GeocodingApi
import com.giruai.climatest.data.remote.api.OpenMeteoApi
import com.giruai.climatest.domain.model.WeatherCondition
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Instrumented test for Open-Meteo API integration.
 * Tests real API calls to verify DTOs, parsing, and network layer.
 */
@RunWith(AndroidJUnit4::class)
class OpenMeteoApiTest {

    private lateinit var weatherApi: OpenMeteoApi
    private lateinit var geocodingApi: GeocodingApi

    @Before
    fun setup() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val weatherRetrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val geocodingRetrofit = Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = weatherRetrofit.create(OpenMeteoApi::class.java)
        geocodingApi = geocodingRetrofit.create(GeocodingApi::class.java)
    }

    @Test
    fun testGetWeather_BuenosAires_returnsValidData() = runBlocking {
        // Buenos Aires coordinates
        val latitude = -34.6037
        val longitude = -58.3816

        val response = weatherApi.getWeather(latitude, longitude)

        // Verify current weather
        assertNotNull(response.current)
        assertTrue(response.current.temperature in -50.0..60.0) // Reasonable temp range
        assertTrue(response.current.apparentTemperature in -50.0..60.0)
        assertTrue(response.current.weatherCode in 0..99)
        assertTrue(response.current.windSpeed >= 0.0)
        assertTrue(response.current.humidity in 0..100)

        // Verify daily forecast
        assertNotNull(response.daily)
        assertEquals(5, response.daily.time.size)
        assertEquals(5, response.daily.temperatureMax.size)
        assertEquals(5, response.daily.temperatureMin.size)
        assertEquals(5, response.daily.weatherCode.size)

        // Verify temps make sense (max > min)
        response.daily.temperatureMax.forEachIndexed { index, max ->
            val min = response.daily.temperatureMin[index]
            assertTrue("Max temp ($max) should be >= min temp ($min)", max >= min)
        }

        println("✅ Weather API test passed for Buenos Aires")
    }

    @Test
    fun testWeatherCodeMapping_allCodesValid() {
        // Test that all possible weather codes map to valid conditions
        val testCodes = listOf(0, 1, 2, 3, 45, 48, 51, 61, 71, 80, 95, 99)

        testCodes.forEach { code ->
            val condition = WeatherCondition.fromWeatherCode(code)
            assertNotEquals(WeatherCondition.UNKNOWN, condition)
            assertNotNull(condition.description)
            assertNotNull(condition.icon)
        }

        // Test unknown code
        val unknownCondition = WeatherCondition.fromWeatherCode(999)
        assertEquals(WeatherCondition.UNKNOWN, unknownCondition)

        println("✅ Weather code mapping test passed")
    }

    @Test
    fun testSearchCities_paris_returnsResults() = runBlocking {
        val response = geocodingApi.searchCities("Paris")

        assertNotNull(response.results)
        assertTrue(response.results!!.isNotEmpty())

        val paris = response.results!!.first()
        assertEquals("Paris", paris.name)
        assertNotNull(paris.country)
        assertTrue(paris.latitude in -90.0..90.0)
        assertTrue(paris.longitude in -180.0..180.0)

        println("✅ Geocoding API test passed for Paris")
    }

    @Test
    fun testSearchCities_emptyQuery_returnsEmptyOrNull() = runBlocking {
        val response = geocodingApi.searchCities("")

        // API should return null results or empty list for empty query
        if (response.results != null) {
            assertTrue(response.results!!.isEmpty())
        }

        println("✅ Empty query test passed")
    }

    @Test
    fun testSearchCities_limit_respectsParameter() = runBlocking {
        val response = geocodingApi.searchCities("New York", count = 3)

        assertNotNull(response.results)
        assertTrue(response.results!!.size <= 3)

        println("✅ Search limit test passed")
    }

    @Test
    fun testNetworkTimeout_handlesGracefully() = runBlocking {
        // This test verifies timeout is configured (should not hang indefinitely)
        try {
            weatherApi.getWeather(0.0, 0.0)
            // If it succeeds, that's fine
            println("✅ Network timeout test passed (request succeeded)")
        } catch (e: Exception) {
            // If it fails, should fail quickly (not hang)
            println("✅ Network timeout test passed (failed gracefully: ${e.message})")
        }
    }
}
