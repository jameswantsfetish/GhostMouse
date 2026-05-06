package com.ghostmouse.control;

import com.ghostmouse.math.LandmarkMath;
import com.ghostmouse.model.Landmark;
import com.ghostmouse.model.ScreenPoint;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

public final class ScreenMapper {
  private final int width;
  private final int height;
  private final boolean mirrorX;
  private final double minX;
  private final double maxX;
  private final double minY;
  private final double maxY;

  public ScreenMapper(boolean mirrorX, double minX, double maxX, double minY, double maxY) {
    this(resolveScreenSize(), mirrorX, minX, maxX, minY, maxY);
  }

  public ScreenMapper(Dimension screenSize, boolean mirrorX) {
    this(screenSize, mirrorX, 0.0, 1.0, 0.0, 1.0);
  }

  public ScreenMapper(Dimension screenSize, boolean mirrorX, double minX, double maxX, double minY, double maxY) {
    this.width = Math.max(1, screenSize.width);
    this.height = Math.max(1, screenSize.height);
    this.mirrorX = mirrorX;
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
  }

  public ScreenPoint toScreenPoint(Landmark landmark) {
    double orientedX = mirrorX ? 1.0 - landmark.x() : landmark.x();
    // The active zone lets a comfortable camera region stretch to the full
    // monitor, so the user does not need to reach to the camera edges.
    double normalizedX = normalizeToActiveZone(orientedX, minX, maxX);
    double normalizedY = normalizeToActiveZone(landmark.y(), minY, maxY);
    int x = (int) Math.round(normalizedX * (width - 1));
    int y = (int) Math.round(normalizedY * (height - 1));
    return new ScreenPoint(x, y);
  }

  private static double normalizeToActiveZone(double value, double min, double max) {
    return LandmarkMath.clamp((value - min) / (max - min), 0.0, 1.0);
  }

  private static Dimension resolveScreenSize() {
    if (GraphicsEnvironment.isHeadless()) {
      return new Dimension(1920, 1080);
    }
    return Toolkit.getDefaultToolkit().getScreenSize();
  }
}
