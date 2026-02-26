package com.giruai.climatest.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giruai.climatest.domain.model.WeatherCondition

/**
 * Premium temperature display with gradient text effect
 * - 80-96sp font size
 * - Thin/light weight for number
 * - Gradient effect based on temperature
 */
@Composable
fun TemperatureDisplay(
    temperature: String,
    weatherCondition: WeatherCondition,
    modifier: Modifier = Modifier
) {
    // Determine gradient colors based on temperature and weather
    val gradientColors = when (weatherCondition) {
        WeatherCondition.CLEAR_SKY -> {
            // Warm gradient for sunny
            listOf(
                Color(0xFFFFE4B5), // Warm cream
                Color(0xFFFFD700), // Gold
                Color(0xFFFFA500)  // Orange
            )
        }
        WeatherCondition.RAIN,
        WeatherCondition.SHOWERS -> {
            // Cool gradient for rain
            listOf(
                Color(0xFFB0E0E6), // Powder blue
                Color(0xFF87CEEB), // Sky blue
                Color(0xFF4682B4)  // Steel blue
            )
        }
        WeatherCondition.SNOW -> {
            // Ice gradient for snow
            listOf(
                Color(0xFFFFFFFF), // White
                Color(0xFFE0F7FA), // Light cyan
                Color(0xFFB2EBF2)  // Cyan
            )
        }
        WeatherCondition.THUNDERSTORM -> {
            // Electric gradient for storms
            listOf(
                Color(0xFFE6E6FA), // Lavender
                Color(0xFFDA70D6), // Orchid
                Color(0xFF9370DB)  // Medium purple
            )
        }
        else -> {
            // Neutral gradient for cloudy/unknown
            listOf(
                Color(0xFFF5F5F5), // White smoke
                Color(0xFFE0E0E0), // Light gray
                Color(0xFFBDBDBD)  // Gray
            )
        }
    }
    
    val gradientBrush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        // Temperature number with gradient
        Text(
            text = temperature.filter { it.isDigit() || it == '-' },
            fontSize = 96.sp,
            fontWeight = FontWeight.Light, // Thin/light weight
            style = TextStyle(
                brush = gradientBrush,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(0f, 4f),
                    blurRadius = 8f
                )
            )
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        // Degree symbol (smaller, medium weight)
        Text(
            text = "°",
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.Top)
        )
    }
}
