package com.edwardflores.magnetar.orpheus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardflores.magnetar.orpheus.audio.AudioCaptureProvider
import com.edwardflores.magnetar.orpheus.audio.PitchDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

data class TunerUiState(
    val frequency: Double = 0.0,
    val noteName: String = "-",
    val cents: Int = 0,
    val isTuned: Boolean = false,
    val isActive: Boolean = false
)

class TunerViewModel(
    private val audioCaptureProvider: AudioCaptureProvider = AudioCaptureProvider(),
    private val pitchDetector: PitchDetector = PitchDetector()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunerUiState())
    val uiState: StateFlow<TunerUiState> = _uiState.asStateFlow()

    private val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    fun startTuning() {
        if (_uiState.value.isActive) return
        
        _uiState.value = _uiState.value.copy(isActive = true)
        
        viewModelScope.launch {
            audioCaptureProvider.startCapture().collect { buffer ->
                val frequency = pitchDetector.estimatePitch(buffer)
                if (frequency > 0) {
                    processFrequency(frequency)
                }
            }
        }
    }

    private fun processFrequency(frequency: Double) {
        // A4 = 440Hz
        val n = 12 * log2(frequency / 440.0) + 69
        val noteIndex = n.roundToInt()
        val noteName = noteNames[noteIndex % 12]
        val octave = (noteIndex / 12) - 1
        val cents = ((n - noteIndex) * 100).toInt()
        
        _uiState.value = _uiState.value.copy(
            frequency = frequency,
            noteName = "$noteName$octave",
            cents = cents,
            isTuned = cents in -5..5
        )
    }
}
