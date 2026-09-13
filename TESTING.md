# Testing Strategy for BlazaresOrpheus

## Test Layers
1. **Unit tests (JUnit 4 + MockK):** DSP, pitch tracking, state management, music theory, and workspace logic.
2. **Instrumentation / Compose tests:** Activity and UI behavior on Android emulators.
3. **Release-build verification:** Prove that the minified release APK and Android App Bundle can be produced on CI.
4. **Physical-device validation:** Measure end-to-end audio latency, tuner accuracy, microphone behavior, and OEM/device-specific audio differences.

## Coverage Targets
* **DSP / pitch logic:** 100% where deterministic synthetic input is practical.
* **State management:** 90%+.
* **UI behavior:** Cover critical navigation/permission/settings flows with instrumentation; visual fidelity still requires device/manual review.

## Local Commands
Use these commands from the repository root:

1. `./gradlew :app:testDebugUnitTest`
2. `./gradlew :app:jacocoTestCoverageVerification`
3. `./gradlew :app:assembleRelease :app:bundleRelease`
4. `./gradlew connectedDebugAndroidTest` when an emulator/device is attached.

## Pull Request CI
`.github/workflows/tests.yml` verifies pull requests targeting `master` or `tests` with:

* unit tests plus the configured JaCoCo coverage gate;
* a minified `release` APK build;
* a `release` Android App Bundle build;
* instrumentation tests on API 26 (the minimum supported API) and API 35 (modern Android coverage).

The emulator matrix is intentionally not presented as full device-fragmentation proof. Audio HAL, microphone hardware, vendor power management, sample-rate behavior, and permission/UI differences still require representative physical devices.

## Release Distribution
`.github/workflows/release-builds.yml` publishes release artifacts from the `builds` branch or manual dispatch.

Expected behavior:

1. Resolve the current app `versionName`.
2. Detect whether all four release-signing secrets are configured.
3. Decode the release keystore only inside the CI runner when signing is configured.
4. Build the R8-minified/resource-shrunk release APK and AAB.
5. Generate SHA-256 checksums.
6. Package the release artifacts into a versioned zip.
7. Publish the `.apk`, `.aab`, checksum file, and `.zip` as a GitHub prerelease.

Required repository secrets for signed output:

* `ORPHEUS_RELEASE_KEYSTORE_BASE64`
* `ORPHEUS_RELEASE_KEYSTORE_PASSWORD`
* `ORPHEUS_RELEASE_KEY_ALIAS`
* `ORPHEUS_RELEASE_KEY_PASSWORD`

Without those secrets the workflow can still verify/build release artifacts, but they are not suitable as a signed production distribution.

## Current Regression Focus
The current branch emphasizes:

* `PitchDetector` frame-level signal analysis;
* `TemporalPitchTracker` median smoothing, outlier rejection, note-change confirmation, octave-jump hysteresis, invalid input, and reset behavior;
* `TunerViewModel` capture state, calibration, naming, waveform/history state, error recovery, stop/restart behavior, and integration with temporal tracking;
* Note Builder theory and state transitions;
* permission-gated launch UI and explicit microphone-permission action;
* navigation and language/settings UI.

## Performance and Accuracy Evidence
Synthetic JVM tests and benchmarks are useful for algorithm correctness and processing cost, but they do not prove total microphone-to-screen latency or ±1-cent accuracy on production devices.

Before making strong production claims, validate on representative physical Android hardware across low/mid/high device tiers and multiple OEMs. Capture at minimum:

* known reference tones across the supported frequency range;
* cents error distribution;
* time to first stable lock;
* response time when changing notes/strings;
* behavior with background noise and strong harmonics;
* end-to-end microphone-to-visible-update latency;
* capture restart after background/foreground transitions.

## Bug Reporting Process
1. Identify the issue.
2. Log it as a blocker in `BLOCKERS.md` if it halts progress.
3. Record resolution steps and important assumptions in `BITACORA.md`.
