package com.edwardflores.magnetar.orpheus.notebuilder

import com.edwardflores.magnetar.orpheus.ui.notebuilder.NoteSelection
import kotlin.math.pow

data class NoteSetAnalysis(
    val title: String,
    val subtitle: String,
    val quality: String
)

object NoteBuilderMusicTheory {
    private val chromaticNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    fun analyzeSelection(notes: List<NoteSelection>): NoteSetAnalysis {
        if (notes.isEmpty()) {
            return NoteSetAnalysis(
                title = "No note set selected",
                subtitle = "Select notes from the keyboard or grid",
                quality = "None"
            )
        }

        val sorted = notes.distinctBy { it.pitchClass to it.octave }.sortedBy(::toMidiNumber)
        val root = sorted.first()
        val rootName = root.displayName
        val intervals = sorted.map { ((toMidiNumber(it) - toMidiNumber(root)) % 12 + 12) % 12 }.distinct().sorted()
        val displayNotes = sorted.joinToString(", ") { it.displayName }
        val intervalText = intervals.joinToString(", ")

        val (suffix, title, quality) = when (intervals) {
            listOf(0) -> Triple("single", "$rootName Note", "Single Note")
            listOf(0, 4, 7) -> Triple("major", "$rootName Major Triad", "Major")
            listOf(0, 3, 7) -> Triple("minor", "$rootName Minor Triad", "Minor")
            listOf(0, 3, 6) -> Triple("dim", "$rootName Diminished Triad", "Diminished")
            listOf(0, 4, 8) -> Triple("aug", "$rootName Augmented Triad", "Augmented")
            listOf(0, 2, 7) -> Triple("sus2", "$rootName Suspended Second", "Suspended")
            listOf(0, 5, 7) -> Triple("sus4", "$rootName Suspended Fourth", "Suspended")
            listOf(0, 4, 7, 10) -> Triple("7", "$rootName Dominant Seventh", "Dominant")
            listOf(0, 4, 7, 11) -> Triple("maj7", "$rootName Major Seventh", "Major")
            listOf(0, 3, 7, 10) -> Triple("m7", "$rootName Minor Seventh", "Minor")
            listOf(0, 3, 6, 10) -> Triple("m7b5", "$rootName Half-Diminished Seventh", "Half-Diminished")
            else -> Triple("custom", "$rootName Custom Note Set", "Custom")
        }

        val compactName = when (suffix) {
            "single" -> rootName
            "major" -> rootName.removeOctaveSuffix()
            "minor" -> "${rootName.removeOctaveSuffix()}m"
            "dim" -> "${rootName.removeOctaveSuffix()}dim"
            "aug" -> "${rootName.removeOctaveSuffix()}aug"
            "sus2" -> "${rootName.removeOctaveSuffix()}sus2"
            "sus4" -> "${rootName.removeOctaveSuffix()}sus4"
            "7" -> "${rootName.removeOctaveSuffix()}7"
            "maj7" -> "${rootName.removeOctaveSuffix()}maj7"
            "m7" -> "${rootName.removeOctaveSuffix()}m7"
            "m7b5" -> "${rootName.removeOctaveSuffix()}m7b5"
            else -> title
        }

        return NoteSetAnalysis(
            title = compactName,
            subtitle = "Notes: $displayNotes  •  Semitones: $intervalText",
            quality = quality
        )
    }

    fun noteToFrequency(note: NoteSelection): Double {
        val midi = toMidiNumber(note)
        return 440.0 * 2.0.pow((midi - 69) / 12.0)
    }

    fun toMidiNumber(note: NoteSelection): Int {
        val pitchIndex = chromaticNotes.indexOf(note.pitchClass)
        require(pitchIndex >= 0) { "Unsupported pitch class: ${note.pitchClass}" }
        return (note.octave + 1) * 12 + pitchIndex
    }

    private fun String.removeOctaveSuffix(): String = trimEnd { it.isDigit() }
}
