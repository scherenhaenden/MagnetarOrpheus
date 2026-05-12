package com.edwardflores.magnetar.orpheus.ui

enum class AppDestination(
    val title: String,
    val subtitle: String
) {
    TUNER(
        title = "Tuner",
        subtitle = "Live chromatic tuning"
    ),
    NOTE_BUILDER(
        title = "Note Builder",
        subtitle = "Keyboard and grid workspace"
    )
}

fun AppDestination.localizedTitle(strings: AppStrings): String = when (this) {
    AppDestination.TUNER -> strings.tunerTitle
    AppDestination.NOTE_BUILDER -> strings.noteBuilderTitle
}

fun AppDestination.localizedSubtitle(strings: AppStrings): String = when (this) {
    AppDestination.TUNER -> strings.tunerSubtitle
    AppDestination.NOTE_BUILDER -> strings.noteBuilderSubtitle
}
