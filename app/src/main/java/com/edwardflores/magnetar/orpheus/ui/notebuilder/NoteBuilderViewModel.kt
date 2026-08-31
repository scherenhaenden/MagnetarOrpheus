package com.edwardflores.magnetar.orpheus.ui.notebuilder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardflores.magnetar.orpheus.notebuilder.NoteBuilderMusicTheory
import com.edwardflores.magnetar.orpheus.notebuilder.audio.NotePlaybackEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.edwardflores.magnetar.orpheus.ui.NoteLanguage

class NoteBuilderViewModel(
    private val playbackEngine: NotePlaybackEngine = NotePlaybackEngine(),
    private val playbackDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val _uiState = MutableStateFlow(defaultNoteBuilderState())
    val uiState: StateFlow<NoteBuilderUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    @Volatile
    private var playbackGeneration: Long = 0

    fun updateInputMode(mode: NoteBuilderInputMode) {
        _uiState.update { it.copy(inputMode = mode) }
    }

    fun toggleHold() {
        _uiState.update { it.copy(holdEnabled = !it.holdEnabled) }
    }

    fun updateNoteLanguage(noteLanguage: NoteLanguage) {
        val current = _uiState.value
        val analysis = NoteBuilderMusicTheory.analyzeSelection(current.selectedNotes, noteLanguage)
        _uiState.update {
            it.copy(
                noteLanguage = noteLanguage,
                detectedPrimaryName = analysis.title,
                detectedSecondaryName = analysis.subtitle,
                quality = analysis.quality
            )
        }
    }

    fun toggleNote(note: NoteSelection) {
        val current = _uiState.value
        val updatedNotes = if (current.selectedNotes.contains(note)) {
            current.selectedNotes - note
        } else {
            (current.selectedNotes + note)
                .distinct()
                .sortedWith(compareBy<NoteSelection> { it.octave }.thenBy { CHROMATIC_NOTES.indexOf(it.pitchClass) })
        }

        val analysis = NoteBuilderMusicTheory.analyzeSelection(updatedNotes, current.noteLanguage)
        _uiState.update {
            it.copy(
                selectedNotes = updatedNotes,
                detectedPrimaryName = analysis.title,
                detectedSecondaryName = analysis.subtitle,
                quality = analysis.quality,
                playbackError = null
            )
        }

        if (current.isPlaying) {
            playSelection()
        }
    }

    fun clearSelection() {
        stopPlayback()
        val cleared = emptyNoteBuilderState().copy(
            inputMode = _uiState.value.inputMode,
            holdEnabled = _uiState.value.holdEnabled
        )
        _uiState.value = cleared
    }

    fun playSelection() {
        val snapshot = _uiState.value
        if (snapshot.selectedNotes.isEmpty()) return

        stopPlayback(updateState = false)
        playbackGeneration += 1
        val generation = playbackGeneration
        _uiState.update { it.copy(isPlaying = true, playbackError = null) }

        playbackJob = viewModelScope.launch(playbackDispatcher) {
            try {
                playbackEngine.playSelection(
                    selectedNotes = snapshot.selectedNotes,
                    holdEnabled = snapshot.holdEnabled
                )
            } catch (exception: Exception) {
                Log.e("NoteBuilderViewModel", "Playback failed", exception)
                _uiState.update {
                    if (generation == playbackGeneration) {
                        it.copy(playbackError = "Playback unavailable on this device.", isPlaying = false)
                    } else {
                        it
                    }
                }
            } finally {
                _uiState.update {
                    if (generation == playbackGeneration) {
                        it.copy(isPlaying = false)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun stopPlayback(updateState: Boolean = true) {
        playbackGeneration += 1
        playbackJob?.cancel()
        playbackJob = null
        playbackEngine.stop()
        if (updateState) {
            _uiState.update { it.copy(isPlaying = false) }
        }
    }

    override fun onCleared() {
        stopPlayback()
        super.onCleared()
    }
}

private val CHROMATIC_NOTES = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

private fun defaultNoteBuilderState(): NoteBuilderUiState {
    val selectedNotes = listOf(
        NoteSelection("C", 4),
        NoteSelection("E", 4),
        NoteSelection("G", 4),
        NoteSelection("B", 4)
    )
    val noteLanguage = NoteLanguage.ENGLISH
    val analysis = NoteBuilderMusicTheory.analyzeSelection(selectedNotes, noteLanguage)
    return NoteBuilderUiState(
        inputMode = NoteBuilderInputMode.KEYBOARD,
        noteLanguage = noteLanguage,
        selectedNotes = selectedNotes,
        detectedPrimaryName = analysis.title,
        detectedSecondaryName = analysis.subtitle,
        quality = analysis.quality
    )
}

private fun emptyNoteBuilderState(): NoteBuilderUiState {
    val noteLanguage = NoteLanguage.ENGLISH
    val analysis = NoteBuilderMusicTheory.analyzeSelection(emptyList(), noteLanguage)
    return NoteBuilderUiState(
        inputMode = NoteBuilderInputMode.KEYBOARD,
        noteLanguage = noteLanguage,
        selectedNotes = emptyList(),
        detectedPrimaryName = analysis.title,
        detectedSecondaryName = analysis.subtitle,
        quality = analysis.quality
    )
}
