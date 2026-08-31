package com.edwardflores.magnetar.orpheus.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.R
import com.edwardflores.magnetar.orpheus.ui.AppLanguage
import com.edwardflores.magnetar.orpheus.ui.AppDestination
import com.edwardflores.magnetar.orpheus.ui.NoteLanguage
import com.edwardflores.magnetar.orpheus.ui.NoteNamingSystem
import com.edwardflores.magnetar.orpheus.ui.TunerUiState
import com.edwardflores.magnetar.orpheus.ui.components.AppHeader
import com.edwardflores.magnetar.orpheus.ui.components.ChromaticNoteRow
import com.edwardflores.magnetar.orpheus.ui.components.ControlCardTrailingMode
import com.edwardflores.magnetar.orpheus.ui.components.CurrentNoteDisplay
import com.edwardflores.magnetar.orpheus.ui.components.InputWaveform
import com.edwardflores.magnetar.orpheus.ui.components.TabletSidePanel
import com.edwardflores.magnetar.orpheus.ui.components.TunerControlCard
import com.edwardflores.magnetar.orpheus.ui.components.TuningGauge
import com.edwardflores.magnetar.orpheus.ui.theme.OrpheusColors

@Composable
fun TunerScreen(
    uiState: TunerUiState,
    hasPermission: Boolean,
    versionName: String,
    appLanguage: AppLanguage,
    noteLanguage: NoteLanguage,
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onAppLanguageChange: (AppLanguage) -> Unit,
    onNoteLanguageChange: (NoteLanguage) -> Unit,
    onCalibrationChange: (Double) -> Unit,
    onNamingSystemChange: (NoteNamingSystem) -> Unit,
    onPresetSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!hasPermission) {
            PermissionState(modifier = Modifier.fillMaxSize())
            return@Surface
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            val isTabletLayout = maxWidth >= 920.dp
            if (isTabletLayout) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    AppHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 4.dp),
                        showProfile = true,
                        appLanguage = appLanguage,
                        noteLanguage = noteLanguage,
                        currentDestination = currentDestination,
                        onNavigate = onNavigate,
                        onAppLanguageChange = onAppLanguageChange,
                        onNoteLanguageChange = onNoteLanguageChange
                    )
                    TabletLayout(
                        uiState = uiState,
                        versionName = versionName,
                        onCalibrationChange = onCalibrationChange,
                        onNamingSystemChange = onNamingSystemChange,
                        onPresetSelected = onPresetSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            } else {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    AppHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 4.dp),
                        showProfile = false,
                        appLanguage = appLanguage,
                        noteLanguage = noteLanguage,
                        currentDestination = currentDestination,
                        onNavigate = onNavigate,
                        onAppLanguageChange = onAppLanguageChange,
                        onNoteLanguageChange = onNoteLanguageChange
                    )
                    PhoneLayout(
                        uiState = uiState,
                        versionName = versionName,
                        onCalibrationChange = onCalibrationChange,
                        onNamingSystemChange = onNamingSystemChange
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneLayout(
    uiState: TunerUiState,
    versionName: String,
    onCalibrationChange: (Double) -> Unit,
    onNamingSystemChange: (NoteNamingSystem) -> Unit
) {
    var showPitchDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TunerControlCard(
                title = "Tuner Mode",
                value = uiState.tunerMode,
                subtitle = uiState.selectedInstrument,
                leadingIcon = Icons.Outlined.GraphicEq,
                modifier = Modifier.weight(1f),
                onClick = { showModeDialog = true }
            )
            TunerControlCard(
                title = "Reference Pitch",
                value = "A4 = ${uiState.referenceA4.toInt()} Hz",
                subtitle = uiState.selectedTuning,
                leadingIcon = Icons.Outlined.Tune,
                modifier = Modifier.weight(1f),
                onClick = { showPitchDialog = true }
            )
        }
        ChromaticNoteRow(currentNote = uiState.chromaticNote)
        MainTunerPanel(uiState = uiState, gaugeHeight = 380.dp)
        InputWaveform(
            waveformSamples = uiState.waveformSamples,
            inputLevel = uiState.inputLevel,
            isActive = uiState.isActive
        )
        CalibrationError(uiState = uiState)
        NoteSystemSelector(
            currentSystem = uiState.namingSystem,
            onSystemSelected = onNamingSystemChange
        )
        VersionFooter(versionName = versionName)
    }

    if (showPitchDialog) {
        ReferencePitchDialog(
            currentPitch = uiState.referenceA4,
            calibrationErrorResId = uiState.calibrationErrorResId,
            onDismiss = { showPitchDialog = false },
            onSave = { newPitch ->
                onCalibrationChange(newPitch)
            }
        )
    }

    if (showModeDialog) {
        TunerModeDialog(
            currentMode = uiState.tunerMode,
            currentInstrument = uiState.selectedInstrument,
            onDismiss = { showModeDialog = false }
        )
    }
}

@Composable
private fun TabletLayout(
    uiState: TunerUiState,
    versionName: String,
    onCalibrationChange: (Double) -> Unit,
    onNamingSystemChange: (NoteNamingSystem) -> Unit,
    onPresetSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPitchDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        val leftScrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1.75f)
                .fillMaxHeight()
                .verticalScroll(leftScrollState),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                TunerControlCard(
                    title = "Tuner Mode",
                    value = uiState.tunerMode,
                    subtitle = uiState.selectedInstrument,
                    leadingIcon = Icons.Outlined.GraphicEq,
                    modifier = Modifier.weight(1f),
                    onClick = { showModeDialog = true }
                )
                TunerControlCard(
                    title = "Reference Pitch",
                    value = "A4 = ${uiState.referenceA4.toInt()} Hz",
                    subtitle = uiState.selectedTuning,
                    leadingIcon = Icons.Outlined.Tune,
                    modifier = Modifier.weight(1f),
                    onClick = { showPitchDialog = true }
                )
                TunerControlCard(
                    title = "Note System",
                    value = uiState.namingSystem.displayName,
                    subtitle = "Display format",
                    leadingIcon = Icons.Outlined.MusicNote,
                    modifier = Modifier.weight(1f)
                )
            }
            ChromaticNoteRow(currentNote = uiState.chromaticNote)
            MainTunerPanel(uiState = uiState, gaugeHeight = 460.dp)
            InputWaveform(
                waveformSamples = uiState.waveformSamples,
                inputLevel = uiState.inputLevel,
                isActive = uiState.isActive
            )
            CalibrationError(uiState = uiState)
            NoteSystemSelector(
                currentSystem = uiState.namingSystem,
                onSystemSelected = onNamingSystemChange
            )
            VersionFooter(versionName = versionName)
        }

        TabletSidePanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            noteHistory = uiState.noteHistory,
            pitchStabilityPoints = uiState.pitchStabilityPoints,
            selectedInstrument = uiState.selectedInstrument,
            selectedTuning = uiState.selectedTuning,
            quickPresets = uiState.quickPresets,
            referenceA4 = uiState.referenceA4,
            onPresetSelected = onPresetSelected
        )
    }

    if (showPitchDialog) {
        ReferencePitchDialog(
            currentPitch = uiState.referenceA4,
            calibrationErrorResId = uiState.calibrationErrorResId,
            onDismiss = { showPitchDialog = false },
            onSave = { newPitch ->
                onCalibrationChange(newPitch)
            }
        )
    }

    if (showModeDialog) {
        TunerModeDialog(
            currentMode = uiState.tunerMode,
            currentInstrument = uiState.selectedInstrument,
            onDismiss = { showModeDialog = false }
        )
    }
}

@Composable
private fun TunerModeDialog(
    currentMode: String,
    currentInstrument: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tuner Mode",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Chromatic", "Guitar", "Bass", "Violin", "Ukulele").forEach { mode ->
                    val isSelected = mode == currentMode || mode == currentInstrument
                    Surface(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) OrpheusColors.PrimaryGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mode,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = if (isSelected) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OrpheusColors.PrimaryGreen)
            ) {
                Text("Close", color = MaterialTheme.colorScheme.surface)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    )
}

@Composable
private fun MainTunerPanel(
    uiState: TunerUiState,
    gaugeHeight: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(gaugeHeight)
    ) {
        TuningGauge(
            cents = uiState.cents,
            modifier = Modifier.fillMaxSize()
        )
        CurrentNoteDisplay(
            uiState = uiState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun CalibrationError(uiState: TunerUiState) {
    uiState.calibrationErrorResId?.let { errorResId ->
        Text(
            text = stringResource(errorResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun VersionFooter(versionName: String) {
    Text(
        text = stringResource(R.string.version_label, versionName),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NoteSystemSelector(
    currentSystem: NoteNamingSystem,
    onSystemSelected: (NoteNamingSystem) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        NoteNamingSystem.entries.forEach { system ->
            val isSelected = system == currentSystem
            Text(
                text = system.displayName,
                modifier = Modifier
                    .background(
                        color = if (isSelected) OrpheusColors.PrimaryGreen.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) OrpheusColors.PrimaryGreen.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onSystemSelected(system) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = if (isSelected) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReferencePitchDialog(
    currentPitch: Double,
    calibrationErrorResId: Int?,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var tempPitch by remember(currentPitch) { mutableStateOf(currentPitch) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reference Pitch",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "A4 = ${tempPitch.toInt()} Hz",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OrpheusColors.PrimaryGreen
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { tempPitch = (tempPitch - 1).coerceAtLeast(410.0) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = "Decrease",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "${tempPitch.toInt()} Hz",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { tempPitch = (tempPitch + 1).coerceAtMost(470.0) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Increase",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    listOf(415, 432, 440, 442, 444).forEach { hz ->
                        val isSelected = tempPitch.toInt() == hz
                        Surface(
                            onClick = { tempPitch = hz.toDouble() },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) OrpheusColors.PrimaryGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = "$hz Hz",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) OrpheusColors.PrimaryGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (calibrationErrorResId != null) {
                    Text(
                        text = stringResource(calibrationErrorResId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(tempPitch)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrpheusColors.PrimaryGreen)
            ) {
                Text("Save", color = MaterialTheme.colorScheme.surface)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    )
}

@Composable
private fun PermissionState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Microphone access is required",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Grant RECORD_AUDIO permission to activate the tuner and waveform panels.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
