package com.edwardflores.magnetar.orpheus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors
import kotlin.math.abs

@Composable
fun InputWaveform(
    waveformSamples: List<Float>,
    inputLevel: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
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
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = OrpheusColors.PrimaryGreen.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = OrpheusColors.PrimaryGreen.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = "Microphone active",
                    tint = if (isActive) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (isActive) "MIC ACTIVE" else "MIC IDLE",
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        ) {
            val centerY = size.height / 2f
            val spacing = size.width / (waveformSamples.size.coerceAtLeast(1))
            val amplitudeBoost = 0.45f + inputLevel * 1.4f
            waveformSamples.forEachIndexed { index, sample ->
                val x = index * spacing + spacing / 2f
                val lineHeight = abs(sample).coerceAtLeast(0.03f) * size.height * amplitudeBoost
                drawLine(
                    color = OrpheusColors.PrimaryGreen.copy(alpha = 0.55f + abs(sample).coerceIn(0f, 0.35f)),
                    start = Offset(x, centerY - lineHeight / 2f),
                    end = Offset(x, centerY + lineHeight / 2f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
