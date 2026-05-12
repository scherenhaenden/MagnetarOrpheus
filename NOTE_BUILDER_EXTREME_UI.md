# Magnetar Orpheus Note Builder Extreme UI

## Purpose
This document defines the secondary **Note Builder** feature for Magnetar Orpheus. It extends the same product family as the tuner and must preserve the same premium dark graphite visual language, restrained neon active states, and musician-tool tone.

This feature does **not** replace the existing tuner screens. It adds a separate workspace for selecting, auditioning, and inspecting notes, intervals, and chords.

## Design Source
The implementation direction is based on the mockups in:

* `design/phone2.png`
* `design/tablet2.png`

These files are treated as visual direction, not as literal one-to-one layout requirements for every form factor.

## Core Product Intent
The Note Builder allows the user to:

* Select one note or multiple notes.
* Build intervals, chords, and arbitrary note sets.
* Switch between a piano-style note selector and an octave grid.
* Keep one shared musical selection state across all input methods.
* Play, hold, stop, and clear the current selection.
* Inspect a detected musical interpretation such as `C Major Triad`, `Cmaj7`, `Perfect Fifth`, or `Unknown Note Set`.

## Critical UX Decision
The phone mockup is visually strong, but showing the full piano keyboard and the full note grid at the same time creates a layout that is too dense for production use on compact screens.

Because of that, the implementation rule is:

* **Phone:** show only one primary input mode at a time.
* **Tablet:** allow both major input systems to coexist in a workspace layout.

The selected note state remains shared in both cases.

## Shared State Model
The piano keyboard and the octave grid are two input methods for the same `SelectedNotesState`.

That means:

* Selecting `C4`, `E4`, and `G4` from the keyboard must keep those notes active when the user switches to grid mode.
* Selecting or deselecting notes from the grid must illuminate or clear the corresponding piano keys when the user returns to keyboard mode.
* The selected notes card, playback controls, and detected-name card always reflect the same state.

## Visual Language
The Note Builder must inherit the same family identity as the tuner:

* Background: near-black graphite (`#05080A`)
* Main cards: `#0B1115`
* Elevated cards: `#10171D`
* Borders: low-opacity blue graphite around `#1B2A31`
* Primary active: neon green `#35F58A`
* Secondary active: cyan-green `#20D6C7`
* Warning: amber `#FFD84A`
* Destructive: red `#FF4B4B`
* Primary text: `#F2F5F4`
* Secondary text: `#8D989E`
* Muted text: `#5E686E`

The feature must feel like a premium studio utility, not a game, toy piano, or generic music app.

## Phone Decision
The phone layout is a focused single-column tool. It must never show the full piano keyboard and the full octave note grid at the same time.

### Phone Structure
The screen order is:

* Header
* Small feature label
* Segmented mode selector: `Keyboard` / `Grid`
* Active input area: keyboard or grid
* Selected notes card
* Playback controls: `Play`, `Stop`, `Clear`, `Hold`
* Detected-name card
* Optional compact details

### Phone Keyboard Mode
Keyboard mode shows:

* A large horizontal mini piano inside a rounded card.
* One to two visible octaves with room for future horizontal movement.
* Strong selected-key highlighting in neon green/cyan.
* Subtle octave anchors like `C4`, `C5`, `C6`.

### Phone Grid Mode
Grid mode shows:

* A vertically scrollable octave grid.
* Each row represents one octave.
* Each row contains 12 chromatic note buttons.
* The left edge shows octave labels such as `C3`, `C4`, `C5`.
* Selected notes use the same active visual treatment as the keyboard.

### Phone Usability Rule
The phone UI must remain touch-friendly, readable at arm's length, and fast to use for repeated note selection. It must not degrade into a miniature tablet dashboard.

## Tablet Decision
The tablet layout is a full workspace and may show both the keyboard and the grid simultaneously.

### Tablet Structure
The tablet design uses four areas:

* Left column: piano keyboard and direct playback controls
* Center workspace: selected note-set summary and large note grid
* Right sidebar: note sequence, saved patterns, harmonization placeholder
* Bottom bar: playback settings

### Tablet Behavior
Tablet is not a stretched phone layout. It is a music workstation view where the user can inspect and manipulate a note set with more context visible at once.

## First-Version Scope
The first implementation should visually and structurally support:

* Shared note selection state
* Keyboard mode on phone
* Grid mode on phone
* Simultaneous keyboard and grid workspace on tablet
* Selected note chips
* Playback actions
* Detected note-set / chord summary

The following can remain placeholders:

* Saved patterns
* Harmonization
* Inversions
* Sequence editing
* Enharmonic switching
* Arpeggiator behavior
* Deep theory analysis

## Implementation Rule
The new Note Builder views must be added as **new composables** and **new screens**. They do not replace the current tuner implementation.

## Current Branch Implementation Snapshot
The current local branch state goes beyond static mockup preparation and includes implementation wiring for:

*   A separate `AppDestination` model distinguishing `Tuner` and `Note Builder`.
*   A `NoteBuilderViewModel` that owns selection state, hold state, playback state, and basic note-set analysis.
*   A `NoteBuilderMusicTheory` layer that derives compact names such as `Cmaj7` and supporting subtitles from the current selection.
*   A `NotePlaybackEngine` integration path for local playback of the selected notes.
*   Responsive Compose views for phone and tablet-oriented Note Builder workspaces.
*   Tablet workspace scroll regions bounded explicitly so the large-screen layout does not hit infinite-height Compose measurement failures.

These branch-local changes do not replace the tuner. They extend the app into a two-workspace suite.

## Manual Test Expectations
When this branch is manually tested, the following behaviors should be verified:

1.  The user can navigate to Note Builder without losing the tuner feature.
2.  Tuner and Note Builder feel like two different tools in the same product family.
3.  Note Builder phone behavior shows one primary input method at a time.
4.  Note Builder tablet behavior exposes a wider multi-panel workspace without crashing or collapsing due to unbounded scroll/layout constraints.
5.  Shared selected-note state remains coherent as the input mode changes.
6.  Play, Stop, Clear, and Hold behave sensibly with real device audio output.
