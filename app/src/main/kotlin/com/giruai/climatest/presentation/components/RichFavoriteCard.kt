package com.giruai.climatest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.theme.GlassBackground
import com.giruai.climatest.presentation.theme.GlassBorder

/**
 * Rich favorite city card with weather preview and glassmorphism design.
 *
 * Features:
 * - Glassmorphism container (semi-transparent + subtle border)
 * - Weather icon with condition-based glow
 * - Current temperature with color coding
 * - City name with country
 * - Swipe-to-delete ready (delete button on right)
 */
@Composable
fun RichFavoriteCard(
    cityName: String,
    country: String,
    temperature: Double?,
    condition: WeatherCondition,
    tempUnit: TemperatureUnit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine temperature color
    val tempColor = when {
        temperature == null -> Color.White
        else -> getTempColor(temperature, tempUnit)
    }

    // Determine condition colors for subtle background tint
    val conditionColors = when (condition) {
        WeatherCondition.CLEAR_SKY, WeatherCondition.PARTLY_CLOUDY ->
            listOf(Color(0x1AFFB946), Color.Transparent) // subtle amber
        WeatherCondition.OVERCAST, WeatherCondition.FOG ->
            listOf(Color(0x1AA0A0A0), Color.Transparent) // subtle gray
        WeatherCondition.DRIZZLE, WeatherCondition.RAIN, WeatherCondition.SHOWERS ->
            listOf(Color(0x1A5CB8FF), Color.Transparent) // subtle blue
        WeatherCondition.THUNDERSTORM ->
            listOf(Color(0x1A9B59B6), Color.Transparent) // subtle purple
        WeatherCondition.SNOW ->
            listOf(Color(0x1A7DD3FC), Color.Transparent) // subtle ice
        else -> listOf(Color.Transparent, Color.Transparent)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(conditionColors),
                alpha = 1f
            )
            .background(GlassBackground)
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Weather icon with glow
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                // Subtle glow background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = tempColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
                WeatherIcon(
                    condition = condition,
                    large = false
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Center: City info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Right: Temperature + Delete
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Temperature
                if (temperature != null) {
                    Text(
                        text = "${temperature.toInt()}°",
                        style = MaterialTheme.typography.headlineMedium,
                        color = tempColor,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "—°",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete button (subtle)
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getTempColor(celsius: Double, unit: TemperatureUnit): Color {
    val tempC = when (unit) {
        TemperatureUnit.CELSIUS -> celsius
        TemperatureUnit.FAHRENHEIT -> (celsius - 32) * 5 / 9
    }

    return when {
        tempC >= 30 -> Color(0xFFFF7A5C) // Hot - coral
        tempC >= 20 -> Color(0xFFFFB946) // Warm - amber
        tempC >= 10 -> Color(0xFF4ADE80) // Mild - green
        tempC >= 0 -> Color(0xFF5CB8FF)  // Cool - blue
        else -> Color(0xFF7DD3FC)        // Cold - ice blue
    }
}
