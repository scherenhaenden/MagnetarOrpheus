package com.blazares.orpheus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.blazares.orpheus.ui.AppDestination
import com.blazares.orpheus.ui.AppLanguage
import com.blazares.orpheus.ui.NoteLanguage
import com.blazares.orpheus.ui.TunerUiState
import com.blazares.orpheus.ui.screen.TunerScreen
import com.blazares.orpheus.ui.theme.BlazaresOrpheusTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TunerScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun instrumentProfileDialog_delegatesCanonicalProfileId() {
        var selectedProfileId: String? = null
        var startCalls = 0

        composeRule.setContent {
            BlazaresOrpheusTheme {
                TunerScreen(
                    uiState = TunerUiState(),
                    hasPermission = true,
                    versionName = "test",
                    appLanguage = AppLanguage.ENGLISH,
                    noteLanguage = NoteLanguage.ENGLISH,
                    currentDestination = AppDestination.TUNER,
                    onNavigate = {},
                    onAppLanguageChange = {},
                    onNoteLanguageChange = {},
                    onCalibrationChange = {},
                    onNamingSystemChange = {},
                    onPresetSelected = {},
                    onInstrumentProfileSelected = { selectedProfileId = it },
                    onStartTuning = { startCalls++ },
                    onStopTuning = {}
                )
            }
        }

        composeRule.waitForIdle()
        assertTrue(startCalls >= 1)
        composeRule.onNodeWithText("Instrument Profile").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Drop D (DADGBE)").assertIsDisplayed().performClick()

        composeRule.runOnIdle {
            assertEquals("guitar_drop_d", selectedProfileId)
        }
    }
}
