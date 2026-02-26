package com.giruai.climatest.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.giruai.climatest.domain.model.WeatherCondition

/**
 * Premium Color Schemes for ClimaApp
 * Custom schemes that replace stock Material 3 colors
 */

private val ClimaDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    onError = DarkOnError,
    outline = GlassBorder,
    outlineVariant = GlassBackground,
    scrim = Color(0x80000000),
    inverseSurface = LightSurface,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = LightPrimary,
)

private val ClimaLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = LightError,
    onError = LightOnError,
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB),
    scrim = Color(0x80000000),
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkTextPrimary,
    inversePrimary = DarkPrimary,
)

/**
 * Data class holding weather-specific colors for dynamic theming.
 * Provides colors based on current weather condition.
 */
data class WeatherColors(
    val condition: WeatherCondition,
    val primary: Color,
    val secondary: Color,
    val gradientStart: Color,
    val gradientMid: Color,
    val gradientEnd: Color,
    val iconGlow: Color,
    val textTint: Color
)

/**
 * Get weather-specific colors for a given condition
 */
fun getWeatherColors(condition: WeatherCondition, isNight: Boolean = false): WeatherColors {
    val scheme = when (condition) {
        WeatherCondition.CLEAR_SKY -> if (isNight) ClearNightColors else SunnyColors
        WeatherCondition.PARTLY_CLOUDY -> PartlyCloudyColors
        WeatherCondition.OVERCAST -> CloudyColors
        WeatherCondition.FOG -> FoggyColors
        WeatherCondition.DRIZZLE,
        WeatherCondition.RAIN,
        WeatherCondition.SHOWERS -> RainyColors
        WeatherCondition.THUNDERSTORM -> StormyColors
        WeatherCondition.SNOW -> SnowyColors
        WeatherCondition.UNKNOWN -> DefaultWeatherColors
    }

    return WeatherColors(
        condition = condition,
        primary = scheme.primary,
        secondary = scheme.secondary,
        gradientStart = scheme.gradientStart,
        gradientMid = scheme.gradientMid,
        gradientEnd = scheme.gradientEnd,
        iconGlow = scheme.iconGlow,
        textTint = scheme.textTint
    )
}

/**
 * CompositionLocal to provide weather-specific colors throughout the UI
 */
val LocalWeatherColors = staticCompositionLocalOf {
    getWeatherColors(WeatherCondition.UNKNOWN)
}

/**
 * Main theme for ClimaApp
 *
 * @param darkTheme Whether to use dark theme
 * @param dynamicColor Whether to use Material You dynamic colors (Android 12+)
 * @param content Content to theme
 */
@Composable
fun ClimaTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default - we want our premium palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // Even with dynamic color, we use our base as fallback
            if (darkTheme) ClimaDarkColorScheme else ClimaLightColorScheme
        }
        darkTheme -> ClimaDarkColorScheme
        else -> ClimaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Theme wrapper that provides weather-specific colors
 *
 * @param weatherCondition Current weather condition for dynamic coloring
 * @param isNight Whether it's nighttime (affects clear sky colors)
 * @param content Content to theme
 */
@Composable
fun WeatherTheme(
    weatherCondition: WeatherCondition,
    isNight: Boolean = false,
    content: @Composable () -> Unit
) {
    val weatherColors = getWeatherColors(weatherCondition, isNight)

    CompositionLocalProvider(LocalWeatherColors provides weatherColors) {
        content()
    }
}

/**
 * Extension to get temperature color based on value
 */
fun getTemperatureColor(celsius: Double, darkTheme: Boolean = true): Color {
    return when {
        celsius >= 30 -> if (darkTheme) TempHotHigh else Color(0xFFDC2626)
        celsius >= 20 -> if (darkTheme) TempHotLow else Color(0xFFEA580C)
        celsius >= 10 -> if (darkTheme) TempMild else Color(0xFF16A34A)
        celsius >= 0 -> if (darkTheme) TempCool else Color(0xFF0284C7)
        else -> if (darkTheme) TempCold else Color(0xFF0369A1)
    }
}

/**
 * Extension to get gradient brush for weather backgrounds
 */
@Composable
fun getWeatherGradientBrush(colors: WeatherColors): androidx.compose.ui.graphics.Brush {
    return androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            colors.gradientStart,
            colors.gradientMid,
            colors.gradientEnd
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )
}
