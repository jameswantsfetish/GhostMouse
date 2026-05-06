package com.ghostmouse.model;

public record Landmark(double x, double y, double z) {
  public Landmark {
    if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
      throw new IllegalArgumentException("Landmark coordinates must be finite.");
    }
  }
}
