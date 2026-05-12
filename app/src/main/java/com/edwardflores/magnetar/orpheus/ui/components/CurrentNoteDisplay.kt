package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.ui.TunerUiState
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

@Composable
fun CurrentNoteDisplay(
    uiState: TunerUiState,
    modifier: Modifier = Modifier
) {
    val showDetectedNote = uiState.noteLabel != "-" && uiState.frequency > 0
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (showDetectedNote) uiState.noteLabel else "—",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Light),
                color = if (uiState.isActive) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurface
            )
            if (showDetectedNote) {
                Text(
                    text = uiState.octave.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = if (uiState.isActive) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
        Text(
            text = uiState.frequencyText,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = uiState.centsText,
            style = MaterialTheme.typography.headlineSmall,
            color = when {
                uiState.cents in -5..5 -> OrpheusColors.PrimaryGreen
                uiState.cents in -20..20 -> OrpheusColors.WarningAmber
                else -> OrpheusColors.DangerRed
            }
        )
    }
}
