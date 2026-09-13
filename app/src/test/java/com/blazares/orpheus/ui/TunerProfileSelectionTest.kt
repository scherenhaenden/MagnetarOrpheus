package com.blazares.orpheus.ui

import com.blazares.orpheus.audio.AudioCaptureProvider
import com.blazares.orpheus.audio.PitchDetector
import com.blazares.orpheus.models.InstrumentProfiles
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TunerProfileSelectionTest {
    private fun createViewModel() = TunerViewModel(
        audioCaptureProvider = mockk<AudioCaptureProvider>(relaxed = true),
        pitchDetector = mockk<PitchDetector>(relaxed = true)
    )

    @Test
    fun `default profile identity matches guitar standard`() {
        val state = createViewModel().uiState.value

        assertEquals(InstrumentProfiles.GuitarStandard.id, state.selectedProfileId)
        assertEquals("Guitar", state.selectedInstrument)
        assertEquals("Standard (EADGBE)", state.selectedTuning)
    }

    @Test
    fun `alternate tuning updates identity instrument and tuning together`() {
        val viewModel = createViewModel()

        assertTrue(viewModel.selectInstrumentProfile("guitar_drop_d"))

        val state = viewModel.uiState.value
        assertEquals("guitar_drop_d", state.selectedProfileId)
        assertEquals("Guitar", state.selectedInstrument)
        assertEquals("Drop D (DADGBE)", state.selectedTuning)
    }

    @Test
    fun `every canonical profile can be selected without UI-specific mapping`() {
        val viewModel = createViewModel()

        InstrumentProfiles.All.forEach { profile ->
            assertTrue("Profile ${profile.id} should be selectable", viewModel.selectInstrumentProfile(profile.id))
            val state = viewModel.uiState.value
            assertEquals(profile.id, state.selectedProfileId)
            assertEquals(profile.instrumentName, state.selectedInstrument)
            assertEquals(profile.tuningDisplayName, state.selectedTuning)
        }
    }

    @Test
    fun `unknown profile is rejected without changing current selection`() {
        val viewModel = createViewModel()
        viewModel.selectInstrumentProfile("bass_drop_d")
        val before = viewModel.uiState.value

        assertFalse(viewModel.selectInstrumentProfile("not-a-profile"))
        assertEquals(before, viewModel.uiState.value)
    }

    @Test
    fun `selecting active profile is an idempotent success`() {
        val viewModel = createViewModel()

        assertTrue(viewModel.selectInstrumentProfile("guitar_std"))
        assertEquals("guitar_std", viewModel.uiState.value.selectedProfileId)
    }
}
