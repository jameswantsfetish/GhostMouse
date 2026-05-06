# Contributing

Thanks for helping improve GhostMouse.

## Local Setup

1. Install Java 21 or newer.
2. Clone the repository.
3. Run `./mvnw test`.
4. For camera testing, create a Python virtual environment and install `mediapipe` and `opencv-python`.

## Development Checks

Before opening a pull request, run:

```sh
./mvnw test
./mvnw package
```

If your change affects the Python sidecar, also run:

```sh
python scripts/mediapipe_sidecar.py --help
```

## Pull Requests

- Keep changes focused on one bug fix or feature.
- Include tests for gesture, math, mapping, or protocol changes when practical.
- Update `README.md` when setup, usage, gestures, or command-line options change.
- Do not commit generated logs, local app bundles, `target/`, `dist/`, `.venv/`, or `outputs/`.

## Safety

Changes that touch real mouse movement or clicking should be tested first with demo mode or `--disable-clicks`.
