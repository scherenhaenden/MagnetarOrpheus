# Canonical Ruleset of MagnetarOrpheus

## Introduction
These rules codify the Magnetar standard for the MagnetarOrpheus project. Compliance is mandatory for all contributors, including AI agents. Any exception must be formally documented in `BITACORA.md`.

## Naming Conventions
*   **Repositories:** `magnetar-android-orpheus` (for internal identifiers) / `MagnetarOrpheus` (GitHub).
*   **Namespace / Package:** `com.edwardflores.magnetar.orpheus`.
*   **Branches:** `<type>/<short-description>`.
    *   Types: `feature`, `fix`, `chore`, `experiment`, `hotfix`.
*   **Tasks and Blockers:** `kebab-case` (e.g., `task-audio-capture`, `blocker-latency-issue`).
*   **YAML Keys:** `lower_snake_case`.
*   **File Names:** Must mirror the canonical repository structure.

## Required Files
The following files MUST exist and be maintained:
`README.md`, `PLAN.md`, `BITACORA.md`, `REQUIREMENTS.md`, `ARCHITECTURE.md`, `RULES.md`, `STATUS.md`, `TESTING.md`, `BLOCKERS.md`, `BRANCHING_MODEL.md`, `WIP_GUIDELINES.md`, `CONTRIBUTING.md`, and `projects/magnetar_orpheus.project.yml`.

## Branching Conventions
*   **`master`:** The immutable release line and default branch. Merges require passing tests and updated documentation.
*   **Feature Branches:** Originate from `master`. Must be rebased before merging.
*   **Pull Requests:** Must reference specific Task IDs and include `BITACORA.md` entries.

## Allowed Task States
1.  **`planned`**: Task identified but not ready for work.
2.  **`ready`**: Requirements clear, ready to be picked up.
3.  **`in_progress`**: Actively being worked on.
4.  **`in_review`**: Implementation complete, awaiting verification/review.
5.  **`blocked`**: Work stopped due to an external impediment.
6.  **`done`**: Verified, merged, and documented.

## Work-In-Progress (WIP) Constraints
*   **WIP Limit:** Maximum of 2 `in_progress` tasks per contributor.
*   **Enforcement:** Cannot start a new task if the limit is reached without closing or blocking existing ones.

## Blocker Lifecycle
1.  **Discovery:** Log in `BLOCKERS.md`.
2.  **Assessment:** Update `STATUS.md` risks.
3.  **Resolution:** Document solution in `BITACORA.md`.
4.  **Closing:** Move blocker to `resolved` and resume task.

## Documentation Discipline
*   **`BITACORA.md`**: Must be updated for EVERY state change or key decision.
*   **`STATUS.md`**: Updated daily or after every PR merge.
*   **`PLAN.md`**: Source of truth for milestone progress.

## AI Agent Responsibilities
*   Always read `projects/magnetar_orpheus.project.yml` first.
*   Do not modify code without an assigned task in `in_progress` state.
*   Document all technical assumptions in `BITACORA.md`.
