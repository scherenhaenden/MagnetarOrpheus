package com.edwardflores.magnetar.orpheus.notebuilder

import com.edwardflores.magnetar.orpheus.ui.NoteLanguage
import com.edwardflores.magnetar.orpheus.ui.notebuilder.NoteSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteBuilderMusicTheoryTest {

    @Test
    fun `analyzeSelection identifies major triad`() {
        val analysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("C", 4),
                NoteSelection("E", 4),
                NoteSelection("G", 4)
            )
        )

        assertEquals("C", analysis.title)
        assertEquals("Major", analysis.quality)
        assertTrue(analysis.subtitle.contains("C4, E4, G4"))
    }

    @Test
    fun `analyzeSelection identifies major seventh`() {
        val analysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("C", 4),
                NoteSelection("E", 4),
                NoteSelection("G", 4),
                NoteSelection("B", 4)
            )
        )

        assertEquals("Cmaj7", analysis.title)
        assertEquals("Major", analysis.quality)
    }

    @Test
    fun `analyzeSelection identifies minor and dominant chords`() {
        val minorAnalysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("A", 3),
                NoteSelection("C", 4),
                NoteSelection("E", 4)
            )
        )
        val dominantAnalysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("G", 3),
                NoteSelection("B", 3),
                NoteSelection("D", 4),
                NoteSelection("F", 4)
            )
        )

        assertEquals("Am", minorAnalysis.title)
        assertEquals("Minor", minorAnalysis.quality)
        assertEquals("G7", dominantAnalysis.title)
        assertEquals("Dominant", dominantAnalysis.quality)
    }

    @Test
    fun `analyzeSelection handles custom and single note sets`() {
        val singleAnalysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("F#", 4))
        )
        val customAnalysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("C", 4),
                NoteSelection("C#", 4),
                NoteSelection("G", 4)
            )
        )

        assertEquals("F#4", singleAnalysis.title)
        assertEquals("Single Note", singleAnalysis.quality)
        assertEquals("Custom", customAnalysis.quality)
        assertTrue(customAnalysis.subtitle.contains("C4, C#4, G4"))
    }

    @Test
    fun `analyzeSelection identifies suspended altered and seventh variants`() {
        val susTwo = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("D", 4), NoteSelection("E", 4), NoteSelection("A", 4))
        )
        val susFour = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("D", 4), NoteSelection("G", 4), NoteSelection("A", 4))
        )
        val diminished = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("B", 3), NoteSelection("D", 4), NoteSelection("F", 4))
        )
        val augmented = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("C", 4), NoteSelection("E", 4), NoteSelection("G#", 4))
        )
        val minorSeven = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("D", 4), NoteSelection("F", 4), NoteSelection("A", 4), NoteSelection("C", 5))
        )
        val halfDiminished = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("B", 3), NoteSelection("D", 4), NoteSelection("F", 4), NoteSelection("A", 4))
        )

        assertEquals("Dsus2", susTwo.title)
        assertEquals("Suspended", susTwo.quality)
        assertEquals("Dsus4", susFour.title)
        assertEquals("Bdim", diminished.title)
        assertEquals("Diminished", diminished.quality)
        assertEquals("Caug", augmented.title)
        assertEquals("Augmented", augmented.quality)
        assertEquals("Dm7", minorSeven.title)
        assertEquals("Minor", minorSeven.quality)
        assertEquals("Bm7b5", halfDiminished.title)
        assertEquals("Half-Diminished", halfDiminished.quality)
    }

    @Test
    fun `analyzeSelection handles empty state`() {
        val analysis = NoteBuilderMusicTheory.analyzeSelection(emptyList())

        assertEquals("No note set selected", analysis.title)
        assertEquals("None", analysis.quality)
    }

    @Test
    fun `analyzeSelection localizes note names and theory text in spanish`() {
        val analysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("A", 3),
                NoteSelection("C", 4),
                NoteSelection("E", 4)
            ),
            NoteLanguage.SPANISH
        )

        assertEquals("Lam", analysis.title)
        assertEquals("Menor", analysis.quality)
        assertTrue(analysis.subtitle.contains("La3, Do4, Mi4"))
        assertTrue(analysis.subtitle.contains("Notas"))
    }

    @Test
    fun `analyzeSelection localizes german spellings`() {
        val analysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(NoteSelection("B", 4)),
            NoteLanguage.GERMAN
        )

        assertEquals("H4", analysis.title)
        assertEquals("Einzelton", analysis.quality)
        assertTrue(analysis.subtitle.contains("H4"))
        assertTrue(analysis.subtitle.contains("Halbtöne"))
    }

    @Test
    fun `analyzeSelection identifies intervals before falling back to custom`() {
        val analysis = NoteBuilderMusicTheory.analyzeSelection(
            listOf(
                NoteSelection("C", 4),
                NoteSelection("G", 4)
            )
        )

        assertEquals("Perfect Fifth", analysis.title)
        assertEquals("Perfect Fifth", analysis.quality)
        assertTrue(analysis.subtitle.contains("C4, G4"))
    }

    @Test
    fun `noteToFrequency maps concert A correctly`() {
        val frequency = NoteBuilderMusicTheory.noteToFrequency(NoteSelection("A", 4))
        assertEquals(440.0, frequency, 0.01)
    }

    @Test
    fun `toMidiNumber maps middle C correctly`() {
        val midi = NoteBuilderMusicTheory.toMidiNumber(NoteSelection("C", 4))
        assertEquals(60, midi)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toMidiNumber rejects unsupported pitch classes`() {
        NoteBuilderMusicTheory.toMidiNumber(NoteSelection("Hb", 4))
    }

    @Test
    fun `all localized theory labels and interval names are reachable`() {
        val chordExamples = listOf(
            listOf(NoteSelection("C", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("E", 4), NoteSelection("G", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("D#", 4), NoteSelection("G", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("D#", 4), NoteSelection("F#", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("E", 4), NoteSelection("G#", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("D", 4), NoteSelection("G", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("F", 4), NoteSelection("G", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("E", 4), NoteSelection("G", 4), NoteSelection("A#", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("E", 4), NoteSelection("G", 4), NoteSelection("B", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("D#", 4), NoteSelection("G", 4), NoteSelection("A#", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("D#", 4), NoteSelection("F#", 4), NoteSelection("A", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("D#", 4), NoteSelection("F#", 4), NoteSelection("A#", 4)),
            listOf(NoteSelection("C", 4), NoteSelection("C#", 4), NoteSelection("G", 4))
        )
        val chromatic = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        NoteLanguage.entries.forEach { language ->
            assertTrue(NoteBuilderMusicTheory.analyzeSelection(emptyList(), language).title.isNotBlank())
            chordExamples.forEach { notes ->
                val analysis = NoteBuilderMusicTheory.analyzeSelection(notes, language)
                assertTrue(analysis.title.isNotBlank())
                assertTrue(analysis.subtitle.isNotBlank())
                assertTrue(analysis.quality.isNotBlank())
            }
            chromatic.forEach { pitchClass ->
                val interval = NoteBuilderMusicTheory.analyzeSelection(
                    listOf(NoteSelection("C", 4), NoteSelection(pitchClass, 5)),
                    language
                )
                assertTrue(interval.title.isNotBlank())
            }
            assertTrue(
                NoteBuilderMusicTheory.analyzeSelection(
                    listOf(NoteSelection("C", 4), NoteSelection("C", 5)),
                    language
                ).title.isNotBlank()
            )
        }
    }

    @Test
    fun `note naming formatter supports every language and chromatic pitch`() {
        val expectedNames = mapOf(
            NoteLanguage.ENGLISH to listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"),
            NoteLanguage.SPANISH to listOf("Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si"),
            NoteLanguage.GERMAN to listOf("C", "Cis", "D", "Dis", "E", "F", "Fis", "G", "Gis", "A", "Ais", "H")
        )
        val chromatic = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        expectedNames.forEach { (language, names) ->
            chromatic.zip(names).forEach { (pitchClass, expected) ->
                assertEquals(expected, NoteNamingFormatter.formatPitchClass(pitchClass, language))
                assertEquals("${expected}4", NoteNamingFormatter.formatNote(NoteSelection(pitchClass, 4), language))
                assertEquals(expected, NoteNamingFormatter.formatNote(NoteSelection(pitchClass, 4), language, includeOctave = false))
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `note naming formatter rejects unsupported pitch class`() {
        NoteNamingFormatter.formatPitchClass("Hb", NoteLanguage.ENGLISH)
    }
}
