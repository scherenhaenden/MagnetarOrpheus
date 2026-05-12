package com.edwardflores.magnetar.orpheus.ui.screen

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.R
import com.edwardflores.magnetar.orpheus.ui.AppDestination
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
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
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
                    showProfile = isTabletLayout,
                    currentDestination = currentDestination,
                    onNavigate = onNavigate
                )

                if (isTabletLayout) {
                    TabletLayout(
                        uiState = uiState,
                        versionName = versionName,
                        onCalibrationChange = onCalibrationChange,
                        onNamingSystemChange = onNamingSystemChange,
                        onPresetSelected = onPresetSelected
                    )
                } else {
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
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ChromaticNoteRow(currentNote = uiState.chromaticNote)
        MainTunerPanel(uiState = uiState, gaugeHeight = 420.dp)
        InputWaveform(
            waveformSamples = uiState.waveformSamples,
            inputLevel = uiState.inputLevel,
            isActive = uiState.isActive
        )
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            TunerControlCard(
                title = "Tuner Mode",
                value = uiState.tunerMode,
                subtitle = uiState.selectedInstrument,
                leadingIcon = Icons.Outlined.GraphicEq,
                modifier = Modifier.weight(1f)
            )
            TunerControlCard(
                title = "Reference Pitch",
                value = "A4 = ${uiState.referenceA4.toInt()} Hz",
                subtitle = uiState.selectedTuning,
                leadingIcon = Icons.Outlined.Tune,
                modifier = Modifier.weight(1f),
                trailingMode = ControlCardTrailingMode.Stepper(
                    onIncrement = { onCalibrationChange(uiState.referenceA4 + 1) },
                    onDecrement = { onCalibrationChange(uiState.referenceA4 - 1) }
                )
            )
        }
        CalibrationError(uiState = uiState)
        NoteSystemSelector(
            currentSystem = uiState.namingSystem,
            onSystemSelected = onNamingSystemChange
        )
        VersionFooter(versionName = versionName)
    }
}

@Composable
private fun TabletLayout(
    uiState: TunerUiState,
    versionName: String,
    onCalibrationChange: (Double) -> Unit,
    onNamingSystemChange: (NoteNamingSystem) -> Unit,
    onPresetSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1.75f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ChromaticNoteRow(currentNote = uiState.chromaticNote)
            MainTunerPanel(uiState = uiState, gaugeHeight = 500.dp)
            InputWaveform(
                waveformSamples = uiState.waveformSamples,
                inputLevel = uiState.inputLevel,
                isActive = uiState.isActive
            )
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                TunerControlCard(
                    title = "Tuner Mode",
                    value = uiState.tunerMode,
                    subtitle = uiState.selectedInstrument,
                    leadingIcon = Icons.Outlined.GraphicEq,
                    modifier = Modifier.weight(1f)
                )
                TunerControlCard(
                    title = "Reference Pitch",
                    value = "A4 = ${uiState.referenceA4.toInt()} Hz",
                    subtitle = uiState.selectedTuning,
                    leadingIcon = Icons.Outlined.Tune,
                    modifier = Modifier.weight(1f),
                    trailingMode = ControlCardTrailingMode.Stepper(
                        onIncrement = { onCalibrationChange(uiState.referenceA4 + 1) },
                        onDecrement = { onCalibrationChange(uiState.referenceA4 - 1) }
                    )
                )
                TunerControlCard(
                    title = "Note System",
                    value = uiState.namingSystem.displayName,
                    subtitle = "Display format",
                    leadingIcon = Icons.Outlined.MusicNote,
                    modifier = Modifier.weight(1f)
                )
            }
            CalibrationError(uiState = uiState)
            NoteSystemSelector(
                currentSystem = uiState.namingSystem,
                onSystemSelected = onNamingSystemChange
            )
            VersionFooter(versionName = versionName)
        }

        TabletSidePanel(
            modifier = Modifier.weight(1f),
            noteHistory = uiState.noteHistory,
            pitchStabilityPoints = uiState.pitchStabilityPoints,
            selectedInstrument = uiState.selectedInstrument,
            selectedTuning = uiState.selectedTuning,
            quickPresets = uiState.quickPresets,
            referenceA4 = uiState.referenceA4,
            onPresetSelected = onPresetSelected
        )
    }
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
