package com.edwardflores.magnetar.orpheus.notebuilder

import com.edwardflores.magnetar.orpheus.ui.NoteLanguage
import com.edwardflores.magnetar.orpheus.ui.notebuilder.NoteSelection

object NoteNamingFormatter {
    private val english = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val spanish = listOf("Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si")
    private val german = listOf("C", "Cis", "D", "Dis", "E", "F", "Fis", "G", "Gis", "A", "Ais", "H")

    fun formatPitchClass(pitchClass: String, language: NoteLanguage): String {
        val semitone = english.indexOf(pitchClass)
        require(semitone >= 0) { "Unsupported pitch class: $pitchClass" }
        val names = when (language) {
            NoteLanguage.ENGLISH -> english
            NoteLanguage.SPANISH -> spanish
            NoteLanguage.GERMAN -> german
        }
        return names[semitone]
    }

    fun formatNote(note: NoteSelection, language: NoteLanguage, includeOctave: Boolean = true): String {
        val pitch = formatPitchClass(note.pitchClass, language)
        return if (includeOctave) "$pitch${note.octave}" else pitch
    }
}
