package com.edwardflores.magnetar.orpheus.ui

import com.edwardflores.magnetar.orpheus.ui.notebuilder.NoteBuilderUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppModelsTest {

    @Test
    fun `app destinations expose menu metadata`() {
        assertEquals(2, AppDestination.entries.size)
        assertEquals("Tuner", AppDestination.TUNER.title)
        assertEquals("Keyboard and grid workspace", AppDestination.NOTE_BUILDER.subtitle)
    }

    @Test
    fun `tuner ui state helper text is formatted`() {
        val state = TunerUiState(
            frequency = 440.12,
            cents = -3,
            noteHistory = listOf(NoteHistoryItem("A", "A4", 440.0, 2, "10:00:00")),
            quickPresets = listOf(QuickPreset("Standard", 440))
        )

        assertEquals("440.1 Hz", state.frequencyText)
        assertEquals("-3 cents", state.centsText)
        assertEquals("A4", state.noteHistory.first().note)
        assertEquals(440, state.quickPresets.first().referenceHz)
    }

    @Test
    fun `note history and preset models behave like data classes`() {
        val historyItem = NoteHistoryItem("A", "A4", 440.0, 2, "10:00:00")
        val historyCopy = historyItem.copy(cents = 1)
        val preset = QuickPreset("Concert", 442)
        val presetCopy = preset.copy(name = "Orchestral")

        assertEquals("A", historyItem.component1())
        assertEquals("A4", historyItem.component2())
        assertEquals(440.0, historyItem.component3(), 0.0)
        assertEquals(1, historyCopy.cents)
        assertEquals("Concert", preset.component1())
        assertEquals(442, preset.component2())
        assertEquals("Orchestral", presetCopy.name)
    }

    @Test
    fun `tuner ui state data class operations remain stable`() {
        val state = TunerUiState(cents = 10, noteLabel = "A")
        val updated = state.copy(frequency = 220.0)

        assertFalse(state == updated)
        assertEquals(220.0, updated.frequency, 0.0)
        assertTrue(updated.toString().contains("TunerUiState"))
        assertEquals("A", state.noteLabel)
    }

    @Test
    fun `note builder ui state supports playback flags`() {
        val state = NoteBuilderUiState(isPlaying = true, playbackError = "err")
        val updated = state.copy(isPlaying = false, playbackError = null)

        assertTrue(state.isPlaying)
        assertEquals("err", state.playbackError)
        assertFalse(updated.isPlaying)
        assertEquals(null, updated.playbackError)
    }

    @Test
    fun `note naming system display names are readable`() {
        assertEquals("Scientific", NoteNamingSystem.SCIENTIFIC.displayName)
        assertEquals("Syllabic", NoteNamingSystem.SYLLABIC.displayName)
        assertEquals("German", NoteNamingSystem.GERMAN.displayName)
    }

    @Test
    fun `app and note language models expose localized metadata`() {
        val finnish = appStrings(AppLanguage.FINNISH)

        assertEquals("Asetukset", finnish.settings)
        assertEquals("Nuottien kieli", finnish.noteLanguage)
        assertEquals(NoteNamingSystem.SYLLABIC, NoteLanguage.SPANISH.toNamingSystem())
        assertEquals(NoteNamingSystem.GERMAN, NoteLanguage.GERMAN.toNamingSystem())
    }

    @Test
    fun `all app languages provide complete strings and localized destinations`() {
        AppLanguage.entries.forEach { language ->
            val strings = appStrings(language)
            val labels = listOf(
                strings.settings,
                strings.appLanguage,
                strings.noteLanguage,
                strings.close,
                strings.tunerTitle,
                strings.tunerSubtitle,
                strings.noteBuilderTitle,
                strings.noteBuilderSubtitle,
                strings.noteBuilderFeatureLabel,
                strings.keyboard,
                strings.grid,
                strings.selectedNotes,
                strings.noNotesSelected,
                strings.selectNotesPrompt,
                strings.play,
                strings.replay,
                strings.stop,
                strings.clear,
                strings.hold,
                strings.aboutThisSelection,
                strings.quality
            )

            assertTrue("$language has an empty translation", labels.all { it.isNotBlank() })
            assertEquals(strings.tunerTitle, AppDestination.TUNER.localizedTitle(strings))
            assertEquals(strings.tunerSubtitle, AppDestination.TUNER.localizedSubtitle(strings))
            assertEquals(strings.noteBuilderTitle, AppDestination.NOTE_BUILDER.localizedTitle(strings))
            assertEquals(strings.noteBuilderSubtitle, AppDestination.NOTE_BUILDER.localizedSubtitle(strings))
        }

        assertEquals(NoteNamingSystem.SCIENTIFIC, NoteLanguage.ENGLISH.toNamingSystem())
    }
}
