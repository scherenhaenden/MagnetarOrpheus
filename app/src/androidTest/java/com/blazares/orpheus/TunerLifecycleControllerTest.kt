package com.blazares.orpheus

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blazares.orpheus.ui.TunerLifecycleController
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TunerLifecycleControllerTest {
    private lateinit var scenario: ActivityScenario<ComponentActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(ComponentActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun startedActivityStartsTunerExactlyOnceWhenPermissionExists() {
        var starts = 0
        var stops = 0

        scenario.onActivity { activity ->
            val controller = TunerLifecycleController(
                hasPermission = true,
                onStartTuning = { starts++ },
                onStopTuning = { stops++ }
            )
            activity.lifecycle.addObserver(controller)
        }
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        assertEquals(1, starts)
        assertEquals(0, stops)
    }

    @Test
    fun backgroundTransitionStopsOnceAndForegroundCanStartAgain() {
        var starts = 0
        var stops = 0

        scenario.onActivity { activity ->
            activity.lifecycle.addObserver(
                TunerLifecycleController(
                    hasPermission = true,
                    onStartTuning = { starts++ },
                    onStopTuning = { stops++ }
                )
            )
        }
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        assertEquals(2, starts)
        assertEquals(1, stops)
    }

    @Test
    fun permissionGateNeverStartsCapture() {
        var starts = 0
        var stops = 0

        scenario.onActivity { activity ->
            activity.lifecycle.addObserver(
                TunerLifecycleController(
                    hasPermission = false,
                    onStartTuning = { starts++ },
                    onStopTuning = { stops++ }
                )
            )
        }
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        assertEquals(0, starts)
        assertEquals(0, stops)
    }

    @Test
    fun synchronizingBelowStartedStopsAnActiveCapture() {
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
    fun disposeStopsActiveCaptureButIsIdempotent() {
        var stops = 0
        lateinit var controller: TunerLifecycleController

        scenario.onActivity { activity ->
            controller = TunerLifecycleController(
                hasPermission = true,
                onStartTuning = {},
                onStopTuning = { stops++ }
            )
            activity.lifecycle.addObserver(controller)
        }
        controller.dispose()
        controller.dispose()

        assertEquals(1, stops)
    }
}
