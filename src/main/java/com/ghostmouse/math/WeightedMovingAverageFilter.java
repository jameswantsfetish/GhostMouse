package com.ghostmouse.math;

import com.ghostmouse.model.ScreenPoint;

public final class WeightedMovingAverageFilter {
  private final double previousWeight;
  private ScreenPoint current;

  public WeightedMovingAverageFilter(double previousWeight) {
    if (previousWeight < 0.0 || previousWeight >= 1.0) {
      throw new IllegalArgumentException("Smoothing factor must be >= 0.0 and < 1.0.");
    }
    this.previousWeight = previousWeight;
  }

  public ScreenPoint apply(ScreenPoint next) {
    if (current == null) {
      current = next;
      return next;
    }
    double nextWeight = 1.0 - previousWeight;
    int x = (int) Math.round(current.x() * previousWeight + next.x() * nextWeight);
    int y = (int) Math.round(current.y() * previousWeight + next.y() * nextWeight);
    current = new ScreenPoint(x, y);
    return current;
  }

  public void reset() {
    current = null;
  }
}
