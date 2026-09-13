# Magnetar Orpheus to Blazares Orpheus

The Blazares Orpheus rebrand intentionally changes the Android application ID to
`com.blazares.orpheus`. This gives the rebranded application a clean package
identity, but Android cannot treat it as an in-place update of the former
`com.edwardflores.magnetar.orpheus` installation.

## Release strategy

- Publish `com.blazares.orpheus` as a new application identity/listing, or
  communicate the package change clearly before distribution.
- Keep the previous application available during the transition so existing
  users can still access it and export any important tuner or Note Builder data.
- If data migration is needed, add an export/import flow before the package
  change. Android does not provide an automatic cross-package upgrade path for
  the old app's private storage.
- Do not expect an update signed with the new package name to upgrade the old
  install; users may need to install the new app separately and import their
  exported settings.

This is intentional for the current rebrand and should remain part of the
release checklist until a dedicated migration flow exists.
