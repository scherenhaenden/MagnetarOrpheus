package com.edwardflores.magnetar.orpheus.notebuilder

import com.edwardflores.magnetar.orpheus.ui.NoteLanguage
import com.edwardflores.magnetar.orpheus.ui.notebuilder.NoteSelection
import kotlin.math.pow

data class NoteSetAnalysis(
    val title: String,
    val subtitle: String,
    val quality: String
)

object NoteBuilderMusicTheory {
    private val chromaticNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    fun analyzeSelection(
        notes: List<NoteSelection>,
        noteLanguage: NoteLanguage = NoteLanguage.ENGLISH
    ): NoteSetAnalysis {
        val strings = theoryStrings(noteLanguage)
        if (notes.isEmpty()) {
            return NoteSetAnalysis(
                title = strings.noSelectionTitle,
                subtitle = strings.noSelectionSubtitle,
                quality = strings.none
            )
        }

        val sorted = notes.distinctBy { it.pitchClass to it.octave }.sortedBy(::toMidiNumber)
        val root = sorted.first()
        val rootName = formatNote(root, noteLanguage)
        val rootPitch = formatPitchClass(root.pitchClass, noteLanguage)
        val intervals = sorted.map { ((toMidiNumber(it) - toMidiNumber(root)) % 12 + 12) % 12 }.distinct().sorted()
        val displayNotes = sorted.joinToString(", ") { formatNote(it, noteLanguage) }
        val intervalText = intervals.joinToString(", ")

        if (sorted.size == 2) {
            val intervalSemitones = toMidiNumber(sorted[1]) - toMidiNumber(sorted[0])
            val normalizedInterval = if (intervalSemitones == 12) 12 else intervalSemitones.mod(12)
            val intervalName = strings.intervalName(normalizedInterval)
            return NoteSetAnalysis(
                title = intervalName,
                subtitle = "${strings.notes}: $displayNotes  •  ${strings.semitones}: $intervalText",
                quality = intervalName
            )
        }

        val pattern = when (intervals) {
            listOf(0) -> AnalysisPattern("single", strings.singleNote(rootName), strings.singleNote)
            listOf(0, 4, 7) -> AnalysisPattern("major", strings.majorTriad(rootName), strings.major)
            listOf(0, 3, 7) -> AnalysisPattern("minor", strings.minorTriad(rootName), strings.minor)
            listOf(0, 3, 6) -> AnalysisPattern("dim", strings.diminishedTriad(rootName), strings.diminished)
            listOf(0, 4, 8) -> AnalysisPattern("aug", strings.augmentedTriad(rootName), strings.augmented)
            listOf(0, 2, 7) -> AnalysisPattern("sus2", strings.suspendedSecond(rootName), strings.suspended)
            listOf(0, 5, 7) -> AnalysisPattern("sus4", strings.suspendedFourth(rootName), strings.suspended)
            listOf(0, 4, 7, 10) -> AnalysisPattern("7", strings.dominantSeventh(rootName), strings.dominant)
            listOf(0, 4, 7, 11) -> AnalysisPattern("maj7", strings.majorSeventh(rootName), strings.major)
            listOf(0, 3, 7, 10) -> AnalysisPattern("m7", strings.minorSeventh(rootName), strings.minor)
            listOf(0, 3, 6, 9) -> AnalysisPattern("dim7", strings.diminishedSeventh(rootName), strings.diminished)
            listOf(0, 3, 6, 10) -> AnalysisPattern("m7b5", strings.halfDiminishedSeventh(rootName), strings.halfDiminished)
            else -> AnalysisPattern("custom", strings.customNoteSet(rootName), strings.custom)
        }

        val compactName = when (pattern.suffix) {
            "single" -> rootName
            "major" -> rootPitch
            "minor" -> "${rootPitch}m"
            "dim" -> "${rootPitch}dim"
            "aug" -> "${rootPitch}aug"
            "sus2" -> "${rootPitch}sus2"
            "sus4" -> "${rootPitch}sus4"
            "7" -> "${rootPitch}7"
            "maj7" -> "${rootPitch}maj7"
            "m7" -> "${rootPitch}m7"
            "dim7" -> "${rootPitch}dim7"
            "m7b5" -> "${rootPitch}m7b5"
            else -> pattern.fullTitle
        }

        return NoteSetAnalysis(
            title = compactName,
            subtitle = "${strings.notes}: $displayNotes  •  ${strings.semitones}: $intervalText",
            quality = pattern.quality
        )
    }

    fun formatNote(note: NoteSelection, noteLanguage: NoteLanguage): String =
        "${formatPitchClass(note.pitchClass, noteLanguage)}${note.octave}"

    fun formatPitchClass(pitchClass: String, noteLanguage: NoteLanguage): String {
        val index = chromaticNotes.indexOf(pitchClass)
        require(index >= 0) { "Unsupported pitch class: $pitchClass" }
        return when (noteLanguage) {
            NoteLanguage.ENGLISH -> chromaticNotes[index]
            NoteLanguage.SPANISH -> listOf("Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si")[index]
            NoteLanguage.GERMAN -> listOf("C", "Cis", "D", "Dis", "E", "F", "Fis", "G", "Gis", "A", "Ais", "H")[index]
        }
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
}

private data class AnalysisPattern(
    val suffix: String,
    val fullTitle: String,
    val quality: String
)

private data class TheoryStrings(
    val noSelectionTitle: String,
    val noSelectionSubtitle: String,
    val none: String,
    val notes: String,
    val semitones: String,
    val singleNote: String,
    val major: String,
    val minor: String,
    val diminished: String,
    val augmented: String,
    val suspended: String,
    val dominant: String,
    val halfDiminished: String,
    val custom: String,
    val intervalNames: Map<Int, String>,
    val singleNoteFormatter: (String) -> String,
    val majorTriadFormatter: (String) -> String,
    val minorTriadFormatter: (String) -> String,
    val diminishedTriadFormatter: (String) -> String,
    val augmentedTriadFormatter: (String) -> String,
    val suspendedSecondFormatter: (String) -> String,
    val suspendedFourthFormatter: (String) -> String,
    val dominantSeventhFormatter: (String) -> String,
    val majorSeventhFormatter: (String) -> String,
    val minorSeventhFormatter: (String) -> String,
    val diminishedSeventhFormatter: (String) -> String,
    val halfDiminishedSeventhFormatter: (String) -> String,
    val customNoteSetFormatter: (String) -> String
) {
    fun intervalName(semitones: Int): String = intervalNames[semitones] ?: custom
    fun singleNote(rootName: String): String = singleNoteFormatter(rootName)
    fun majorTriad(rootName: String): String = majorTriadFormatter(rootName)
    fun minorTriad(rootName: String): String = minorTriadFormatter(rootName)
    fun diminishedTriad(rootName: String): String = diminishedTriadFormatter(rootName)
    fun augmentedTriad(rootName: String): String = augmentedTriadFormatter(rootName)
    fun suspendedSecond(rootName: String): String = suspendedSecondFormatter(rootName)
    fun suspendedFourth(rootName: String): String = suspendedFourthFormatter(rootName)
    fun dominantSeventh(rootName: String): String = dominantSeventhFormatter(rootName)
    fun majorSeventh(rootName: String): String = majorSeventhFormatter(rootName)
    fun minorSeventh(rootName: String): String = minorSeventhFormatter(rootName)
    fun diminishedSeventh(rootName: String): String = diminishedSeventhFormatter(rootName)
    fun halfDiminishedSeventh(rootName: String): String = halfDiminishedSeventhFormatter(rootName)
    fun customNoteSet(rootName: String): String = customNoteSetFormatter(rootName)
}

private fun theoryStrings(language: NoteLanguage): TheoryStrings = when (language) {
    NoteLanguage.ENGLISH -> TheoryStrings(
        noSelectionTitle = "No note set selected",
        noSelectionSubtitle = "Select notes from the keyboard or grid",
        none = "None",
        notes = "Notes",
        semitones = "Semitones",
        singleNote = "Single Note",
        major = "Major",
        minor = "Minor",
        diminished = "Diminished",
        augmented = "Augmented",
        suspended = "Suspended",
        dominant = "Dominant",
        halfDiminished = "Half-Diminished",
        custom = "Custom",
        intervalNames = mapOf(
            0 to "Unison",
            1 to "Minor Second",
            2 to "Major Second",
            3 to "Minor Third",
            4 to "Major Third",
            5 to "Perfect Fourth",
            6 to "Tritone",
            7 to "Perfect Fifth",
            8 to "Minor Sixth",
            9 to "Major Sixth",
            10 to "Minor Seventh",
            11 to "Major Seventh",
            12 to "Octave"
        ),
        singleNoteFormatter = { rootName -> "$rootName Note" },
        majorTriadFormatter = { rootName -> "$rootName Major Triad" },
        minorTriadFormatter = { rootName -> "$rootName Minor Triad" },
        diminishedTriadFormatter = { rootName -> "$rootName Diminished Triad" },
        augmentedTriadFormatter = { rootName -> "$rootName Augmented Triad" },
        suspendedSecondFormatter = { rootName -> "$rootName Suspended Second" },
        suspendedFourthFormatter = { rootName -> "$rootName Suspended Fourth" },
        dominantSeventhFormatter = { rootName -> "$rootName Dominant Seventh" },
        majorSeventhFormatter = { rootName -> "$rootName Major Seventh" },
        minorSeventhFormatter = { rootName -> "$rootName Minor Seventh" },
        diminishedSeventhFormatter = { rootName -> "$rootName Diminished Seventh" },
        halfDiminishedSeventhFormatter = { rootName -> "$rootName Half-Diminished Seventh" },
        customNoteSetFormatter = { rootName -> "$rootName Custom Note Set" }
    )
    NoteLanguage.SPANISH -> TheoryStrings(
        noSelectionTitle = "Ningún conjunto seleccionado",
        noSelectionSubtitle = "Selecciona notas desde el teclado o la rejilla",
        none = "Ninguna",
        notes = "Notas",
        semitones = "Semitonos",
        singleNote = "Nota única",
        major = "Mayor",
        minor = "Menor",
        diminished = "Disminuido",
        augmented = "Aumentado",
        suspended = "Suspendido",
        dominant = "Dominante",
        halfDiminished = "Semidisminuido",
        custom = "Personalizado",
        intervalNames = mapOf(
            0 to "Unísono",
            1 to "Segunda menor",
            2 to "Segunda mayor",
            3 to "Tercera menor",
            4 to "Tercera mayor",
            5 to "Cuarta justa",
            6 to "Tritono",
            7 to "Quinta justa",
            8 to "Sexta menor",
            9 to "Sexta mayor",
            10 to "Séptima menor",
            11 to "Séptima mayor",
            12 to "Octava"
        ),
        singleNoteFormatter = { rootName -> "Nota: $rootName" },
        majorTriadFormatter = { rootName -> "Tríada mayor de $rootName" },
        minorTriadFormatter = { rootName -> "Tríada menor de $rootName" },
        diminishedTriadFormatter = { rootName -> "Tríada disminuida de $rootName" },
        augmentedTriadFormatter = { rootName -> "Tríada aumentada de $rootName" },
        suspendedSecondFormatter = { rootName -> "$rootName suspendido 2" },
        suspendedFourthFormatter = { rootName -> "$rootName suspendido 4" },
        dominantSeventhFormatter = { rootName -> "Séptima dominante de $rootName" },
        majorSeventhFormatter = { rootName -> "Séptima mayor de $rootName" },
        minorSeventhFormatter = { rootName -> "Séptima menor de $rootName" },
        diminishedSeventhFormatter = { rootName -> "Séptima disminuida de $rootName" },
        halfDiminishedSeventhFormatter = { rootName -> "Semidisminuido de $rootName" },
        customNoteSetFormatter = { rootName -> "Conjunto personalizado de $rootName" }
    )
    NoteLanguage.GERMAN -> TheoryStrings(
        noSelectionTitle = "Kein Notensatz ausgewählt",
        noSelectionSubtitle = "Wähle Noten über Tastatur oder Raster aus",
        none = "Keine",
        notes = "Noten",
        semitones = "Halbtöne",
        singleNote = "Einzelton",
        major = "Dur",
        minor = "Moll",
        diminished = "Vermindert",
        augmented = "Übermäßig",
        suspended = "Sus",
        dominant = "Dominant",
        halfDiminished = "Halbvermindert",
        custom = "Benutzerdefiniert",
        intervalNames = mapOf(
            0 to "Prime",
            1 to "Kleine Sekunde",
            2 to "Große Sekunde",
            3 to "Kleine Terz",
            4 to "Große Terz",
            5 to "Reine Quarte",
            6 to "Tritonus",
            7 to "Reine Quinte",
            8 to "Kleine Sexte",
            9 to "Große Sexte",
            10 to "Kleine Septime",
            11 to "Große Septime",
            12 to "Oktave"
        ),
        singleNoteFormatter = { rootName -> "Ton $rootName" },
        majorTriadFormatter = { rootName -> "$rootName-Dur-Dreiklang" },
        minorTriadFormatter = { rootName -> "$rootName-Moll-Dreiklang" },
        diminishedTriadFormatter = { rootName -> "$rootName verminderter Dreiklang" },
        augmentedTriadFormatter = { rootName -> "$rootName übermäßiger Dreiklang" },
        suspendedSecondFormatter = { rootName -> "$rootName sus2" },
        suspendedFourthFormatter = { rootName -> "$rootName sus4" },
        dominantSeventhFormatter = { rootName -> "$rootName Dominantseptakkord" },
        majorSeventhFormatter = { rootName -> "$rootName Durmaj7" },
        minorSeventhFormatter = { rootName -> "$rootName Moll7" },
        diminishedSeventhFormatter = { rootName -> "$rootName verminderter Septakkord" },
        halfDiminishedSeventhFormatter = { rootName -> "$rootName halbverminderter Septakkord" },
        customNoteSetFormatter = { rootName -> "$rootName benutzerdefinierter Notensatz" }
    )
}
