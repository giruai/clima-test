package com.giruai.climatest.presentation.screen.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.giruai.climatest.domain.model.CurrentWeather
import com.giruai.climatest.domain.model.DailyForecast
import com.giruai.climatest.presentation.components.ErrorMessage
import com.giruai.climatest.presentation.components.LoadingIndicator
import com.giruai.climatest.presentation.components.WeatherIcon
import com.giruai.climatest.presentation.util.rememberLocationPermissionHandler
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherScreen(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = false,
                    onClick = onNavigateToSearch
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = onNavigateToFavorites
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = onNavigateToSettings
                )
            }
        },
        floatingActionButton = {
            if (uiState is WeatherUiState.Success) {
                FloatingActionButton(
                    onClick = { viewModel.addToFavorites() },
                    containerColor = if (isFavorite)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) "Already in favorites" else "Add to favorites"
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    LoadingIndicator()
                }

                is WeatherUiState.Success -> {
                    WeatherContent(
                        currentWeather = state.currentWeather,
                        forecast = state.forecast,
                        cityName = state.cityName,
                        lastUpdated = state.lastUpdated
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
                        onRequestPermission = { permissionHandler.requestPermission() },
                        onNavigateToSearch = onNavigateToSearch
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

@Composable
private fun WeatherContent(
    currentWeather: CurrentWeather,
    forecast: List<DailyForecast>,
    cityName: String,
    lastUpdated: Long
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
                lastUpdated = lastUpdated
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
            ForecastItem(day)
        }
    }
}

@Composable
private fun CurrentWeatherSection(
    currentWeather: CurrentWeather,
    cityName: String,
    lastUpdated: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // City Name
            if (cityName.isNotEmpty()) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Weather Icon
            WeatherIcon(
                condition = currentWeather.weatherCondition,
                large = true
            )

            // Temperature (large)
            Text(
                text = "${currentWeather.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Weather Description
            Text(
                text = currentWeather.weatherCondition.description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider()

            // Weather Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(
                    label = "Feels Like",
                    value = "${currentWeather.apparentTemperature.toInt()}°C"
                )
                WeatherDetail(
                    label = "Wind",
                    value = "${currentWeather.windSpeed.toInt()} km/h"
                )
                WeatherDetail(
                    label = "Humidity",
                    value = "${currentWeather.humidity}%"
                )
            }

            // Last Updated
            Text(
                text = "Updated ${formatLastUpdated(lastUpdated)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeatherDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ForecastItem(day: DailyForecast) {
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
                    text = "${day.temperatureMax.toInt()}°C",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${day.temperatureMin.toInt()}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PermissionRequiredContent(
    onRequestPermission: () -> Unit,
    onNavigateToSearch: () -> Unit
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
                text = "ClimaApp needs your location to show weather for your area.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRequestPermission) {
                Text("Enable Location")
            }
            TextButton(onClick = onNavigateToSearch) {
                Text("Search for a city instead")
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
