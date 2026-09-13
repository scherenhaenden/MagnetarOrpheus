package com.edwardflores.magnetar.orpheus.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * A repeatable, benchmark-style check of the CPU-side pitch processing path.
 *
 * The elapsed time is intentionally reported rather than used as a pass/fail threshold. JVM
 * scheduling, CPU frequency scaling, and Gradle/JUnit overhead make a hard timing assertion
 * unsuitable for a unit test. The fixed signal workload and output assertions make results
 * comparable between runs without presenting this as an end-to-end audio-latency measurement.
 */
class PitchDetectorProcessingBenchmarkTest {

    @Test
    fun `fixed pitch workload is repeatable and reports processing timing`() {
        val frames = buildWorkload()

        val firstRun = analyze(frames)
        val secondRun = analyze(frames)

        assertEquals("The fixed workload must produce one result per frame", frames.size, firstRun.size)
        firstRun.zip(secondRun).forEachIndexed { index, (first, second) ->
            assertEquals("candidate frequency changed at frame $index", first.candidateFrequencyHz, second.candidateFrequencyHz)
            assertEquals("confidence changed at frame $index", first.confidence, second.confidence, 0.0)
            assertEquals("RMS changed at frame $index", first.rms, second.rms, 0.0)
            assertEquals("noise floor changed at frame $index", first.noiseFloor, second.noiseFloor, 0.0)
            assertEquals("validity changed at frame $index", first.isPitchValid, second.isPitchValid)
        }

        val warmupDetector = newDetector()
        repeat(WARMUP_ROUNDS) {
            frames.forEach(warmupDetector::analyze)
        }

        val measuredDetector = newDetector()
        val startNanos = System.nanoTime()
        val measuredResults = frames.map(measuredDetector::analyze)
        val elapsedNanos = System.nanoTime() - startNanos

        val validResults = measuredResults.filter { it.isPitchValid }
        assertEquals("Every deterministic musical frame should be accepted", frames.size, validResults.size)
        validResults.forEachIndexed { index, result ->
            val expectedFrequency = EXPECTED_FREQUENCIES[index % EXPECTED_FREQUENCIES.size]
            val detectedFrequency = result.candidateFrequencyHz
            assertNotNull("Missing candidate frequency at frame $index", detectedFrequency)
            assertTrue(
                "Frame $index frequency $detectedFrequency differs from $expectedFrequency",
                abs(detectedFrequency!! - expectedFrequency) < FREQUENCY_TOLERANCE_HZ
            )
        }

        val averageMicros = elapsedNanos.toDouble() / frames.size / NANOS_PER_MICROSECOND
        val checksum = measuredResults.sumOf { result ->
            (result.candidateFrequencyHz ?: 0.0) + result.confidence + result.rms
        }
        println(
            "PitchDetector JVM processing benchmark: " +
                "frames=${frames.size}, samplesPerFrame=$FRAME_SIZE, sampleRate=${SAMPLE_RATE}Hz, " +
                "totalMillis=${elapsedNanos / NANOS_PER_MILLISECOND}, " +
                "averageMicros=$averageMicros, validFrames=${validResults.size}, checksum=$checksum"
        )

        assertTrue("The monotonic clock must report positive elapsed time", elapsedNanos > 0L)
    }

    private fun analyze(frames: List<FloatArray>): List<PitchResult> =
        frames.map(newDetector()::analyze)

    private fun newDetector(): PitchDetector =
        PitchDetector(
            sampleRate = SAMPLE_RATE,
            config = TunerConfig(initialCalibrationDurationMs = 0L)
        )

    private fun buildWorkload(): List<FloatArray> =
        List(FRAME_COUNT) { frameIndex ->
            val frequency = EXPECTED_FREQUENCIES[frameIndex % EXPECTED_FREQUENCIES.size]
            FloatArray(FRAME_SIZE) { sampleInFrame ->
                val absoluteSample = frameIndex * FRAME_SIZE + sampleInFrame
                val signal = 0.75 * sin(2.0 * PI * frequency * absoluteSample / SAMPLE_RATE)
                val noise = deterministicNoise(absoluteSample) * 0.01
                (signal + noise).toFloat()
            }
        }

    private fun deterministicNoise(sampleIndex: Int): Double {
        val state = (sampleIndex.toLong() * 1_103_515_245L + 12_345L) and 0x7fffffffL
        return (state / 1_073_741_824.0) - 1.0
    }

    private companion object {
        const val SAMPLE_RATE = 44_100
        const val FRAME_SIZE = 2_048
        const val FRAME_COUNT = 32
        const val WARMUP_ROUNDS = 8
        const val FREQUENCY_TOLERANCE_HZ = 2.0
        const val NANOS_PER_MICROSECOND = 1_000.0
        const val NANOS_PER_MILLISECOND = 1_000_000L
        val EXPECTED_FREQUENCIES = doubleArrayOf(82.41, 110.0, 220.0, 440.0, 659.255, 1_046.5)
    }
}
