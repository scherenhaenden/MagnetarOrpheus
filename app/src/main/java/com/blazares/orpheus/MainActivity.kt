package com.blazares.orpheus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.blazares.orpheus.permissions.MicrophonePermissionAction
import com.blazares.orpheus.permissions.MicrophonePermissionPolicy
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
    private var shouldOpenAudioSettings by mutableStateOf(false)
    private val preferences by lazy { getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE) }

    private val skipAudioPermissionHandling: Boolean
        get() = BuildConfig.DEBUG && intent.getBooleanExtra(EXTRA_SKIP_AUDIO_PERMISSION_REQUEST, false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioPermission = isGranted
        shouldOpenAudioSettings = resolveAudioPermissionAction(isGranted) ==
            MicrophonePermissionAction.OPEN_APP_SETTINGS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!skipAudioPermissionHandling) {
            refreshAudioPermissionState()
        }
        restoreTunerPreferences()

        enableEdgeToEdge()
        setContent {
            BlazaresOrpheusTheme {
                // Android 12+ owns the first-frame splash. Keep the richer Compose
                // animation only on older releases so users never see two splashes.
                var showSplash by rememberSaveable {
                    mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                }

                if (showSplash) {
                    OrpheusSplashScreen(onFinished = { showSplash = false })
                } else {
                    val uiState by viewModel.uiState.collectAsState()
                    val noteBuilderUiState by noteBuilderViewModel.uiState.collectAsState()
                    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.TUNER) }
                    var appLanguageCode by rememberSaveable {
                        mutableStateOf(
                            preferences.getString(APP_LANGUAGE_KEY, AppLanguage.ENGLISH.code)
                                ?: AppLanguage.ENGLISH.code
                        )
                    }
                    val appLanguage = AppLanguage.entries.firstOrNull { it.code == appLanguageCode }
                        ?: AppLanguage.ENGLISH
                    var noteLanguageCode by rememberSaveable {
                        mutableStateOf(
                            preferences.getString(NOTE_LANGUAGE_KEY, NoteLanguage.ENGLISH.code)
                                ?: NoteLanguage.ENGLISH.code
                        )
                    }
                    val noteLanguage = NoteLanguage.entries.firstOrNull { it.code == noteLanguageCode }
                        ?: NoteLanguage.ENGLISH

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

                    fun setReferencePitch(referenceHz: Double) {
                        viewModel.updateCalibration(referenceHz)
                        if (referenceHz > 0.0 && referenceHz.isFinite()) {
                            preferences.edit().putFloat(REFERENCE_A4_KEY, referenceHz.toFloat()).apply()
                        }
                    }

                    fun setInstrumentProfile(profileId: String) {
                        if (viewModel.selectInstrumentProfile(profileId)) {
                            preferences.edit().putString(INSTRUMENT_PROFILE_KEY, profileId).apply()
                        }
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
                            AppDestination.TUNER -> Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                TunerScreen(
                                    uiState = uiState,
                                    hasPermission = hasAudioPermission,
                                    versionName = BuildConfig.VERSION_NAME,
                                    appLanguage = appLanguage,
                                    noteLanguage = noteLanguage,
                                    currentDestination = currentDestination,
                                    onNavigate = ::navigateTo,
                                    onAppLanguageChange = ::setAppLanguage,
                                    onNoteLanguageChange = ::setNoteLanguage,
                                    onCalibrationChange = ::setReferencePitch,
                                    onNamingSystemChange = { viewModel.updateNamingSystem(it) },
                                    onPresetSelected = { setReferencePitch(it.toDouble()) },
                                    onInstrumentProfileSelected = ::setInstrumentProfile,
                                    onStartTuning = viewModel::startTuning,
                                    onStopTuning = viewModel::stopTuning,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (!hasAudioPermission) {
                                    Button(
                                        onClick = ::requestAudioPermission,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 48.dp)
                                    ) {
                                        Text(
                                            stringResource(
                                                if (shouldOpenAudioSettings) {
                                                    R.string.microphone_permission_open_settings
                                                } else {
                                                    R.string.microphone_permission_action
                                                }
                                            )
                                        )
                                    }
                                }
                            }

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
    }

    override fun onResume() {
        super.onResume()
        if (!skipAudioPermissionHandling) {
            refreshAudioPermissionState()
        }
    }

    companion object {
        /** Keeps instrumentation tests on the deterministic permission-gated UI. */
        const val EXTRA_SKIP_AUDIO_PERMISSION_REQUEST =
            "com.blazares.orpheus.extra.SKIP_AUDIO_PERMISSION_REQUEST"
        const val PREFERENCES_NAME = "orpheus_preferences"
        const val APP_LANGUAGE_KEY = "app_language"
        const val NOTE_LANGUAGE_KEY = "note_language"
        const val INSTRUMENT_PROFILE_KEY = "instrument_profile"
        const val REFERENCE_A4_KEY = "reference_a4"
        const val AUDIO_PERMISSION_REQUESTED_KEY = "audio_permission_requested"
    }

    private fun restoreTunerPreferences() {
        preferences.getString(INSTRUMENT_PROFILE_KEY, null)?.let(viewModel::selectInstrumentProfile)
        if (preferences.contains(REFERENCE_A4_KEY)) {
            viewModel.updateCalibration(
                preferences.getFloat(REFERENCE_A4_KEY, 440f).toDouble()
            )
        }
    }

    private fun refreshAudioPermissionState() {
        val action = resolveAudioPermissionAction()
        hasAudioPermission = action == MicrophonePermissionAction.AVAILABLE
        shouldOpenAudioSettings = action == MicrophonePermissionAction.OPEN_APP_SETTINGS
    }

    private fun resolveAudioPermissionAction(
        isGranted: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    ): MicrophonePermissionAction = MicrophonePermissionPolicy.resolve(
        isGranted = isGranted,
        requestedBefore = hasRequestedAudioPermissionBefore(),
        shouldShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
    )

    private fun hasRequestedAudioPermissionBefore(): Boolean =
        preferences.getBoolean(AUDIO_PERMISSION_REQUESTED_KEY, false)

    private fun requestAudioPermission() {
        when (resolveAudioPermissionAction()) {
            MicrophonePermissionAction.AVAILABLE -> {
                hasAudioPermission = true
                shouldOpenAudioSettings = false
            }

            MicrophonePermissionAction.OPEN_APP_SETTINGS -> {
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                )
            }

            MicrophonePermissionAction.REQUEST_PERMISSION -> {
                preferences.edit().putBoolean(AUDIO_PERMISSION_REQUESTED_KEY, true).apply()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}
