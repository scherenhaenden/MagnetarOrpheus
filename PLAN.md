# Canonical Plan of MagnetarOrpheus

## Introduction
This plan captures the milestones, tasks, and progress for the MagnetarOrpheus Android application.

## Milestones Overview Table
| Milestone ID | Name | Target Date | Description | Completion Criteria |
| :--- | :--- | :--- | :--- | :--- |
| `ms-01` | Project Setup & Canon | 2026-05-12 | Establish the Magnetar Canonical Model and project structure. | All canonical files created, git init, remote linked. |
| `ms-02` | Audio Foundation | 2026-05-20 | Implement low-level audio capture and basic DSP pipeline. | Successful AudioRecord capture and fundamental pitch estimation. |
| `ms-03` | Core Tuner UI | 2026-05-30 | Build the Jetpack Compose interface for visual feedback. | Responsive needle/indicator reacting to frequency data. |
| `ms-04` | MVP Release | 2026-06-15 | Stable chromatic guitar tuner with calibration settings. | Accurate tuning for 6-string guitar, A4 calibration functional. |

## Task Backlog Table
| Task ID | Milestone | Title | Owner | Effort (pts) | State | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `task-001` | `ms-01` | Initialize Canonical Files | Gemini | 1 | `done` | README, RULES, PLAN, etc. |
| `task-002` | `ms-01` | Project YAML Setup | Gemini | 1 | `in_progress` | Machine-readable project config. |
| `task-003` | `ms-02` | AudioRecord Integration | TBD | 5 | `planned` | Native audio capture layer. |
| `task-004` | `ms-02` | Pitch Detection Algorithm | TBD | 8 | `planned` | Implementation of YIN or Autocorrelation. |
| `task-005` | `ms-03` | Compose Tuner UI | TBD | 5 | `planned` | Visual indicator and note display. |

## Effort Summary
*   **Total effort:** 20 pts
*   **Completed:** 1 pts
*   **In progress:** 1 pts
*   **Remaining:** 18 pts

## State Definitions
*   `planned`: Identified for future work.
*   `ready`: Clear specs, ready for execution.
*   `in_progress`: Under active development.
*   `in_review`: Undergoing testing or code review.
*   `blocked`: Stopped by an external issue.
*   `done`: Fully implemented and verified.
