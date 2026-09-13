# Security Policy

## Supported Versions

BlazaresOrpheus is currently developed as a single active release line. Security fixes are applied to the latest code on `master` and to the most recent published prerelease/release when a fix is applicable.

| Version | Supported |
| --- | --- |
| Latest `master` / latest release | ✅ |
| Older builds | ❌ |

Users should update to the newest available build before reporting a security issue that may already have been fixed.

## Reporting a Vulnerability

Please do not disclose exploitable security details in a public issue, discussion, or pull request.

Preferred reporting path:

1. Use GitHub's private **Security / Report a vulnerability** flow for this repository when it is available.
2. If private vulnerability reporting is not available, open a public issue containing only a short request for a private reporting channel. Do not include proof-of-concept code, secrets, personal data, or exploit details in that issue.

A report should include the affected app version/commit, Android version and device class, reproduction prerequisites, impact, and the smallest safe set of reproduction steps.

## Response Expectations

The project aims to acknowledge a valid private report within 7 days. After triage, the reporter will be told whether the issue is accepted, requires more information, or is not considered a vulnerability. Fix timing depends on severity and whether the issue depends on Android/platform behavior outside the application's control.

## Security Scope

Particularly relevant areas include:

- microphone permission and capture lifecycle;
- unintended persistence or transmission of captured audio;
- release-signing and update-integrity weaknesses;
- dependency or build-pipeline compromise;
- exported Android components and unsafe intent handling;
- accidental exposure of credentials, signing material, or private user data.

The repository must never contain release keystores, passwords, private keys, API credentials, or equivalent secrets. CI release signing is configured exclusively through repository secrets/environment variables.
