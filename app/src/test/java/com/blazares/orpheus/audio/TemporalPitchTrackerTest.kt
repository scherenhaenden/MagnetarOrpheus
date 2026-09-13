package com.blazares.orpheus.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TemporalPitchTrackerTest {

    @Test
    fun `first valid candidate locks immediately`() {
        val tracker = TemporalPitchTracker()

        assertEquals(440.0, tracker.update(440.0)!!, 0.001)
    }

    @Test
    fun `nearby candidates use a bounded rolling median`() {
        val tracker = TemporalPitchTracker()

        tracker.update(440.0)
        assertEquals(441.0, tracker.update(442.0)!!, 0.001)
        assertEquals(442.0, tracker.update(444.0)!!, 0.001)
        assertEquals(444.0, tracker.update(446.0)!!, 0.001)
    }

    @Test
    fun `isolated large jump does not steal the current lock`() {
        val tracker = TemporalPitchTracker()

        tracker.update(440.0)
        tracker.update(440.2)
        tracker.update(439.9)

        val afterOutlier = tracker.update(329.63)

        assertEquals(440.0, afterOutlier!!, 0.25)
    }

    @Test
    fun `consistent non octave note change switches after confirmation`() {
        val tracker = TemporalPitchTracker()

        tracker.update(440.0)
        val firstDifferentFrame = tracker.update(329.63)
        val confirmedChange = tracker.update(329.7)

        assertEquals(440.0, firstDifferentFrame!!, 0.001)
        assertEquals(329.665, confirmedChange!!, 0.01)
    }

    @Test
    fun `octave sized jump requires an extra confirmation`() {
        val tracker = TemporalPitchTracker()

        tracker.update(440.0)
        assertEquals(440.0, tracker.update(880.0)!!, 0.001)
        assertEquals(440.0, tracker.update(879.8)!!, 0.001)

        val confirmedOctave = tracker.update(880.2)

        assertEquals(880.0, confirmedOctave!!, 0.25)
    }

    @Test
    fun `unrelated alternating outliers never accumulate switch confirmation`() {
        val tracker = TemporalPitchTracker()

        tracker.update(440.0)
        tracker.update(329.63)
        tracker.update(523.25)
        val stable = tracker.update(440.1)

        assertEquals(440.05, stable!!, 0.1)
    }

    @Test
    fun `invalid candidates are ignored before and after lock`() {
        val tracker = TemporalPitchTracker()

        assertNull(tracker.update(Double.NaN))
        assertNull(tracker.update(0.0))
        assertNull(tracker.update(-440.0))

        tracker.update(440.0)
        assertEquals(440.0, tracker.update(Double.POSITIVE_INFINITY)!!, 0.001)
    }

    @Test
    fun `reset removes the previous pitch lock and pending switch`() {
        val tracker = TemporalPitchTracker()

        tracker.update(440.0)
        tracker.update(329.63)
        tracker.reset()

        assertEquals(329.63, tracker.update(329.63)!!, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero median window is rejected`() {
        TemporalPitchTracker(medianWindowSize = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero continuity range is rejected`() {
        TemporalPitchTracker(continuityCents = 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero switch cluster range is rejected`() {
        TemporalPitchTracker(switchClusterCents = 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero switch confirmation count is rejected`() {
        TemporalPitchTracker(switchConfirmationFrames = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `octave confirmation cannot be shorter than normal confirmation`() {
        TemporalPitchTracker(
            switchConfirmationFrames = 3,
            octaveSwitchConfirmationFrames = 2
        )
    }
}
