package com.giruai.climatest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.domain.model.DailyForecast
import com.giruai.climatest.presentation.theme.GlassBackground
import com.giruai.climatest.presentation.theme.GlassBorder
import com.giruai.climatest.presentation.util.UnitConverter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Compact forecast day card for horizontal scrolling row.
 * Features glassmorphism style with temperature color coding.
 *
 * @param day Daily forecast data
 * @param isToday Whether this card represents today (for highlighting)
 * @param tempUnit Temperature unit (C/F)
 * @param modifier Modifier for the component
 */
@Composable
fun ForecastDayCard(
    day: DailyForecast,
    isToday: Boolean,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    // Get temperature colors for color coding
    val highTempColor = getTemperatureColor(day.temperatureMax, tempUnit)
    val lowTempColor = getTemperatureColor(day.temperatureMin, tempUnit)

    Column(
        modifier = modifier
            .width(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBackground)
            .then(
                if (isToday) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = GlassBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Day name
        Text(
            text = formatDayName(day.date),
            style = MaterialTheme.typography.labelMedium,
            color = if (isToday) MaterialTheme.colorScheme.primary else Color.White,
            textAlign = TextAlign.Center
        )

        // Weather icon
        WeatherIcon(
            condition = day.weatherCondition,
            large = false,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // High temp (color coded)
        Text(
            text = UnitConverter.formatTemperature(day.temperatureMax, tempUnit),
            style = MaterialTheme.typography.titleMedium,
            color = highTempColor,
            textAlign = TextAlign.Center
        )

        // Low temp (color coded, slightly muted)
        Text(
            text = UnitConverter.formatTemperature(day.temperatureMin, tempUnit),
            style = MaterialTheme.typography.bodyMedium,
            color = lowTempColor.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Get color for temperature value (warm = coral/amber, cool = blue)
 */
@Composable
private fun getTemperatureColor(celsius: Double, unit: TemperatureUnit): Color {
    // Convert to Celsius for consistent color mapping
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

private fun formatDayName(date: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = sdf.parse(date) ?: return date
        val calendar = Calendar.getInstance().apply { time = parsedDate }
        val today = Calendar.getInstance()

        when {
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Today"
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) + 1 -> "Tomorrow"
            else -> SimpleDateFormat("EEE", Locale.getDefault()).format(parsedDate) // Mon, Tue, etc.
        }
    } catch (e: Exception) {
        date
    }
}
