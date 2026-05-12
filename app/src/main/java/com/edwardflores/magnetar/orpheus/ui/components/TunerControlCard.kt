package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

@Composable
fun TunerControlCard(
    title: String,
    value: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailingMode: ControlCardTrailingMode = ControlCardTrailingMode.Chevron
) {
    val containerModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    Row(
        modifier = containerModifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = OrpheusColors.SecondaryCyan
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        when (trailingMode) {
            ControlCardTrailingMode.Chevron -> {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            is ControlCardTrailingMode.Stepper -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StepperButton(icon = Icons.Outlined.Remove, onClick = trailingMode.onDecrement)
                    StepperButton(icon = Icons.Outlined.Add, onClick = trailingMode.onIncrement)
                }
            }
        }
    }
}

sealed interface ControlCardTrailingMode {
    data object Chevron : ControlCardTrailingMode
    data class Stepper(
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit
    ) : ControlCardTrailingMode
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
