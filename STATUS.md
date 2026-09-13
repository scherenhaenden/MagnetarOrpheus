# Status of BlazaresOrpheus

## Progress Summary
**Phase 1 (Tuner MVP): 100%**
[████████████████████]

**Phase 2 (Advanced Musician Utilities): In Progress**
[█████████████░░░░░░░]

**Phase 3 (Production Hardening): In Progress**
[███████████████░░░░░]

## Current Milestones
* `ms-01`: Project Setup & Canon - **Completed**
* `ms-02`: Audio Foundation - **Completed**
* `ms-03`: Core Tuner UI - **Completed**
* `ms-04`: MVP Release - **Completed**
* `ms-05`: Instrument Profiles / Note Builder Expansion - **In Progress**
* `ms-06`: Visual Analysis - **Planned**
* `ms-07`: Production Hardening - **In Progress**

## Health Check
* **Architecture:** ✅ Defined
* **Documentation:** 🟡 Production-hardening docs synchronized on PR #35; final merge sync still pending.
* **CI/CD:** 🟡 Release APK/AAB, minification, optional signing, and emulator instrumentation are configured; PR #35 validation is pending.
* **Test Coverage:** ✅ High unit/state coverage with JaCoCo verification plus dedicated temporal pitch-tracker tests.
* **Privacy/Security:** 🟡 Repository policies are now explicit; final Play Console declarations and release-key configuration remain external release steps.

## Current Branch Highlights
Branch: `fix/production-hardening-2026-09-13` / PR #35.

* Existing tuner lifecycle handling stops capture when the composable/activity lifecycle leaves the foreground and when navigating away from the tuner.
* Microphone permission is now requested only after an explicit user action; permission state is refreshed when returning to the app.
* The frame-level YIN/noise gate is now followed by `TemporalPitchTracker`, which uses a rolling median, continuity window, confirmed large-note changes, and stronger octave-jump hysteresis.
* `release` builds enable R8 and resource shrinking.
* The release pipeline now produces APK and AAB artifacts instead of publishing a debug APK as a release build.
* CI release signing is supported through four `ORPHEUS_RELEASE_*` secrets without storing signing material in the repository.
* Pull-request CI verifies unit coverage, release APK/AAB builds, and Android instrumentation on API 26 and API 35.
* `SECURITY.md`, `PRIVACY.md`, and `DATA_SAFETY.md` now document the current security/privacy posture and remaining release obligations.

## Verification State
* Static review of PR #35 confirms the branch is based directly on the current `master` and contains only the intended hardening changes.
* Automated PR validation must pass before `task-018` can move from `in_progress` to `in_review`/`done`.
* Physical-device validation is still required for end-to-end microphone latency, ±1-cent claims, noise robustness, capture behavior across OEMs, and signed Play-distribution confidence.

## Risks and Mitigations

| Risk | Severity | Mitigation |
| :--- | :--- | :--- |
| Audio Latency | High | Keep low-level `AudioRecord`; measure microphone-to-screen latency on physical devices before making a production claim. |
| Pitch Stability | Medium | Frame-level confidence/noise gating plus temporal median/hysteresis tracker; validate with real instruments and background noise. |
| Device Fragmentation | Medium | API 26/API 35 emulator matrix plus planned physical-device coverage across OEMs/device tiers. |
| Release Integrity | High | R8/resource shrinking, APK+AAB build verification, checksums, and optional secret-backed release signing; real key material still must be configured. |
| Microphone Permission UX | Medium | User-initiated permission request, permission refresh on resume, and permission-gated UI. |
| Note Playback Variability | Medium | Validate `AudioTrack` playback behavior on multiple Android devices and API levels. |
| Responsive UI Density | Medium | Keep phone/tablet layouts behaviorally distinct and verify manually on multiple form factors. |
