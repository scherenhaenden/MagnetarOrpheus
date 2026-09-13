package com.blazares.orpheus.audio

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.sin

class PitchDetectorHarmonicRobustnessTest {
    private val sampleRate = 44_100

    @Test
    fun `harmonic rich noisy instrument-like signals stay within ten cents`() {
        val frequencies = listOf(
            41.2034,  // Bass E1
            73.4162,  // Drop-D guitar D2
            82.4069,  // Guitar E2
            110.0,    // Guitar A2
            196.0,    // Guitar G3
            246.9417, // Guitar B3
            329.6276  // Guitar E4
        )

        frequencies.forEachIndexed { index, expectedHz ->
            val detector = PitchDetector(
                sampleRate = sampleRate,
                config = TunerConfig(initialCalibrationDurationMs = 0L)
            )
            val result = detector.analyze(
                generateInstrumentLikeWave(
                    frequencyHz = expectedHz,
                    bufferSize = 8192,
                    seed = 1_000L + index
                )
            )
            val detectedHz = result.candidateFrequencyHz

            assertTrue("Expected valid harmonic-rich pitch for $expectedHz Hz", result.isPitchValid)
            assertNotNull("Expected candidate for $expectedHz Hz", detectedHz)

            val centsError = abs(1200.0 * log2(requireNotNull(detectedHz) / expectedHz))
            assertTrue(
                "Expected <= 10 cents error for harmonic-rich $expectedHz Hz but measured $centsError cents ($detectedHz Hz)",
                centsError <= 10.0
            )
        }
    }

    private fun generateInstrumentLikeWave(
        frequencyHz: Double,
        bufferSize: Int,
        seed: Long
    ): FloatArray {
        val random = Random(seed)
        return FloatArray(bufferSize) { index ->
            val phase = 2.0 * PI * frequencyHz * index / sampleRate
            val fundamental = 0.58 * sin(phase)
            val secondHarmonic = 0.22 * sin(2.0 * phase + 0.17)
            val thirdHarmonic = 0.11 * sin(3.0 * phase + 0.31)
            val fourthHarmonic = 0.05 * sin(4.0 * phase + 0.47)
            val noise = (random.nextDouble() * 2.0 - 1.0) * 0.015
            (fundamental + secondHarmonic + thirdHarmonic + fourthHarmonic + noise)
                .coerceIn(-1.0, 1.0)
                .toFloat()
        }
    }
}
