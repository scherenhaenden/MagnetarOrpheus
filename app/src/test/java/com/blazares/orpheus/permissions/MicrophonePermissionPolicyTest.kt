package com.blazares.orpheus.permissions

import org.junit.Assert.assertEquals
import org.junit.Test

class MicrophonePermissionPolicyTest {

    @Test
    fun `granted permission is immediately available`() {
        assertEquals(
            MicrophonePermissionAction.AVAILABLE,
            MicrophonePermissionPolicy.resolve(
                isGranted = true,
                requestedBefore = false,
                shouldShowRationale = false
            )
        )
    }

    @Test
    fun `first request asks Android for microphone permission`() {
        assertEquals(
            MicrophonePermissionAction.REQUEST_PERMISSION,
            MicrophonePermissionPolicy.resolve(
                isGranted = false,
                requestedBefore = false,
                shouldShowRationale = false
            )
        )
    }

    @Test
    fun `denied permission with rationale can be requested again`() {
        assertEquals(
            MicrophonePermissionAction.REQUEST_PERMISSION,
            MicrophonePermissionPolicy.resolve(
                isGranted = false,
                requestedBefore = true,
                shouldShowRationale = true
            )
        )
    }

    @Test
    fun `previously requested permission without rationale routes to app settings`() {
        assertEquals(
            MicrophonePermissionAction.OPEN_APP_SETTINGS,
            MicrophonePermissionPolicy.resolve(
                isGranted = false,
                requestedBefore = true,
                shouldShowRationale = false
            )
        )
    }
}
