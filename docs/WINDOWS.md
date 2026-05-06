# Windows Guide

GhostMouse can run on Windows through the Java jar and Python sidecar. The macOS launchers in the repository are not used on Windows.

## What Works On Windows

- Java app build and tests
- Demo mode
- Swing control/debug window
- Socket landmark provider
- Python MediaPipe webcam sidecar
- Real cursor movement through Java AWT Robot, subject to Windows desktop permissions and environment behavior

## macOS-Only Pieces

- `Start GhostMouse.command`
- `Stop GhostMouse.command`
- `scripts/start_ghostmouse.sh`
- `scripts/stop_ghostmouse.sh`
- `GhostMouseCameraSidecar.app`
- `GhostMousePython.app`
- `native/ghostmouse_camera_sidecar.c`

## Setup

Install:

- Git
- Java 21 or newer
- Python 3.10 or newer

Clone:

```powershell
git clone https://github.com/jameswantsfetish/GhostMouse.git
cd GhostMouse
```

Build and test:

```powershell
.\mvnw test
.\mvnw package
```

Run safe demo mode:

```powershell
java -jar target\ghostmouse-0.1.0-SNAPSHOT.jar --source=demo
```

## Camera Sidecar

Create the virtual environment:

```powershell
py -3 -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

Start Java in socket mode. During first calibration, keep clicks disabled:

```powershell
java -jar target\ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
```

In a second PowerShell window:

```powershell
.\.venv\Scripts\Activate.ps1
python scripts\mediapipe_sidecar.py
```

When movement is stable, restart Java without `--disable-clicks`:

```powershell
java -jar target\ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot
```

## Troubleshooting

If PowerShell blocks virtual environment activation, run:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

If the webcam does not open, close other camera apps and check Windows camera privacy settings.

If real cursor control does not work, test demo mode first, then try running Windows Terminal normally on your local desktop session. Java AWT Robot may not work in remote, locked, or heavily restricted desktop environments.
