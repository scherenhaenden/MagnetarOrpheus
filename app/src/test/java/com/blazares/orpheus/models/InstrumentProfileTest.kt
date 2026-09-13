package com.blazares.orpheus.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InstrumentProfileTest {

    @Test
    fun `GuitarStandard profile has correct notes and labels`() {
        val profile = InstrumentProfiles.GuitarStandard
        assertEquals("Guitar (Standard)", profile.name)
        assertEquals("Guitar", profile.instrumentName)
        assertEquals("Standard", profile.tuningName)
        assertEquals("EADGBE", profile.noteSequence)
        assertEquals("Standard (EADGBE)", profile.tuningDisplayName)
        assertEquals(6, profile.notes.size)
        assertEquals("E2", profile.notes[0].name)
        assertEquals(82.41, profile.notes[0].frequency, 0.01)
    }

    @Test
    fun `alternate guitar profiles expose their actual note sequences`() {
        assertEquals("Drop D (DADGBE)", InstrumentProfiles.GuitarDropD.tuningDisplayName)
        assertEquals("D Standard (DGCFAD)", InstrumentProfiles.GuitarDStandard.tuningDisplayName)
    }

    @Test
    fun `Bass profiles cover standard and drop D`() {
        val standard = InstrumentProfiles.BassStandard
        assertEquals("Bass", standard.instrumentName)
        assertEquals("Standard (EADG)", standard.tuningDisplayName)
        assertEquals("G2", standard.notes.last().name)

        val dropD = InstrumentProfiles.BassDropD
        assertEquals("Drop D (DADG)", dropD.tuningDisplayName)
        assertEquals(36.71, dropD.notes.first().frequency, 0.01)
    }

    @Test
    fun `Ukulele profiles cover high and low G variants`() {
        val standard = InstrumentProfiles.UkuleleStandard
        assertEquals("Soprano (GCEA)", standard.tuningDisplayName)
        assertEquals("A4", standard.notes.last().name)

        val lowG = InstrumentProfiles.UkuleleLowG
        assertEquals("Low G (GCEA)", lowG.tuningDisplayName)
        assertEquals("G3", lowG.notes.first().name)
    }

    @Test
    fun `nearest target identifies the closest profile string`() {
        val target = requireNotNull(InstrumentProfiles.GuitarDropD.nearestTarget(73.42))

        assertEquals("D2", target.note.name)
        assertEquals(6, target.note.stringNumber)
        assertEquals(73.42, target.calibratedFrequencyHz, 0.001)
        assertEquals(0, target.centsFromTarget)
    }

    @Test
    fun `nearest target follows user A4 calibration`() {
        val calibratedA4 = 442.0
        val expectedScaledA2 = 110.0 * calibratedA4 / 440.0
        val target = requireNotNull(
            InstrumentProfiles.GuitarStandard.nearestTarget(
                frequencyHz = expectedScaledA2,
                referenceA4Hz = calibratedA4
            )
        )

        assertEquals("A2", target.note.name)
        assertEquals(expectedScaledA2, target.calibratedFrequencyHz, 0.0001)
        assertEquals(0, target.centsFromTarget)
    }

    @Test
    fun `nearest target reports signed cents from selected string`() {
        val target = requireNotNull(InstrumentProfiles.GuitarStandard.nearestTarget(110.64))

        assertEquals("A2", target.note.name)
        assertTrue(target.centsFromTarget > 0)
        assertTrue(target.centsFromTarget in 9..11)
    }

    @Test
    fun `nearest target rejects invalid input and empty profiles`() {
        val empty = InstrumentProfile("empty", "Empty (None)", emptyList())

        assertNull(InstrumentProfiles.GuitarStandard.nearestTarget(0.0))
        assertNull(InstrumentProfiles.GuitarStandard.nearestTarget(Double.NaN))
        assertNull(InstrumentProfiles.GuitarStandard.nearestTarget(110.0, 0.0))
        assertNull(InstrumentProfiles.GuitarStandard.nearestTarget(110.0, Double.NaN))
        assertNull(empty.nearestTarget(440.0))
    }

    @Test
    fun `All list contains unique valid production profiles`() {
        val all = InstrumentProfiles.All

        assertEquals(7, all.size)
        assertEquals(all.size, all.map { it.id }.distinct().size)
        assertTrue(all.contains(InstrumentProfiles.GuitarStandard))
        assertTrue(all.contains(InstrumentProfiles.GuitarDropD))
        assertTrue(all.contains(InstrumentProfiles.GuitarDStandard))
        assertTrue(all.contains(InstrumentProfiles.BassStandard))
        assertTrue(all.contains(InstrumentProfiles.BassDropD))
        assertTrue(all.contains(InstrumentProfiles.UkuleleStandard))
        assertTrue(all.contains(InstrumentProfiles.UkuleleLowG))
        assertTrue(all.all { profile -> profile.notes.isNotEmpty() })
        assertTrue(all.flatMap { it.notes }.all { note -> note.frequency > 0.0 && note.stringNumber > 0 })
    }

    @Test
    fun `TuningNote data class methods`() {
        val note = TuningNote("A4", 440.0, 1)
        val note2 = note.copy()
        assertEquals(note, note2)
        assertEquals(note.hashCode(), note2.hashCode())
        assertEquals("TuningNote(name=A4, frequency=440.0, stringNumber=1)", note.toString())
        assertEquals("A4", note.component1())
        assertEquals(440.0, note.component2(), 0.0)
        assertEquals(1, note.component3())
        assertEquals(1, note.stringNumber)
    }

    @Test
    fun `InstrumentProfile data class methods and fallback labels are safe`() {
        val profile = InstrumentProfile("id", "name", emptyList())
        val profile2 = profile.copy()
        assertEquals(profile, profile2)
        assertEquals(profile.hashCode(), profile2.hashCode())
        assertEquals("InstrumentProfile(id=id, name=name, notes=[])", profile.toString())
        assertEquals("id", profile.component1())
        assertEquals("name", profile.component2())
        assertEquals(emptyList<TuningNote>(), profile.component3())
        assertEquals("id", profile.id)
        assertEquals("name", profile.instrumentName)
        assertEquals("name", profile.tuningName)
        assertEquals("", profile.noteSequence)
        assertEquals("name ()", profile.tuningDisplayName)
    }

    @Test
    fun `note sequence preserves flat pitch names`() {
        val profile = InstrumentProfile(
            id = "flat",
            name = "Clarinet (Bb)",
            notes = listOf(TuningNote("Bb3", 233.08, 1))
        )

        assertEquals("Bb", profile.noteSequence)
        assertEquals("Bb (Bb)", profile.tuningDisplayName)
    }
}
