package com.edwardflores.magnetar.orpheus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.edwardflores.magnetar.orpheus.ui.TunerViewModel
import com.edwardflores.magnetar.orpheus.ui.screen.TunerScreen
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
                        onCalibrationChange = { viewModel.updateCalibration(it) },
                        onNamingSystemChange = { viewModel.updateNamingSystem(it) },
                        onPresetSelected = { viewModel.applyPreset(it) },
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
