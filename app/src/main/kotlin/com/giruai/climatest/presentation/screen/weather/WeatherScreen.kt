package com.giruai.climatest.presentation.screen.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.giruai.climatest.data.local.preferences.UserSettings
import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.model.DailyForecast
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.components.ErrorMessage
import com.giruai.climatest.presentation.components.LoadingIndicator
import com.giruai.climatest.presentation.components.WeatherBackground
import com.giruai.climatest.presentation.components.WeatherIcon
import com.giruai.climatest.presentation.util.UnitConverter
import com.giruai.climatest.presentation.util.rememberLocationPermissionHandler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    // Pass initial coords to ViewModel if provided
    LaunchedEffect(initialLatitude, initialLongitude) {
        if (initialLatitude != null && initialLongitude != null) {
            viewModel.loadWeatherForCoordinates(initialLatitude, initialLongitude)
        }
    }
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar when message changes
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.snackbarShown()
        }
    }

    val permissionHandler = rememberLocationPermissionHandler { granted ->
        if (granted) {
            viewModel.loadWeather()
        }
    }

    // Handle permission required state
    LaunchedEffect(uiState) {
        if (uiState is WeatherUiState.PermissionRequired) {
            if (!permissionHandler.hasPermission()) {
                permissionHandler.requestPermission()
            }
        }
    }

    val isRefreshing = uiState is WeatherUiState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    // Get city name and weather condition for TopAppBar and Background
    val topBarTitle = when (val state = uiState) {
        is WeatherUiState.Success -> state.cityName
        else -> "Weather"
    }

    // Get current weather condition for dynamic background
    val currentWeatherCondition = when (val state = uiState) {
        is WeatherUiState.Success -> state.currentWeather.weatherCondition
        else -> WeatherCondition.UNKNOWN
    }

    // Determine if it's night (simplified - between 20:00 and 06:00)
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val isNight = hourOfDay >= 20 || hourOfDay < 6

    WeatherBackground(
        weatherCondition = currentWeatherCondition,
        isNight = isNight
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(topBarTitle) },
                    actions = {
                        if (uiState is WeatherUiState.Success) {
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                    tint = if (isFavorite)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pullRefresh(pullRefreshState)
            ) {
                when (val state = uiState) {
                    is WeatherUiState.Loading -> {
                        // LoadingIndicator removed - PullRefreshIndicator handles loading state
                    }

                    is WeatherUiState.Success -> {
                        WeatherContent(
                            currentWeather = state.currentWeather,
                            forecast = state.forecast,
                            cityName = state.cityName,
                            lastUpdated = state.lastUpdated,
                            userSettings = userSettings
                        )
                    }

                    is WeatherUiState.Error -> {
                        ErrorMessage(
                            message = state.message,
                            onRetry = { viewModel.refresh() }
                        )
                    }

                    is WeatherUiState.PermissionRequired -> {
                        PermissionRequiredContent(
                            onRequestPermission = { permissionHandler.requestPermission() }
                        )
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun WeatherContent(
    currentWeather: CurrentWeather,
    forecast: List<DailyForecast>,
    cityName: String,
    lastUpdated: Long,
    userSettings: UserSettings
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Current Weather Section
        item {
            CurrentWeatherSection(
                currentWeather = currentWeather,
                cityName = cityName,
                lastUpdated = lastUpdated,
                userSettings = userSettings
            )
        }

        // 5-Day Forecast Header
        item {
            Text(
                text = "5-Day Forecast",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Forecast Items
        items(forecast) { day ->
            ForecastItem(day, userSettings)
        }
    }
}

@Composable
private fun CurrentWeatherSection(
    currentWeather: CurrentWeather,
    cityName: String,
    lastUpdated: Long,
    userSettings: UserSettings
) {
    // S7.3: Premium Weather Hero Card (replaces old card-based design)
    WeatherHero(
        currentWeather = currentWeather,
        userSettings = userSettings
    )
}

@Composable

private fun ForecastItem(day: DailyForecast, userSettings: UserSettings) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day Name
            Text(
                text = formatDayName(day.date),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Weather Icon
            WeatherIcon(
                condition = day.weatherCondition,
                large = false
            )

            // Max/Min Temps
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = UnitConverter.formatTemperature(day.temperatureMax, userSettings.temperatureUnit),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = UnitConverter.formatTemperature(day.temperatureMin, userSettings.temperatureUnit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PermissionRequiredContent(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📍",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "ClimaApp needs your location to show weather for your area. Or use the Search tab to find a city.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRequestPermission) {
                Text("Enable Location")
            }
        }
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
            else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(parsedDate)
        }
    } catch (e: Exception) {
        date
    }
}

private fun formatLastUpdated(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "just now"
        diff < 3600_000 -> "${diff / 60_000} min ago"
        else -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
