# Canonical Plan of MagnetarOrpheus

## Introduction
This plan captures the milestones, tasks, and progress for the MagnetarOrpheus Android application.

## Milestones Overview Table
| Milestone ID | Name | Target Date | Description | Completion Criteria |
| :--- | :--- | :--- | :--- | :--- |
| `ms-01` | Project Setup & Canon | 2026-05-12 | Establish the Magnetar Canonical Model and project structure. | `done` |
| `ms-02` | Audio Foundation | 2026-05-20 | Implement low-level audio capture and basic DSP pipeline. | `done` |
| `ms-03` | Core Tuner UI | 2026-05-30 | Build the Jetpack Compose interface for visual feedback. | `done` |
| `ms-04` | MVP Release | 2026-06-15 | Stable chromatic guitar tuner with calibration settings. | `done` |
| `ms-05` | Instrument Profiles | 2026-06-30 | Preset tunings for Guitar, Bass, and Ukulele. | `planned` |
| `ms-06` | Visual Analysis | 2026-07-15 | Real-time waveform display and pitch history. | `planned` |

## Task Backlog Table
| Task ID | Milestone | Title | Owner | Effort (pts) | State | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `task-001` | `ms-01` | Initialize Canonical Files | Gemini | 1 | `done` | README, RULES, PLAN, etc. |
| `task-002` | `ms-01` | Project YAML Setup | Gemini | 1 | `done` | Machine-readable project config. |
| `task-003` | `ms-02` | AudioRecord Integration | Gemini | 5 | `done` | Native audio capture layer. |
| `task-004` | `ms-02` | Pitch Detection Algorithm | Gemini | 8 | `done` | Implementation of YIN or Autocorrelation. |
| `task-005` | `ms-03` | Compose Tuner UI | Gemini | 5 | `done` | Visual indicator and note display. |
| `task-006` | `ms-04` | Calibration & Stability | Gemini | 3 | `done` | A4 reference and temporal filtering. |
| `task-007` | `ms-05` | Instrument Data Layer | TBD | 3 | `planned` | Define tuning frequencies for profiles. |
| `task-008` | `ms-05` | Profile Selection UI | TBD | 4 | `planned` | Dropdown/Menu for switching instruments. |
| `task-009` | `ms-06` | Waveform Canvas | TBD | 6 | `planned` | Real-time PCM data visualization. |

## Effort Summary
*   **Total effort:** 36 pts
*   **Completed:** 23 pts
*   **In progress:** 0 pts
*   **Remaining:** 13 pts

## State Definitions
*   `planned`: Identified for future work.
*   `ready`: Clear specs, ready for execution.
*   `in_progress`: Under active development.
*   `in_review`: Undergoing testing or code review.
*   `blocked`: Stopped by an external issue.
*   `done`: Fully implemented and verified.
