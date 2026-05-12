package com.edwardflores.magnetar.orpheus.ui.notebuilder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edwardflores.magnetar.orpheus.ui.AppDestination
import com.edwardflores.magnetar.orpheus.ui.components.AppHeader
import com.edwardflores.magnetar.orpheus.notebuilder.NoteBuilderMusicTheory
import com.edwardflores.magnetar.orpheus.ui.theme.MagnetarOrpheusTheme

enum class NoteBuilderInputMode {
    KEYBOARD,
    GRID
}

data class NoteSelection(
    val pitchClass: String,
    val octave: Int
) {
    val displayName: String = "$pitchClass$octave"
}

data class NoteBuilderUiState(
    val inputMode: NoteBuilderInputMode = NoteBuilderInputMode.KEYBOARD,
    val selectedNotes: List<NoteSelection> = emptyList(),
    val detectedPrimaryName: String = "No note set selected",
    val detectedSecondaryName: String = "Select notes from the keyboard or grid",
    val quality: String = "Unknown",
    val holdEnabled: Boolean = false,
    val sequence: List<String> = listOf("Cmaj7", "Am7", "Dm7", "G7", "Cmaj7"),
    val isPlaying: Boolean = false,
    val playbackError: String? = null
)

private object NoteBuilderPalette {
    val Background = Color(0xFF05080A)
    val Surface = Color(0xFF0B1115)
    val Elevated = Color(0xFF10171D)
    val Border = Color(0x661B2A31)
    val Primary = Color(0xFF35F58A)
    val Secondary = Color(0xFF20D6C7)
    val Warning = Color(0xFFFFD84A)
    val Danger = Color(0xFFFF4B4B)
    val TextPrimary = Color(0xFFF2F5F4)
    val TextSecondary = Color(0xFF8D989E)
    val TextMuted = Color(0xFF5E686E)
    val WhiteKey = Color(0xFFEAEDED)
    val BlackKey = Color(0xFF131A1F)
}

private val ChromaticNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
private val KeyboardRange = listOf(
    NoteSelection("C", 4), NoteSelection("C#", 4), NoteSelection("D", 4), NoteSelection("D#", 4),
    NoteSelection("E", 4), NoteSelection("F", 4), NoteSelection("F#", 4), NoteSelection("G", 4),
    NoteSelection("G#", 4), NoteSelection("A", 4), NoteSelection("A#", 4), NoteSelection("B", 4),
    NoteSelection("C", 5), NoteSelection("C#", 5), NoteSelection("D", 5), NoteSelection("D#", 5),
    NoteSelection("E", 5), NoteSelection("F", 5), NoteSelection("F#", 5), NoteSelection("G", 5),
    NoteSelection("G#", 5), NoteSelection("A", 5), NoteSelection("A#", 5), NoteSelection("B", 5),
    NoteSelection("C", 6)
)

@Composable
fun NoteBuilderDemoScreen(
    modifier: Modifier = Modifier,
    currentDestination: AppDestination = AppDestination.NOTE_BUILDER,
    onNavigate: (AppDestination) -> Unit = {}
) {
    var state by remember {
        mutableStateOf(
            NoteBuilderUiState(
                inputMode = NoteBuilderInputMode.KEYBOARD,
                selectedNotes = listOf(
                    NoteSelection("C", 4),
                    NoteSelection("E", 4),
                    NoteSelection("G", 4),
                    NoteSelection("B", 4)
                ),
                detectedPrimaryName = "Cmaj7",
                detectedSecondaryName = "C Major Seventh",
                quality = "Major"
            )
        )
    }

    NoteBuilderScreen(
        state = state,
        onInputModeChange = { state = state.copy(inputMode = it) },
        onToggleHold = { state = state.copy(holdEnabled = !state.holdEnabled) },
        onToggleNote = { note ->
            val updated = if (state.selectedNotes.contains(note)) {
                state.selectedNotes - note
            } else {
                (state.selectedNotes + note).sortedWith(compareBy<NoteSelection> { it.octave }.thenBy { ChromaticNotes.indexOf(it.pitchClass) })
            }
            state = state.copy(
                selectedNotes = updated,
                detectedPrimaryName = when (updated.map { it.displayName }) {
                    listOf("C4", "E4", "G4") -> "C Major Triad"
                    listOf("C4", "E4", "G4", "B4") -> "Cmaj7"
                    emptyList<String>() -> "No note set selected"
                    else -> "Unknown Note Set"
                },
                detectedSecondaryName = when (updated.map { it.displayName }) {
                    listOf("C4", "E4", "G4") -> "Notes: C4, E4, G4  •  Intervals: 1, 3, 5"
                    listOf("C4", "E4", "G4", "B4") -> "Notes: C4, E4, G4, B4  •  Intervals: 1, 3, 5, 7"
                    emptyList<String>() -> "Select notes from the keyboard or grid"
                    else -> "Theory naming placeholder for future analysis"
                },
                quality = when (updated.map { it.displayName }) {
                    listOf("C4", "E4", "G4"), listOf("C4", "E4", "G4", "B4") -> "Major"
                    emptyList<String>() -> "None"
                    else -> "Unknown"
                }
            )
        },
        onPlaySelection = {},
        onStopPlayback = {},
        onClearSelection = {
            val analysis = NoteBuilderMusicTheory.analyzeSelection(emptyList())
            state = state.copy(
                selectedNotes = emptyList(),
                detectedPrimaryName = analysis.title,
                detectedSecondaryName = analysis.subtitle,
                quality = analysis.quality,
                isPlaying = false,
                playbackError = null
            )
        },
        currentDestination = currentDestination,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun NoteBuilderScreen(
    state: NoteBuilderUiState,
    onInputModeChange: (NoteBuilderInputMode) -> Unit,
    onToggleHold: () -> Unit,
    onToggleNote: (NoteSelection) -> Unit,
    onPlaySelection: () -> Unit,
    onStopPlayback: () -> Unit,
    onClearSelection: () -> Unit,
    currentDestination: AppDestination = AppDestination.NOTE_BUILDER,
    onNavigate: (AppDestination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = NoteBuilderPalette.Background
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            if (maxWidth < 900.dp) {
                NoteBuilderPhoneScreen(
                    state = state,
                    onInputModeChange = onInputModeChange,
                    onToggleHold = onToggleHold,
                    onToggleNote = onToggleNote,
                    onPlaySelection = onPlaySelection,
                    onStopPlayback = onStopPlayback,
                    onClearSelection = onClearSelection,
                    currentDestination = currentDestination,
                    onNavigate = onNavigate
                )
            } else {
                NoteBuilderTabletScreen(
                    state = state,
                    onInputModeChange = onInputModeChange,
                    onToggleHold = onToggleHold,
                    onToggleNote = onToggleNote,
                    onPlaySelection = onPlaySelection,
                    onStopPlayback = onStopPlayback,
                    onClearSelection = onClearSelection,
                    currentDestination = currentDestination,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
private fun NoteBuilderPhoneScreen(
    state: NoteBuilderUiState,
    onInputModeChange: (NoteBuilderInputMode) -> Unit,
    onToggleHold: () -> Unit,
    onToggleNote: (NoteSelection) -> Unit,
    onPlaySelection: () -> Unit,
    onStopPlayback: () -> Unit,
    onClearSelection: () -> Unit,
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            AppHeader(
                showProfile = false,
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
        item { FeatureLabel(text = "NOTE BUILDER") }
        item {
            InputModeSelector(
                selectedMode = state.inputMode,
                onInputModeChange = onInputModeChange
            )
        }
        item {
            when (state.inputMode) {
                NoteBuilderInputMode.KEYBOARD -> PhoneKeyboardCard(
                    selectedNotes = state.selectedNotes,
                    onToggleNote = onToggleNote
                )
                NoteBuilderInputMode.GRID -> PhoneGridCard(
                    selectedNotes = state.selectedNotes,
                    onToggleNote = onToggleNote
                )
            }
        }
        item {
            SelectedNotesAndControlsCard(
                state = state,
                onToggleHold = onToggleHold,
                onPlaySelection = onPlaySelection,
                onStopPlayback = onStopPlayback,
                onClearSelection = onClearSelection
            )
        }
        item {
            AboutSelectionCard(
                title = state.detectedPrimaryName,
                subtitle = state.detectedSecondaryName,
                quality = state.quality
            )
        }
    }
}

@Composable
private fun NoteBuilderTabletScreen(
    state: NoteBuilderUiState,
    onInputModeChange: (NoteBuilderInputMode) -> Unit,
    onToggleHold: () -> Unit,
    onToggleNote: (NoteSelection) -> Unit,
    onPlaySelection: () -> Unit,
    onStopPlayback: () -> Unit,
    onClearSelection: () -> Unit,
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppHeader(
            showProfile = true,
            currentDestination = currentDestination,
            onNavigate = onNavigate
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val leftScrollState = rememberScrollState()
            val rightScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .verticalScroll(leftScrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PianoKeyboardWorkspaceCard(
                    selectedNotes = state.selectedNotes,
                    onToggleNote = onToggleNote
                )
                SelectedNotesAndControlsCard(
                    state = state,
                    onToggleHold = onToggleHold,
                    onPlaySelection = onPlaySelection,
                    onStopPlayback = onStopPlayback,
                    onClearSelection = onClearSelection
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SelectedChordSummaryCard(state = state)
                WorkspaceGridCard(
                    modifier = Modifier.weight(1f),
                    selectedNotes = state.selectedNotes,
                    onToggleNote = onToggleNote
                )
            }
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
                    .verticalScroll(rightScrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NoteSequenceCard(sequence = state.sequence)
                SavedPatternsCard()
                HarmonizationPlaceholderCard()
            }
        }
        PlaybackSettingsBar(selectedMode = state.inputMode, onInputModeChange = onInputModeChange)
    }
}

@Composable
private fun FeatureLabel(text: String) {
    Text(
        text = text,
        color = NoteBuilderPalette.TextMuted,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun InputModeSelector(
    selectedMode: NoteBuilderInputMode,
    onInputModeChange: (NoteBuilderInputMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = NoteBuilderPalette.Surface,
        border = BorderStroke(1.dp, NoteBuilderPalette.Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModeChip(
                text = "Keyboard",
                icon = { Icon(Icons.Default.GraphicEq, contentDescription = null) },
                selected = selectedMode == NoteBuilderInputMode.KEYBOARD,
                onClick = { onInputModeChange(NoteBuilderInputMode.KEYBOARD) },
                modifier = Modifier.weight(1f)
            )
            ModeChip(
                text = "Grid",
                icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                selected = selectedMode == NoteBuilderInputMode.GRID,
                onClick = { onInputModeChange(NoteBuilderInputMode.GRID) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModeChip(
    text: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) NoteBuilderPalette.Primary.copy(alpha = 0.18f) else Color.Transparent,
            contentColor = if (selected) NoteBuilderPalette.TextPrimary else NoteBuilderPalette.TextSecondary
        ),
        border = BorderStroke(
            1.dp,
            if (selected) NoteBuilderPalette.Primary else NoteBuilderPalette.Border
        )
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            icon()
            Text(text = text)
        }
    }
}

@Composable
private fun PhoneKeyboardCard(
    selectedNotes: List<NoteSelection>,
    onToggleNote: (NoteSelection) -> Unit
) {
    MagnetarCard {
        Text("KEYBOARD INPUT", color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(14.dp))
        PianoKeyboard(
            notes = KeyboardRange,
            selectedNotes = selectedNotes,
            onToggleNote = onToggleNote,
            height = 180.dp
        )
    }
}

@Composable
private fun PianoKeyboardWorkspaceCard(
    selectedNotes: List<NoteSelection>,
    onToggleNote: (NoteSelection) -> Unit
) {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PIANO KEYBOARD", color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.labelLarge)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NoteBuilderPalette.Elevated,
                border = BorderStroke(1.dp, NoteBuilderPalette.Border)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Sustain", color = NoteBuilderPalette.TextSecondary, style = MaterialTheme.typography.labelMedium)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NoteBuilderPalette.Primary)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        PianoKeyboard(
            notes = KeyboardRange,
            selectedNotes = selectedNotes,
            onToggleNote = onToggleNote,
            height = 360.dp
        )
    }
}

@Composable
private fun PianoKeyboard(
    notes: List<NoteSelection>,
    selectedNotes: List<NoteSelection>,
    onToggleNote: (NoteSelection) -> Unit,
    height: androidx.compose.ui.unit.Dp
) {
    val whiteNotes = notes.filterNot { it.pitchClass.contains("#") }
    val blackNotes = notes.filter { it.pitchClass.contains("#") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF091015))
            .border(1.dp, NoteBuilderPalette.Border, RoundedCornerShape(22.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            whiteNotes.forEach { note ->
                val selected = selectedNotes.contains(note)
                val brush = if (selected) {
                    Brush.verticalGradient(
                        listOf(
                            NoteBuilderPalette.Secondary.copy(alpha = 0.95f),
                            NoteBuilderPalette.Primary.copy(alpha = 0.92f)
                        )
                    )
                } else {
                    Brush.verticalGradient(listOf(NoteBuilderPalette.WhiteKey, Color(0xFFDDE1E2)))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(brush)
                        .border(1.dp, NoteBuilderPalette.Border, RoundedCornerShape(14.dp))
                ) {
                    val whiteName = note.displayName
                    OutlinedButton(
                        onClick = { onToggleNote(note) },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = if (selected) NoteBuilderPalette.Background else NoteBuilderPalette.TextMuted
                        ),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        if (note.pitchClass == "C") {
                            Text(
                                text = whiteName,
                                color = if (selected) NoteBuilderPalette.Background else NoteBuilderPalette.TextMuted,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            blackNotes.forEach { note ->
                val selected = selectedNotes.contains(note)
                if (note.pitchClass in listOf("E#", "B#")) {
                    Spacer(modifier = Modifier.width(0.dp))
                } else {
                    OutlinedButton(
                        onClick = { onToggleNote(note) },
                        modifier = Modifier
                            .width(28.dp)
                            .height(height * 0.58f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) NoteBuilderPalette.Primary.copy(alpha = 0.28f) else NoteBuilderPalette.BlackKey,
                            contentColor = if (selected) NoteBuilderPalette.Primary else NoteBuilderPalette.TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (selected) NoteBuilderPalette.Primary else NoteBuilderPalette.Border)
                    ) {
                        Text(note.pitchClass, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneGridCard(
    selectedNotes: List<NoteSelection>,
    onToggleNote: (NoteSelection) -> Unit
) {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NOTE GRID", color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.labelLarge)
            Text("Scroll for more octaves", color = NoteBuilderPalette.TextSecondary, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            (3..7).forEach { octave ->
                OctaveRow(
                    octave = octave,
                    selectedNotes = selectedNotes,
                    onToggleNote = onToggleNote
                )
            }
        }
    }
}

@Composable
private fun WorkspaceGridCard(
    modifier: Modifier = Modifier,
    selectedNotes: List<NoteSelection>,
    onToggleNote: (NoteSelection) -> Unit
) {
    MagnetarCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NOTE GRID", color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Show Enharmonic", color = NoteBuilderPalette.TextSecondary, style = MaterialTheme.typography.labelMedium)
                Switch(checked = false, onCheckedChange = {})
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items((0..5).toList()) { octave ->
                OctaveRow(
                    octave = octave,
                    selectedNotes = selectedNotes,
                    onToggleNote = onToggleNote
                )
            }
        }
    }
}

@Composable
private fun OctaveRow(
    octave: Int,
    selectedNotes: List<NoteSelection>,
    onToggleNote: (NoteSelection) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "C$octave",
            color = NoteBuilderPalette.TextSecondary,
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        ChromaticNotes.forEach { pitch ->
            val note = NoteSelection(pitch, octave)
            val selected = selectedNotes.contains(note)
            OutlinedButton(
                onClick = { onToggleNote(note) },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selected) NoteBuilderPalette.Primary.copy(alpha = 0.18f) else NoteBuilderPalette.Surface,
                    contentColor = if (selected) NoteBuilderPalette.Primary else NoteBuilderPalette.TextSecondary
                ),
                border = BorderStroke(1.dp, if (selected) NoteBuilderPalette.Primary else NoteBuilderPalette.Border)
            ) {
                Text(text = pitch, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun SelectedNotesAndControlsCard(
    state: NoteBuilderUiState,
    onToggleHold: () -> Unit,
    onPlaySelection: () -> Unit,
    onStopPlayback: () -> Unit,
    onClearSelection: () -> Unit
) {
    MagnetarCard {
        Text(
            text = if (state.selectedNotes.isEmpty()) "NO NOTES SELECTED" else "SELECTED NOTES",
            color = NoteBuilderPalette.TextMuted,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.selectedNotes.isEmpty()) {
            Text(
                text = "Select notes from the keyboard or grid",
                color = NoteBuilderPalette.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.selectedNotes) { note ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NoteBuilderPalette.Primary.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, NoteBuilderPalette.Primary)
                    ) {
                        Text(
                            text = note.displayName,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = NoteBuilderPalette.Primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        state.playbackError?.let { playbackError ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = playbackError,
                color = NoteBuilderPalette.Danger,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onPlaySelection,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(18.dp),
                enabled = state.selectedNotes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NoteBuilderPalette.Primary.copy(alpha = 0.2f),
                    contentColor = NoteBuilderPalette.TextPrimary,
                    disabledContainerColor = NoteBuilderPalette.Elevated,
                    disabledContentColor = NoteBuilderPalette.TextMuted
                ),
                border = BorderStroke(1.dp, if (state.selectedNotes.isNotEmpty()) NoteBuilderPalette.Primary else NoteBuilderPalette.Border)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.isPlaying) "Replay" else "Play")
            }
            OutlinedButton(
                onClick = onStopPlayback,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NoteBuilderPalette.TextSecondary),
                border = BorderStroke(1.dp, if (state.isPlaying) NoteBuilderPalette.Secondary else NoteBuilderPalette.Border),
                enabled = state.isPlaying
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onClearSelection,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NoteBuilderPalette.Danger),
                border = BorderStroke(1.dp, NoteBuilderPalette.Danger.copy(alpha = 0.55f)),
                enabled = state.selectedNotes.isNotEmpty()
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear")
            }
            OutlinedButton(
                onClick = onToggleHold,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (state.holdEnabled) NoteBuilderPalette.Secondary.copy(alpha = 0.14f) else Color.Transparent,
                    contentColor = if (state.holdEnabled) NoteBuilderPalette.Secondary else NoteBuilderPalette.TextSecondary
                ),
                border = BorderStroke(1.dp, if (state.holdEnabled) NoteBuilderPalette.Secondary else NoteBuilderPalette.Border)
            ) {
                Icon(Icons.Default.PauseCircleOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hold")
            }
        }
    }
}

@Composable
private fun AboutSelectionCard(
    title: String,
    subtitle: String,
    quality: String
) {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = NoteBuilderPalette.Primary.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, NoteBuilderPalette.Primary.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = NoteBuilderPalette.Primary
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("ABOUT THIS SELECTION", color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(title, color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = NoteBuilderPalette.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Quality: $quality",
                    color = if (quality == "Unknown") NoteBuilderPalette.Warning else NoteBuilderPalette.Primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SelectedChordSummaryCard(state: NoteBuilderUiState) {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("SELECTED CHORD / NOTE SET", color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.selectedNotes) { note ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = NoteBuilderPalette.Primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, NoteBuilderPalette.Primary.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = note.displayName,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
                                color = NoteBuilderPalette.Primary,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NoteBuilderPalette.TextSecondary),
                    border = BorderStroke(1.dp, NoteBuilderPalette.Border)
                ) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(state.detectedPrimaryName, color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.displaySmall)
                Text(state.detectedSecondaryName, color = NoteBuilderPalette.TextSecondary, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Inversions", color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.labelMedium)
                Text("Root Position", color = NoteBuilderPalette.Primary, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun NoteSequenceCard(sequence: List<String>) {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NOTE SEQUENCE", color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.labelLarge)
            Icon(Icons.Default.Add, contentDescription = null, tint = NoteBuilderPalette.TextSecondary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sequence.forEachIndexed { index, item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NoteBuilderPalette.Elevated,
                    border = BorderStroke(1.dp, NoteBuilderPalette.Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}", color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.bodyMedium)
                        Text(item, color = NoteBuilderPalette.TextPrimary, modifier = Modifier.weight(1f).padding(start = 12.dp))
                        Text(if (index == sequence.lastIndex) "2 Bars" else "1 Bar", color = NoteBuilderPalette.TextSecondary)
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NoteBuilderPalette.TextSecondary)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Loop", color = NoteBuilderPalette.TextSecondary)
            Switch(checked = true, onCheckedChange = {})
            Text("BPM 120", color = NoteBuilderPalette.TextSecondary)
        }
    }
}

@Composable
private fun SavedPatternsCard() {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SAVED PATTERNS", color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.labelLarge)
            Text("View All", color = NoteBuilderPalette.Primary, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        listOf(
            "Ambient Progression 01" to "4 Items",
            "Jazz ii–V–I" to "3 Items",
            "Cinematic Build" to "5 Items",
            "Worship Essentials" to "4 Items"
        ).forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(item.first, color = NoteBuilderPalette.TextPrimary)
                    Text(item.second, color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.bodySmall)
                }
                Icon(
                    imageVector = Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (index == 1) NoteBuilderPalette.Primary else NoteBuilderPalette.TextMuted
                )
            }
            if (index < 3) {
                Divider(color = NoteBuilderPalette.Border)
            }
        }
    }
}

@Composable
private fun HarmonizationPlaceholderCard() {
    MagnetarCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("HARMONIZATION (BETA)", color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.labelLarge)
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NoteBuilderPalette.TextSecondary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = NoteBuilderPalette.Surface,
            border = BorderStroke(1.dp, NoteBuilderPalette.Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NoteBuilderPalette.TextMuted, modifier = Modifier.size(36.dp))
                Text(
                    "Select a note or chord to generate harmonizations",
                    color = NoteBuilderPalette.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    border = BorderStroke(1.dp, NoteBuilderPalette.Border)
                ) {
                    Text("Generate")
                }
            }
        }
    }
}

@Composable
private fun PlaybackSettingsBar(
    selectedMode: NoteBuilderInputMode,
    onInputModeChange: (NoteBuilderInputMode) -> Unit
) {
    MagnetarCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaybackSetting("Instrument", "Piano")
            Divider(modifier = Modifier.height(34.dp).width(1.dp), color = NoteBuilderPalette.Border)
            PlaybackSetting("Velocity", "80%")
            Divider(modifier = Modifier.height(34.dp).width(1.dp), color = NoteBuilderPalette.Border)
            PlaybackSetting("Duration", "1/4 Note")
            Divider(modifier = Modifier.height(34.dp).width(1.dp), color = NoteBuilderPalette.Border)
            PlaybackSetting("Humanize", "10%")
            Divider(modifier = Modifier.height(34.dp).width(1.dp), color = NoteBuilderPalette.Border)
            PlaybackSetting("Swing", "0%")
            Divider(modifier = Modifier.height(34.dp).width(1.dp), color = NoteBuilderPalette.Border)
            PlaybackSetting("Arpeggiator", "Off")
            Spacer(modifier = Modifier.weight(1f))
            InputModeSelector(
                selectedMode = selectedMode,
                onInputModeChange = onInputModeChange,
                modifier = Modifier.width(280.dp)
            )
        }
    }
}

@Composable
private fun PlaybackSetting(label: String, value: String) {
    Column {
        Text(label, color = NoteBuilderPalette.TextMuted, style = MaterialTheme.typography.labelMedium)
        Text(value, color = NoteBuilderPalette.TextPrimary, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun MagnetarCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = NoteBuilderPalette.Surface,
        border = BorderStroke(1.dp, NoteBuilderPalette.Border)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF05080A, widthDp = 412, heightDp = 915)
@Composable
private fun NoteBuilderPhonePreview() {
    MagnetarOrpheusTheme(dynamicColor = false) {
        NoteBuilderDemoScreen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF05080A, widthDp = 1280, heightDp = 800)
@Composable
private fun NoteBuilderTabletPreview() {
    MagnetarOrpheusTheme(dynamicColor = false) {
        NoteBuilderDemoScreen()
    }
}
