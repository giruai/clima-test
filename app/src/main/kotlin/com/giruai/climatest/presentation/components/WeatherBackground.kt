package com.giruai.climatest.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.theme.WeatherColorScheme
import com.giruai.climatest.presentation.theme.getWeatherColors

/**
 * Dynamic weather background that changes based on current weather condition.
 * Features smooth crossfade animation when weather changes.
 *
 * @param weatherCondition Current weather condition to determine background colors
 * @param isNight Whether it's nighttime (affects clear sky colors)
 * @param modifier Modifier for the background container
 * @param content Content to display on top of the background
 */
@Composable
fun WeatherBackground(
    weatherCondition: WeatherCondition,
    isNight: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Get weather-specific colors
    val weatherColors = getWeatherColors(weatherCondition, isNight)

    // Use key with weather condition to trigger crossfade animation
    Crossfade(
        targetState = weatherColors,
        animationSpec = tween(durationMillis = 500),
        label = "weather_background"
    ) { colors ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.gradientStart,
                            colors.gradientMid,
                            colors.gradientEnd
                        )
                    )
                )
        ) {
            content()
        }
    }
}

/**
 * Static weather background without animation.
 * Use this when you don't need smooth transitions (e.g., initial load).
 */
@Composable
fun StaticWeatherBackground(
    weatherCondition: WeatherCondition,
    isNight: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = getWeatherColors(weatherCondition, isNight)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.gradientStart,
                        colors.gradientMid,
                        colors.gradientEnd
                    )
                )
            )
    ) {
        content()
    }
}
