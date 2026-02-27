package com.giruai.climatest.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Glassmorphism-style floating bottom navigation bar.
 *
 * Features:
 * - Floating design with 16dp horizontal margin and 12dp bottom margin
 * - Semi-transparent dark background with subtle border
 * - 24dp rounded corners
 * - Selected state: icon scales up (1.1x) with primary accent color
 * - Labels: 10sp, medium weight
 */
@Composable
fun GlassBottomNav(
    items: List<GlassNavItem>,
    selectedRoute: String?,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xD9142030)) // rgba(20,32,48,0.85)
                .border(
                    width = 1.dp,
                    color = Color(0x14FFFFFF), // rgba(255,255,255,0.08)
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedRoute?.startsWith(item.route) == true

                GlassNavItemView(
                    icon = item.icon,
                    label = item.label,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GlassNavItemView(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scale animation for selected state
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        label = "nav_scale"
    )

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.White.copy(alpha = 0.6f)
    }

    val labelColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.White.copy(alpha = 0.5f)
    }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Data class for glass navigation items.
 */
data class GlassNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
