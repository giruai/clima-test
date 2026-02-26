package com.giruai.climatest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giruai.climatest.data.local.preferences.UserSettings
import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.util.UnitConverter

/**
 * Premium Weather Hero Card
 * - No card container, content sits directly on gradient background
 * - Large temperature display with text gradient
 * - Large weather icon with radial glow
 * - Glass pill stats row
 * - Elegant typography
 */
@Composable
fun WeatherHero(
    currentWeather: CurrentWeather,
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Weather Icon with Radial Glow
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            // Subtle radial glow behind icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .blur(40.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                getWeatherAccentColor(currentWeather.weatherCondition).copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                            radius = 0.5f
                        )
                    )
            )
            
            // Weather Icon (120dp minimum)
            WeatherIcon(
                condition = currentWeather.weatherCondition,
                large = true,
                modifier = Modifier.size(120.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Temperature Display (Premium with gradient)
        TemperatureDisplay(
            temperature = UnitConverter.formatTemperature(
                currentWeather.temperature, 
                userSettings.temperatureUnit
            ),
            weatherCondition = currentWeather.weatherCondition
        )
        
        // Weather Description (elegant styling)
        Text(
            text = currentWeather.weatherCondition.description
                .replaceFirstChar { it.uppercase() },
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = getWeatherAccentColor(currentWeather.weatherCondition).copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Glass Pills Row (Feels Like, Wind, Humidity)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            GlassPill(
                label = "Feels Like",
                value = UnitConverter.formatTemperature(
                    currentWeather.apparentTemperature,
                    userSettings.temperatureUnit
                )
            )
            
            GlassPill(
                label = "Wind",
                value = UnitConverter.formatWindSpeed(
                    currentWeather.windSpeed,
                    userSettings.windUnit
                )
            )
            
            GlassPill(
                label = "Humidity",
                value = "${currentWeather.humidity}%"
            )
        }
    }
}

/**
 * Get accent color based on weather condition for glow effects
 */
private fun getWeatherAccentColor(condition: WeatherCondition): Color {
    return when (condition) {
        WeatherCondition.CLEAR_SKY -> 
            Color(0xFFFFD700) // Gold
        WeatherCondition.PARTLY_CLOUDY -> 
            Color(0xFFB0C4DE) // Light steel blue
        WeatherCondition.OVERCAST -> 
            Color(0xFFB0C4DE) // Light steel blue
        WeatherCondition.SHOWERS -> 
            Color(0xFF87CEEB) // Sky blue
        WeatherCondition.RAIN -> 
            Color(0xFF87CEEB) // Sky blue
        WeatherCondition.THUNDERSTORM -> 
            Color(0xFFDA70D6) // Orchid
        WeatherCondition.SNOW -> 
            Color(0xFFFFFFFF) // White
        WeatherCondition.FOG -> 
            Color(0xFFD3D3D3) // Light gray
        else -> Color(0xFFB0C4DE)
    }
}
