package com.giruai.climatest.presentation.screen.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.data.local.preferences.ThemeMode
import com.giruai.climatest.data.local.preferences.WindUnit
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.components.WeatherBackground
import com.giruai.climatest.presentation.theme.GlassBackground
import com.giruai.climatest.presentation.theme.GlassBorder

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    WeatherBackground(
        weatherCondition = WeatherCondition.PARTLY_CLOUDY
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            // Units Section
            item {
                GlassSection(
                    icon = Icons.Default.Settings,
                    title = "Units",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Temperature
                            SettingOption(
                                label = "Temperature",
                                value = uiState.settings.temperatureUnit.symbol
                            ) {
                                SegmentedGlassButtons(
                                    selectedValue = uiState.settings.temperatureUnit,
                                    options = TemperatureUnit.entries,
                                    onValueChange = viewModel::setTemperatureUnit,
                                    getLabel = { it.symbol }
                                )
                            }

                            // Wind Speed
                            SettingOption(
                                label = "Wind Speed",
                                value = uiState.settings.windUnit.symbol
                            ) {
                                SegmentedGlassButtons(
                                    selectedValue = uiState.settings.windUnit,
                                    options = WindUnit.entries,
                                    onValueChange = viewModel::setWindUnit,
                                    getLabel = { it.symbol }
                                )
                            }
                        }
                    }
                )
            }

            // Appearance Section
            item {
                GlassSection(
                    icon = Icons.Default.Settings,
                    title = "Appearance",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            SettingOption(
                                label = "Theme",
                                value = uiState.settings.themeMode.name.lowercase().replaceFirstChar { it.uppercase() }
                            ) {
                                SegmentedGlassButtons(
                                    selectedValue = uiState.settings.themeMode,
                                    options = ThemeMode.entries,
                                    onValueChange = viewModel::setThemeMode,
                                    getLabel = { mode ->
                                        when (mode) {
                                            ThemeMode.SYSTEM -> "System"
                                            ThemeMode.LIGHT -> "Light"
                                            ThemeMode.DARK -> "Dark"
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
            }

            // Permissions Section
            item {
                GlassSection(
                    icon = Icons.Default.LocationOn,
                    title = "Permissions",
                    content = {
                        PermissionCard(
                            title = "Location Access",
                            subtitle = if (uiState.hasLocationPermission) "Enabled" else "Disabled",
                            isEnabled = uiState.hasLocationPermission,
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                )
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun GlassSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp
            )
        }

        // Glass card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(GlassBackground)
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingOption(
    label: String,
    value: String,
    control: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        control()
    }
}

@Composable
private fun <T> SegmentedGlassButtons(
    selectedValue: T,
    options: List<T>,
    onValueChange: (T) -> Unit,
    getLabel: (T) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedValue == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else GlassBackground
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) Color.Transparent else GlassBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onValueChange(option) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getLabel(option),
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isEnabled) Color(0x1A4ADE80) else GlassBackground)
            .border(
                width = 1.dp,
                color = if (isEnabled) Color(0x334ADE80) else GlassBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isEnabled) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.6f)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isEnabled) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.1f)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isEnabled) "ON" else "MANAGE",
                color = if (isEnabled) Color.Black else Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}
