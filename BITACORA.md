# Logbook of BlazaresOrpheus

## Introduction
Chronological record of all significant project events, decisions, and state changes for BlazaresOrpheus.

---
**Timestamp:** 2026-09-13 15:25 UTC
**Author:** ChatGPT
**Entry:** Continued `task-008` and `task-018` from PR #35 on branch `fix/production-hardening-continuation-2026-09-13`. Expanded the canonical instrument catalog from three standard-only profiles to seven profiles (Guitar Standard/Drop D/D Standard, Bass Standard/Drop D, Ukulele Soprano/Low G), added stable profile identity to tuner state, removed the duplicated UI profile table, added Compose/unit coverage for profile selection, extracted deterministic microphone-permission and lifecycle controllers with unit coverage, added harmonic-rich/noisy pitch-accuracy tests, added release lint, dependency review, CodeQL, Dependabot, signed artifact verification, explicit Android backup scopes, and cleartext-traffic hardening. Physical-device latency/accuracy and the real release keystore remain external validation items.

---
**Timestamp:** 2026-09-13 15:10 UTC
**Author:** ChatGPT
**Entry:** Reconciled `task-007` to `in_review` after confirming that the Guitar, Bass, and Ukulele profile data layer already exists on the current codebase, and started `task-008` (`in_progress`) as the second ChatGPT WIP slot. The remaining gap is behavioral/UI wiring: `TunerViewModel` is still hard-coded to Guitar Standard and the current tuner-mode dialog dismisses without changing profiles.

---
**Timestamp:** 2026-09-13 14:54 UTC
**Author:** ChatGPT
**Entry:** Started `task-018` (`in_progress`) on branch `fix/production-hardening-2026-09-13`. Scope is intentionally grouped as one production-hardening task to respect WIP limits: stop tuner capture when the app backgrounds, make microphone permission user-initiated, harden release output with R8 plus AAB/APK and optional CI signing, replace the placeholder security policy, document privacy/Data Safety posture, and extend regression coverage. Hardware-dependent latency/accuracy validation remains evidence work rather than a claim to be manufactured in CI.

---
**Timestamp:** 2026-09-13 00:45 UTC
**Author:** Codex
**Entry:** Resolved PR #32 review feedback. Android 12+ now uses the native dark platform splash with the Orpheus figure as the only initial splash, older releases retain the Compose animation, the ring and figure scale from available width without clipping on narrow phones, and `MIGRATION.md` documents the new package identity migration strategy.

---
**Timestamp:** 2026-09-13 00:00 UTC
**Author:** Codex
**Entry:** Started and completed the local Blazares Orpheus rebrand. Migrated the Android namespace and application ID to `com.blazares.orpheus`, updated shipped and repository-facing branding, renamed the local project metadata, refreshed launcher and design screenshot assets, and intentionally left the external Git remote unchanged pending explicit authorization.

---
**Timestamp:** 2026-09-13 00:20 UTC
**Author:** Codex
**Entry:** Added the Orpheus loading splash: a short Compose transition with an animated green/cyan waveform ring, a transparent Orpheus-playing-lyre illustration, and resource-backed BLAZARES / ORPHEUS branding. The existing tuner and Note Builder content remain unchanged behind the splash.

---
**Timestamp:** 2026-05-12 23:10 UTC
**Author:** Codex
**Entry:** Started `task-014` (`in_progress`) to add a safe settings surface for `app language` and `note language`. Immediate focus is on shared settings UI in the existing header, app-language options (`en`, `es`, `de`, `it`, `pt`, `nl`, `fi`), and second-feature note-language support without destabilizing the tuner workflow.

---
**Timestamp:** 2026-05-12 22:45 UTC
**Author:** Codex
**Entry:** Expanded `.github/workflows/release-builds.yml` so GitHub Releases now receives all three distribution assets from the `builds` branch pipeline: the versioned `.apk`, its `.apk.sha256` checksum, and the bundled `.zip`. The release source archives remain provided automatically by GitHub for the generated tag.

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
**Entry:** Documentation-only sync for branch `feature/note-builder-extreme-ui`. Updated `README.md`, `STATUS.md`, `TESTING.md`, `PLAN.md`, `ARCHITECTURE.md`, `REQUIREMENTS.md`, `NOTE_BUILDER_EXTREME_UI.md`, and `projects/blazares_orpheus.project.yml` to reflect the actual branch state: restored premium tuner surface, separate `Tuner` / `Note Builder` destinations, local Note Builder playback/theory wiring, version `1.1.0`, and the fact that `task-012` is now best treated as `in_review` pending manual validation rather than still `in_progress`.

---
**Timestamp:** 2026-05-12 21:55 UTC
**Author:** GitHub Copilot CLI
**Entry:** Finished wiring the premium tuner restore and the new Note Builder workspace into the app. Added in-app menu navigation between tuner and Note Builder, implemented real note playback for Note Builder with an `AudioTrack` synthesis engine, connected play/stop/clear/hold state through `NoteBuilderViewModel`, raised the app version to `1.1.0`, and restored JaCoCo verification to passing by adding unit coverage for theory/state logic while excluding Compose-only UI shells from the bundle threshold.

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
**Entry:** Refactored project namespace from `com.eflores` to `com.blazares.orpheus` to align with technical requirements. Added `RECORD_AUDIO` permission to `AndroidManifest.xml`.

---
**Timestamp:** 2026-05-12 16:30 UTC
**Author:** Gemini CLI
**Entry:** `task-001` completed. Initialized Blazares Canonical Project Model documentation (README, RULES, PLAN, REQUIREMENTS, ARCHITECTURE, STATUS, TESTING, BLOCKERS, etc.).

---
**Timestamp:** 2026-05-12 16:15 UTC
**Author:** Gemini CLI
**Entry:** Decision: Initializing the project with `master` as the default branch per user requirement. The external GitHub repository was created at that time; its remote name remains outside this local rebrand scope.

---
**Timestamp:** 2026-05-12 16:00 UTC
**Author:** Gemini CLI
**Entry:** Project Initialization: BlazaresOrpheus defined as a Native Android Application (`com.blazares.orpheus`) using Kotlin and Jetpack Compose.
