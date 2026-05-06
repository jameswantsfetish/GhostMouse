package com.ghostmouse.math;

import com.ghostmouse.model.ScreenPoint;

public final class AdaptivePointerFilter {
  private final double calmWeight;
  private ScreenPoint current;

  public AdaptivePointerFilter(double calmWeight) {
    if (calmWeight < 0.0 || calmWeight >= 1.0) {
      throw new IllegalArgumentException("Smoothing factor must be >= 0.0 and < 1.0.");
    }
    this.calmWeight = calmWeight;
  }

  public ScreenPoint apply(ScreenPoint next) {
    if (current == null) {
      current = next;
      return next;
    }

    double distance = distance(current, next);
    if (distance < 1.5) {
      // Ignore sub-pixel hand jitter so the cursor does not visibly shake.
      return current;
    }

    double previousWeight = adaptiveWeight(distance);
    double nextWeight = 1.0 - previousWeight;
    int x = (int) Math.round(current.x() * previousWeight + next.x() * nextWeight);
    int y = (int) Math.round(current.y() * previousWeight + next.y() * nextWeight);
    current = new ScreenPoint(x, y);
    return current;
  }

  public void reset() {
    current = null;
  }

  private double adaptiveWeight(double distance) {
    // Large hand jumps need a faster cursor, while small corrections can stay
    // heavily smoothed for control.
    if (distance > 360.0) {
      return Math.min(calmWeight, 0.35);
    }
    if (distance > 140.0) {
      return Math.min(calmWeight, 0.55);
    }
    if (distance > 45.0) {
      return Math.min(calmWeight, 0.70);
    }
    return calmWeight;
  }

  private static double distance(ScreenPoint a, ScreenPoint b) {
    double dx = b.x() - a.x();
    double dy = b.y() - a.y();
    return Math.sqrt(dx * dx + dy * dy);
  }
}
