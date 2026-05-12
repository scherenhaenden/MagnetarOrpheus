package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edwardflores.magnetar.orpheus.ui.AppLanguage
import com.edwardflores.magnetar.orpheus.ui.AppDestination
import com.edwardflores.magnetar.orpheus.ui.NoteLanguage
import com.edwardflores.magnetar.orpheus.ui.appStrings
import com.edwardflores.magnetar.orpheus.ui.localizedSubtitle
import com.edwardflores.magnetar.orpheus.ui.localizedTitle
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    showProfile: Boolean,
    appLanguage: AppLanguage,
    noteLanguage: NoteLanguage,
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onAppLanguageChange: (AppLanguage) -> Unit,
    onNoteLanguageChange: (NoteLanguage) -> Unit
) {
    val strings = appStrings(appLanguage)
    var menuExpanded by remember { mutableStateOf(false) }
    var settingsExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AppNavigationMenu(
                expanded = menuExpanded,
                appLanguage = appLanguage,
                currentDestination = currentDestination,
                onDismiss = { menuExpanded = false },
                onNavigate = { destination ->
                    menuExpanded = false
                    onNavigate(destination)
                }
            )
        }

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
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                    color = OrpheusColors.PrimaryGreen
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { settingsExpanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = strings.settings,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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

    if (settingsExpanded) {
        SettingsDialog(
            appLanguage = appLanguage,
            noteLanguage = noteLanguage,
            onDismiss = { settingsExpanded = false },
            onAppLanguageChange = onAppLanguageChange,
            onNoteLanguageChange = onNoteLanguageChange
        )
    }
}

@Composable
private fun AppNavigationMenu(
    expanded: Boolean,
    appLanguage: AppLanguage,
    currentDestination: AppDestination,
    onDismiss: () -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    val strings = appStrings(appLanguage)
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        AppDestination.entries.forEach { destination ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(
                            text = destination.localizedTitle(strings),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = destination.localizedSubtitle(strings),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = when (destination) {
                            AppDestination.TUNER -> Icons.Outlined.GraphicEq
                            AppDestination.NOTE_BUILDER -> Icons.Outlined.LibraryMusic
                        },
                        contentDescription = null,
                        tint = if (destination == currentDestination) {
                            OrpheusColors.PrimaryGreen
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                onClick = { onNavigate(destination) }
            )
        }
    }
}

@Composable
private fun SettingsDialog(
    appLanguage: AppLanguage,
    noteLanguage: NoteLanguage,
    onDismiss: () -> Unit,
    onAppLanguageChange: (AppLanguage) -> Unit,
    onNoteLanguageChange: (NoteLanguage) -> Unit
) {
    val strings = appStrings(appLanguage)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(strings.close)
            }
        },
        title = {
            Text(
                text = strings.settings,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                SettingsOptionGroup(
                    title = strings.appLanguage,
                    selectedValue = appLanguage,
                    values = AppLanguage.entries,
                    label = { it.nativeLabel },
                    onSelected = onAppLanguageChange
                )
                SettingsOptionGroup(
                    title = strings.noteLanguage,
                    selectedValue = noteLanguage,
                    values = NoteLanguage.entries,
                    label = { it.nativeLabel },
                    onSelected = onNoteLanguageChange
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    )
}

@Composable
private fun <T> SettingsOptionGroup(
    title: String,
    selectedValue: T,
    values: List<T>,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            values.forEach { value ->
                val isSelected = value == selectedValue
                Surface(
                    onClick = { onSelected(value) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) {
                        OrpheusColors.PrimaryGreen.copy(alpha = 0.14f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) {
                            OrpheusColors.PrimaryGreen.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        }
                    )
                ) {
                    Text(
                        text = label(value),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) {
                            OrpheusColors.PrimaryGreen
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
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
