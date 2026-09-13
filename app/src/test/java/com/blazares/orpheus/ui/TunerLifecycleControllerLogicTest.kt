package com.blazares.orpheus.ui

import androidx.lifecycle.Lifecycle
import org.junit.Assert.assertEquals
import org.junit.Test

class TunerLifecycleControllerLogicTest {
    @Test
    fun `synchronizing a started lifecycle starts capture once`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.STARTED)
        controller.synchronize(Lifecycle.State.RESUMED)

        assertEquals(1, starts)
        assertEquals(0, stops)
    }

    @Test
    fun `synchronizing below started stops capture once`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.STARTED)
        controller.synchronize(Lifecycle.State.CREATED)
        controller.synchronize(Lifecycle.State.INITIALIZED)

        assertEquals(1, starts)
        assertEquals(1, stops)
    }

    @Test
    fun `permission gate prevents capture from starting`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = false,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.RESUMED)
        controller.dispose()

        assertEquals(0, starts)
        assertEquals(0, stops)
    }

    @Test
    fun `dispose stops active capture idempotently`() {
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = {},
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.STARTED)
        controller.dispose()
        controller.dispose()

        assertEquals(1, stops)
    }
}
