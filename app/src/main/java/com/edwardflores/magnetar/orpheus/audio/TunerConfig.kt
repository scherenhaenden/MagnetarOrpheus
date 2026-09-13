package com.edwardflores.magnetar.orpheus.audio

/**
 * Tuner configuration parameters for DSP pitch detection and noise floor estimation.
 *
 * All parameters are centralized here to allow straightforward tuning on physical hardware.
 */
data class TunerConfig(
    /** Minimum frequency supported by the chromatic tuner (Hz). Suitable for E1 (~41.2 Hz) or B0 (~30.9 Hz). Default covers common musical instruments including bass. */
    val minPitchHz: Double = 30.0,

    /** Maximum frequency supported by the chromatic tuner (Hz). Covers guitar, violin, flute harmonics (~2000 Hz). */
    val maxPitchHz: Double = 2100.0,

    /** YIN primary threshold for absolute minimum search in CMNDF. */
    val yinThreshold: Double = 0.1,

    /** Minimum YIN confidence required to consider a pitch valid. Range [0.0, 1.0]. */
    val minConfidence: Double = 0.80,

    /** Minimum confidence threshold below which noise floor updates are permitted. */
    val noiseUpdateMaxConfidence: Double = 0.50,

    /** Multiplier applied to noise floor to establish the minimum RMS required for a signal to be considered valid pitch above ambient noise. */
    val noiseMultiplier: Double = 2.0,

    /** Minimum absolute RMS level to prevent numeric issues or micro-noise in silent environments. */
    val minAbsoluteRms: Double = 0.001,

    /** Initial calibration window in milliseconds. Audio buffers during this period populate initial noise floor. */
    val initialCalibrationDurationMs: Long = 400L,

    /** Exponential Moving Average (EMA) smoothing factor for continuous noise floor adaptation. Small value = slow update. */
    val noiseEmaAlpha: Double = 0.02,

    /** Maximum permitted change factor for noise floor per update step to prevent transient noise spikes from corrupting the baseline. */
    val maxNoiseStepMultiplier: Double = 1.5
)
