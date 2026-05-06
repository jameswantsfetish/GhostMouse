# Setup Guide

This guide takes GhostMouse from a fresh clone to a working local install.

## 1. Clone

macOS/Linux:

```sh
git clone https://github.com/jameswantsfetish/GhostMouse.git
cd GhostMouse
```

Windows PowerShell:

```powershell
git clone https://github.com/jameswantsfetish/GhostMouse.git
cd GhostMouse
```

## 2. Install Java

GhostMouse targets Java 21.

Check your version:

```sh
java -version
```

If Java is missing or older than 21, install a Java 21 distribution such as Eclipse Temurin.

## 3. Build And Test

The repo includes a lightweight Maven wrapper that downloads Maven into `.mvn/cache` when needed.

macOS/Linux:

```sh
./mvnw test
./mvnw package
```

Windows PowerShell:

```powershell
.\mvnw test
.\mvnw package
```

The runnable jar is created at:

```text
target/ghostmouse-0.1.0-SNAPSHOT.jar
```

## 4. Start Safely In Demo Mode

Demo mode uses simulated hand landmarks and does not move the real cursor.

macOS/Linux:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=demo
```

Windows PowerShell:

```powershell
java -jar target\ghostmouse-0.1.0-SNAPSHOT.jar --source=demo
```

Use this mode first to confirm the Java app, UI, tray behavior, and tests work on your machine.

## 5. Set Up The Python Sidecar

The sidecar reads the webcam with OpenCV and MediaPipe, then streams landmarks to the Java app.

macOS/Linux:

```sh
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
```

Windows PowerShell:

```powershell
py -3 -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## 6. Run With The Camera

Terminal 1: start Java in socket mode. Use `--disable-clicks` during calibration.

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
```

Windows PowerShell:

```powershell
java -jar target\ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
```

Terminal 2: start the sidecar.

```sh
. .venv/bin/activate
python scripts/mediapipe_sidecar.py
```

Windows PowerShell:

```powershell
.\.venv\Scripts\Activate.ps1
python scripts\mediapipe_sidecar.py
```

When movement feels stable, restart Java without `--disable-clicks`:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot
```

## 7. Platform Notes

For real cursor control on macOS, grant Accessibility permission to the Java process or terminal app you use to run GhostMouse.

For the Python sidecar, grant camera access to the terminal app or packaged sidecar app.

On Windows, the Java jar and Python sidecar are the supported path. The `.command`, `.sh`, `.app`, and native wrapper files are macOS-only convenience files.

## 8. Calibration

If the pointer does not reach the whole screen naturally, tune the active camera zone:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks --active-zone=0.30,0.72,0.28,0.78
```

Format:

```text
--active-zone=minX,maxX,minY,maxY
```

All values are normalized camera coordinates from `0.0` to `1.0`.
