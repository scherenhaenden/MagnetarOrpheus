# Testing Strategy for MagnetarOrpheus

## Types of Tests
1.  **Unit Tests (JUnit 5 + MockK):** Test DSP algorithms with synthetic audio buffers.
2.  **Instrumentation Tests (Compose Test):** Verify UI responsiveness to state changes.
3.  **Performance Tests:** Measure latency and CPU usage during real-time capture.

## Target Code Coverage
*   **DSP Logic:** 100%
*   **State Management:** 90%
*   **UI Components:** 70%

## Bug Reporting Process
1.  Identify issue.
2.  Log as a Blocker in `BLOCKERS.md` if it halts progress.
3.  Record resolution steps in `BITACORA.md`.
