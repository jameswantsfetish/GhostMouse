# Security Policy

GhostMouse controls the local OS mouse when `--enable-robot` is used. Treat gesture, socket, and launch-script changes carefully because a bug can move or click the user's cursor unexpectedly.

## Reporting a Vulnerability

Please open a private report through GitHub Security Advisories if available, or contact the maintainer directly.

Include:

- A short description of the issue
- Steps to reproduce
- Expected and actual behavior
- Your operating system and Java version

## Scope

Security-sensitive areas include:

- Socket frame parsing
- Launch scripts
- Real mouse movement and click behavior
- Packaged app permissions
