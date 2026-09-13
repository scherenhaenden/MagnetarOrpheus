package com.blazares.orpheus.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blazares.orpheus.R
import com.blazares.orpheus.ui.theme.OrpheusColors
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MILLIS = 1_250L

@Composable
fun OrpheusSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        onFinished()
    }

    val transition = rememberInfiniteTransition(label = "orpheus-splash")
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orpheus-splash-pulse"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.42f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orpheus-splash-glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OrpheusColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(width = 300.dp, height = 390.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(350.dp)
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        }
                        .alpha(glowAlpha)
                ) {
                    val center = this.center
                    val radius = size.minDimension * 0.39f
                    drawCircle(
                        color = OrpheusColors.SecondaryCyan.copy(alpha = 0.28f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawArc(
                        color = OrpheusColors.PrimaryGreen,
                        startAngle = -132f,
                        sweepAngle = 264f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            center.x - radius,
                            center.y - radius
                        ),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    val bars = listOf(0.22f, 0.38f, 0.58f, 0.82f, 0.58f, 0.38f, 0.22f)
                    val spacing = 16.dp.toPx()
                    bars.forEachIndexed { index, heightRatio ->
                        val x = center.x + (index - 3) * spacing
                        val halfHeight = size.minDimension * 0.16f * heightRatio
                        drawLine(
                            color = if (index == 3) OrpheusColors.PrimaryGreen else OrpheusColors.SecondaryCyan,
                            start = androidx.compose.ui.geometry.Offset(x, center.y - halfHeight),
                            end = androidx.compose.ui.geometry.Offset(x, center.y + halfHeight),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                Image(
                    painter = painterResource(R.drawable.orpheus_splash_figure),
                    contentDescription = stringResource(R.string.splash_figure_description),
                    modifier = Modifier
                        .size(width = 250.dp, height = 375.dp)
                        .alpha(0.96f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.brand_primary),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 6.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.brand_secondary),
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = OrpheusColors.PrimaryGreen
            )
            Spacer(modifier = Modifier.height(22.dp))
            LoadingPulse(alpha = glowAlpha)
        }
    }
}

@Composable
private fun LoadingPulse(alpha: Float) {
    val bars = remember { listOf(0.35f, 0.65f, 1f, 0.65f, 0.35f) }
    Canvas(modifier = Modifier.size(width = 54.dp, height = 18.dp).alpha(alpha)) {
        val spacing = size.width / (bars.size + 1)
        bars.forEachIndexed { index, heightRatio ->
            val x = spacing * (index + 1)
            val halfHeight = size.height * 0.5f * heightRatio
            drawLine(
                color = if (index == 2) OrpheusColors.PrimaryGreen else OrpheusColors.SecondaryCyan,
                start = androidx.compose.ui.geometry.Offset(x, center.y - halfHeight),
                end = androidx.compose.ui.geometry.Offset(x, center.y + halfHeight),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
