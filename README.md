# GhostMouse

[![CI](https://github.com/jameswantsfetish/GhostMouse/actions/workflows/ci.yml/badge.svg)](https://github.com/jameswantsfetish/GhostMouse/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](pom.xml)
[![MediaPipe](https://img.shields.io/badge/MediaPipe-Hand%20Tracking-orange.svg)](https://github.com/google-ai-edge/mediapipe)

GhostMouse turns your hand into a desktop mouse. It reads webcam hand landmarks through a MediaPipe-powered Python sidecar, then uses a Java 21 desktop app for gesture recognition, smoothing, tray integration, and optional real OS mouse control.

Created by **James Mamhiyo**.

## Why It Exists

GhostMouse is built as a practical, inspectable desktop gesture bridge. The project keeps the computer-vision boundary simple, keeps the mouse-control behavior testable in Java, and includes a safe demo mode so contributors can work without accidentally moving or clicking the real cursor.

## At A Glance

```text
Webcam -> Python MediaPipe sidecar -> TCP landmarks -> Java gesture engine -> OS mouse
```

Start safely with demo landmarks, then move to the real webcam sidecar when you are ready.

## Features

- Java 21 desktop app with a Swing control/debug window
- Demo landmark source for safe testing without a camera
- TCP socket landmark bridge for a local MediaPipe process
- Palm-center cursor movement with adaptive smoothing
- Thumb/index pinch for left click
- Thumb/pinky pinch for right click
- Thumb/index/middle pinch for click-and-hold drag
- Index/middle scroll mode
- Release-based click state machine to avoid repeated click bursts
- Shutdown guard that releases held mouse buttons
- GitHub Actions CI, issue templates, contribution notes, and MIT license

## Requirements

- Java 21 or newer
- Maven through the included `./mvnw` wrapper
- Python 3.10+ for the MediaPipe sidecar
- Webcam access for real hand tracking
- macOS, Windows, or Linux for the Java app and demo mode
- macOS recommended for the included double-click launcher scripts
- Accessibility permission for Java when real OS mouse control is enabled on macOS

## Quick Start

Clone and enter the repo:

```sh
git clone https://github.com/jameswantsfetish/GhostMouse.git
cd GhostMouse
```

Build and test:

```sh
./mvnw test
./mvnw package
```

Run in safe demo mode. This opens the app but does not move the real cursor:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=demo
```

When you are ready for camera tracking, start the Java socket app:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot
```

In another terminal, start the Python sidecar:

```sh
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
python scripts/mediapipe_sidecar.py
```

Run with real cursor movement but clicks disabled while calibrating:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
```

Tune the camera active zone if your hand does not reach the screen edges comfortably:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks --active-zone=0.30,0.72,0.28,0.78
```

Show all Java app options:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --help
```

For more detail, see [Setup Guide](docs/SETUP.md).

## Windows Support

GhostMouse is designed to run on Windows through the Java jar and Python sidecar. The included `.command`, `.sh`, `.app`, and native wrapper files are for macOS convenience only.

On Windows, use PowerShell or Windows Terminal:

```powershell
.\mvnw test
.\mvnw package
java -jar target\ghostmouse-0.1.0-SNAPSHOT.jar --source=demo
```

For webcam tracking:

```powershell
py -3 -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python scripts\mediapipe_sidecar.py
```

See [Windows Guide](docs/WINDOWS.md) for the recommended setup path.

## macOS Launcher

For local macOS use, the project includes command launchers:

- `Start GhostMouse.command`
- `Stop GhostMouse.command`

The start command builds the Java jar when it is missing or stale, launches the Java socket app with real mouse control, waits for `127.0.0.1:8765`, and opens the local camera sidecar app if it exists.

Generated `.app` bundles, logs, `dist/`, and `outputs/` are ignored for GitHub. If you clone the repository on a new machine, run the Java app and Python sidecar manually first, or rebuild your local app bundles before relying on the double-click launchers.

## Python MediaPipe Sidecar

The Java app expects one hand landmark frame per line on a TCP socket. The included Python sidecar can stream those landmarks from your webcam.

```sh
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
python scripts/mediapipe_sidecar.py
```

Start Java first in socket mode, then start the sidecar.

## Gestures

- Move: palm center controls the cursor.
- Left click: quick thumb-index pinch, then release.
- Drag: thumb-index-middle pinch starts a left-button hold; release the pinch to mouse-up.
- Right click: thumb-pinky pinch, then release.
- Scroll: index-middle together with the thumb away; move your hand up or down. Cursor movement freezes while scroll mode is active.

## Landmark Protocol

In `--source=socket` mode, GhostMouse listens as a TCP server. Send one frame per line:

```text
v2|n=x,y,z;... 21 normalized landmarks|w=x,y,z;... 21 world landmarks|h=Right,0.98
```

The Java app also accepts the original legacy format:

```text
x,y,z;x,y,z;x,y,z;... 21 total landmarks
```

Normalized coordinates are MediaPipe image coordinates from `0.0` to `1.0`. World coordinates are preferred for click detection when available because they make thumb-to-finger pinches less sensitive to camera distance.

MediaPipe Hands landmark indexes used by GhostMouse:

- `4`: thumb tip
- `8`: index fingertip
- `12`: middle fingertip
- `16`: ring fingertip
- `20`: pinky fingertip

## Package

After building the jar, create a local macOS app image with `jpackage`:

```sh
jpackage \
  --type app-image \
  --name GhostMouse \
  --input target \
  --main-jar ghostmouse-0.1.0-SNAPSHOT.jar \
  --dest dist
```

## Project Structure

```text
docs/                                  Setup, architecture, and troubleshooting
models/                                MediaPipe hand landmarker model
scripts/                               Launch and MediaPipe helper scripts
src/main/java/com/ghostmouse/app       Startup, config, and service loop
src/main/java/com/ghostmouse/control   Mouse control and screen mapping
src/main/java/com/ghostmouse/gesture   Gesture plugins and intent handling
src/main/java/com/ghostmouse/math      Pointer smoothing and landmark math
src/main/java/com/ghostmouse/ui        Swing window and landmark preview
src/main/java/com/ghostmouse/vision    Demo and socket frame providers
```

Read the [Architecture Notes](docs/ARCHITECTURE.md) for the main design boundaries.

## Development

Run the full test suite:

```sh
./mvnw test
```

Build the runnable shaded jar:

```sh
./mvnw package
```

Before opening a pull request, run:

```sh
./mvnw test
./mvnw package
```

Python sidecar syntax check:

```sh
python3 -m py_compile scripts/mediapipe_sidecar.py
```

Maintainer and contributor guidance lives in [CONTRIBUTING.md](CONTRIBUTING.md).

## Safety Notes

Real mouse control can move and click your OS cursor. Start with `--source=demo` or `--disable-clicks` while tuning gestures, and keep a keyboard or trackpad available so you can stop the app quickly.

If something does not behave correctly, see [Troubleshooting](docs/TROUBLESHOOTING.md).

## References

- [MediaPipe](https://github.com/google-ai-edge/mediapipe)
- [MediaPipe hand landmarker Java API](https://ai.google.dev/edge/api/mediapipe/java/com/google/mediapipe/tasks/vision/handlandmarker/package-summary)
- [Handsfree.js](https://github.com/midiblocks/handsfree)

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
