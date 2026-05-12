package com.edwardflores.magnetar.orpheus.audio

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class PitchDetectorTest {

    private val sampleRate = 44100
    private val pitchDetector = PitchDetector(sampleRate)

    @Test
    fun `test pitch detection with 440Hz sine wave`() {
        val frequency = 440.0
        val bufferSize = 2048
        val buffer = FloatArray(bufferSize)
        
        for (i in 0 until bufferSize) {
            buffer[i] = sin(2.0 * PI * frequency * i / sampleRate).toFloat()
        }

        val estimatedPitch = pitchDetector.estimatePitch(buffer)
        
        // Allow for small error (+/- 1Hz)
        assertEquals(frequency, estimatedPitch, 1.0)
    }

    @Test
    fun `test pitch detection with 82,41Hz sine wave (E2)`() {
        val frequency = 82.41
        val bufferSize = 4096 // Larger buffer for lower frequency
        val buffer = FloatArray(bufferSize)
        
        for (i in 0 until bufferSize) {
            buffer[i] = sin(2.0 * PI * frequency * i / sampleRate).toFloat()
        }

        val estimatedPitch = pitchDetector.estimatePitch(buffer)
        
        assertEquals(frequency, estimatedPitch, 1.0)
    }

    @Test
    fun `test no pitch detection with silence`() {
        val buffer = FloatArray(1024)
        val estimatedPitch = pitchDetector.estimatePitch(buffer)
        assertEquals(-1.0, estimatedPitch, 0.0)
    }
}
