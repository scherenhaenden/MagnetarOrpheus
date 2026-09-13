# Pitch detection latency evidence

The JVM test `PitchDetectorProcessingBenchmarkTest` provides a deterministic performance datapoint for the CPU-side `PitchDetector.analyze` path. It generates the same 32 synthetic 44.1 kHz frames on every run, each containing one of six fixed musical frequencies plus deterministic low-level noise. The test warms up the detector, analyzes the fixed workload, checks that every frame remains valid and within 2 Hz of its known frequency, and prints total and average processing time.

Run it from the repository root with:

```text
./gradlew :app:testDebugUnitTest --tests com.edwardflores.magnetar.orpheus.audio.PitchDetectorProcessingBenchmarkTest
```

The Gradle test output includes a line like:

```text
PitchDetector JVM processing benchmark: frames=32, samplesPerFrame=2048, sampleRate=44100Hz, totalMillis=..., averageMicros=..., validFrames=32, checksum=...
```

The workload and correctness checks are reproducible; the reported elapsed time is informational. It is not guarded by a fixed threshold because a desktop JVM test is affected by host CPU, JIT compilation, scheduling, and Gradle overhead.

## What this does and does not measure

This test measures repeated processing of an already available `FloatArray` through `PitchDetector.analyze`. It does not measure microphone capture, `AudioRecord.read`, the device audio HAL, buffer scheduling, coroutine dispatch, UI state updates, stability filtering, or display time.

The production capture buffer is selected from Android's device-specific `AudioRecord.getMinBufferSize`, so the test's 2,048-sample frame is a fixed processing workload rather than a claim about every device's capture period. At 44.1 kHz, 2,048 samples represent about 46.4 ms of audio before any processing begins.

Consequently, this test can support the narrower statement that the pitch-processing path is exercised on the JVM and can be compared across code changes. It cannot establish the product requirement of less than 50 ms total loop latency, nor the architecture document's sub-20 ms Android audio-latency target. Those claims still require measurements on representative physical devices, including microphone-to-result timestamps under realistic capture and UI conditions.
