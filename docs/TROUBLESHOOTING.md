# Troubleshooting

## The Java App Starts But The Cursor Does Not Move

- Confirm you used `--enable-robot`.
- On macOS, grant Accessibility permission to the terminal or Java app.
- Check whether you are in `--source=demo`; demo mode intentionally uses a no-op mouse controller unless `--enable-robot` is supplied.

## The Sidecar Says It Is Waiting For GhostMouse

Start the Java app first in socket mode:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
```

Then start the Python sidecar:

```sh
python scripts/mediapipe_sidecar.py
```

The default socket is `127.0.0.1:8765`.

## The Webcam Does Not Open

- Close other apps using the camera.
- Grant camera permission to your terminal app.
- Confirm OpenCV can see webcam `0`.

## Movement Feels Too Jittery

Increase smoothing:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks --smoothing=0.88
```

Higher values are calmer. Lower values are more responsive.

## Movement Feels Too Slow

Lower smoothing:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks --smoothing=0.65
```

## Clicks Trigger Too Easily

Start with clicks disabled while calibrating:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
```

Then try a lower sensitivity by reducing the tap threshold:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --tap-threshold=0.30
```

## The Pointer Does Not Reach The Screen Edges

Tune the active camera zone:

```sh
java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks --active-zone=0.25,0.80,0.20,0.85
```

## Maven Download Fails

The wrapper downloads Maven into `.mvn/cache`. If your network blocks Maven Central, install Maven manually and run:

```sh
mvn test
mvn package
```

## GitHub Actions Fails But Local Tests Pass

Open the failed CI run and compare the Java version and command output. CI runs:

```sh
./mvnw test
```
