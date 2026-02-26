package com.giruai.climatest.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Premium Color Palette for ClimaApp
 * Replaces stock Material 3 with weather-appropriate, modern colors
 */

// ============================================================================
// DARK MODE PALETTE
// ============================================================================

// Backgrounds
val DarkBackground = Color(0xFF0F1523)          // Deep navy with warmth
val DarkSurface = Color(0xFF1A2332)             // Elevated surfaces
val DarkSurfaceVariant = Color(0xFF252F3E)      // Cards, panels

// Primary Accent - Electric Cyan-Blue (main brand color)
val DarkPrimary = Color(0xFF00D4FF)
val DarkOnPrimary = Color(0xFF00151A)
val DarkPrimaryContainer = Color(0xFF004959)
val DarkOnPrimaryContainer = Color(0xFFB3F0FF)

// Secondary Accent - Warm Amber/Gold (for temperatures, sun)
val DarkSecondary = Color(0xFFFFB946)
val DarkOnSecondary = Color(0xFF1A0F00)
val DarkSecondaryContainer = Color(0xFF593D00)
val DarkOnSecondaryContainer = Color(0xFFFFE4B8)

// Tertiary - Soft Lavender (humidity, moonlight, subtle accents)
val DarkTertiary = Color(0xFFB48EFF)
val DarkOnTertiary = Color(0xFF1A0F33)
val DarkTertiaryContainer = Color(0xFF4A3366)
val DarkOnTertiaryContainer = Color(0xFFE8D9FF)

// Text
val DarkTextPrimary = Color(0xFFF0F4F8)         // Soft white
val DarkTextSecondary = Color(0xFF8899AA)       // Muted blue-gray
val DarkTextTertiary = Color(0xFF5A6A7A)        // Subtle text

// Semantic Colors
val DarkError = Color(0xFFFF6B6B)
val DarkOnError = Color(0xFF1A0505)
val DarkSuccess = Color(0xFF4ADE80)
val DarkWarning = Color(0xFFFBBF24)

// Temperature Colors (for dynamic UI)
val TempHotHigh = Color(0xFFFF7A5C)             // Coral for high temps
val TempHotLow = Color(0xFFFFB946)              // Amber for warm
val TempMild = Color(0xFF4ADE80)                // Green for mild
val TempCool = Color(0xFF5CB8FF)                // Blue for cool
val TempCold = Color(0xFF7DD3FC)                // Ice blue for cold

// Glassmorphism
val GlassBackground = Color(0x14FFFFFF)         // 8% white
val GlassBorder = Color(0x1AFFFFFF)             // 10% white
val GlassBackgroundStrong = Color(0x29FFFFFF)   // 16% white

// ============================================================================
// LIGHT MODE PALETTE
// ============================================================================

// Backgrounds
val LightBackground = Color(0xFFF4F7FB)         // Warm off-white with blue tint
val LightSurface = Color(0xFFFFFFFF)            // Pure white
val LightSurfaceVariant = Color(0xFFE8EEF4)     // Slightly darker for cards

// Primary Accent - Deep Ocean Blue
val LightPrimary = Color(0xFF0077CC)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFB3D9FF)
val LightOnPrimaryContainer = Color(0xFF001A33)

// Secondary Accent - Rich Amber
val LightSecondary = Color(0xFFE6960A)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFFFE4B8)
val LightOnSecondaryContainer = Color(0xFF331E00)

// Tertiary - Soft Purple
val LightTertiary = Color(0xFF8B5CF6)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFE8D9FF)
val LightOnTertiaryContainer = Color(0xFF1A0F33)

// Text
val LightTextPrimary = Color(0xFF1A2332)        // Near-black blue
val LightTextSecondary = Color(0xFF5A6A7A)      // Medium slate
val LightTextTertiary = Color(0xFF8A9AAA)       // Light slate

// Semantic Colors
val LightError = Color(0xFFDC2626)
val LightOnError = Color(0xFFFFFFFF)
val LightSuccess = Color(0xFF16A34A)
val LightWarning = Color(0xFFD97706)

// ============================================================================
// WEATHER CONDITION COLORS (for dynamic backgrounds)
// ============================================================================

/**
 * Weather-specific color schemes for dynamic theming.
 * Each condition has a gradient palette that evokes the weather mood.
 */
data class WeatherColorScheme(
    val primary: Color,           // Main accent
    val secondary: Color,         // Secondary accent
    val gradientStart: Color,     // Background gradient start
    val gradientMid: Color,       // Background gradient middle
    val gradientEnd: Color,       // Background gradient end
    val iconGlow: Color,          // Radial glow behind weather icon
    val textTint: Color           // Temperature text gradient end
)

// Clear/Sunny - Warm golden tones
val SunnyColors = WeatherColorScheme(
    primary = Color(0xFFFFB946),
    secondary = Color(0xFFFF7A5C),
    gradientStart = Color(0xFF1E3A5F),
    gradientMid = Color(0xFF2D5F8A),
    gradientEnd = Color(0xFF1A4670),
    iconGlow = Color(0x40FFB946),      // 25% amber
    textTint = Color(0xFFFFD4A8)       // Warm white
)

// Clear Night - Deep midnight blues
val ClearNightColors = WeatherColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFF8B5CF6),
    gradientStart = Color(0xFF0A1628),
    gradientMid = Color(0xFF162040),
    gradientEnd = Color(0xFF1A1040),
    iconGlow = Color(0x30C0D0E0),      // 19% silver
    textTint = Color(0xFFA8D4FF)       // Cool white
)

// Partly Cloudy - Steel-blue/silver gradient, brighter than default
val PartlyCloudyColors = WeatherColorScheme(
    primary = Color(0xFF7BA4C0),         // Soft steel blue
    secondary = Color(0xFFFFB74D),       // Soft amber (sun hint)
    gradientStart = Color(0xFF4A6A80),   // Steel blue (visibly lighter)
    gradientMid = Color(0xFF7A9AB0),     // Silver-blue (bright mid)
    gradientEnd = Color(0xFF354D60),     // Blue-gray base
    iconGlow = Color(0x40FFB74D),        // 25% amber
    textTint = Color(0xFFE3F2FD)         // Very light blue
)

// Cloudy - Muted silver-blues with lighter mid-tones
val CloudyColors = WeatherColorScheme(
    primary = Color(0xFF90A4AE),
    secondary = Color(0xFFB0BEC5),
    gradientStart = Color(0xFF253545),     // Lighter dark blue-gray
    gradientMid = Color(0xFF4A6070),       // Silver-blue mid tone
    gradientEnd = Color(0xFF2C3E50),       // Dark blue-gray
    iconGlow = Color(0x3090A4AE),          // 19% silver
    textTint = Color(0xFFECEFF1)           // Very light gray/silver
)

// Rainy - Deep cool blues
val RainyColors = WeatherColorScheme(
    primary = Color(0xFF4A90D9),
    secondary = Color(0xFF5CB8FF),
    gradientStart = Color(0xFF1A2530),
    gradientMid = Color(0xFF253545),
    gradientEnd = Color(0xFF1D2D3D),
    iconGlow = Color(0x204A90D9),      // 13% blue
    textTint = Color(0xFF90CAF9)       // Light blue
)

// Stormy - Dramatic purples
val StormyColors = WeatherColorScheme(
    primary = Color(0xFF9C6ADE),
    secondary = Color(0xFFB794F6),
    gradientStart = Color(0xFF1A1030),
    gradientMid = Color(0xFF2D1B4E),
    gradientEnd = Color(0xFF1A1535),
    iconGlow = Color(0x309C6ADE),      // 19% purple
    textTint = Color(0xFFD1B4F6)       // Light purple
)

// Snowy - Ice crystal tones
val SnowyColors = WeatherColorScheme(
    primary = Color(0xFF7DD3FC),
    secondary = Color(0xFFB8E6FF),
    gradientStart = Color(0xFF1A2A3A),
    gradientMid = Color(0xFF253A50),
    gradientEnd = Color(0xFF1E3040),
    iconGlow = Color(0x307DD3FC),      // 19% ice blue
    textTint = Color(0xFFE0F4FF)       // Ice white
)

// Foggy/Misty - Soft grays
val FoggyColors = WeatherColorScheme(
    primary = Color(0xFFB0BEC5),
    secondary = Color(0xFFCFD8DC),
    gradientStart = Color(0xFF252F3E),
    gradientMid = Color(0xFF303A4A),
    gradientEnd = Color(0xFF2A3444),
    iconGlow = Color(0x18B0BEC5),      // 9% gray
    textTint = Color(0xFFECEFF1)       // Very light gray
)

// Default/Fallback - Neutral dark blues
val DefaultWeatherColors = WeatherColorScheme(
    primary = Color(0xFF00D4FF),
    secondary = Color(0xFF5CB8FF),
    gradientStart = Color(0xFF0F1523),
    gradientMid = Color(0xFF1A2535),
    gradientEnd = Color(0xFF0F1523),
    iconGlow = Color(0x2000D4FF),      // 13% cyan
    textTint = Color(0xFFB3F0FF)       // Light cyan
)
