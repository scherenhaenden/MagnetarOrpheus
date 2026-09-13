package com.blazares.orpheus.ui

import androidx.annotation.StringRes
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
    @field:StringRes val calibrationErrorResId: Int? = null,
    @field:StringRes val captureErrorResId: Int? = null,
    val tunerMode: String = "Chromatic",
    val inputLevel: Float = 0f,
    val waveformSamples: List<Float> = List(48) { 0f },
    val noteHistory: List<NoteHistoryItem> = emptyList(),
    val pitchStabilityPoints: List<Float> = emptyList(),
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

private fun defaultQuickPresets(): List<QuickPreset> = listOf(
    QuickPreset(name = "Standard", referenceHz = 440),
    QuickPreset(name = "Orchestral", referenceHz = 442),
    QuickPreset(name = "Baroque", referenceHz = 415)
)
