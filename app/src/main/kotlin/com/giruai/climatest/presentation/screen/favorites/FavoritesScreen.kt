package com.giruai.climatest.presentation.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.giruai.climatest.domain.model.WeatherCondition
import com.giruai.climatest.presentation.components.LoadingIndicator
import com.giruai.climatest.presentation.components.RichFavoriteCard
import com.giruai.climatest.presentation.components.WeatherBackground

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onCitySelected: (Long, Double, Double) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    val isRefreshing = uiState is FavoritesUiState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    WeatherBackground(
        weatherCondition = WeatherCondition.PARTLY_CLOUDY // Neutral background for favorites
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when (val state = uiState) {
                is FavoritesUiState.Loading -> {
                    LoadingIndicator()
                }

                is FavoritesUiState.Success -> {
                    FavoritesList(
                        favorites = state.favorites,
                        tempUnit = userSettings.temperatureUnit,
                        onCityClick = { favorite ->
                            onCitySelected(
                                favorite.city.id,
                                favorite.city.latitude,
                                favorite.city.longitude
                            )
                        },
                        onDeleteClick = { favorite ->
                            viewModel.onDeleteClick(favorite.city.id, favorite.city.name)
                        }
                    )
                }

                is FavoritesUiState.Empty -> {
                    EmptyState()
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { (cityId, cityName) ->
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteCancel() },
            title = { Text("Remove Favorite?") },
            text = { Text("Are you sure you want to remove $cityName from favorites?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onDeleteConfirm() }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDeleteCancel() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FavoritesList(
    favorites: List<RichFavorite>,
    tempUnit: com.giruai.climatest.data.local.preferences.TemperatureUnit,
    onCityClick: (RichFavorite) -> Unit,
    onDeleteClick: (RichFavorite) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = favorites,
            key = { it.city.id }
        ) { favorite ->
            RichFavoriteCard(
                cityName = favorite.city.name,
                country = favorite.city.country,
                temperature = favorite.temperature,
                condition = favorite.condition,
                tempUnit = tempUnit,
                onClick = { onCityClick(favorite) },
                onDeleteClick = { onDeleteClick(favorite) }
            )
        }
    }
}

@Composable
private fun EmptyState() {
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
                text = "⭐",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No favorites yet",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Search for a city to add to your favorites",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
