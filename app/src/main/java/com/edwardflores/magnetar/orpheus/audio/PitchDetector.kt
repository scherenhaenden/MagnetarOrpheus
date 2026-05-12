package com.edwardflores.magnetar.orpheus.audio

import kotlin.math.sqrt

/**
 * Implementation of the YIN algorithm for fundamental frequency estimation.
 */
class PitchDetector(
    private val sampleRate: Int = 44100,
    private val threshold: Double = 0.1
) {
    /**
     * Estimates the fundamental frequency (F0) of an audio buffer.
     * Returns the frequency in Hz, or -1.0 if no clear pitch is detected.
     */
    fun estimatePitch(buffer: FloatArray): Double {
        val tauMax = buffer.size / 2
        val yinBuffer = DoubleArray(tauMax)

        // Step 1: Difference Function
        for (tau in 1 until tauMax) {
            for (i in 0 until tauMax) {
                val delta = buffer[i].toDouble() - buffer[i + tau].toDouble()
                yinBuffer[tau] += delta * delta
            }
        }

        // Step 2: Cumulative Mean Normalized Difference Function
        yinBuffer[0] = 1.0
        var runningSum = 0.0
        for (tau in 1 until tauMax) {
            runningSum += yinBuffer[tau]
            if (runningSum == 0.0) {
                yinBuffer[tau] = 1.0
            } else {
                yinBuffer[tau] *= tau / runningSum
            }
        }

        // Step 3: Absolute Threshold
        var tauEstimate = -1
        for (tau in 2 until tauMax) {
            if (yinBuffer[tau] < threshold) {
                // Find the local minimum below the threshold
                var localMinTau = tau
                while (localMinTau + 1 < tauMax && yinBuffer[localMinTau + 1] < yinBuffer[localMinTau]) {
                    localMinTau++
                }
                tauEstimate = localMinTau
                break
            }
        }

        // If no tau is below threshold, find the global minimum or return -1
        if (tauEstimate == -1) {
            var minVal = Double.MAX_VALUE
            for (tau in 2 until tauMax) {
                if (yinBuffer[tau] < minVal) {
                    minVal = yinBuffer[tau]
                    tauEstimate = tau
                }
            }
            if (tauEstimate == -1 || yinBuffer[tauEstimate] >= 0.4) return -1.0
        }

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

        return sampleRate / betterTau
    }
}
