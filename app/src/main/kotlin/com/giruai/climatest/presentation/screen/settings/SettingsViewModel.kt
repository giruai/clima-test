package com.giruai.climatest.presentation.screen.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.data.local.preferences.SettingsManager
import com.giruai.climatest.data.local.preferences.TemperatureUnit
import com.giruai.climatest.data.local.preferences.ThemeMode
import com.giruai.climatest.data.local.preferences.UserSettings
import com.giruai.climatest.data.local.preferences.WindUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: UserSettings,
    val hasLocationPermission: Boolean
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsManager.settings
        .map { settings ->
            SettingsUiState(
                settings = settings,
                hasLocationPermission = checkLocationPermission()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState(
                settings = UserSettings(),
                hasLocationPermission = checkLocationPermission()
            )
        )

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            settingsManager.setTemperatureUnit(unit)
        }
    }

    fun setWindUnit(unit: WindUnit) {
        viewModelScope.launch {
            settingsManager.setWindUnit(unit)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
