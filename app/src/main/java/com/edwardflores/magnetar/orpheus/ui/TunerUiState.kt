package com.edwardflores.magnetar.orpheus.ui

import java.util.Locale

enum class NoteNamingSystem(val displayName: String) {
    SCIENTIFIC("Scientific"),
    SYLLABIC("Syllabic"),
    GERMAN("German")
}

data class NoteHistoryItem(
    val badgeLabel: String,
    val note: String,
    val frequencyHz: Double,
    val cents: Int,
    val timeLabel: String
)

data class QuickPreset(
    val name: String,
    val referenceHz: Int
)

data class TunerUiState(
    val frequency: Double = 0.0,
    val noteName: String = "-",
    val noteLabel: String = "-",
    val chromaticNote: String = "-",
    val octave: Int = 4,
    val cents: Int = 0,
    val isTuned: Boolean = false,
    val isActive: Boolean = false,
    val referenceA4: Double = 440.0,
    val namingSystem: NoteNamingSystem = NoteNamingSystem.SCIENTIFIC,
    val tunerMode: String = "Chromatic",
    val inputLevel: Float = 0f,
    val waveformSamples: List<Float> = defaultWaveformSamples(),
    val noteHistory: List<NoteHistoryItem> = defaultNoteHistory(),
    val pitchStabilityPoints: List<Float> = defaultPitchStabilityPoints(),
    val selectedInstrument: String = "Guitar",
    val selectedTuning: String = "Standard (EADGBE)",
    val quickPresets: List<QuickPreset> = defaultQuickPresets()
) {
    val frequencyText: String
        get() = if (frequency > 0) {
            String.format(Locale.US, "%.1f Hz", frequency)
        } else {
            "--.- Hz"
        }

    val centsText: String
        get() = when {
            cents > 0 -> "+$cents cents"
            cents < 0 -> "$cents cents"
            else -> "0 cents"
        }
}

fun defaultQuickPresets(): List<QuickPreset> = listOf(
    QuickPreset(name = "Standard", referenceHz = 440),
    QuickPreset(name = "Orchestral", referenceHz = 442),
    QuickPreset(name = "Baroque", referenceHz = 415)
)

fun defaultWaveformSamples(): List<Float> = listOf(
    0.04f, 0.08f, 0.12f, 0.18f, 0.15f, 0.10f, 0.06f, 0.02f,
    -0.03f, -0.08f, -0.16f, -0.22f, -0.18f, -0.10f, -0.04f, 0.03f,
    0.10f, 0.18f, 0.26f, 0.30f, 0.24f, 0.12f, 0.06f, -0.02f,
    -0.10f, -0.18f, -0.24f, -0.26f, -0.20f, -0.12f, -0.05f, 0.01f,
    0.08f, 0.15f, 0.22f, 0.18f, 0.10f, 0.04f, 0.00f, -0.03f
)

fun defaultNoteHistory(): List<NoteHistoryItem> = listOf(
    NoteHistoryItem("A", "A4", 440.0, 2, "10:42:21"),
    NoteHistoryItem("E", "E4", 329.6, -1, "10:41:56"),
    NoteHistoryItem("G", "G4", 392.0, 3, "10:41:28"),
    NoteHistoryItem("B", "B4", 493.9, 5, "10:40:57"),
    NoteHistoryItem("D", "D5", 587.3, -2, "10:40:21")
)

fun defaultPitchStabilityPoints(): List<Float> = listOf(
    1f, 2f, 0f, 3f, 1f, -1f, 0f, -3f,
    -1f, 2f, 1f, 0f, 3f, 2f, 1f, 0f,
    -1f, -2f, 0f, 1f, 2f, 1f, 0f, -1f
)
