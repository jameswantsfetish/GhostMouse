package com.ghostmouse.gesture;

import com.ghostmouse.math.LandmarkMath;
import com.ghostmouse.model.HandFrame;

public final class PinchMeasurements {
  private static final double MIN_HAND_SCALE = 1.0e-6;

  private PinchMeasurements() {}

  public static PinchMeasurement thumbTo(HandFrame frame, PinchFinger finger) {
    return between(frame, 4, finger.landmarkIndex());
  }

  public static PinchMeasurement between(HandFrame frame, int firstLandmark, int secondLandmark) {
    double distance = LandmarkMath.distance3d(
        frame.measurementLandmark(firstLandmark),
        frame.measurementLandmark(secondLandmark));
    double scale = handScale(frame);
    return new PinchMeasurement(distance, scale, distance / scale);
  }

  public static double handScale(HandFrame frame) {
    double palmWidth = LandmarkMath.distance3d(frame.measurementLandmark(5), frame.measurementLandmark(17));
    double palmLength = LandmarkMath.distance3d(frame.measurementLandmark(0), frame.measurementLandmark(9));
    return Math.max(MIN_HAND_SCALE, Math.max(palmWidth, palmLength));
  }
}
