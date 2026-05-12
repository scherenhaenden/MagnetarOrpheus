package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.ui.NoteHistoryItem
import com.edwardflores.magnetar.orpheus.ui.QuickPreset
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

@Composable
fun TabletSidePanel(
    noteHistory: List<NoteHistoryItem>,
    pitchStabilityPoints: List<Float>,
    selectedInstrument: String,
    selectedTuning: String,
    quickPresets: List<QuickPreset>,
    referenceA4: Double,
    modifier: Modifier = Modifier,
    onPresetSelected: (Int) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PanelCard(
            title = "Note History",
            leadingIcon = Icons.Outlined.History,
            actionText = "View Full History"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                noteHistory.take(5).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.badgeLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = item.note,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.7f)
                        )
                        Text(
                            text = String.format("%.1f Hz", item.frequencyHz),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (item.cents > 0) "+${item.cents}c" else "${item.cents}c",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = if (item.cents >= 0) OrpheusColors.PrimaryGreen else OrpheusColors.DangerRed,
                            modifier = Modifier.weight(0.6f)
                        )
                        Text(
                            text = item.timeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        PanelCard(
            title = "Pitch Stability",
            leadingIcon = Icons.Outlined.GraphicEq,
            trailingText = "LIVE"
        ) {
            PitchStabilityChart(points = pitchStabilityPoints)
        }

        PanelCard(
            title = "Instrument / Tuning",
            leadingIcon = Icons.Outlined.LibraryMusic
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                MetadataRow(label = "Instrument", value = selectedInstrument)
                MetadataRow(label = "Tuning", value = selectedTuning)
            }
        }

        PanelCard(
            title = "Quick Presets",
            leadingIcon = Icons.Outlined.Tune,
            actionText = "Manage Presets"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quickPresets.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPresetSelected(preset.referenceHz) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "A4 = ${preset.referenceHz} Hz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (preset.referenceHz == referenceA4.toInt()) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = OrpheusColors.PrimaryGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelCard(
    title: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    actionText: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = OrpheusColors.SecondaryCyan
                )
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.labelLarge,
                    color = OrpheusColors.PrimaryGreen
                )
            }
        }

        content()

        if (actionText != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = actionText.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PitchStabilityChart(points: List<Float>) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val baselineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val horizontalCenter = size.height / 2f
        val verticalPadding = size.height * 0.18f
        val chartHeight = size.height - verticalPadding * 2f
        val spacing = size.width / (points.size.coerceAtLeast(2) - 1)

        listOf(-20f, 0f, 20f).forEach { grid ->
            val normalized = (grid + 20f) / 40f
            val y = size.height - verticalPadding - normalized * chartHeight
            drawLine(
                color = outlineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val path = Path()
        points.forEachIndexed { index, point ->
            val normalized = (point.coerceIn(-20f, 20f) + 20f) / 40f
            val x = index * spacing
            val y = size.height - verticalPadding - normalized * chartHeight
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = OrpheusColors.PrimaryGreen,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        drawLine(
            color = baselineColor,
            start = Offset(0f, horizontalCenter),
            end = Offset(size.width, horizontalCenter),
            strokeWidth = 1.dp.toPx()
        )
    }
}
