package com.edwardflores.magnetar.orpheus.ui

enum class AppLanguage(
    val code: String,
    val nativeLabel: String
) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano"),
    PORTUGUESE("pt", "Português"),
    DUTCH("nl", "Nederlands"),
    FINNISH("fi", "Suomi")
}

enum class NoteLanguage(
    val code: String,
    val nativeLabel: String
) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    GERMAN("de", "Deutsch")
}

fun NoteLanguage.toNamingSystem(): NoteNamingSystem = when (this) {
    NoteLanguage.ENGLISH -> NoteNamingSystem.SCIENTIFIC
    NoteLanguage.SPANISH -> NoteNamingSystem.SYLLABIC
    NoteLanguage.GERMAN -> NoteNamingSystem.GERMAN
}
