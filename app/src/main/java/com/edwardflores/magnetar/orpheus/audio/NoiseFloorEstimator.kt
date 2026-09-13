package com.edwardflores.magnetar.orpheus.audio

import kotlin.math.max
import kotlin.math.min

/**
 * Estimator for ambient noise floor level.
 *
 * Provides a short initial calibration window (non-blocking) and continues to adapt slowly
 * during ambient/low-confidence audio frames. Noise floor updates are frozen when a clear, high-confidence
 * pitch frame is detected to prevent sustained musical notes from increasing the noise floor.
 */
class NoiseFloorEstimator(
    private val config: TunerConfig = TunerConfig(),
    private val sampleRate: Int = 44100
) {
    private var currentNoiseFloor: Double = config.minAbsoluteRms
    private var totalSamplesProcessed: Long = 0L
    private val calibrationSampleThreshold: Long = (sampleRate.toLong() * config.initialCalibrationDurationMs) / 1000L
    private val calibrationRmsBuffer = mutableListOf<Double>()

    val isCalibrated: Boolean
        get() = totalSamplesProcessed >= calibrationSampleThreshold

    val noiseFloor: Double
        get() = currentNoiseFloor

    /**
     * Resets the noise floor estimator state for a new tuning session.
     */
    fun reset() {
        currentNoiseFloor = config.minAbsoluteRms
        totalSamplesProcessed = 0L
        calibrationRmsBuffer.clear()
    }

    /**
     * Updates the noise floor estimate given frame RMS and YIN pitch confidence.
     *
     * @param rms Frame RMS level.
     * @param confidence Pitch confidence of the frame [0.0, 1.0].
     * @param frameSize Number of audio samples in the frame.
     * @return Current noise floor level.
     */
    fun update(rms: Double, confidence: Double, frameSize: Int): Double {
        if (!rms.isFinite() || rms < 0.0) return currentNoiseFloor

        totalSamplesProcessed += frameSize

        if (!isCalibrated) {
            // Do not learn a clear musical note as ambient noise when tuning starts while
            // the instrument is already sounding. Only low-confidence frames form the baseline.
            if (confidence <= config.noiseUpdateMaxConfidence) {
                calibrationRmsBuffer.add(rms)
                val sorted = calibrationRmsBuffer.sorted()
                val medianRms = if (sorted.isNotEmpty()) sorted[sorted.size / 2] else config.minAbsoluteRms
                currentNoiseFloor = max(medianRms, config.minAbsoluteRms)
            }
            return currentNoiseFloor
        }

        // Post-calibration continuous adaptation:
        // Freeze noise floor updates when pitch confidence is high (sustained notes)
        if (confidence <= config.noiseUpdateMaxConfidence) {
            // Cap transient noise spikes so a single loud pop does not jump noise floor drastically
            val cappedRms = min(rms, currentNoiseFloor * config.maxNoiseStepMultiplier)
            currentNoiseFloor = (currentNoiseFloor * (1.0 - config.noiseEmaAlpha)) + (cappedRms * config.noiseEmaAlpha)
            currentNoiseFloor = max(currentNoiseFloor, config.minAbsoluteRms)
        }

        return currentNoiseFloor
    }
}
