# Privacy Policy

Last updated: 2026-09-13

BlazaresOrpheus is designed as an offline-first tuner and note-builder application. The current application does not include advertising SDKs, analytics SDKs, account login, cloud sync, or an Internet permission.

## Microphone Access

The tuner requires Android's `RECORD_AUDIO` permission to analyse pitch. Microphone samples are processed locally on the device for real-time tuning and waveform feedback.

The current application does not intentionally save microphone recordings to files, upload microphone audio, transmit derived pitch data to a server, or share microphone data with third parties. Audio buffers are used transiently by the in-memory processing pipeline and are released when capture stops.

Microphone capture is stopped when the tuner leaves its active lifecycle and when the user navigates away from the tuner.

## Local Settings

The application stores limited preferences locally on the device, such as app language and musical note-language choices. These settings are not used to identify the user and are not transmitted by the current application.

Android backup behavior may include app preferences according to the operating system's backup rules and the user's device/account settings.

## Network and Third Parties

The current Android manifest does not request the `INTERNET` permission. The production application therefore has no app-level network channel for uploading microphone data, settings, analytics, advertising identifiers, or other user content.

If a future version adds networking, analytics, advertising, accounts, crash-reporting services, or cloud functionality, this policy and the Google Play Data Safety declaration must be reviewed before that version is distributed.

## Data Retention and Deletion

Because the current application does not operate a backend or user account service, there is no server-side user dataset to retain or delete. Local app settings and app-managed data can be removed by clearing the application's storage or uninstalling the application through Android.

## Permissions

Current sensitive permission:

- `RECORD_AUDIO`: required only for tuner microphone input and pitch analysis.

Permission can be denied or revoked through Android system settings. Note Builder functionality does not require microphone capture.

## Changes to This Policy

Material privacy changes must be documented in the repository and reflected in store disclosures before a release containing those changes is published.
