# Testing Strategy for MagnetarOrpheus

## Types of Tests
1.  **Unit Tests (JUnit 5 + MockK):** Test DSP algorithms with synthetic audio buffers.
2.  **Instrumentation Tests (Compose Test):** Verify UI responsiveness to state changes.
3.  **Performance Tests:** Measure latency and CPU usage during real-time capture.
4.  **Music Theory / Workspace Logic Tests:** Validate note-set naming, Note Builder state transitions, and workspace-level models.

## Target Code Coverage
*   **DSP Logic:** 100%
*   **State Management:** 90%
*   **UI Components:** 70%

## Current Commands
Use these commands from the repository root:

1.  `./gradlew :app:testDebugUnitTest`
2.  `./gradlew :app:jacocoTestCoverageVerification`
3.  `./gradlew :app:compileDebugKotlin`

## Build Distribution
The repository now includes a separate workflow, `.github/workflows/release-builds.yml`, for manual build publication.

Expected behavior:

1.  Trigger the workflow from GitHub Actions using `workflow_dispatch`.
2.  The workflow builds the debug APK with JDK 21.
3.  The workflow creates a prerelease in GitHub Releases.
4.  The release includes the APK and a SHA-256 checksum file for later download.

## Current Coverage Focus
The current branch state emphasizes:

*   `PitchDetector` signal analysis coverage.
*   `TunerViewModel` state and calibration guard coverage.
*   Note Builder theory analysis coverage in `NoteBuilderMusicTheoryTest`.
*   Note Builder state and playback-control coverage in `NoteBuilderViewModelTest`.
*   Shared app-model coverage in `AppModelsTest`.

## Manual Validation Focus
Manual verification is still important for this branch because several changes are UI- and device-sensitive:

1.  Confirm the premium tuner visual restoration appears correctly in dark mode.
2.  Confirm in-app navigation between `Tuner` and `Note Builder`.
3.  Confirm phone and tablet Note Builder layouts behave as two distinct responsive experiences and that tablet workspaces load without infinite-height Compose measurement failures.
4.  Confirm note playback, stop, clear, and hold interactions on a real device or emulator.
5.  Confirm selected-note state remains coherent when switching Note Builder input modes.

## Bug Reporting Process
1.  Identify issue.
2.  Log as a Blocker in `BLOCKERS.md` if it halts progress.
3.  Record resolution steps in `BITACORA.md`.
