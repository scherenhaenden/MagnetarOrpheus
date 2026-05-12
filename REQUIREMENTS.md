# Requirements for MagnetarOrpheus

## Functional Requirements
*   **Capture Audio (Must-Have):** Capture real-time microphone input using `AudioRecord` API.
*   **Pitch Detection (Must-Have):** Analyze audio signals to estimate fundamental frequency (Hz).
*   **Note Identification (Must-Have):** Map frequency to musical notes (e.g., E2, A2).
*   **Visual Feedback (Must-Have):** Real-time UI showing tuning deviation (Flat/Sharp/In-Tune).
*   **Calibration (Should-Have):** User-configurable A4 reference frequency (default 440Hz).
*   **Stability Filtering (Should-Have):** Reduce "flicker" in pitch detection for stable readings.
*   **Instrument Profiles (Could-Have):** Pre-set tunings for Guitar, Bass, Ukulele.
*   **Note Builder Workspace (Should-Have):** Provide a separate note-selection and note-playback feature that does not replace the tuner UI.
*   **Shared Note Selection State (Should-Have):** The Note Builder keyboard and octave grid must edit the same selected-note set.
*   **Responsive Input Model (Should-Have):** On phones, Note Builder must show either Keyboard or Grid mode at a time. On tablets, it may show both in a workspace layout.

## Non-Functional Requirements
*   **Latency:** Low-latency DSP pipeline (target < 50ms total loop).
*   **Accuracy:** Precision within +/- 1 cent.
*   **Performance:** Native Android performance using Kotlin Coroutines for off-main-thread processing.
*   **UI/UX:** Modern, responsive interface using Jetpack Compose.
*   **Minimum Version:** Android 8.0 (API 26).
*   **Architecture:** Clean, modular structure for future expansion.
*   **Feature Separation:** Tuner and Note Builder must remain distinct feature surfaces even when they share a visual language.
