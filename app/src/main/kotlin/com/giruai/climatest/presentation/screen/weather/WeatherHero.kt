package com.giruai.climatest.presentation.screen.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giruai.climatest.data.local.preferences.UserSettings
import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.presentation.components.GlassPill
import com.giruai.climatest.presentation.components.TemperatureDisplay
import com.giruai.climatest.presentation.components.WeatherIconWithGlow
import com.giruai.climatest.presentation.theme.LocalWeatherColors
import com.giruai.climatest.presentation.util.UnitConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Premium Weather Hero Card (S7.3)
 *
 * Features:
 * - Large temperature display with gradient text (80sp)
 * - Weather icon with radial glow (120dp)
 * - Stats as glass pills (Feels Like, Wind, Humidity)
 * - No card container — content sits directly on gradient background
 * - Removed redundant city name (already in TopAppBar)
 */
@Composable
fun WeatherHero(
    currentWeather: CurrentWeather,
    userSettings: UserSettings
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weather Icon with Glow (120dp)
        WeatherIconWithGlow(
            condition = currentWeather.weatherCondition,
            size = 120.dp
        )

        // Temperature with Gradient Text (80sp)
        TemperatureDisplay(
            temperature = UnitConverter.formatTemperature(
                currentWeather.temperature,
                userSettings.temperatureUnit
            ),
            fontSize = 80.sp
        )

        // Weather Description
        Text(
            text = currentWeather.weatherCondition.description,
            style = MaterialTheme.typography.titleMedium,
            color = LocalWeatherColors.current.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stats as Glass Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GlassPill(
                icon = Icons.Default.Info,
                value = UnitConverter.formatTemperature(
                    currentWeather.apparentTemperature,
                    userSettings.temperatureUnit
                ),
                label = "Feels Like"
            )
            GlassPill(
                icon = Icons.Default.Info,
                value = UnitConverter.formatWindSpeed(
                    currentWeather.windSpeed,
                    userSettings.windUnit
                ),
                label = "Wind"
            )
            GlassPill(
                icon = Icons.Default.Info,
                value = "${currentWeather.humidity}%",
                label = "Humidity"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Last Updated
        Text(
            text = "Updated ${formatLastUpdated(System.currentTimeMillis())}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

private fun formatLastUpdated(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "just now"
        diff < 3600_000 -> "${diff / 60_000} min ago"
        else -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
