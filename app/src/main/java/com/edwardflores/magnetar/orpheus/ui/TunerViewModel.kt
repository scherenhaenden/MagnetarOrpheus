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

enum class NoteNamingSystem {
    SCIENTIFIC, // C, D, E, F, G, A, B
    SYLLABIC,   // Do, Re, Mi, Fa, Sol, La, Si (Italian/French)
    GERMAN      // C, D, E, F, G, A, H
}

data class TunerUiState(
    val frequency: Double = 0.0,
    val noteName: String = "-",
    val cents: Int = 0,
    val isTuned: Boolean = false,
    val isActive: Boolean = false,
    val referenceA4: Double = 440.0,
    val namingSystem: NoteNamingSystem = NoteNamingSystem.SCIENTIFIC
)

class TunerViewModel(
    private val audioCaptureProvider: AudioCaptureProvider = AudioCaptureProvider(),
    private val pitchDetector: PitchDetector = PitchDetector()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunerUiState())
    val uiState: StateFlow<TunerUiState> = _uiState.asStateFlow()

    private val scientificNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val syllabicNotes = listOf("Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si")
    private val germanNotes = listOf("C", "Cis", "D", "Dis", "E", "F", "Fis", "G", "Gis", "A", "Ais", "H")
    
    // Simple temporal filter: sliding window or EMA
    private var lastFrequencies = mutableListOf<Double>()
    private val windowSize = 3

    fun startTuning() {
        if (_uiState.value.isActive) return
        
        _uiState.value = _uiState.value.copy(isActive = true)
        
        viewModelScope.launch {
            audioCaptureProvider.startCapture().collect { buffer ->
                val frequency = pitchDetector.estimatePitch(buffer)
                if (frequency > 0) {
                    val stableFreq = updateStabilityFilter(frequency)
                    processFrequency(stableFreq)
                }
            }
        }
    }

    private var lastProcessedFrequency: Double? = null

    fun updateCalibration(ref: Double) {
        if (ref <= 0) return
        _uiState.value = _uiState.value.copy(referenceA4 = ref)
        lastProcessedFrequency?.let { processFrequency(it) }
    }

    fun updateNamingSystem(system: NoteNamingSystem) {
        _uiState.value = _uiState.value.copy(namingSystem = system)
        lastProcessedFrequency?.let { processFrequency(it) }
    }

    private fun updateStabilityFilter(freq: Double): Double {
        lastFrequencies.add(freq)
        if (lastFrequencies.size > windowSize) {
            lastFrequencies.removeAt(0)
        }
        return lastFrequencies.average()
    }

    private fun processFrequency(frequency: Double) {
        if (frequency <= 0) return
        lastProcessedFrequency = frequency
        val refA4 = _uiState.value.referenceA4
        // Calculate note based on reference A4
        val n = 12 * log2(frequency / refA4) + 69
        val noteIndex = n.roundToInt()
        
        val notes = when (_uiState.value.namingSystem) {
            NoteNamingSystem.SCIENTIFIC -> scientificNotes
            NoteNamingSystem.SYLLABIC -> syllabicNotes
            NoteNamingSystem.GERMAN -> germanNotes
        }
        
        val noteName = notes[(noteIndex % 12 + 12) % 12]
        val octave = (noteIndex / 12) - 1
        val cents = ((n - noteIndex) * 100).toInt()
        
        _uiState.value = _uiState.value.copy(
            frequency = frequency,
            noteName = if (_uiState.value.namingSystem == NoteNamingSystem.SYLLABIC) noteName else "$noteName$octave",
            cents = cents,
            isTuned = cents in -5..5
        )
    }
}
