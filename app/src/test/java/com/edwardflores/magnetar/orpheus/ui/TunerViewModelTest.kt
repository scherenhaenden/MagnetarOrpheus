package com.edwardflores.magnetar.orpheus.ui

import android.util.Log
import com.edwardflores.magnetar.orpheus.R
import com.edwardflores.magnetar.orpheus.audio.AudioCaptureProvider
import com.edwardflores.magnetar.orpheus.audio.PitchDetector
import com.edwardflores.magnetar.orpheus.audio.PitchResult
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
        every { pitchDetector.reset() } returns Unit
        viewModel = TunerViewModel(audioCaptureProvider, pitchDetector)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockPitchResult(
        freq: Double?,
        isValid: Boolean,
        rms: Double = 0.5,
        confidence: Double = 0.95
    ): PitchResult {
        return PitchResult(
            candidateFrequencyHz = freq,
            confidence = confidence,
            rms = rms,
            noiseFloor = 0.01,
            signalToNoiseRatio = 50.0,
            isPitchValid = isValid
        )
    }

    @Test
    fun `startTuning updates state and processes valid pitch result`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, true)

        viewModel.startTuning()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isActive)
        assertEquals(440.0, state.frequency, 0.01)
        assertEquals("A4", state.noteName)
        assertTrue(state.isTuned)
        verify { pitchDetector.reset() }
    }

    @Test
    fun `invalid pitch due to low confidence does not update target note frequency`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, false, confidence = 0.3)

        viewModel.startTuning()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isActive)
        assertEquals(0.0, state.frequency, 0.0) // initial frequency remains unchanged
    }

    @Test
    fun `invalid pitch due to insufficient signal RMS does not update frequency`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, false, rms = 0.0001)

        viewModel.startTuning()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isActive)
        assertEquals(0.0, state.frequency, 0.0)
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
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, true)

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
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(493.88, true)

        viewModel.startTuning()
        viewModel.updateNamingSystem(NoteNamingSystem.GERMAN)
        advanceUntilIdle()

        assertEquals("H4", viewModel.uiState.value.noteName)
    }

    @Test
    fun `startTuning does nothing if already active`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, true)

        viewModel.startTuning()
        viewModel.startTuning() // Second call
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isActive)
        verify(exactly = 1) { audioCaptureProvider.startCapture() }
    }

    @Test
    fun `stability filter averages valid frequencies`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer, buffer, buffer, buffer)
        every { pitchDetector.analyze(buffer) } returnsMany listOf(
            mockPitchResult(440.0, true),
            mockPitchResult(442.0, true),
            mockPitchResult(444.0, true),
            mockPitchResult(446.0, true)
        )

        viewModel.startTuning()
        advanceUntilIdle()

        // Average of last 3: (442 + 444 + 446) / 3 = 444.0
        assertEquals(444.0, viewModel.uiState.value.frequency, 0.01)
    }

    @Test
    fun `updateCalibration without last frequency does not crash`() {
        viewModel.updateCalibration(432.0)
        assertEquals(432.0, viewModel.uiState.value.referenceA4, 0.0)
    }

    @Test
    fun `applyPreset updates reference pitch`() {
        viewModel.applyPreset(442)
        assertEquals(442.0, viewModel.uiState.value.referenceA4, 0.0)
    }

    @Test
    fun `updateCalibration with invalid value surfaces validation error`() {
        viewModel.updateCalibration(0.0)

        assertEquals(440.0, viewModel.uiState.value.referenceA4, 0.0)
        assertEquals(R.string.calibration_error_positive_hz, viewModel.uiState.value.calibrationErrorResId)
    }

    @Test
    fun `valid calibration clears previous validation error`() {
        viewModel.updateCalibration(0.0)
        viewModel.updateCalibration(441.0)

        assertEquals(441.0, viewModel.uiState.value.referenceA4, 0.0)
        assertEquals(null, viewModel.uiState.value.calibrationErrorResId)
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
        assertEquals(10, state.cents)
    }

    @Test
    fun `test all note naming systems`() = runTest {
        val buffer = floatArrayOf(0f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, true)

        viewModel.startTuning()
        advanceUntilIdle()

        NoteNamingSystem.entries.forEach { system ->
            viewModel.updateNamingSystem(system)
            assertTrue(viewModel.uiState.value.noteName.isNotEmpty())
        }
    }

    @Test
    fun `startTuning updates waveform input level history and labels`() = runTest {
        val buffer = floatArrayOf(0.5f, -0.5f, 0.5f, -0.5f, 0.25f, -0.25f)
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer)
        every { pitchDetector.analyze(buffer) } returns mockPitchResult(440.0, true, rms = 0.5)

        viewModel.startTuning()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.inputLevel > 0f)
        assertTrue(state.waveformSamples.isNotEmpty())
        assertEquals("A", state.noteLabel)
        assertEquals("A", state.chromaticNote)
        assertEquals("A4", state.noteHistory.first().note)
        assertTrue(state.pitchStabilityPoints.isNotEmpty())
        assertEquals("Guitar", state.selectedInstrument)
    }

    @Test
    fun `tuner state handles empty input and keeps bounded history`() = runTest {
        val buffer = floatArrayOf()
        every { audioCaptureProvider.startCapture() } returns flowOf(buffer, buffer, buffer, buffer, buffer, buffer)
        every { pitchDetector.estimatePitch(buffer) } returnsMany listOf(220.0, 246.94, 261.63, 293.66, 329.63, 349.23)

        viewModel.startTuning()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(48, state.waveformSamples.size)
        assertEquals(0f, state.inputLevel)
        assertEquals(5, state.noteHistory.size)
        assertEquals(24, state.pitchStabilityPoints.size)
    }
}
