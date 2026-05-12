package com.edwardflores.magnetar.orpheus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.edwardflores.magnetar.orpheus.ui.TunerUiState
import com.edwardflores.magnetar.orpheus.ui.TunerViewModel
import com.edwardflores.magnetar.orpheus.ui.theme.MagnetarOrpheusTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TunerViewModel by viewModels()
    private var hasAudioPermission by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioPermission = isGranted
        if (isGranted) {
            viewModel.startTuning()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAudioPermission()
        
        enableEdgeToEdge()
        setContent {
            MagnetarOrpheusTheme {
                val uiState by viewModel.uiState.collectAsState()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TunerScreen(
                        uiState = uiState,
                        hasPermission = hasAudioPermission,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasAudioPermission = true
                viewModel.startTuning()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}

@Composable
fun TunerScreen(
    uiState: TunerUiState,
    hasPermission: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            if (hasPermission) {
                Text(
                    text = uiState.noteName,
                    style = MaterialTheme.typography.displayLarge,
                    color = if (uiState.isTuned) Color.Green else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = String.format("%.2f Hz", uiState.frequency),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TunerIndicator(cents = uiState.cents)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (uiState.cents > 0) "+${uiState.cents} cents" else "${uiState.cents} cents",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(text = "Microphone Access Denied", color = MaterialTheme.colorScheme.error)
                Text(text = "Please grant permission to use the tuner.")
            }
        }
    }
}

@Composable
fun TunerIndicator(cents: Int) {
    val animatedCents by animateFloatAsState(targetValue = cents.toFloat(), label = "cents")
    
    Canvas(modifier = Modifier.size(300.dp, 100.dp)) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height)
        
        // Draw scale
        drawLine(
            color = Color.Gray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )
        
        // Draw ticks
        for (i in -50..50 step 10) {
            val x = width / 2 + (i / 50f) * (width / 2)
            val tickHeight = if (i == 0) 30f else 15f
            drawLine(
                color = Color.Gray,
                start = Offset(x, height),
                end = Offset(x, height - tickHeight),
                strokeWidth = 2f
            )
        }
        
        // Draw needle
        val rotationAngle = (animatedCents / 50f) * 45f // Max 45 degrees
        rotate(degrees = rotationAngle, pivot = center) {
            drawLine(
                color = if (cents in -5..5) Color.Green else Color.Red,
                start = center,
                end = Offset(width / 2, 0f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}