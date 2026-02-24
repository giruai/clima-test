package com.giruai.climatest.presentation.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SearchScreen(
    onCitySelected: (Long, Double, Double) -> Unit,
    onBack: () -> Unit
) {
    // Placeholder - will be implemented in S3.2
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Search Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
