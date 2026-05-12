package com.edwardflores.magnetar.orpheus.models

/**
 * Represents a specific note in an instrument's tuning.
 */
data class TuningNote(
    val name: String,
    val frequency: Double,
    val stringNumber: Int
)

/**
 * Defines a tuning profile for a musical instrument.
 */
data class InstrumentProfile(
    val id: String,
    val name: String,
    val notes: List<TuningNote>
)

object InstrumentProfiles {
    val GuitarStandard = InstrumentProfile(
        id = "guitar_std",
        name = "Guitar (Standard)",
        notes = listOf(
            TuningNote("E2", 82.41, 6),
            TuningNote("A2", 110.00, 5),
            TuningNote("D3", 146.83, 4),
            TuningNote("G3", 196.00, 3),
            TuningNote("B3", 246.94, 2),
            TuningNote("E4", 329.63, 1)
        )
    )

    val BassStandard = InstrumentProfile(
        id = "bass_std",
        name = "Bass (Standard)",
        notes = listOf(
            TuningNote("E1", 41.20, 4),
            TuningNote("A1", 55.00, 3),
            TuningNote("D2", 73.42, 2),
            TuningNote("G2", 98.00, 1)
        )
    )

    val UkuleleStandard = InstrumentProfile(
        id = "ukulele_std",
        name = "Ukulele (Soprano)",
        notes = listOf(
            TuningNote("G4", 392.00, 4),
            TuningNote("C4", 261.63, 3),
            TuningNote("E4", 329.63, 2),
            TuningNote("A4", 440.00, 1)
        )
    )

    val All = listOf(GuitarStandard, BassStandard, UkuleleStandard)
}
