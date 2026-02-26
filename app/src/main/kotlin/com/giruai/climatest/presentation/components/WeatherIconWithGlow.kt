package com.giruai.climatest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.theme.LocalWeatherColors

/**
 * Large weather icon (120dp) with radial glow effect.
 * The glow color matches the weather condition accent.
 *
 * @param condition Current weather condition
 * @param modifier Modifier for the component
 * @param size Size of the icon (default 120dp)
 */
@Composable
fun WeatherIconWithGlow(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp
) {
    val weatherColors = LocalWeatherColors.current

    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        // Radial glow behind the icon
        Box(
            modifier = Modifier
                .size(size * 0.8f)
                .background(
                    color = weatherColors.iconGlow,
                    shape = CircleShape
                )
                .blur(radius = 20.dp)
        )

        // The actual weather icon
        WeatherIcon(
            condition = condition,
            large = true,
            modifier = Modifier.size(size)
        )
    }
}
