package com.blazares.orpheus.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TunerInstrumentProfileTest {

    @Test
    fun `profile selection updates instrument and tuning labels`() {
        val viewModel = TunerViewModel()

        assertTrue(viewModel.selectInstrumentProfile("bass_std"))
        assertEquals("Bass", viewModel.uiState.value.selectedInstrument)
        assertEquals("Standard (EADG)", viewModel.uiState.value.selectedTuning)

        assertTrue(viewModel.selectInstrumentProfile("ukulele_std"))
        assertEquals("Ukulele", viewModel.uiState.value.selectedInstrument)
        assertEquals("Soprano (GCEA)", viewModel.uiState.value.selectedTuning)
    }

    @Test
    fun `unknown profile is rejected without changing selection`() {
        val viewModel = TunerViewModel()
        val before = viewModel.uiState.value

        assertFalse(viewModel.selectInstrumentProfile("does_not_exist"))
        assertEquals(before.selectedInstrument, viewModel.uiState.value.selectedInstrument)
        assertEquals(before.selectedTuning, viewModel.uiState.value.selectedTuning)
    }
}
