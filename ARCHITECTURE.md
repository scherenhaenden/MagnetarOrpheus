# Architecture of MagnetarOrpheus

## System Overview
MagnetarOrpheus follows a Clean Architecture pattern, separating audio acquisition, signal processing, and reactive UI state.

The product now has two UI feature surfaces with shared branding but different interaction goals:

1.  **Tuner:** Real-time pitch-detection and calibration workspace.
2.  **Note Builder:** Secondary note/chord construction workspace with keyboard and grid-based input.

## High-Level Layers
1.  **Presentation Layer (UI):**
    *   **Jetpack Compose:** Declarative UI for tuner and note-builder views.
    *   **Tuner Surface:** Focused pitch detection, calibration, and tuning feedback.
    *   **Note Builder Surface:** Separate responsive workspace for note selection, note playback, and musical interpretation.
    *   **AppDestination Routing:** Branch-local navigation layer that keeps Tuner and Note Builder as distinct feature destinations.
    *   **Reusable UI Components:** Premium tuner visuals are decomposed into component-level building blocks under `ui/components` and `ui/screen`.
    *   **StateFlow:** Reactive state updates from the processing layer.
2.  **Service/Processing Layer (DSP):**
    *   **PitchEstimator:** Implementation of YIN/Autocorrelation algorithms.
    *   **BufferManager:** Handles incoming audio chunks.
    *   **Playback / Theory Layer:** Note Builder music-theory naming and `AudioTrack`-based note playback infrastructure for note-set auditioning.
3.  **Data/Capture Layer:**
    *   **AudioCaptureProvider:** Wrapper for Android `AudioRecord`.
    *   **CoroutineScope:** Manages the lifecycle of audio streaming threads.

## Technology Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose
*   **Concurrency:** Kotlin Coroutines & Flow
*   **Audio API:** Android AudioRecord
*   **Build System:** Gradle (Kotlin DSL)
