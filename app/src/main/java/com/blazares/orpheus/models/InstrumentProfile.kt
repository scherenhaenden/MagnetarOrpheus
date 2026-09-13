package com.blazares.orpheus.models

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
 *
 * `name` intentionally keeps the compact `Instrument (Tuning)` representation used by the
 * canonical data model. The derived properties below provide one shared display contract so the
 * UI and ViewModel do not need to duplicate/parsing logic independently.
 */
data class InstrumentProfile(
    val id: String,
    val name: String,
    val notes: List<TuningNote>
) {
    val instrumentName: String
        get() = name.substringBefore(" (")

    val tuningName: String
        get() = name.substringAfter(" (").removeSuffix(")")

    val noteSequence: String
        get() = notes.joinToString("") { note ->
            note.name.takeWhile { character -> character.isLetter() || character == '#' || character == 'b' }
        }

    val tuningDisplayName: String
        get() = "$tuningName ($noteSequence)"
}

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

    val GuitarDropD = InstrumentProfile(
        id = "guitar_drop_d",
        name = "Guitar (Drop D)",
        notes = listOf(
            TuningNote("D2", 73.42, 6),
            TuningNote("A2", 110.00, 5),
            TuningNote("D3", 146.83, 4),
            TuningNote("G3", 196.00, 3),
            TuningNote("B3", 246.94, 2),
            TuningNote("E4", 329.63, 1)
        )
    )

    val GuitarDStandard = InstrumentProfile(
        id = "guitar_d_std",
        name = "Guitar (D Standard)",
        notes = listOf(
            TuningNote("D2", 73.42, 6),
            TuningNote("G2", 98.00, 5),
            TuningNote("C3", 130.81, 4),
            TuningNote("F3", 174.61, 3),
            TuningNote("A3", 220.00, 2),
            TuningNote("D4", 293.66, 1)
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

    val BassDropD = InstrumentProfile(
        id = "bass_drop_d",
        name = "Bass (Drop D)",
        notes = listOf(
            TuningNote("D1", 36.71, 4),
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

    val UkuleleLowG = InstrumentProfile(
        id = "ukulele_low_g",
        name = "Ukulele (Low G)",
        notes = listOf(
            TuningNote("G3", 196.00, 4),
            TuningNote("C4", 261.63, 3),
            TuningNote("E4", 329.63, 2),
            TuningNote("A4", 440.00, 1)
        )
    )

    val All = listOf(
        GuitarStandard,
        GuitarDropD,
        GuitarDStandard,
        BassStandard,
        BassDropD,
        UkuleleStandard,
        UkuleleLowG
    )
}
