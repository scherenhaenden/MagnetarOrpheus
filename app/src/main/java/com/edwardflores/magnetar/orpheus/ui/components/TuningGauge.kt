package com.edwardflores.magnetar.orpheus.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors
import kotlin.math.cos
import kotlin.math.sin

private const val gaugeStartAngle = 160f
private const val gaugeSweepAngle = 220f

@Composable
fun TuningGauge(
    cents: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val labelPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT
        }
    }
    val labelTextSize = with(density) { 16.sp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val strokeWidth = width * 0.015f
        val tickLengthMinor = width * 0.025f
        val tickLengthMajor = width * 0.065f
        val radius = width * 0.42f
        val center = Offset(width / 2f, height * 0.88f)
        val arcRect = Rect(
            offset = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f)
        )

        drawArc(
            color = OrpheusColors.DangerRed.copy(alpha = 0.9f),
            startAngle = gaugeStartAngle,
            sweepAngle = 66f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = OrpheusColors.WarningAmber.copy(alpha = 0.95f),
            startAngle = gaugeStartAngle + 66f,
            sweepAngle = 22f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = OrpheusColors.PrimaryGreen.copy(alpha = 0.95f),
            startAngle = gaugeStartAngle + 88f,
            sweepAngle = 44f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = OrpheusColors.WarningAmber.copy(alpha = 0.95f),
            startAngle = gaugeStartAngle + 132f,
            sweepAngle = 22f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = OrpheusColors.DangerRed.copy(alpha = 0.9f),
            startAngle = gaugeStartAngle + 154f,
            sweepAngle = 66f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        for (mark in -100..100 step 5) {
            val angle = centsToAngle(mark.toFloat())
            val radians = Math.toRadians(angle.toDouble())
            val outerRadius = radius + width * 0.01f
            val isMajor = mark % 20 == 0 || mark % 50 == 0 || mark == 0
            val innerRadius = outerRadius - if (isMajor) tickLengthMajor else tickLengthMinor
            val start = Offset(
                x = center.x + cos(radians).toFloat() * innerRadius,
                y = center.y + sin(radians).toFloat() * innerRadius
            )
            val end = Offset(
                x = center.x + cos(radians).toFloat() * outerRadius,
                y = center.y + sin(radians).toFloat() * outerRadius
            )
            drawLine(
                color = if (isMajor) Color.White.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.15f),
                start = start,
                end = end,
                strokeWidth = if (isMajor) width * 0.0038f else width * 0.002f,
                cap = StrokeCap.Round
            )
        }

        val needleAngle = centsToAngle(cents.coerceIn(-100, 100).toFloat())
        val needleRadians = Math.toRadians(needleAngle.toDouble())
        val needleEnd = Offset(
            x = center.x + cos(needleRadians).toFloat() * (radius - width * 0.08f),
            y = center.y + sin(needleRadians).toFloat() * (radius - width * 0.08f)
        )
        val needleStart = Offset(center.x, center.y - height * 0.34f)

        drawLine(
            color = if (cents in -5..5) OrpheusColors.PrimaryGreen else Color.White.copy(alpha = 0.75f),
            start = needleStart,
            end = needleEnd,
            strokeWidth = width * 0.0048f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color.White,
            radius = width * 0.012f,
            center = needleEnd
        )

        val labels = listOf(-100, -20, -50, 0, 50, 20, 100)
        labels.forEach { label ->
            val angle = centsToAngle(label.toFloat())
            val radians = Math.toRadians(angle.toDouble())
            val labelRadius = radius + width * 0.07f
            val position = Offset(
                x = center.x + cos(radians).toFloat() * labelRadius,
                y = center.y + sin(radians).toFloat() * labelRadius
            )
            labelPaint.color = when {
                label == 0 -> OrpheusColors.TextPrimary.toArgb()
                kotlin.math.abs(label) == 20 -> OrpheusColors.WarningAmber.toArgb()
                kotlin.math.abs(label) == 100 -> OrpheusColors.DangerRed.toArgb()
                else -> OrpheusColors.TextSecondary.toArgb()
            }
            labelPaint.textSize = labelTextSize
            drawContext.canvas.nativeCanvas.drawText(
                if (label > 0) "+$label" else label.toString(),
                position.x,
                position.y,
                labelPaint
            )
        }
    }
}

private fun centsToAngle(cents: Float): Float {
    val normalized = (cents.coerceIn(-100f, 100f) + 100f) / 200f
    return gaugeStartAngle + normalized * gaugeSweepAngle
}
