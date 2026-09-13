package com.blazares.orpheus.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blazares.orpheus.R
import com.blazares.orpheus.BuildConfig
import com.blazares.orpheus.audio.AudioCaptureException
import com.blazares.orpheus.audio.AudioCaptureProvider
import com.blazares.orpheus.audio.PitchDetector
import com.blazares.orpheus.audio.TemporalPitchTracker
import com.blazares.orpheus.models.InstrumentProfiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    private val pitchDetector: PitchDetector = PitchDetector(),
    private val temporalPitchTracker: TemporalPitchTracker = TemporalPitchTracker(),
    private val clock: java.time.Clock = java.time.Clock.systemDefaultZone()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunerUiState())
    val uiState: StateFlow<TunerUiState> = _uiState.asStateFlow()

    private val scientificNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val syllabicNotes = listOf("Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si")
    private val germanNotes = listOf("C", "Cis", "D", "Dis", "E", "F", "Fis", "G", "Gis", "A", "Ais", "H")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private var selectedProfile = InstrumentProfiles.GuitarStandard
    private val selectedInstrument: String
        get() = selectedProfile.instrumentName
    private val selectedTuning: String
        get() = selectedProfile.tuningDisplayName
    private var lastProcessedFrequency: Double? = null
    private var tuningJob: Job? = null

    private val waveformSampleCount = 48
    private val historyLimit = 5
    private val stabilityLimit = 24

    fun startTuning() {
        if (_uiState.value.isActive) return

        pitchDetector.reset()
        temporalPitchTracker.reset()
        _uiState.value = _uiState.value.copy(
            isActive = true,
            selectedProfileId = selectedProfile.id,
            selectedInstrument = selectedInstrument,
            selectedTuning = selectedTuning,
            calibrationErrorResId = null,
            captureErrorResId = null
        )

        tuningJob = viewModelScope.launch {
            try {
                audioCaptureProvider.startCapture().collect { buffer ->
                    val result = pitchDetector.analyze(buffer)
                    if (BuildConfig.DEBUG && result.candidateFrequencyHz != null && !result.isPitchValid) {
                        Log.d(
                            "PitchTracker",
                            "Rejected candidate=${result.candidateFrequencyHz}Hz confidence=${result.confidence} " +
                                "rms=${result.rms} floor=${result.noiseFloor} snr=${result.signalToNoiseRatio}"
                        )
                    }
                    val inputLevel = result.rms.toFloat().coerceIn(0f, 1f)
                    val waveformSamples = downSampleWaveform(buffer)

                    _uiState.value = _uiState.value.copy(
                        inputLevel = inputLevel,
                        waveformSamples = waveformSamples
                    )

                    if (result.isPitchValid && result.candidateFrequencyHz != null) {
                        temporalPitchTracker.update(result.candidateFrequencyHz)?.let { stableFrequency ->
                            processFrequency(stableFrequency, inputLevel, waveformSamples)
                        }
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: AudioCaptureException) {
                Log.w("TunerViewModel", "Audio capture failed: ${exception.message}")
                markCaptureFailed()
            } catch (exception: Exception) {
                Log.w("TunerViewModel", "Unexpected audio capture failure: ${exception.message}")
                markCaptureFailed()
            }
        }
    }

    private fun markCaptureFailed() {
        temporalPitchTracker.reset()
        _uiState.value = _uiState.value.copy(
            isActive = false,
            inputLevel = 0f,
            waveformSamples = List(waveformSampleCount) { 0f },
            captureErrorResId = R.string.capture_error_microphone_unavailable
        )
    }

    fun stopTuning() {
        tuningJob?.cancel()
        tuningJob = null
        temporalPitchTracker.reset()
        _uiState.value = _uiState.value.copy(
            isActive = false,
            inputLevel = 0f,
            waveformSamples = List(waveformSampleCount) { 0f }
        )
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
        // Re-label both the chromatic note and the nearest profile target for the new calibration.
        lastProcessedFrequency?.let {
            processFrequency(it, recordHistory = false, recordStability = false)
        }
    }

    fun applyPreset(referenceHz: Int) {
        updateCalibration(referenceHz.toDouble())
    }

    fun selectInstrumentProfile(profileId: String): Boolean {
        val profile = InstrumentProfiles.All.firstOrNull { it.id == profileId } ?: return false
        if (profile == selectedProfile) return true

        selectedProfile = profile
        temporalPitchTracker.reset()
        _uiState.value = _uiState.value.copy(
            selectedProfileId = selectedProfile.id,
            selectedInstrument = selectedInstrument,
            selectedTuning = selectedTuning
        )
        lastProcessedFrequency?.let {
            processFrequency(it, recordHistory = false, recordStability = false)
        }
        return true
    }

    fun updateNamingSystem(system: NoteNamingSystem) {
        _uiState.value = _uiState.value.copy(namingSystem = system)
        lastProcessedFrequency?.let { processFrequency(it, recordHistory = false, recordStability = false) }
    }

    private fun processFrequency(
        frequency: Double,
        inputLevel: Float = _uiState.value.inputLevel,
        waveformSamples: List<Float> = _uiState.value.waveformSamples,
        recordHistory: Boolean = true,
        recordStability: Boolean = true
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
        val scientificNoteName = "${scientificNotes[normalizedIndex]}$octave"
        val profileTarget = selectedProfile.nearestTarget(frequency, refA4)

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
            noteHistory = if (recordHistory) updateNoteHistory(scientificNoteName, frequency, cents) else _uiState.value.noteHistory,
            pitchStabilityPoints = if (recordStability) updatePitchStability(cents) else _uiState.value.pitchStabilityPoints,
            selectedProfileId = selectedProfile.id,
            selectedInstrument = selectedInstrument,
            selectedTuning = selectedTuning,
            profileTargetNote = profileTarget?.note?.name,
            profileTargetStringNumber = profileTarget?.note?.stringNumber,
            profileTargetFrequencyHz = profileTarget?.calibratedFrequencyHz,
            profileTargetCents = profileTarget?.centsFromTarget,
            calibrationErrorResId = null
        )
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
                var maxAbs = 0f
                for (sampleIndex in start until end) {
                    maxAbs = max(maxAbs, abs(buffer[sampleIndex]))
                }
                maxAbs.coerceIn(0f, 1f)
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
            timeLabel = LocalTime.now(clock).format(timeFormatter)
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

    override fun onCleared() {
        stopTuning()
        super.onCleared()
    }
}
