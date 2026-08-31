package com.edwardflores.magnetar.orpheus.audio

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Fundamental frequency detector implementing the YIN algorithm with pitch confidence calculation,
 * signal level (RMS) measurement, adaptive noise floor estimation, and pitch validation criteria.
 */
class PitchDetector(
    private val sampleRate: Int = 44100,
    private val config: TunerConfig = TunerConfig(),
    private val noiseFloorEstimator: NoiseFloorEstimator = NoiseFloorEstimator(config, sampleRate)
) {
    /**
     * Resets internal state (such as noise floor estimator) for a new tuning session.
     */
    fun reset() {
        noiseFloorEstimator.reset()
    }

    /**
     * Analyzes an audio buffer and produces detailed [PitchResult] containing frequency candidate,
     * YIN pitch confidence score, signal RMS, ambient noise floor, SNR, and validity decision.
     */
    fun analyze(buffer: FloatArray): PitchResult {
        val rms = calculateRms(buffer)

        if (buffer.isEmpty() || buffer.size < 4) {
            val currentNoiseFloor = noiseFloorEstimator.update(rms, 0.0, buffer.size)
            return PitchResult(
                candidateFrequencyHz = null,
                confidence = 0.0,
                rms = rms,
                noiseFloor = currentNoiseFloor,
                signalToNoiseRatio = calculateSnr(rms, currentNoiseFloor),
                isPitchValid = false
            )
        }

        // Determine tau search range based on configured min and max frequency
        val tauMin = max(2, (sampleRate / config.maxPitchHz).toInt())
        val maxTauAllowed = buffer.size / 2
        val tauMax = min((sampleRate / config.minPitchHz).toInt(), maxTauAllowed)

        if (tauMax <= tauMin) {
            val currentNoiseFloor = noiseFloorEstimator.update(rms, 0.0, buffer.size)
            return PitchResult(
                candidateFrequencyHz = null,
                confidence = 0.0,
                rms = rms,
                noiseFloor = currentNoiseFloor,
                signalToNoiseRatio = calculateSnr(rms, currentNoiseFloor),
                isPitchValid = false
            )
        }

        val yinBuffer = DoubleArray(tauMax)

        // Step 1: Difference Function
        for (tau in 1 until tauMax) {
            var sum = 0.0
            for (i in 0 until maxTauAllowed) {
                val delta = buffer[i].toDouble() - buffer[i + tau].toDouble()
                sum += delta * delta
            }
            yinBuffer[tau] = sum
        }

        // Step 2: Cumulative Mean Normalized Difference Function (CMNDF)
        yinBuffer[0] = 1.0
        var runningSum = 0.0
        for (tau in 1 until tauMax) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] = if (runningSum == 0.0) 1.0 else yinBuffer[tau] * tau / runningSum
        }

        // Step 3: Absolute Threshold Search
        var tauEstimate = -1
        for (tau in tauMin until tauMax) {
            if (yinBuffer[tau] < config.yinThreshold) {
                var localMinTau = tau
                while (localMinTau + 1 < tauMax && yinBuffer[localMinTau + 1] < yinBuffer[localMinTau]) {
                    localMinTau++
                }
                tauEstimate = localMinTau
                break
            }
        }

        // YIN Fallback: search for global minimum in tau range
        var isFallbackCandidate = false
        if (tauEstimate == -1) {
            var minVal = Double.MAX_VALUE
            for (tau in tauMin until tauMax) {
                if (yinBuffer[tau] < minVal) {
                    minVal = yinBuffer[tau]
                    tauEstimate = tau
                }
            }
            isFallbackCandidate = true
        }

        if (tauEstimate <= 0 || tauEstimate >= tauMax) {
            val currentNoiseFloor = noiseFloorEstimator.update(rms, 0.0, buffer.size)
            return PitchResult(
                candidateFrequencyHz = null,
                confidence = 0.0,
                rms = rms,
                noiseFloor = currentNoiseFloor,
                signalToNoiseRatio = calculateSnr(rms, currentNoiseFloor),
                isPitchValid = false
            )
        }

        // Compute normalized periodicity confidence in range [0.0, 1.0] derived from YIN CMNDF quality
        val rawYinVal = yinBuffer[tauEstimate]
        val confidence = (1.0 - rawYinVal).coerceIn(0.0, 1.0)

        // Step 4: Parabolic Interpolation
        var betterTau = tauEstimate.toDouble()
        if (tauEstimate > 0 && tauEstimate < tauMax - 1) {
            val s0 = yinBuffer[tauEstimate - 1]
            val s1 = yinBuffer[tauEstimate]
            val s2 = yinBuffer[tauEstimate + 1]

            val denominator = 2 * (2 * s1 - s2 - s0)
            if (denominator != 0.0) {
                betterTau += (s2 - s0) / denominator
            }
        }

        val candidateFrequency = if (betterTau > 0.0) sampleRate / betterTau else null
        val currentNoiseFloor = noiseFloorEstimator.update(rms, confidence, buffer.size)
        val snr = calculateSnr(rms, currentNoiseFloor)

        val frequencyInBounds = candidateFrequency != null &&
                candidateFrequency >= config.minPitchHz &&
                candidateFrequency <= config.maxPitchHz &&
                candidateFrequency.isFinite()

        val signalAboveNoiseFloor = rms >= currentNoiseFloor * config.noiseMultiplier &&
                rms >= config.minAbsoluteRms

        val isPitchValid = frequencyInBounds &&
                signalAboveNoiseFloor &&
                confidence >= config.minConfidence &&
                (!isFallbackCandidate || rawYinVal < config.yinThreshold)

        return PitchResult(
            candidateFrequencyHz = candidateFrequency,
            confidence = confidence,
            rms = rms,
            noiseFloor = currentNoiseFloor,
            signalToNoiseRatio = snr,
            isPitchValid = isPitchValid
        )
    }

    /**
     * Legacy pitch estimation wrapper for backward compatibility.
     * Returns candidate frequency in Hz if pitch is valid, or -1.0 if no valid pitch is detected.
     */
    fun estimatePitch(buffer: FloatArray): Double {
        val result = analyze(buffer)
        return if (result.isPitchValid && result.candidateFrequencyHz != null) {
            result.candidateFrequencyHz
        } else {
            -1.0
        }
    }

    private fun calculateRms(buffer: FloatArray): Double {
        if (buffer.isEmpty()) return 0.0
        var sumSquares = 0.0
        for (sample in buffer) {
            val sampleDb = sample.toDouble()
            sumSquares += sampleDb * sampleDb
        }
        val meanSquare = sumSquares / buffer.size
        val rms = sqrt(meanSquare)
        return if (rms.isFinite()) max(0.0, rms) else 0.0
    }

    private fun calculateSnr(rms: Double, noiseFloor: Double): Double {
        if (noiseFloor <= 0.0 || !noiseFloor.isFinite() || !rms.isFinite()) return 0.0
        return (rms / noiseFloor).coerceAtLeast(0.0)
    }
}
