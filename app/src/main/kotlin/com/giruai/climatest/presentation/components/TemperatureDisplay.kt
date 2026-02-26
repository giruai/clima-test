package com.giruai.climatest.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giruai.climatest.presentation.theme.LocalWeatherColors

/**
 * Temperature display with gradient text effect.
 * Shows temperature with large typography and gradient coloring.
 *
 * @param temperature Temperature string (e.g., "22°C")
 * @param modifier Modifier for the component
 * @param fontSize Font size (default 80sp for hero display)
 */
@Composable
fun TemperatureDisplay(
    temperature: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 80.sp
) {
    val weatherColors = LocalWeatherColors.current

    // Create gradient brush for text
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            weatherColors.textTint
        )
    )

    Box(modifier = modifier) {
        Text(
            text = temperature,
            fontSize = fontSize,
            fontWeight = FontWeight.Light, // Thin weight for elegance
            style = TextStyle(
                brush = gradientBrush
            )
        )
    }
}

/**
 * Alternative temperature display that splits number and unit
 * for different styling (number = light, unit = medium weight).
 */
@Composable
fun TemperatureDisplaySplit(
    value: String, // e.g., "22"
    unit: String,  // e.g., "°C"
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 96.sp,
    unitFontSize: TextUnit = 48.sp
) {
    val weatherColors = LocalWeatherColors.current

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            weatherColors.textTint
        )
    )

    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        // Temperature number - thin weight
        Text(
            text = value,
            fontSize = valueFontSize,
            fontWeight = FontWeight.Thin,
            style = TextStyle(brush = gradientBrush)
        )
        // Unit - medium weight
        Text(
            text = unit,
            fontSize = unitFontSize,
            fontWeight = FontWeight.Medium,
            style = TextStyle(brush = gradientBrush),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
