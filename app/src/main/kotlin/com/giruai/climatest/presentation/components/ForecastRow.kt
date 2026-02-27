package com.giruai.climatest.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.domain.model.DailyForecast

/**
 * Horizontal scrolling 5-day forecast row.
 *
 * @param forecast List of daily forecasts
 * @param tempUnit Temperature unit (C/F)
 * @param modifier Modifier for the component
 */
@Composable
fun ForecastRow(
    forecast: List<DailyForecast>,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section header
        Text(
            text = "5-DAY FORECAST",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal scrolling forecast cards
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(forecast) { day ->
                val isToday = isToday(day.date)
                ForecastDayCard(
                    day = day,
                    isToday = isToday,
                    tempUnit = tempUnit
                )
            }
        }
    }
}

/**
 * Check if a date string represents today.
 */
private fun isToday(date: String): Boolean {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val parsedDate = sdf.parse(date) ?: return false
        val calendar = java.util.Calendar.getInstance().apply { time = parsedDate }
        val today = java.util.Calendar.getInstance()
        calendar.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
    } catch (e: Exception) {
        false
    }
}
