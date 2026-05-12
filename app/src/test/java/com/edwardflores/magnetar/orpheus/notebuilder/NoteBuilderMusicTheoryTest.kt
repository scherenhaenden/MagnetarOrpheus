package com.edwardflores.magnetar.orpheus.notebuilder

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
}
