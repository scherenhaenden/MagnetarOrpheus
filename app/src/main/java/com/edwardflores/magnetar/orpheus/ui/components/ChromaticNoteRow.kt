package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

private val chromaticNotes = listOf("E", "F", "F#", "G", "G#", "A", "A#", "B", "C")

@Composable
fun ChromaticNoteRow(
    currentNote: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        chromaticNotes.forEach { note ->
            val isSelected = note == currentNote
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = if (isSelected) OrpheusColors.PrimaryGreen.copy(alpha = 0.16f) else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) OrpheusColors.PrimaryGreen.copy(alpha = 0.5f) else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = note,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    ),
                    color = if (isSelected) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
