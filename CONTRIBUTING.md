# Contributing

Thanks for helping improve GhostMouse.

## Local Setup

1. Install Java 21 or newer.
2. Clone the repository.
3. Run `./mvnw test`.
4. For camera testing, create a Python virtual environment and run `pip install -r requirements.txt`.

See [docs/SETUP.md](docs/SETUP.md) for the full setup path.

## Development Checks

Before opening a pull request, run:

```sh
./mvnw test
./mvnw package
```

If your change affects the Python sidecar, also run:

```sh
python3 -m py_compile scripts/mediapipe_sidecar.py
```

## Pull Requests

- Keep changes focused on one bug fix or feature.
- Include tests for gesture, math, mapping, or protocol changes when practical.
- Update `README.md` when setup, usage, gestures, or command-line options change.
- Update docs in `docs/` when architecture, setup, or troubleshooting changes.
- Do not commit generated logs, local app bundles, `target/`, `dist/`, `.venv/`, or `outputs/`.

## Safety

Changes that touch real mouse movement or clicking should be tested first with demo mode or `--disable-clicks`.
