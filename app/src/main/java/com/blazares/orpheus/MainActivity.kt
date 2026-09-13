package com.blazares.orpheus

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.blazares.orpheus.ui.AppLanguage
import com.blazares.orpheus.ui.AppDestination
import com.blazares.orpheus.ui.NoteLanguage
import com.blazares.orpheus.ui.OrpheusSplashScreen
import com.blazares.orpheus.ui.TunerViewModel
import com.blazares.orpheus.ui.toNamingSystem
import com.blazares.orpheus.ui.notebuilder.NoteBuilderScreen
import com.blazares.orpheus.ui.notebuilder.NoteBuilderViewModel
import com.blazares.orpheus.ui.screen.TunerScreen
import com.blazares.orpheus.ui.theme.BlazaresOrpheusTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TunerViewModel by viewModels()
    private val noteBuilderViewModel: NoteBuilderViewModel by viewModels()
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
            BlazaresOrpheusTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }

                if (showSplash) {
                    OrpheusSplashScreen(onFinished = { showSplash = false })
                } else {
                    val uiState by viewModel.uiState.collectAsState()
                    val noteBuilderUiState by noteBuilderViewModel.uiState.collectAsState()
                    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.TUNER) }
                    var appLanguageCode by rememberSaveable { mutableStateOf(AppLanguage.ENGLISH.code) }
                    val appLanguage = AppLanguage.entries.firstOrNull { it.code == appLanguageCode } ?: AppLanguage.ENGLISH
                    var noteLanguageCode by rememberSaveable { mutableStateOf(NoteLanguage.ENGLISH.code) }
                    val noteLanguage = NoteLanguage.entries.firstOrNull { it.code == noteLanguageCode } ?: NoteLanguage.ENGLISH

                    LaunchedEffect(noteLanguage) {
                        viewModel.updateNamingSystem(noteLanguage.toNamingSystem())
                        noteBuilderViewModel.updateNoteLanguage(noteLanguage)
                    }

                    fun navigateTo(destination: AppDestination) {
                        if (currentDestination == AppDestination.NOTE_BUILDER &&
                            destination != AppDestination.NOTE_BUILDER
                        ) {
                            noteBuilderViewModel.stopPlayback()
                        }
                        currentDestination = destination
                    }

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        when (currentDestination) {
                        AppDestination.TUNER -> TunerScreen(
                            uiState = uiState,
                            hasPermission = hasAudioPermission,
                            versionName = BuildConfig.VERSION_NAME,
                            appLanguage = appLanguage,
                            noteLanguage = noteLanguage,
                            currentDestination = currentDestination,
                            onNavigate = ::navigateTo,
                            onAppLanguageChange = { appLanguageCode = it.code },
                            onNoteLanguageChange = {
                                noteLanguageCode = it.code
                                viewModel.updateNamingSystem(it.toNamingSystem())
                                noteBuilderViewModel.updateNoteLanguage(it)
                            },
                            onCalibrationChange = { viewModel.updateCalibration(it) },
                            onNamingSystemChange = { viewModel.updateNamingSystem(it) },
                            onPresetSelected = { viewModel.applyPreset(it) },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppDestination.NOTE_BUILDER -> NoteBuilderScreen(
                            state = noteBuilderUiState,
                            onInputModeChange = noteBuilderViewModel::updateInputMode,
                            onToggleHold = noteBuilderViewModel::toggleHold,
                            onToggleNote = noteBuilderViewModel::toggleNote,
                            onPlaySelection = noteBuilderViewModel::playSelection,
                            onStopPlayback = noteBuilderViewModel::stopPlayback,
                            onClearSelection = noteBuilderViewModel::clearSelection,
                            appLanguage = appLanguage,
                            noteLanguage = noteLanguage,
                            currentDestination = currentDestination,
                            onNavigate = ::navigateTo,
                            onAppLanguageChange = { appLanguageCode = it.code },
                            onNoteLanguageChange = {
                                noteLanguageCode = it.code
                                viewModel.updateNamingSystem(it.toNamingSystem())
                                noteBuilderViewModel.updateNoteLanguage(it)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                        }
                    }
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
