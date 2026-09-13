# Google Play Data Safety Working Declaration

Last reviewed: 2026-09-13

This document is the repository-side working basis for completing the Google Play Console Data Safety form. It is not a substitute for checking the exact Play Console wording at release time.

## Current application behavior

The current Android application:

- requests `RECORD_AUDIO` for tuner input;
- processes microphone audio locally and transiently;
- does not request the Android `INTERNET` permission;
- explicitly disables cleartext network traffic at the application manifest level;
- does not include advertising, analytics, account-login, cloud-sync, or remote crash-reporting SDKs in the current Gradle dependencies;
- stores limited non-sensitive preferences including app/note language, selected instrument profile, A4 reference pitch, and the local marker used to manage microphone-permission UX;
- permits only the `orpheus_preferences.xml` SharedPreferences file to participate in Android cloud backup/device transfer according to the user's Android backup settings;
- does not intentionally write microphone recordings to persistent storage;
- does not intentionally transmit microphone samples or derived pitch information off-device.

## Play Data Safety interpretation

Based on the current implementation, microphone/audio data is used only for on-device functionality and is not collected by the developer in the sense of being transmitted off the device. No user data is currently shared with third parties by the app.

Android operating-system backup or device-transfer of the limited app preferences is distinct from an application-operated backend or cloud-sync service. The project does not receive those preference values on a developer server.

The microphone permission should still be disclosed wherever Google Play asks about sensitive permissions or core feature access, even when the data remains on-device.

## Pre-release verification checklist

Before every Play release, verify all of the following against the exact build being uploaded:

- `AndroidManifest.xml` and merged manifests contain no unexpected sensitive permissions.
- Runtime dependencies contain no newly introduced analytics, advertising, telemetry, crash-reporting, or networking SDKs.
- Android backup/data-extraction rules still include only intentionally restorable non-sensitive state.
- No new feature transmits audio, pitch history, settings, device identifiers, diagnostics, or user-created content.
- The privacy policy matches actual app behavior.
- If any networked or third-party feature is added, reassess collection, sharing, retention, deletion, security practices, and purpose declarations in Play Console before release.

## Important limitation

The answers above describe the repository state reviewed on 2026-09-13. Google Play's questions and definitions can change, so the final console declaration must be checked against the then-current form and the exact release artifact.
