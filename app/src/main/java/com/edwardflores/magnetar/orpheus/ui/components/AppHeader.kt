package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    showProfile: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Outlined.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BrandMark()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MAGNETAR",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 6.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ORPHEUS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 4.sp
                    ),
                    color = OrpheusColors.PrimaryGreen
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showProfile) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.width(112.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(OrpheusColors.PrimaryGreen, CircleShape)
                            )
                        }
                        Text(
                            text = "Live",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandMark() {
    Canvas(modifier = Modifier.size(width = 26.dp, height = 32.dp)) {
        val barXs = listOf(3f, 9f, 15f, 21f, 27f)
        val heights = listOf(8f, 20f, 30f, 20f, 8f)
        val stroke = size.width / 8f
        val brush = Brush.verticalGradient(
            colors = listOf(OrpheusColors.SecondaryCyan, OrpheusColors.PrimaryGreen)
        )
        barXs.zip(heights).forEach { (x, height) ->
            drawLine(
                brush = brush,
                start = Offset(x / 30f * size.width, size.height / 2f - height / 32f * size.height / 2f),
                end = Offset(x / 30f * size.width, size.height / 2f + height / 32f * size.height / 2f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
