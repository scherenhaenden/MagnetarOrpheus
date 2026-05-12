package com.edwardflores.magnetar.orpheus.notebuilder.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.edwardflores.magnetar.orpheus.notebuilder.NoteBuilderMusicTheory
import com.edwardflores.magnetar.orpheus.ui.notebuilder.NoteSelection
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

class NotePlaybackEngine(
    private val sampleRate: Int = 44_100,
    private val chunkFrames: Int = 2_048
) {
    @Volatile
    private var activeTrack: AudioTrack? = null

    suspend fun playSelection(
        selectedNotes: List<NoteSelection>,
        holdEnabled: Boolean
    ) {
        stop()
        if (selectedNotes.isEmpty()) return

        val noteFrequencies = selectedNotes.map(NoteBuilderMusicTheory::noteToFrequency)
        val totalFrames = if (holdEnabled) Int.MAX_VALUE else sampleRate * 2
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(chunkFrames * 2)

        val track = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            minBufferSize,
            AudioTrack.MODE_STREAM,
            AudioTrack.WRITE_NON_BLOCKING
        )
        activeTrack = track

        var frameCursor = 0
        try {
            track.play()
            while (currentCoroutineContext().isActive && (holdEnabled || frameCursor < totalFrames)) {
                val framesToGenerate = if (holdEnabled) {
                    chunkFrames
                } else {
                    min(chunkFrames, totalFrames - frameCursor)
                }

                val buffer = generateBuffer(
                    frequencies = noteFrequencies,
                    frameCount = framesToGenerate,
                    startFrame = frameCursor,
                    holdEnabled = holdEnabled,
                    totalFrames = totalFrames
                )
                track.write(buffer, 0, framesToGenerate, AudioTrack.WRITE_BLOCKING)
                frameCursor += framesToGenerate
            }
        } finally {
            if (activeTrack === track) {
                activeTrack = null
            }
            releaseTrack(track)
        }
    }

    fun stop() {
        activeTrack?.let { track ->
            activeTrack = null
            releaseTrack(track)
        }
    }

    private fun generateBuffer(
        frequencies: List<Double>,
        frameCount: Int,
        startFrame: Int,
        holdEnabled: Boolean,
        totalFrames: Int
    ): ShortArray {
        val buffer = ShortArray(frameCount)
        val attackFrames = (sampleRate * 0.01f).toInt().coerceAtLeast(1)
        val releaseFrames = (sampleRate * 0.10f).toInt().coerceAtLeast(1)
        val amplitude = 0.45 / frequencies.size.coerceAtLeast(1)

        for (frame in 0 until frameCount) {
            val absoluteFrame = startFrame + frame
            var mixedSample = 0.0

            frequencies.forEach { frequency ->
                val time = absoluteFrame / sampleRate.toDouble()
                mixedSample += sin(2.0 * PI * frequency * time)
                mixedSample += 0.22 * sin(2.0 * PI * frequency * 2.0 * time)
                mixedSample += 0.08 * sin(2.0 * PI * frequency * 3.0 * time)
            }

            val envelope = when {
                absoluteFrame < attackFrames -> absoluteFrame / attackFrames.toDouble()
                !holdEnabled && absoluteFrame > totalFrames - releaseFrames ->
                    ((totalFrames - absoluteFrame).coerceAtLeast(0)) / releaseFrames.toDouble()
                else -> 1.0
            }

            val sampleValue = (mixedSample * amplitude * envelope).coerceIn(-1.0, 1.0)
            buffer[frame] = (sampleValue * Short.MAX_VALUE).toInt().toShort()
        }

        return buffer
    }

    private fun releaseTrack(track: AudioTrack) {
        runCatching {
            when (track.playState) {
                AudioTrack.PLAYSTATE_PLAYING,
                AudioTrack.PLAYSTATE_PAUSED -> track.stop()
            }
        }
        runCatching { track.flush() }
        runCatching { track.release() }
    }
}
