package com.ghostmouse.gesture;

import com.ghostmouse.model.HandFrame;
import com.ghostmouse.model.Landmark;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

final class HandFrameFixtures {
  private HandFrameFixtures() {}

  static HandFrame validPointing() {
    return frame(
        new Landmark(0.30, 0.72, 0.0),
        new Landmark(0.55, 0.20, 0.0),
        new Landmark(0.56, 0.74, 0.0),
        new Landmark(0.63, 0.76, 0.0),
        new Landmark(0.70, 0.78, 0.0));
  }

  static HandFrame clickArmed() {
    return frame(
        new Landmark(0.55, 0.205, 0.0),
        new Landmark(0.55, 0.20, 0.0),
        new Landmark(0.56, 0.74, 0.0),
        new Landmark(0.63, 0.76, 0.0),
        new Landmark(0.63, 0.76, 0.0));
  }

  static HandFrame rightClickArmed() {
    return frame(
        new Landmark(0.70, 0.775, 0.0),
        new Landmark(0.55, 0.20, 0.0),
        new Landmark(0.56, 0.74, 0.0),
        new Landmark(0.63, 0.76, 0.0),
        new Landmark(0.70, 0.78, 0.0));
  }

  static HandFrame clickReleased() {
    return frame(
        new Landmark(0.20, 0.76, 0.0),
        new Landmark(0.55, 0.20, 0.0),
        new Landmark(0.56, 0.74, 0.0),
        new Landmark(0.63, 0.76, 0.0),
        new Landmark(0.70, 0.78, 0.0));
  }

  static HandFrame dragArmed() {
    return frame(
        new Landmark(0.55, 0.505, 0.0),
        new Landmark(0.55, 0.50, 0.0),
        new Landmark(0.552, 0.502, 0.0),
        new Landmark(0.88, 0.78, 0.0),
        new Landmark(0.94, 0.78, 0.0));
  }

  static HandFrame scrollArmed(double palmY) {
    List<Landmark> landmarks = base();
    landmarks.set(0, new Landmark(0.50, palmY + 0.25, 0.0));
    landmarks.set(5, new Landmark(0.48, palmY - 0.05, 0.0));
    landmarks.set(9, new Landmark(0.56, palmY - 0.04, 0.0));
    landmarks.set(13, new Landmark(0.63, palmY - 0.03, 0.0));
    landmarks.set(17, new Landmark(0.70, palmY - 0.02, 0.0));
    landmarks.set(4, new Landmark(0.25, 0.72, 0.0));
    landmarks.set(8, new Landmark(0.55, 0.20, 0.0));
    landmarks.set(12, new Landmark(0.552, 0.202, 0.0));
    landmarks.set(20, new Landmark(0.70, 0.78, 0.0));
    return new HandFrame(landmarks, Instant.now());
  }

  static HandFrame upsideDown() {
    List<Landmark> landmarks = base();
    landmarks.set(0, new Landmark(0.50, 0.20, 0.0));
    return new HandFrame(landmarks, Instant.now());
  }

  static HandFrame scrollWithThumbNear() {
    List<Landmark> landmarks = base();
    landmarks.set(4, new Landmark(0.55, 0.205, 0.0));
    landmarks.set(8, new Landmark(0.55, 0.20, 0.0));
    landmarks.set(12, new Landmark(0.552, 0.202, 0.0));
    return new HandFrame(landmarks, Instant.now());
  }

  static HandFrame indexTapWithMessyPose() {
    List<Landmark> landmarks = base();
    landmarks.set(4, new Landmark(0.55, 0.505, 0.0));
    landmarks.set(8, new Landmark(0.55, 0.50, 0.0));
    return new HandFrame(landmarks, Instant.now());
  }

  static HandFrame ambiguousIndexPinkyPinchSuppressesClick() {
    List<Landmark> landmarks = base();
    landmarks.set(4, new Landmark(0.62, 0.50, 0.0));
    landmarks.set(8, new Landmark(0.62, 0.505, 0.0));
    landmarks.set(20, new Landmark(0.62, 0.505, 0.0));
    return new HandFrame(landmarks, Instant.now());
  }

  private static HandFrame frame(
      Landmark thumb,
      Landmark indexTip,
      Landmark middleTip,
      Landmark ringTip,
      Landmark pinkyTip) {
    List<Landmark> landmarks = base();
    landmarks.set(4, thumb);
    landmarks.set(8, indexTip);
    landmarks.set(12, middleTip);
    landmarks.set(16, ringTip);
    landmarks.set(20, pinkyTip);
    return new HandFrame(landmarks, Instant.now());
  }

  private static List<Landmark> base() {
    List<Landmark> landmarks = new ArrayList<>();
    for (int i = 0; i < HandFrame.MEDIA_PIPE_HAND_LANDMARK_COUNT; i++) {
      landmarks.add(new Landmark(0.50, 0.60, 0.0));
    }

    landmarks.set(0, new Landmark(0.50, 0.85, 0.0));

    landmarks.set(5, new Landmark(0.48, 0.55, 0.0));
    landmarks.set(6, new Landmark(0.50, 0.40, 0.0));
    landmarks.set(8, new Landmark(0.55, 0.20, 0.0));

    landmarks.set(9, new Landmark(0.56, 0.56, 0.0));
    landmarks.set(10, new Landmark(0.56, 0.60, 0.0));
    landmarks.set(12, new Landmark(0.56, 0.74, 0.0));

    landmarks.set(13, new Landmark(0.63, 0.57, 0.0));
    landmarks.set(14, new Landmark(0.63, 0.61, 0.0));
    landmarks.set(16, new Landmark(0.63, 0.76, 0.0));

    landmarks.set(17, new Landmark(0.70, 0.58, 0.0));
    landmarks.set(18, new Landmark(0.70, 0.62, 0.0));
    landmarks.set(20, new Landmark(0.70, 0.78, 0.0));
    return landmarks;
  }
}
