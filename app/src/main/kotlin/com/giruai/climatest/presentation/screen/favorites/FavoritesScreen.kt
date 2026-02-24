package com.giruai.climatest.presentation.screen.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FavoritesScreen(
    onCitySelected: (Long, Double, Double) -> Unit,
    onBack: () -> Unit
) {
    // Placeholder - will be implemented in S4.3
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Favorites Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
