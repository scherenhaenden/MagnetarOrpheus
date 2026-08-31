package com.edwardflores.magnetar.orpheus.audio

/**
 * Result of pitch detection analysis for an audio buffer.
 *
 * @property candidateFrequencyHz Candidate frequency estimated in Hz, or null if no valid tau was found.
 * @property confidence Pitch periodicity confidence in range [0.0, 1.0], derived from YIN CMNDF quality.
 * @property rms Root Mean Square energy/amplitude of the analyzed audio frame.
 * @property noiseFloor Estimated ambient noise floor level (RMS).
 * @property signalToNoiseRatio Ratio of signal energy to noise floor (rms / noiseFloor).
 * @property isPitchValid True if frequency is valid, signal is above noise floor threshold, and confidence meets minimum criteria.
 */
data class PitchResult(
    val candidateFrequencyHz: Double?,
    val confidence: Double,
    val rms: Double,
    val noiseFloor: Double,
    val signalToNoiseRatio: Double,
    val isPitchValid: Boolean
)
