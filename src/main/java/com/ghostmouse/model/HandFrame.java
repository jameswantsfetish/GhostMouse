package com.ghostmouse.model;

import java.time.Instant;
import java.util.List;

public record HandFrame(
    List<Landmark> landmarks,
    List<Landmark> worldLandmarks,
    String handedness,
    double handednessScore,
    Instant capturedAt) {
  public static final int MEDIA_PIPE_HAND_LANDMARK_COUNT = 21;

  public HandFrame(List<Landmark> landmarks, Instant capturedAt) {
    this(landmarks, List.of(), "", 0.0, capturedAt);
  }

  public HandFrame {
    landmarks = List.copyOf(landmarks);
    if (landmarks.size() != MEDIA_PIPE_HAND_LANDMARK_COUNT) {
      throw new IllegalArgumentException("Expected exactly 21 hand landmarks, got " + landmarks.size());
    }
    worldLandmarks = worldLandmarks == null ? List.of() : List.copyOf(worldLandmarks);
    if (!worldLandmarks.isEmpty() && worldLandmarks.size() != MEDIA_PIPE_HAND_LANDMARK_COUNT) {
      throw new IllegalArgumentException("Expected exactly 21 world hand landmarks, got " + worldLandmarks.size());
    }
    handedness = handedness == null ? "" : handedness;
    if (!Double.isFinite(handednessScore)) {
      handednessScore = 0.0;
    }
    capturedAt = capturedAt == null ? Instant.now() : capturedAt;
  }

  public boolean hasWorldLandmarks() {
    return !worldLandmarks.isEmpty();
  }

  public Landmark landmark(int index) {
    return landmarks.get(index);
  }

  public Landmark measurementLandmark(int index) {
    return hasWorldLandmarks() ? worldLandmarks.get(index) : landmarks.get(index);
  }

  public Landmark palmCenter() {
    return averageLandmark(landmarks, 0, 5, 9, 13, 17);
  }

  public Landmark thumbTip() {
    return landmarks.get(4);
  }

  public Landmark indexTip() {
    return landmarks.get(8);
  }

  public Landmark middleTip() {
    return landmarks.get(12);
  }

  public Landmark ringTip() {
    return landmarks.get(16);
  }

  public Landmark pinkyTip() {
    return landmarks.get(20);
  }

  private static Landmark averageLandmark(List<Landmark> source, int... indexes) {
    double x = 0.0;
    double y = 0.0;
    double z = 0.0;
    for (int index : indexes) {
      Landmark landmark = source.get(index);
      x += landmark.x();
      y += landmark.y();
      z += landmark.z();
    }
    double count = indexes.length;
    return new Landmark(x / count, y / count, z / count);
  }
}
