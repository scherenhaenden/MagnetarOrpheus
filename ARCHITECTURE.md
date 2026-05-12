# Architecture of MagnetarOrpheus

## System Overview
MagnetarOrpheus follows a Clean Architecture pattern, separating audio acquisition, signal processing, and reactive UI state.

## High-Level Layers
1.  **Presentation Layer (UI):**
    *   **Jetpack Compose:** Declarative UI for tuner visuals.
    *   **StateFlow:** Reactive state updates from the processing layer.
2.  **Service/Processing Layer (DSP):**
    *   **PitchEstimator:** Implementation of YIN/Autocorrelation algorithms.
    *   **BufferManager:** Handles incoming audio chunks.
3.  **Data/Capture Layer:**
    *   **AudioCaptureProvider:** Wrapper for Android `AudioRecord`.
    *   **CoroutineScope:** Manages the lifecycle of audio streaming threads.

## Technology Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose
*   **Concurrency:** Kotlin Coroutines & Flow
*   **Audio API:** Android AudioRecord
*   **Build System:** Gradle (Kotlin DSL)
