package com.blazares.orpheus.audio

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.sin

class PitchDetectorAccuracyTest {

    private val sampleRate = 44_100

    @Test
    fun `clean synthetic instrument range stays within five cents`() {
        val referenceFrequencies = listOf(
            82.4069,   // Guitar E2
            110.0,    // A2
            220.0,    // A3
            261.6256, // Middle C
            329.6276, // E4
            440.0,    // A4
            659.2551, // E5
            880.0     // A5
        )

        referenceFrequencies.forEach { expectedHz ->
            val detector = PitchDetector(
                sampleRate = sampleRate,
                config = TunerConfig(initialCalibrationDurationMs = 0L)
            )
            val result = detector.analyze(generateSineWave(expectedHz, bufferSize = 8192))
            val detectedHz = result.candidateFrequencyHz

            assertTrue("Expected a valid pitch for $expectedHz Hz", result.isPitchValid)
            assertNotNull("Expected a pitch candidate for $expectedHz Hz", detectedHz)

            val centsError = abs(1200.0 * log2(requireNotNull(detectedHz) / expectedHz))
            assertTrue(
                "Expected <= 5 cents error for $expectedHz Hz but measured $centsError cents ($detectedHz Hz)",
                centsError <= 5.0
            )
        }
    }

    private fun generateSineWave(
        frequencyHz: Double,
        bufferSize: Int,
        amplitude: Double = 0.8
    ): FloatArray = FloatArray(bufferSize) { index ->
        (amplitude * sin(2.0 * PI * frequencyHz * index / sampleRate)).toFloat()
    }
}
