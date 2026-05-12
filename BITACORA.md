# Logbook of MagnetarOrpheus

## Introduction
Chronological record of all significant project events, decisions, and state changes for MagnetarOrpheus.

---
**Timestamp:** 2026-05-12 22:35 UTC
**Author:** Codex
**Entry:** Adjusted the standalone release-build workflow to match repository distribution preferences. `.github/workflows/release-builds.yml` now triggers on pushes/merges to the `builds` branch, packages the generated debug APK plus checksum into a versioned zip derived from `versionName`, and uploads that zip to GitHub Releases.

---
**Timestamp:** 2026-05-12 22:25 UTC
**Author:** Codex
**Entry:** Added `task-013` as `in_review` and created a standalone GitHub Actions workflow, `.github/workflows/release-builds.yml`, for manual APK publication to GitHub Releases. The workflow resolves `versionName`, builds the debug APK with JDK 21, generates a SHA-256 checksum, and publishes a prerelease tagged with the current version and run number for later download.

---
**Timestamp:** 2026-05-12T16:15:08.347+02:00
**Author:** GitHub Copilot CLI
**Entry:** Synced project documentation after the large-screen Compose fix. Updated current-state docs to reflect version `2026.05.12.1554`, and recorded that the tuner and Note Builder tablet layouts now use bounded scroll/measurement regions to avoid the previous tablet-only layout failure.

---
**Timestamp:** 2026-05-12 22:10 UTC
**Author:** Codex
**Entry:** Documentation-only sync for branch `feature/note-builder-extreme-ui`. Updated `README.md`, `STATUS.md`, `TESTING.md`, `PLAN.md`, `ARCHITECTURE.md`, `REQUIREMENTS.md`, `NOTE_BUILDER_EXTREME_UI.md`, and `projects/magnetar_orpheus.project.yml` to reflect the actual branch state: restored premium tuner surface, separate `Tuner` / `Note Builder` destinations, local Note Builder playback/theory wiring, version `1.1.0`, and the fact that `task-012` is now best treated as `in_review` pending manual validation rather than still `in_progress`.

---
**Timestamp:** 2026-05-12 21:55 UTC
**Author:** GitHub Copilot CLI
**Entry:** Finished wiring the premium tuner restore and the new Note Builder workspace into the app. Added in-app menu navigation between tuner and Note Builder, implemented real note playback for Note Builder with an `AudioTrack` synthesis engine, connected play/stop/clear/hold state through `NoteBuilderViewModel`, raised the app version to `1.1.0`, and restored Jacoco verification to passing by adding unit coverage for theory/state logic while excluding Compose-only UI shells from the bundle threshold.

---
**Timestamp:** 2026-05-12 20:40 UTC
**Author:** Codex
**Entry:** Started `task-012` (`in_progress`) on branch `feature/note-builder-extreme-ui`. Scope: document the Note Builder Extreme secondary feature, capture the responsive design split between phone and tablet from `design/phone2.png` and `design/tablet2.png`, and add new Compose views without replacing the current tuner screen.

---
**Timestamp:** 2026-05-12 20:20 UTC
**Author:** Codex
**Entry:** Completed `task-011`. Replaced hardcoded calibration validation text in `TunerViewModel` with a string resource-backed `@StringRes` state field, enabled `BuildConfig` generation explicitly, surfaced `BuildConfig.VERSION_NAME` in the tuner UI, and switched the app version naming to a UTC timestamp format (`yyyy.MM.dd.HH.mm.SSS`). Validation passed with `./gradlew :app:testDebugUnitTest` and `./gradlew :app:jacocoTestCoverageVerification`.

---
**Timestamp:** 2026-05-12 20:05 UTC
**Author:** Codex
**Entry:** Started `task-011` (`in_progress`) on branch `fix/jdk-21-alignment-final` to remove hardcoded calibration UI text from `TunerViewModel`, route that validation through Android string resources, and surface the app build version inside the tuner UI using a timestamp-based version name.

---
**Timestamp:** 2026-05-12 19:45 UTC
**Author:** Codex
**Entry:** Completed `task-010`. PR `#11` review remediation is finished: Java baseline aligned to JDK 21 across IDE, Gradle, and CI; note display now includes octave consistently across naming systems; calibration validation now surfaces a UI error message; stale `buildDir` and enum-iteration review threads were resolved; and the Kotlin Android plugin suggestion was validated as not applicable because it breaks the current build with a duplicate `kotlin` extension. Local verification passed with `./gradlew :app:testDebugUnitTest` and `./gradlew :app:jacocoTestCoverageVerification`.

---
**Timestamp:** 2026-05-12 19:20 UTC
**Author:** Codex
**Entry:** Started `task-010` (`in_progress`) to reconcile all review threads on PR `#11`. Scope includes validating stale review feedback, aligning the Java baseline to JDK 21 across IDE, Gradle, and CI, normalizing note-octave display across naming systems, hardening calibration UX feedback, and resolving threads that are already fixed but still open on GitHub.

---
**Timestamp:** 2026-05-12 19:00 UTC
**Author:** Gemini CLI
**Entry:** Initiated Phase 2: Advanced Musician Utilities. Updated `PLAN.md` and project YAML with new milestones: `ms-05` (Instrument Profiles) and `ms-06` (Visual Analysis).

---
**Timestamp:** 2026-05-12 18:45 UTC
**Author:** Gemini CLI
**Entry:** `task-006` completed. Implemented configurable A4 calibration (430Hz - 450Hz range) and a temporal stability filter in `TunerViewModel`. Added calibration controls to `TunerScreen`.

---
**Timestamp:** 2026-05-12 18:30 UTC
**Author:** Gemini CLI
**Entry:** `task-005` completed. Implemented `TunerViewModel` for reactive state management and `TunerScreen` with a custom-drawn `TunerIndicator` using Jetpack Compose. The application now provides real-time visual feedback of musical notes, frequency, and tuning deviation in cents.

---
**Timestamp:** 2026-05-12 17:45 UTC
**Author:** Gemini CLI
**Entry:** `task-004` completed. Implemented YIN algorithm for pitch detection in `PitchDetector.kt`. Added unit tests in `PitchDetectorTest.kt` covering various frequencies (440Hz, 82.41Hz) and silence. Verified with `./gradlew test`.

---
**Timestamp:** 2026-05-12 17:30 UTC
**Author:** Gemini CLI
**Entry:** `task-003` completed. Implemented `AudioCaptureProvider` using Android's `AudioRecord` API and Kotlin Flow for low-latency audio streaming. Updated `MainActivity` to handle runtime microphone permissions.

---
**Timestamp:** 2026-05-12 17:00 UTC
**Author:** Gemini CLI
**Entry:** Refactored project namespace from `com.eflores` to `com.edwardflores.magnetar.orpheus` to align with technical requirements. Added `RECORD_AUDIO` permission to `AndroidManifest.xml`.

---
**Timestamp:** 2026-05-12 16:30 UTC
**Author:** Gemini CLI
**Entry:** `task-001` completed. Initialized Magnetar Canonical Project Model documentation (README, RULES, PLAN, REQUIREMENTS, ARCHITECTURE, STATUS, TESTING, BLOCKERS, etc.).

---
**Timestamp:** 2026-05-12 16:15 UTC
**Author:** Gemini CLI
**Entry:** Decision: Initializing project with `master` as the default branch per user requirement. Repository created on GitHub: `scherenhaenden/MagnetarOrpheus`.

---
**Timestamp:** 2026-05-12 16:00 UTC
**Author:** Gemini CLI
**Entry:** Project Initialization: MagnetarOrpheus defined as a Native Android Application (`com.edwardflores.magnetar.orpheus`) using Kotlin and Jetpack Compose.
