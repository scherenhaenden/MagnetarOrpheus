package com.edwardflores.magnetar.orpheus.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random
import kotlin.math.PI
import kotlin.math.sin

class PitchDetectorTest {

    private val sampleRate = 44100
    private val config = TunerConfig(initialCalibrationDurationMs = 0L) // immediate calibration for simple tests
    private val pitchDetector = PitchDetector(sampleRate, config)

    private fun generateSineWave(frequency: Double, bufferSize: Int, amplitude: Float = 1.0f): FloatArray {
        val buffer = FloatArray(bufferSize)
        for (i in 0 until bufferSize) {
            buffer[i] = (amplitude * sin(2.0 * PI * frequency * i / sampleRate)).toFloat()
        }
        return buffer
    }

    // CASO 1 — Seno limpio. 440 Hz
    @Test
    fun `CASE 1 - clean 440Hz sine wave produces valid pitch with high confidence`() {
        val buffer = generateSineWave(440.0, 2048)
        val result = pitchDetector.analyze(buffer)

        assertNotNull(result.candidateFrequencyHz)
        assertEquals(440.0, result.candidateFrequencyHz!!, 1.0)
        assertTrue("Confidence should be high (> 0.8)", result.confidence >= 0.8)
        assertTrue("Pitch should be valid", result.isPitchValid)
        assertTrue("RMS should be finite and non-negative", result.rms > 0.5)
    }

    // CASO 2 — Otra frecuencia. 110 Hz
    @Test
    fun `CASE 2 - 110Hz sine wave detected correctly`() {
        val buffer = generateSineWave(110.0, 4096)
        val result = pitchDetector.analyze(buffer)

        assertNotNull(result.candidateFrequencyHz)
        assertEquals(110.0, result.candidateFrequencyHz!!, 1.5)
        assertTrue(result.isPitchValid)
    }

    // CASO 3 — Silencio. Buffer de ceros.
    @Test
    fun `CASE 3 - silence buffer produces invalid pitch without NaN or Infinity`() {
        val buffer = FloatArray(2048)
        val result = pitchDetector.analyze(buffer)

        assertFalse(result.isPitchValid)
        assertEquals(0.0, result.rms, 0.0)
        assertFalse(result.rms.isNaN())
        assertFalse(result.rms.isInfinite())
        assertFalse(result.confidence.isNaN())
        assertFalse(result.signalToNoiseRatio.isNaN())
    }

    // CASO 4 — Ruido aleatorio de amplitud pequeña.
    @Test
    fun `CASE 4 - low amplitude random noise does not produce valid pitch`() {
        val rng = Random(42)
        val buffer = FloatArray(2048) { (rng.nextDouble() * 0.02 - 0.01).toFloat() }

        val result = pitchDetector.analyze(buffer)
        assertFalse("Low noise should not produce valid pitch", result.isPitchValid)
    }

    // CASO 5 — Nota sobre ruido.
    @Test
    fun `CASE 5 - sine wave mixed with background noise produces valid pitch`() {
        val rng = Random(123)
        val sineBuffer = generateSineWave(330.0, 2048, amplitude = 0.5f)
        val buffer = FloatArray(2048) { i ->
            sineBuffer[i] + (rng.nextDouble() * 0.04 - 0.02).toFloat()
        }

        val result = pitchDetector.analyze(buffer)
        assertTrue(result.isPitchValid)
        assertNotNull(result.candidateFrequencyHz)
        assertEquals(330.0, result.candidateFrequencyHz!!, 2.0)
    }

    // CASO 6 — Ruido fuerte sin periodicidad.
    @Test
    fun `CASE 6 - high volume non-periodic noise yields high RMS but insufficient confidence`() {
        val rng = Random(999)
        val buffer = FloatArray(2048) { (rng.nextDouble() * 1.8 - 0.9).toFloat() }

        val result = pitchDetector.analyze(buffer)
        assertTrue("RMS should be high for loud noise", result.rms > 0.3)
        assertTrue("Confidence should be low for non-periodic noise", result.confidence < config.minConfidence)
        assertFalse("High RMS noise should NOT be considered valid pitch", result.isPitchValid)
    }

    // CASO 7 — Adaptacion del noise floor.
    @Test
    fun `CASE 7 - noise floor adapts to continuous low ambient noise`() {
        val testConfig = TunerConfig(
            initialCalibrationDurationMs = 50L,
            noiseEmaAlpha = 0.1
        )
        val detector = PitchDetector(sampleRate, testConfig)
        val rng = Random(77)

        var lastNoiseFloor = 0.0
        // Feed multiple noise frames
        for (frame in 0 until 15) {
            val buffer = FloatArray(1024) { (rng.nextDouble() * 0.04 - 0.02).toFloat() }
            val res = detector.analyze(buffer)
            lastNoiseFloor = res.noiseFloor
        }

        assertTrue("Noise floor should adapt near actual noise RMS", lastNoiseFloor > 0.001)
    }

    // CASO 8 — Cambio de ambiente.
    @Test
    fun `CASE 8 - noise floor adapts gradually to step change in ambient noise`() {
        val testConfig = TunerConfig(
            initialCalibrationDurationMs = 10L,
            noiseEmaAlpha = 0.1
        )
        val detector = PitchDetector(sampleRate, testConfig)
        val rng = Random(88)

        // Baseline quiet environment
        repeat(5) {
            val quietBuffer = FloatArray(1024) { (rng.nextDouble() * 0.02 - 0.01).toFloat() }
            detector.analyze(quietBuffer)
        }
        val quietFloor = detector.analyze(FloatArray(1024) { (rng.nextDouble() * 0.02 - 0.01).toFloat() }).noiseFloor

        // Loud ambient step change (non-periodic noise)
        var louderFloor = quietFloor
        repeat(10) {
            val louderNoiseBuffer = FloatArray(1024) { (rng.nextDouble() * 0.08 - 0.04).toFloat() }
            louderFloor = detector.analyze(louderNoiseBuffer).noiseFloor
        }

        assertTrue("Noise floor should increase gradually", louderFloor > quietFloor)
    }

    // CASO 9 — Nota sostenida.
    @Test
    fun `CASE 9 - sustained valid note does NOT cause noise floor to absorb the note`() {
        val testConfig = TunerConfig(
            initialCalibrationDurationMs = 0L,
            noiseEmaAlpha = 0.2
        )
        val detector = PitchDetector(sampleRate, testConfig)

        // Establish quiet noise floor
        val initialResult = detector.analyze(FloatArray(1024))
        val initialNoiseFloor = initialResult.noiseFloor

        // Sustained note played continuously for 20 frames
        val sineBuffer = generateSineWave(440.0, 2048, amplitude = 0.8f)
        var finalNoiseFloor = initialNoiseFloor

        repeat(20) {
            val result = detector.analyze(sineBuffer)
            assertTrue(result.isPitchValid)
            finalNoiseFloor = result.noiseFloor
        }

        assertEquals("Noise floor must freeze during sustained valid note", initialNoiseFloor, finalNoiseFloor, 0.001)
    }

    // CASO 10 — Outlier ambiental.
    @Test
    fun `CASE 10 - single high noise spike does not permanently ruin noise floor`() {
        val testConfig = TunerConfig(
            initialCalibrationDurationMs = 0L,
            noiseEmaAlpha = 0.1,
            maxNoiseStepMultiplier = 1.5
        )
        val detector = PitchDetector(sampleRate, testConfig)
        val rng = Random(101)

        val quietBuffer = FloatArray(1024) { (rng.nextDouble() * 0.02 - 0.01).toFloat() }
        val quietRes = detector.analyze(quietBuffer)

        // Single loud pop / outlier
        val popBuffer = FloatArray(1024) { (rng.nextDouble() * 1.8 - 0.9).toFloat() }
        detector.analyze(popBuffer)

        // Next quiet frame
        val postPopRes = detector.analyze(quietBuffer)
        assertTrue("Noise floor should remain controlled after transient spike", postPopRes.noiseFloor < quietRes.noiseFloor * 3.0)
    }

    // CASO 11 — Edge cases.
    @Test
    fun `CASE 11 - edge cases like empty buffer or small buffer return non-null safe results`() {
        val emptyResult = pitchDetector.analyze(FloatArray(0))
        assertFalse(emptyResult.isPitchValid)
        assertNull(emptyResult.candidateFrequencyHz)
        assertEquals(0.0, emptyResult.rms, 0.0)

        val tinyResult = pitchDetector.analyze(FloatArray(2) { 0.5f })
        assertFalse(tinyResult.isPitchValid)
        assertNull(tinyResult.candidateFrequencyHz)

        val legacyPitch = pitchDetector.estimatePitch(FloatArray(0))
        assertEquals(-1.0, legacyPitch, 0.0)
    }
}
