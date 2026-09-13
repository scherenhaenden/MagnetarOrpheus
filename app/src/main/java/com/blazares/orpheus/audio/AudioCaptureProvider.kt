package com.blazares.orpheus.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

class AudioCaptureException(message: String, cause: Throwable? = null) : IllegalStateException(message, cause)

/**
 * Responsible for low-level audio capture using Android's AudioRecord API.
 */
class AudioCaptureProvider(
    private val sampleRate: Int = 44100,
    private val bufferSizeFactor: Int = 2
) {
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private val bufferSize = minBufferSize * bufferSizeFactor

    @SuppressLint("MissingPermission")
    fun startCapture(): Flow<FloatArray> = flow {
        if (minBufferSize <= 0) {
            throw AudioCaptureException("AudioRecord does not support the requested capture format")
        }

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        try {
            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                throw AudioCaptureException("AudioRecord could not be initialized")
            }

            try {
                audioRecord.startRecording()
            } catch (exception: IllegalStateException) {
                throw AudioCaptureException("AudioRecord could not start recording", exception)
            }

            if (audioRecord.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                throw AudioCaptureException("AudioRecord did not enter the recording state")
            }

            val buffer = ShortArray(minBufferSize)
            val floatBuffer = FloatArray(minBufferSize)
            while (currentCoroutineContext().isActive) {
                val readResult = audioRecord.read(buffer, 0, buffer.size)
                if (readResult > 0) {
                    // Convert ShortArray to FloatArray (-1.0 to 1.0)
                    for (i in 0 until readResult) {
                        floatBuffer[i] = buffer[i] / 32768f
                    }
                    emit(floatBuffer.copyOf(readResult))
                } else if (readResult < 0) {
                    throw AudioCaptureException("AudioRecord read failed with code $readResult")
                }
            }
        } finally {
            if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                runCatching { audioRecord.stop() }
            }
            audioRecord.release()
        }
    }.flowOn(Dispatchers.IO)
}
