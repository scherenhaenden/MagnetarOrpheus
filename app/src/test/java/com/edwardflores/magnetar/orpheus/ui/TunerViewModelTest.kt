package com.edwardflores.magnetar.orpheus.ui

import android.util.Log
import com.edwardflores.magnetar.orpheus.audio.AudioCaptureProvider
import com.edwardflores.magnetar.orpheus.audio.PitchDetector
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TunerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val audioCaptureProvider = mockk<AudioCaptureProvider>()
    private val pitchDetector = mockk<PitchDetector>()
    private lateinit var viewModel: TunerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
        viewModel = TunerViewModel(audioCaptureProvider, pitchDetector)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startTuning updates state and processes frequency`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.estimatePitch(buffer) } returns 440.0

        viewModel.startTuning()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isActive)
        assertEquals(440.0, state.frequency, 0.01)
        assertEquals("A4", state.noteName)
        assertTrue(state.isTuned)
    }

    @Test
    fun `updateCalibration updates referenceA4`() {
        viewModel.updateCalibration(432.0)
        assertEquals(432.0, viewModel.uiState.value.referenceA4, 0.0)
    }

    @Test
    fun `updateNamingSystem updates naming system and recalculates note`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.estimatePitch(buffer) } returns 440.0

        viewModel.startTuning()
        advanceUntilIdle()

        viewModel.updateNamingSystem(NoteNamingSystem.SYLLABIC)
        assertEquals("La4", viewModel.uiState.value.noteName)

        viewModel.updateNamingSystem(NoteNamingSystem.GERMAN)
        assertEquals("A4", viewModel.uiState.value.noteName)
    }

    @Test
    fun `processFrequency handle German system H note`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.estimatePitch(buffer) } returns 493.88 // B4/H4

        viewModel.startTuning()
        viewModel.updateNamingSystem(NoteNamingSystem.GERMAN)
        advanceUntilIdle()

        assertEquals("H4", viewModel.uiState.value.noteName)
    }

    @Test
    fun `startTuning does nothing if already active`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.estimatePitch(buffer) } returns 440.0
        
        viewModel.startTuning()
        viewModel.startTuning() // Second call
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.isActive)
        verify(exactly = 1) { audioCaptureProvider.startCapture() }
    }

    @Test
    fun `stability filter averages frequencies`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer, buffer, buffer, buffer)
        every { pitchDetector.estimatePitch(buffer) } returnsMany listOf(440.0, 442.0, 444.0, 446.0)

        viewModel.startTuning()
        advanceUntilIdle()

        // Average of last 3: (442 + 444 + 446) / 3 = 444.0
        assertEquals(444.0, viewModel.uiState.value.frequency, 0.01)
    }

    @Test
    fun `processFrequency with zero, negative, or non-finite frequency does nothing`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer, buffer, buffer, buffer)
        every { pitchDetector.estimatePitch(buffer) } returnsMany listOf(0.0, -10.0, Double.NaN, Double.POSITIVE_INFINITY)

        viewModel.startTuning()
        advanceUntilIdle()

        assertEquals(0.0, viewModel.uiState.value.frequency, 0.0)
    }

    @Test
    fun `updateCalibration without last frequency does not crash`() {
        viewModel.updateCalibration(432.0)
        assertEquals(432.0, viewModel.uiState.value.referenceA4, 0.0)
    }

    @Test
    fun `updateCalibration with invalid value surfaces validation error`() {
        viewModel.updateCalibration(0.0)

        assertEquals(440.0, viewModel.uiState.value.referenceA4, 0.0)
        assertEquals("Calibration must be greater than 0 Hz.", viewModel.uiState.value.calibrationError)
    }

    @Test
    fun `TunerUiState data class methods`() {
        val state = TunerUiState(cents = 10)
        val state2 = state.copy(frequency = 440.0)
        assertFalse(state == state2)
        assertEquals(440.0, state2.frequency, 0.0)
        assertEquals(state.hashCode(), state.hashCode())
        assertTrue(state.toString().contains("TunerUiState"))
        assertEquals(0.0, state.component1(), 0.0)
        assertEquals(10, state.cents) // Cover getter
    }

    @Test
    fun `test all note naming systems`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.estimatePitch(buffer) } returns 440.0

        viewModel.startTuning()
        advanceUntilIdle()

        NoteNamingSystem.entries.forEach { system ->
            viewModel.updateNamingSystem(system)
            assertTrue(viewModel.uiState.value.noteName.isNotEmpty())
        }
    }
}
