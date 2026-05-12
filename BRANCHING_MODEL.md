# Branching Model of MagnetarOrpheus

## Standard Model
This project uses a simplified branching model optimized for focused development:

1.  **`master` Branch:**
    *   The primary, stable branch.
    *   All development branches must eventually merge here.
    *   Must always be in a deployable/buildable state.
2.  **Development Branches:**
    *   Format: `feature/name`, `fix/issue-id`, `experiment/idea`.
    *   Short-lived branches for specific tasks.
3.  **Merge Policy:**
    *   Rebase onto `master` before merging.
    *   Documentation (`BITACORA.md`, `STATUS.md`) must be updated in the merge commit.
