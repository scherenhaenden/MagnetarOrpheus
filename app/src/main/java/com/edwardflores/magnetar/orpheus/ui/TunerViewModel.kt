package com.edwardflores.magnetar.orpheus.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardflores.magnetar.orpheus.R
import com.edwardflores.magnetar.orpheus.audio.AudioCaptureProvider
import com.edwardflores.magnetar.orpheus.audio.PitchDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class TunerViewModel(
    private val audioCaptureProvider: AudioCaptureProvider = AudioCaptureProvider(),
    private val pitchDetector: PitchDetector = PitchDetector()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunerUiState())
    val uiState: StateFlow<TunerUiState> = _uiState.asStateFlow()

    private val scientificNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val syllabicNotes = listOf("Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si")
    private val germanNotes = listOf("C", "Cis", "D", "Dis", "E", "F", "Fis", "G", "Gis", "A", "Ais", "H")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    private var lastFrequencies = mutableListOf<Double>()
    private var lastProcessedFrequency: Double? = null

    private val windowSize = 3
    private val waveformSampleCount = 48
    private val historyLimit = 5
    private val stabilityLimit = 24

    fun startTuning() {
        if (_uiState.value.isActive) return

        _uiState.value = _uiState.value.copy(
            isActive = true,
            selectedInstrument = "Guitar",
            selectedTuning = "Standard (EADGBE)"
        )

        viewModelScope.launch {
            audioCaptureProvider.startCapture().collect { buffer ->
                val inputLevel = calculateInputLevel(buffer)
                val waveformSamples = downSampleWaveform(buffer)
                _uiState.value = _uiState.value.copy(
                    inputLevel = inputLevel,
                    waveformSamples = waveformSamples
                )

                val frequency = pitchDetector.estimatePitch(buffer)
                if (frequency > 0 && frequency.isFinite()) {
                    val stableFreq = updateStabilityFilter(frequency)
                    processFrequency(stableFreq, inputLevel, waveformSamples)
                }
            }
        }
    }

    fun updateCalibration(ref: Double) {
        if (ref <= 0 || !ref.isFinite()) {
            Log.w("TunerViewModel", "Invalid calibration value ignored: $ref")
            _uiState.value = _uiState.value.copy(
                calibrationErrorResId = R.string.calibration_error_positive_hz
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            referenceA4 = ref,
            calibrationErrorResId = null
        )
        lastProcessedFrequency?.let { processFrequency(it) }
    }

    fun applyPreset(referenceHz: Int) {
        updateCalibration(referenceHz.toDouble())
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

    private fun processFrequency(
        frequency: Double,
        inputLevel: Float = _uiState.value.inputLevel,
        waveformSamples: List<Float> = _uiState.value.waveformSamples
    ) {
        if (frequency <= 0 || !frequency.isFinite()) {
            Log.w("TunerViewModel", "Invalid frequency ignored: $frequency")
            return
        }

        lastProcessedFrequency = frequency
        val refA4 = _uiState.value.referenceA4
        val n = 12 * log2(frequency / refA4) + 69
        val noteIndex = n.roundToInt()

        val notes = when (_uiState.value.namingSystem) {
            NoteNamingSystem.SCIENTIFIC -> scientificNotes
            NoteNamingSystem.SYLLABIC -> syllabicNotes
            NoteNamingSystem.GERMAN -> germanNotes
        }

        val normalizedIndex = (noteIndex % 12 + 12) % 12
        val noteLabel = notes[normalizedIndex]
        val chromaticNote = scientificNotes[normalizedIndex]
        val octave = (noteIndex / 12) - 1
        val cents = ((n - noteIndex) * 100).toInt()

        _uiState.value = _uiState.value.copy(
            frequency = frequency,
            noteName = "$noteLabel$octave",
            noteLabel = noteLabel,
            chromaticNote = chromaticNote,
            octave = octave,
            cents = cents,
            isTuned = cents in -5..5,
            inputLevel = inputLevel,
            waveformSamples = waveformSamples,
            noteHistory = updateNoteHistory("${scientificNotes[normalizedIndex]}$octave", frequency, cents),
            pitchStabilityPoints = updatePitchStability(cents),
            calibrationErrorResId = null
        )
    }

    private fun calculateInputLevel(buffer: FloatArray): Float {
        if (buffer.isEmpty()) return 0f
        val rms = kotlin.math.sqrt(buffer.fold(0.0) { total, sample ->
            total + sample * sample
        } / buffer.size).toFloat()
        return rms.coerceIn(0f, 1f)
    }

    private fun downSampleWaveform(buffer: FloatArray): List<Float> {
        if (buffer.isEmpty()) return List(waveformSampleCount) { 0f }

        val chunkSize = max(1, buffer.size / waveformSampleCount)
        return List(waveformSampleCount) { index ->
            val start = index * chunkSize
            val end = min(buffer.size, start + chunkSize)
            if (start >= buffer.size || start == end) {
                0f
            } else {
                var sum = 0f
                for (sampleIndex in start until end) {
                    sum += buffer[sampleIndex]
                }
                (sum / (end - start)).coerceIn(-1f, 1f)
            }
        }
    }

    private fun updateNoteHistory(note: String, frequency: Double, cents: Int): List<NoteHistoryItem> {
        val currentHistory = _uiState.value.noteHistory.toMutableList()
        val newEntry = NoteHistoryItem(
            badgeLabel = note.firstOrNull()?.toString() ?: "-",
            note = note,
            frequencyHz = frequency,
            cents = cents,
            timeLabel = LocalTime.now().format(timeFormatter)
        )

        if (currentHistory.isNotEmpty() &&
            currentHistory.first().note == note &&
            abs(currentHistory.first().cents - cents) < 2
        ) {
            currentHistory[0] = newEntry
        } else {
            currentHistory.add(0, newEntry)
        }

        return currentHistory.take(historyLimit)
    }

    private fun updatePitchStability(cents: Int): List<Float> {
        val points = _uiState.value.pitchStabilityPoints.toMutableList()
        points.add(cents.toFloat())
        while (points.size > stabilityLimit) {
            points.removeAt(0)
        }
        return points
    }
}
