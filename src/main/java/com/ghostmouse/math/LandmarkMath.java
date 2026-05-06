package com.ghostmouse.math;

import com.ghostmouse.model.Landmark;

public final class LandmarkMath {
  private LandmarkMath() {}

  public static double distance3d(Landmark a, Landmark b) {
    double dx = b.x() - a.x();
    double dy = b.y() - a.y();
    double dz = b.z() - a.z();
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  public static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}
