package com.giruai.climatest.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.giruai.climatest.domain.model.WeatherCondition

@Composable
fun WeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    Text(
        text = condition.icon,
        style = if (large) {
            MaterialTheme.typography.displayLarge
        } else {
            MaterialTheme.typography.headlineLarge
        },
        modifier = modifier
            .size(if (large) 120.dp else 60.dp)
            .semantics {
                contentDescription = "Weather: ${condition.description}"
            }
    )
}
