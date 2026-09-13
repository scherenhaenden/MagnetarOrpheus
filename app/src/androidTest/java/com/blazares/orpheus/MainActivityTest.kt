package com.blazares.orpheus

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val activityRule = ActivityScenarioRule<MainActivity>(
        Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        ).putExtra(MainActivity.EXTRA_SKIP_AUDIO_PERMISSION_REQUEST, true)
    )

    private val composeRule = createEmptyComposeRule()

    @get:Rule
    val testRule: TestRule = RuleChain
        .outerRule(activityRule)
        .around(composeRule)

    @Before
    fun resetAppPreferences() {
        activityRule.scenario.onActivity { activity ->
            activity.getSharedPreferences("orpheus_preferences", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit()
        }
        activityRule.scenario.recreate()
        composeRule.waitForIdle()
    }

    @Test
    fun launchWithoutAudioPermission_showsPermissionGateAndExplicitAction() {
        composeRule.onNodeWithText("Microphone access is required").assertIsDisplayed()
        composeRule.onNodeWithText(
            "Allow microphone access to activate the tuner and waveform panels. Audio is processed locally on this device."
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Allow microphone access").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Menu").assertIsDisplayed()
    }

    @Test
    fun navigationOpensNoteBuilder_andSwitchesInputModes() {
        composeRule.onNodeWithContentDescription("Menu").performClick()
        composeRule.onNodeWithText("Note Builder").performClick()

        composeRule.onNodeWithText("NOTE BUILDER").assertIsDisplayed()
        composeRule.onNodeWithText("Cmaj7").assertIsDisplayed()
        composeRule.onNodeWithText("Major").assertIsDisplayed()

        composeRule.onNodeWithText("Grid").performClick()
        composeRule.onNodeWithText("NOTE GRID").assertIsDisplayed()
        composeRule.onNodeWithText("Scroll for more octaves").assertIsDisplayed()
        composeRule.onAllNodesWithText("KEYBOARD INPUT").assertCountEquals(0)
    }

    @Test
    fun settingsChangesAppLanguage_andUpdatesNavigationLabels() {
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("App language").assertIsDisplayed()

        composeRule.onNodeWithText("Español").performClick()

        composeRule.onNodeWithText("Ajustes").assertIsDisplayed()
        composeRule.onNodeWithText("Idioma de la app").assertIsDisplayed()
        composeRule.onNodeWithText("Cerrar").performClick()

        composeRule.onNodeWithContentDescription("Menu").performClick()
        composeRule.onNodeWithText("Afinador").assertIsDisplayed()
        composeRule.onNodeWithText("Constructor de notas").assertIsDisplayed()
    }

    @Test
    fun appLanguagePreference_survivesActivityRecreation() {
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Español").performClick()
        composeRule.onNodeWithText("Cerrar").performClick()

        activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Menu").performClick()
        composeRule.onNodeWithText("Afinador").assertIsDisplayed()
        composeRule.onNodeWithText("Constructor de notas").assertIsDisplayed()
    }
}
