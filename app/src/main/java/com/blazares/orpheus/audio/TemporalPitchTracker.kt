package com.blazares.orpheus.audio

import kotlin.math.abs
import kotlin.math.log2

/**
 * Stabilizes frame-level pitch candidates without hiding real note changes.
 *
 * The detector already rejects low-confidence/noisy frames. This tracker handles the next layer:
 * short-lived but technically valid candidates caused by harmonics, transients, or background sound.
 * Nearby candidates are smoothed with a median window. Larger jumps must persist across multiple
 * frames before they are allowed to replace the current lock, with an extra confirmation for
 * octave-sized jumps.
 */
class TemporalPitchTracker(
    private val medianWindowSize: Int = 3,
    private val continuityCents: Double = 160.0,
    private val switchClusterCents: Double = 65.0,
    private val switchConfirmationFrames: Int = 2,
    private val octaveJumpCents: Double = 900.0,
    private val octaveSwitchConfirmationFrames: Int = 3
) {
    private val stableWindow = mutableListOf<Double>()
    private val pendingSwitch = mutableListOf<Double>()
    private var lockedFrequencyHz: Double? = null

    init {
        require(medianWindowSize > 0) { "medianWindowSize must be positive" }
        require(continuityCents > 0.0) { "continuityCents must be positive" }
        require(switchClusterCents > 0.0) { "switchClusterCents must be positive" }
        require(switchConfirmationFrames > 0) { "switchConfirmationFrames must be positive" }
        require(octaveSwitchConfirmationFrames >= switchConfirmationFrames) {
            "octaveSwitchConfirmationFrames must be >= switchConfirmationFrames"
        }
    }

    fun update(candidateFrequencyHz: Double): Double? {
        if (candidateFrequencyHz <= 0.0 || !candidateFrequencyHz.isFinite()) {
            return lockedFrequencyHz
        }

        val locked = lockedFrequencyHz
        if (locked == null) {
            stableWindow.add(candidateFrequencyHz)
            lockedFrequencyHz = candidateFrequencyHz
            return candidateFrequencyHz
        }

        val distanceFromLock = centsDistance(candidateFrequencyHz, locked)
        if (distanceFromLock <= continuityCents) {
            pendingSwitch.clear()
            addBounded(stableWindow, candidateFrequencyHz, medianWindowSize)
            return median(stableWindow).also { lockedFrequencyHz = it }
        }

        val pendingCenter = pendingSwitch.takeIf { it.isNotEmpty() }?.let(::median)
        if (pendingCenter == null || centsDistance(candidateFrequencyHz, pendingCenter) <= switchClusterCents) {
            pendingSwitch.add(candidateFrequencyHz)
        } else {
            pendingSwitch.clear()
            pendingSwitch.add(candidateFrequencyHz)
        }

        val requiredFrames = if (distanceFromLock >= octaveJumpCents) {
            octaveSwitchConfirmationFrames
        } else {
            switchConfirmationFrames
        }

        if (pendingSwitch.size >= requiredFrames) {
            val newLock = median(pendingSwitch)
            stableWindow.clear()
            stableWindow.addAll(pendingSwitch.takeLast(medianWindowSize))
            pendingSwitch.clear()
            lockedFrequencyHz = newLock
            return newLock
        }

        return lockedFrequencyHz
    }

    fun reset() {
        stableWindow.clear()
        pendingSwitch.clear()
        lockedFrequencyHz = null
    }

    private fun centsDistance(a: Double, b: Double): Double =
        abs(1200.0 * log2(a / b))

    private fun addBounded(window: MutableList<Double>, value: Double, limit: Int) {
        window.add(value)
        while (window.size > limit) {
            window.removeAt(0)
        }
    }

    private fun median(values: Collection<Double>): Double {
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 0) {
            (sorted[middle - 1] + sorted[middle]) / 2.0
        } else {
            sorted[middle]
        }
    }
}
