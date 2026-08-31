package com.edwardflores.magnetar.orpheus.ui.notebuilder

import android.util.Log
import com.edwardflores.magnetar.orpheus.notebuilder.audio.NotePlaybackEngine
import com.edwardflores.magnetar.orpheus.ui.NoteLanguage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class NoteBuilderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val playbackEngine = mockk<NotePlaybackEngine>(relaxed = true)
    private lateinit var viewModel: NoteBuilderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>(), any()) } returns 0
        viewModel = NoteBuilderViewModel(playbackEngine, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `default state starts with seeded selection`() {
        val state = viewModel.uiState.value

        assertEquals(4, state.selectedNotes.size)
        assertEquals("Cmaj7", state.detectedPrimaryName)
        assertEquals("Major", state.quality)
        assertFalse(state.isPlaying)
    }

    @Test
    fun `updateInputMode changes active input mode`() {
        viewModel.updateInputMode(NoteBuilderInputMode.GRID)

        assertEquals(NoteBuilderInputMode.GRID, viewModel.uiState.value.inputMode)
    }

    @Test
    fun `toggleHold flips hold state`() {
        assertFalse(viewModel.uiState.value.holdEnabled)

        viewModel.toggleHold()
        assertTrue(viewModel.uiState.value.holdEnabled)

        viewModel.toggleHold()
        assertFalse(viewModel.uiState.value.holdEnabled)
    }

    @Test
    fun `updateNoteLanguage reanalyzes current selection`() {
        viewModel.updateNoteLanguage(NoteLanguage.SPANISH)

        val state = viewModel.uiState.value
        assertEquals(NoteLanguage.SPANISH, state.noteLanguage)
        assertEquals("Domaj7", state.detectedPrimaryName)
        assertEquals("Mayor", state.quality)
        assertTrue(state.detectedSecondaryName.contains("Do4"))
    }

    @Test
    fun `toggleNote removes existing note and updates analysis`() {
        viewModel.toggleNote(NoteSelection("B", 4))

        val state = viewModel.uiState.value
        assertEquals(listOf("C4", "E4", "G4"), state.selectedNotes.map { it.displayName })
        assertEquals("C", state.detectedPrimaryName)
        assertEquals("Major", state.quality)
    }

    @Test
    fun `toggleNote adds new note in sorted order`() {
        viewModel.clearSelection()
        viewModel.toggleNote(NoteSelection("G", 4))
        viewModel.toggleNote(NoteSelection("C", 4))
        viewModel.toggleNote(NoteSelection("E", 4))

        assertEquals(listOf("C4", "E4", "G4"), viewModel.uiState.value.selectedNotes.map { it.displayName })
    }

    @Test
    fun `clearSelection removes notes and resets analysis`() {
        viewModel.clearSelection()

        val state = viewModel.uiState.value
        assertTrue(state.selectedNotes.isEmpty())
        assertEquals("No note set selected", state.detectedPrimaryName)
        assertEquals("None", state.quality)
    }

    @Test
    fun `clearSelection preserves current mode and hold state`() {
        viewModel.updateInputMode(NoteBuilderInputMode.GRID)
        viewModel.toggleHold()

        viewModel.clearSelection()

        val state = viewModel.uiState.value
        assertEquals(NoteBuilderInputMode.GRID, state.inputMode)
        assertTrue(state.holdEnabled)
        assertTrue(state.selectedNotes.isEmpty())
    }

    @Test
    fun `playSelection invokes playback engine`() = runTest(testDispatcher) {
        coEvery { playbackEngine.playSelection(any(), any()) } returns Unit

        viewModel.playSelection()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            playbackEngine.playSelection(
                selectedNotes = match { it.size == 4 },
                holdEnabled = false
            )
        }
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `playSelection with hold enabled passes hold flag`() = runTest(testDispatcher) {
        coEvery { playbackEngine.playSelection(any(), any()) } returns Unit

        viewModel.toggleHold()
        viewModel.playSelection()
        advanceUntilIdle()

        coVerify(exactly = 1) { playbackEngine.playSelection(any(), true) }
    }

    @Test
    fun `playSelection does nothing when no notes selected`() = runTest(testDispatcher) {
        viewModel.clearSelection()

        viewModel.playSelection()
        advanceUntilIdle()

        coVerify(exactly = 0) { playbackEngine.playSelection(any(), any()) }
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `playSelection surfaces playback errors`() = runTest(testDispatcher) {
        coEvery { playbackEngine.playSelection(any(), any()) } throws IllegalStateException("boom")

        viewModel.playSelection()
        advanceUntilIdle()

        assertEquals("Playback unavailable on this device.", viewModel.uiState.value.playbackError)
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `successful playback clears previous playback errors`() = runTest(testDispatcher) {
        coEvery { playbackEngine.playSelection(any(), any()) } throws IllegalStateException("boom") andThen Unit

        viewModel.playSelection()
        advanceUntilIdle()
        assertEquals("Playback unavailable on this device.", viewModel.uiState.value.playbackError)

        viewModel.playSelection()
        advanceUntilIdle()
        assertEquals(null, viewModel.uiState.value.playbackError)
    }

    @Test
    fun `stopPlayback stops engine and updates state`() = runTest(testDispatcher) {
        coEvery { playbackEngine.playSelection(any(), any()) } coAnswers { kotlinx.coroutines.awaitCancellation() }

        viewModel.playSelection()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isPlaying)

        viewModel.stopPlayback()
        advanceUntilIdle()

        verify(atLeast = 1) { playbackEngine.stop() }
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `changing notes while playing retriggers playback`() = runTest(testDispatcher) {
        var callCount = 0
        coEvery { playbackEngine.playSelection(any(), any()) } coAnswers {
            callCount += 1
            if (callCount == 1) {
                kotlinx.coroutines.awaitCancellation()
            } else {
                Unit
            }
        }

        viewModel.playSelection()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isPlaying)

        viewModel.toggleNote(NoteSelection("D", 5))
        advanceUntilIdle()

        coVerify(exactly = 2) { playbackEngine.playSelection(any(), any()) }
    }
}
