package com.edwardflores.magnetar.orpheus.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InstrumentProfileTest {

    @Test
    fun `GuitarStandard profile has correct notes`() {
        val profile = InstrumentProfiles.GuitarStandard
        assertEquals("Guitar (Standard)", profile.name)
        assertEquals(6, profile.notes.size)
        assertEquals("E2", profile.notes[0].name)
        assertEquals(82.41, profile.notes[0].frequency, 0.01)
    }

    @Test
    fun `BassStandard profile has correct notes`() {
        val profile = InstrumentProfiles.BassStandard
        assertEquals("Bass (Standard)", profile.name)
        assertEquals(4, profile.notes.size)
        assertEquals("G2", profile.notes.last().name)
    }

    @Test
    fun `UkuleleStandard profile has correct notes`() {
        val profile = InstrumentProfiles.UkuleleStandard
        assertEquals("Ukulele (Soprano)", profile.name)
        assertEquals(4, profile.notes.size)
        assertEquals("A4", profile.notes.last().name)
    }

    @Test
    fun `All list contains all standard profiles`() {
        val all = InstrumentProfiles.All
        assertTrue(all.contains(InstrumentProfiles.GuitarStandard))
        assertTrue(all.contains(InstrumentProfiles.BassStandard))
        assertTrue(all.contains(InstrumentProfiles.UkuleleStandard))
        assertEquals(3, all.size)
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
        assertEquals(1, note.stringNumber) // Explicitly cover getter
    }

    @Test
    fun `InstrumentProfile data class methods`() {
        val profile = InstrumentProfile("id", "name", emptyList())
        val profile2 = profile.copy()
        assertEquals(profile, profile2)
        assertEquals(profile.hashCode(), profile2.hashCode())
        assertEquals("InstrumentProfile(id=id, name=name, notes=[])", profile.toString())
        assertEquals("id", profile.component1())
        assertEquals("name", profile.component2())
        assertEquals(emptyList<TuningNote>(), profile.component3())
        assertEquals("id", profile.id) // Explicitly cover getter
    }
}
