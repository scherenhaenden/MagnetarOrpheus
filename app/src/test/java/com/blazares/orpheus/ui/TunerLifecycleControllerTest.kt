package com.blazares.orpheus.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class TunerLifecycleControllerTest {
    private val owner = mockk<LifecycleOwner>(relaxed = true)

    @Test
    fun `started lifecycle starts tuner exactly once when permission exists`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.STARTED)
        controller.onStart(owner)
        controller.synchronize(Lifecycle.State.RESUMED)

        assertEquals(1, starts)
        assertEquals(0, stops)
    }

    @Test
    fun `background transition stops once and foreground can start again`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.onStart(owner)
        controller.onStop(owner)
        controller.onStop(owner)
        controller.onStart(owner)

        assertEquals(2, starts)
        assertEquals(1, stops)
    }

    @Test
    fun `permission gate never starts capture`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = false,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.RESUMED)
        controller.onStart(owner)
        controller.onStop(owner)
        controller.dispose()

        assertEquals(0, starts)
        assertEquals(0, stops)
    }

    @Test
    fun `synchronizing below started stops an active capture`() {
        var starts = 0
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = { starts++ },
            onStopTuning = { stops++ }
        )

        controller.synchronize(Lifecycle.State.STARTED)
        controller.synchronize(Lifecycle.State.CREATED)

        assertEquals(1, starts)
        assertEquals(1, stops)
    }

    @Test
    fun `dispose stops active capture but is idempotent`() {
        var stops = 0
        val controller = TunerLifecycleController(
            hasPermission = true,
            onStartTuning = {},
            onStopTuning = { stops++ }
        )

        controller.onStart(owner)
        controller.dispose()
        controller.dispose()

        assertEquals(1, stops)
    }
}
