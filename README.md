# Canonical Project Model of MagnetarOrpheus

## Purpose
MagnetarOrpheus is a professionally engineered native Android application focused on real-time musician tooling. It currently contains two distinct feature surfaces inside the same product:

1.  A **chromatic tuner** for live pitch detection, calibration, and tuning feedback.
2.  A **Note Builder workspace** for selecting, auditioning, and inspecting notes, intervals, and chords.

The tuner remains the primary real-time analysis surface. The Note Builder is a secondary workspace that shares the same Magnetar design language but does not replace the tuner.

This project strictly follows the **Magnetar Canonical Project Model** for documentation, planning, and governance to ensure high maintainability, architectural integrity, and professional engineering standards.

## How to Use This Repository
1.  **Understand the Standard:** This repository follows the Magnetar standard for project management.
2.  **Consult the Documentation:** All project decisions, tasks, and status are tracked in the root markdown files.
3.  **Follow the Rules:** Replicate the required documentation set for any major sub-modules.
4.  **Governance:** Follow the WIP (Work-In-Progress), branching (using `master` as default), and blocker escalation rules.
5.  **AI Collaboration:** If you are an AI collaborator, parse the `projects/magnetar_orpheus.project.yml` file first.

## Project Contents
| File | Purpose |
| :--- | :--- |
| `PLAN.md` | Project tasks, milestones, and effort tracking. |
| `BITACORA.md` | Chronological logbook of all significant events and decisions. |
| `NOTE_BUILDER_EXTREME_UI.md` | Premium Note Builder workspace specification and responsive design rules. |
| `REQUIREMENTS.md` | Detailed functional and non-functional specifications. |
| `ARCHITECTURE.md` | System structure, DSP layers, and module definitions. |
| `RULES.md` | Naming conventions, workflow standards, and canonical rules. |
| `STATUS.md` | Real-time health summary, progress stats, and active risks. |
| `TESTING.md` | Test strategy, coverage targets, and reporting rules. |
| `BLOCKERS.md` | Documented impediments and escalation paths. |
| `BRANCHING_MODEL.md` | Git branching strategy (Standard: `master` as default). |
| `WIP_GUIDELINES.md` | Policies regarding Work-In-Progress limits. |

## Progress Model Overview
We track progress through a structured lifecycle:
`planned` → `ready` → `in_progress` → `in_review` → `done`.
Every state transition and major milestone achievement is recorded in `BITACORA.md`.

## Current Branch Snapshot
The current branch state extends the original tuner MVP in several ways:

*   Premium dark-mode tuner visuals have been restored and decomposed into reusable UI components.
*   In-app navigation now separates **Tuner** and **Note Builder** as two different destinations.
*   The Note Builder branch state includes responsive phone/tablet layouts, note-selection state, music-theory naming, playback wiring, and tablet-safe bounded scroll regions.
*   The Android app version target has been raised to `2026.05.12.1554`.

## YAML Project Schema
The source of truth for machine-readable project metadata is located at `projects/magnetar_orpheus.project.yml`. It contains stakeholders, milestones, and the task registry.

## Guidance for AI Collaborators
AI agents MUST:
*   Parse `projects/magnetar_orpheus.project.yml` before taking action.
*   Use `PLAN.md` and `STATUS.md` to determine the current focus.
*   Strictly respect `RULES.md` and `WIP_GUIDELINES.md`.
*   Update `BITACORA.md` immediately after completing any work or changing a task state.

## Applying This Template
This project is already instantiated following the Magnetar canon. To extend it:
1.  Update the project YAML for new milestones.
2.  Reflect changes in `PLAN.md`.
3.  Log the initialization in `BITACORA.md`.

## Validating Canon Compliance
- [ ] All 11+ required canonical files exist.
- [ ] The project YAML matches the current state of `PLAN.md`.
- [ ] `BITACORA.md` is updated chronologically (newest first).
- [ ] Active branches follow the `RULES.md` naming conventions.
