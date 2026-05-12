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
