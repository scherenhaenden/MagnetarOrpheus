package com.edwardflores.magnetar.orpheus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OrpheusDarkColorScheme = darkColorScheme(
    primary = OrpheusColors.PrimaryGreen,
    onPrimary = OrpheusColors.Background,
    secondary = OrpheusColors.SecondaryCyan,
    onSecondary = OrpheusColors.Background,
    tertiary = OrpheusColors.WarningAmber,
    background = OrpheusColors.Background,
    onBackground = OrpheusColors.TextPrimary,
    surface = OrpheusColors.Surface,
    onSurface = OrpheusColors.TextPrimary,
    surfaceVariant = OrpheusColors.SurfaceElevated,
    onSurfaceVariant = OrpheusColors.TextSecondary,
    outline = OrpheusColors.Border,
    error = OrpheusColors.DangerRed
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun MagnetarOrpheusTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OrpheusDarkColorScheme,
        typography = Typography,
        content = content
    )
}
