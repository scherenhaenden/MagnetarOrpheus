package com.blazares.orpheus.permissions

enum class MicrophonePermissionAction {
    AVAILABLE,
    REQUEST_PERMISSION,
    OPEN_APP_SETTINGS
}

/**
 * Converts Android's permission signals into a deterministic UI action.
 *
 * `shouldShowRequestPermissionRationale()` is false both before the first request and after Android
 * has stopped showing the runtime permission dialog. `requestedBefore` disambiguates those cases.
 */
object MicrophonePermissionPolicy {
    fun resolve(
        isGranted: Boolean,
        requestedBefore: Boolean,
        shouldShowRationale: Boolean
    ): MicrophonePermissionAction = when {
        isGranted -> MicrophonePermissionAction.AVAILABLE
        requestedBefore && !shouldShowRationale -> MicrophonePermissionAction.OPEN_APP_SETTINGS
        else -> MicrophonePermissionAction.REQUEST_PERMISSION
    }
}
