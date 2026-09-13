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
    private val preferences by lazy { getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE) }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioPermission = isGranted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.DEBUG || !intent.getBooleanExtra(EXTRA_SKIP_AUDIO_PERMISSION_REQUEST, false)) {
            checkAudioPermission()
        }

        enableEdgeToEdge()
        setContent {
            BlazaresOrpheusTheme {
                val uiState by viewModel.uiState.collectAsState()
                val noteBuilderUiState by noteBuilderViewModel.uiState.collectAsState()
                var currentDestination by rememberSaveable { mutableStateOf(AppDestination.TUNER) }
                var appLanguageCode by rememberSaveable {
                    mutableStateOf(preferences.getString(APP_LANGUAGE_KEY, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code)
                }
                val appLanguage = AppLanguage.entries.firstOrNull { it.code == appLanguageCode } ?: AppLanguage.ENGLISH
                var noteLanguageCode by rememberSaveable {
                    mutableStateOf(preferences.getString(NOTE_LANGUAGE_KEY, NoteLanguage.ENGLISH.code) ?: NoteLanguage.ENGLISH.code)
                }
                val noteLanguage = NoteLanguage.entries.firstOrNull { it.code == noteLanguageCode } ?: NoteLanguage.ENGLISH

                fun setAppLanguage(language: AppLanguage) {
                    appLanguageCode = language.code
                    preferences.edit().putString(APP_LANGUAGE_KEY, language.code).apply()
                }

                fun setNoteLanguage(language: NoteLanguage) {
                    noteLanguageCode = language.code
                    preferences.edit().putString(NOTE_LANGUAGE_KEY, language.code).apply()
                    viewModel.updateNamingSystem(language.toNamingSystem())
                    noteBuilderViewModel.updateNoteLanguage(language)
                }

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
                    if (currentDestination == AppDestination.TUNER &&
                        destination != AppDestination.TUNER
                    ) {
                        viewModel.stopTuning()
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
                            onAppLanguageChange = ::setAppLanguage,
                            onNoteLanguageChange = ::setNoteLanguage,
                            onCalibrationChange = { viewModel.updateCalibration(it) },
                            onNamingSystemChange = { viewModel.updateNamingSystem(it) },
                            onPresetSelected = { viewModel.applyPreset(it) },
                            onStartTuning = viewModel::startTuning,
                            onStopTuning = viewModel::stopTuning,
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
                            onAppLanguageChange = ::setAppLanguage,
                            onNoteLanguageChange = ::setNoteLanguage,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Keeps instrumentation tests in the permission-gated UI without starting audio capture.
         * The normal launch path still requests RECORD_AUDIO as before.
         */
        const val EXTRA_SKIP_AUDIO_PERMISSION_REQUEST =
            "com.blazares.orpheus.extra.SKIP_AUDIO_PERMISSION_REQUEST"
        const val PREFERENCES_NAME = "orpheus_preferences"
        const val APP_LANGUAGE_KEY = "app_language"
        const val NOTE_LANGUAGE_KEY = "note_language"
    }

    private fun checkAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasAudioPermission = true
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}
