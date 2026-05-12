# Architecture of MagnetarOrpheus

## 1. System Overview
MagnetarOrpheus follows a strict **Clean Architecture** pattern, optimized for high-performance audio processing and reactive UI state management. The system is designed for sub-cent tuning precision and sub-20ms audio latency on Android API 26+.

## 2. High-Level Layers

### 2.1 Presentation Layer (UI)
- **Framework:** 100% Jetpack Compose.
- **State Management:** Unidirectional Data Flow (UDF) using `TunerViewModel`.
- **Reactive State:** Exposes a single `TunerUiState` data class via `StateFlow`.
- **Custom Graphics:** High-frequency components (Needle, Gauge, Waveform) are implemented using the `Canvas` API and `drawBehind` to ensure 60FPS performance without excessive recompositions.

### 2.2 Domain & Processing Layer (DSP)
- **PitchEstimator:** Core logic for fundamental frequency detection.
- **Algorithm:** **YIN Pitch Detection**.
    - **Difference Function:** $d_t(\tau) = \sum_{j=1}^W (x_j - x_{j+\tau})^2$.
    - **CMND:** Cumulative Mean Normalized Difference.
    - **Interpolation:** Parabolic interpolation for sub-sample accuracy.
- **Stability Engine:** Implements temporal filters (Exponential Moving Average) to stabilize measurements.

### 2.3 Data & Acquisition Layer
- **Audio Provider:** Wrapper for the Android `AudioRecord` API.
- **Data Streaming:** Uses Kotlin `Flow` to stream raw PCM `FloatArray` chunks from hardware to the DSP layer.
- **Concurrency:** Audio acquisition runs on a dedicated `Dispatchers.IO` thread to prevent UI lag.

## 3. Technology Stack
- **Language:** Kotlin 1.9+
- **UI:** Jetpack Compose (Material 3)
- **Asynchrony:** Kotlin Coroutines & Flow
- **Audio API:** Android AudioRecord (Low-latency config)
- **Build System:** Gradle (Kotlin DSL) with JaCoCo for coverage enforcement.

## 4. Design Patterns
- **Repository Pattern:** For instrument profiles and calibration settings.
- **Factory Pattern:** For creating naming system strategies (Scientific, Syllabic, German).
- **Observer Pattern:** Via Kotlin Flow for real-time audio and UI state updates.
