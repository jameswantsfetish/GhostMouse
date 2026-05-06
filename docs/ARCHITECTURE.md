# Architecture Notes

GhostMouse is split around one important boundary: vision produces landmarks, Java interprets landmarks.

```text
Webcam
  -> Python MediaPipe sidecar
  -> TCP line protocol
  -> SocketHandFrameProvider
  -> GestureManager
  -> Gesture plugins
  -> MouseController
```

## App Layer

`com.ghostmouse.app` owns startup, command-line parsing, configuration, service lifecycle, and source selection.

Key classes:

- `GhostMouseApp`: application entry point
- `GhostMouseConfig`: immutable runtime configuration
- `GhostMouseService`: background frame loop
- `VisionSource`: demo vs socket source selection

## Vision Layer

`com.ghostmouse.vision` exposes `HandFrameProvider`.

Current providers:

- `DemoHandFrameProvider`: deterministic simulated landmarks for safe local testing
- `SocketHandFrameProvider`: TCP server that accepts MediaPipe landmark frames

This keeps MediaPipe out of the Java core. A future native or direct Java provider can implement the same interface.

## Gesture Layer

`com.ghostmouse.gesture` converts hand frames into intent.

The gesture manager owns plugin execution. The main control plugin maps:

- Palm center to cursor movement
- Thumb/index pinch to left click
- Thumb/pinky pinch to right click
- Thumb/index/middle pinch to drag
- Index/middle mode to scroll

## Control Layer

`com.ghostmouse.control` owns the OS interaction boundary.

- `NoopMouseController` keeps demo mode safe.
- `RobotMouseController` uses Java AWT Robot for real cursor movement and clicks.
- `ScreenMapper` maps normalized camera coordinates to screen coordinates.

## Math Layer

`com.ghostmouse.math` contains pointer smoothing and landmark geometry helpers. These classes are intentionally isolated and covered by focused tests.

## UI And Tray

`com.ghostmouse.ui` contains the Swing debug/control window and landmark preview.

`com.ghostmouse.tray` owns the optional system tray menu.

## Protocol

The socket provider reads one UTF-8 line per frame. Preferred format:

```text
v2|n=x,y,z;... 21 normalized landmarks|w=x,y,z;... 21 world landmarks|h=Right,0.98
```

Legacy format remains accepted:

```text
x,y,z;x,y,z;x,y,z;... 21 total landmarks
```

World landmarks are preferred when available because they make pinch detection less sensitive to camera distance.
