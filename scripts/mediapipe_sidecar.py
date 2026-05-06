#!/usr/bin/env python3
"""Streams MediaPipe hand landmarks to the Java GhostMouse socket provider."""

from __future__ import annotations

import socket
import sys
import time
from pathlib import Path

import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision


HOST = "127.0.0.1"
PORT = 8765
MODEL_PATH = Path(__file__).resolve().parents[1] / "models" / "hand_landmarker.task"


def encode_landmarks(hand_landmarks) -> str:
    return ";".join(
        f"{landmark.x:.6f},{landmark.y:.6f},{landmark.z:.6f}"
        for landmark in hand_landmarks
    )


def encode_frame(results) -> str | None:
    if not results.hand_landmarks:
        return None

    normalized = encode_landmarks(results.hand_landmarks[0])
    sections = ["v2", f"n={normalized}"]

    if results.hand_world_landmarks:
        sections.append(f"w={encode_landmarks(results.hand_world_landmarks[0])}")

    if results.handedness:
        category = results.handedness[0][0]
        label = getattr(category, "category_name", "") or getattr(category, "display_name", "")
        score = getattr(category, "score", 0.0) or 0.0
        sections.append(f"h={label},{score:.6f}")

    return "|".join(sections)


def connect_loop() -> socket.socket:
    while True:
        try:
            sock = socket.create_connection((HOST, PORT), timeout=2)
            sock.settimeout(None)
            print(f"Connected to GhostMouse at {HOST}:{PORT}")
            return sock
        except OSError:
            print("Waiting for GhostMouse socket provider...")
            time.sleep(1)


def main() -> None:
    if not MODEL_PATH.exists():
        raise RuntimeError(f"Missing MediaPipe model: {MODEL_PATH}")

    options = vision.HandLandmarkerOptions(
        base_options=python.BaseOptions(model_asset_path=str(MODEL_PATH)),
        running_mode=vision.RunningMode.VIDEO,
        num_hands=1,
        min_hand_detection_confidence=0.6,
        min_hand_presence_confidence=0.6,
        min_tracking_confidence=0.7,
    )
    camera = cv2.VideoCapture(0)
    if not camera.isOpened():
        raise RuntimeError("Could not open webcam 0.")
    camera.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    camera.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    camera.set(cv2.CAP_PROP_FPS, 30)
    camera.set(cv2.CAP_PROP_BUFFERSIZE, 1)

    sock = connect_loop()
    frames_sent = 0
    frames_read = 0
    last_no_hand_log = time.monotonic()
    try:
        with vision.HandLandmarker.create_from_options(options) as landmarker:
            print("MediaPipe hand landmarker started.", flush=True)
            while True:
                ok, frame = camera.read()
                if not ok:
                    print("Camera read failed; retrying.", flush=True)
                    time.sleep(0.05)
                    continue

                frames_read += 1
                rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                image = mp.Image(image_format=mp.ImageFormat.SRGB, data=rgb)
                timestamp_ms = int(time.monotonic() * 1000)
                results = landmarker.detect_for_video(image, timestamp_ms)
                line = encode_frame(results)
                if line:
                    try:
                        sock.sendall((line + "\n").encode("utf-8"))
                        frames_sent += 1
                        if frames_sent == 1 or frames_sent % 120 == 0:
                            print(f"Sent hand frame #{frames_sent}", flush=True)
                    except OSError:
                        print("Lost GhostMouse socket; reconnecting.", flush=True)
                        sock.close()
                        sock = connect_loop()
                elif time.monotonic() - last_no_hand_log > 5:
                    print(f"No hand detected in last {frames_read} camera frames.", flush=True)
                    frames_read = 0
                    last_no_hand_log = time.monotonic()
    finally:
        sock.close()
        camera.release()


if __name__ == "__main__":
    main()
