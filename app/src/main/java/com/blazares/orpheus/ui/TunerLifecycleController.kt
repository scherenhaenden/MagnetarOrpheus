package com.blazares.orpheus.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Owns the tuner capture policy for lifecycle transitions.
 *
 * The controller is intentionally idempotent: adding an observer to an already-started lifecycle
 * and explicitly synchronizing it must never result in two capture starts.
 */
class TunerLifecycleController(
    private val hasPermission: Boolean,
    private val onStartTuning: () -> Unit,
    private val onStopTuning: () -> Unit
) : DefaultLifecycleObserver {
    private var startedByController = false

    override fun onStart(owner: LifecycleOwner) {
        startIfEligible()
    }

    override fun onStop(owner: LifecycleOwner) {
        stopIfStarted()
    }

    fun synchronize(currentState: Lifecycle.State) {
        if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
            startIfEligible()
        } else {
            stopIfStarted()
        }
    }

    fun dispose() {
        stopIfStarted()
    }

    private fun startIfEligible() {
        if (hasPermission && !startedByController) {
            startedByController = true
            onStartTuning()
        }
    }

    private fun stopIfStarted() {
        if (startedByController) {
            startedByController = false
            onStopTuning()
        }
    }
}
