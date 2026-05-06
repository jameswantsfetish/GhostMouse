package com.ghostmouse.app;

import java.time.Duration;

public record GhostMouseConfig(
    double tapThreshold,
    double smoothingFactor,
    Duration clickCooldown,
    int framesPerSecond,
    boolean mirrorX,
    double inputMinX,
    double inputMaxX,
    double inputMinY,
    double inputMaxY,
    double tapReleaseThreshold,
    double clickAmbiguityMargin,
    int clickPressFrames,
    int clickReleaseFrames,
    int socketPort,
    boolean robotEnabled,
    boolean clicksEnabled,
    VisionSource visionSource) {

  public static GhostMouseConfig defaults() {
    return new GhostMouseConfig(
        0.35,
        0.80,
        Duration.ofMillis(350),
        30,
        true,
        0.30,
        0.72,
        0.28,
        0.78,
        0.52,
        0.08,
        2,
        2,
        8765,
        false,
        true,
        VisionSource.DEMO);
  }

  public GhostMouseConfig {
    if (tapThreshold <= 0.0) {
      throw new IllegalArgumentException("tapThreshold must be positive.");
    }
    if (smoothingFactor < 0.0 || smoothingFactor >= 1.0) {
      throw new IllegalArgumentException("smoothingFactor must be >= 0 and < 1.");
    }
    if (framesPerSecond <= 0 || framesPerSecond > 120) {
      throw new IllegalArgumentException("framesPerSecond must be between 1 and 120.");
    }
    if (socketPort < 1 || socketPort > 65535) {
      throw new IllegalArgumentException("socketPort must be a valid TCP port.");
    }
    if (inputMinX < 0.0 || inputMinY < 0.0 || inputMaxX > 1.0 || inputMaxY > 1.0
        || inputMinX >= inputMaxX || inputMinY >= inputMaxY) {
      throw new IllegalArgumentException("Input bounds must be ordered normalized values between 0 and 1.");
    }
    if (tapReleaseThreshold <= tapThreshold) {
      throw new IllegalArgumentException("tapReleaseThreshold must be greater than tapThreshold.");
    }
    if (clickAmbiguityMargin < 0.0 || clickAmbiguityMargin >= tapThreshold) {
      throw new IllegalArgumentException("clickAmbiguityMargin must be >= 0 and less than tapThreshold.");
    }
    if (clickPressFrames <= 0 || clickReleaseFrames <= 0) {
      throw new IllegalArgumentException("clickPressFrames and clickReleaseFrames must be positive.");
    }
  }

  public GhostMouseConfig withRobotEnabled(boolean enabled) {
    return new GhostMouseConfig(tapThreshold, smoothingFactor, clickCooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, enabled, clicksEnabled, visionSource);
  }

  public GhostMouseConfig withClicksEnabled(boolean enabled) {
    return new GhostMouseConfig(tapThreshold, smoothingFactor, clickCooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, robotEnabled, enabled, visionSource);
  }

  public GhostMouseConfig withVisionSource(VisionSource source) {
    return new GhostMouseConfig(tapThreshold, smoothingFactor, clickCooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, robotEnabled, clicksEnabled, source);
  }

  public GhostMouseConfig withSocketPort(int port) {
    return new GhostMouseConfig(tapThreshold, smoothingFactor, clickCooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, port, robotEnabled, clicksEnabled, visionSource);
  }

  public GhostMouseConfig withActiveZone(double minX, double maxX, double minY, double maxY) {
    return new GhostMouseConfig(tapThreshold, smoothingFactor, clickCooldown, framesPerSecond, mirrorX, minX, maxX, minY, maxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, robotEnabled, clicksEnabled, visionSource);
  }

  public GhostMouseConfig withSmoothingFactor(double factor) {
    return new GhostMouseConfig(tapThreshold, factor, clickCooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, robotEnabled, clicksEnabled, visionSource);
  }

  public GhostMouseConfig withTapThreshold(double threshold) {
    double releaseThreshold = Math.max(tapReleaseThreshold, threshold * 1.45);
    double ambiguityMargin = Math.min(clickAmbiguityMargin, threshold * 0.25);
    return new GhostMouseConfig(threshold, smoothingFactor, clickCooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, releaseThreshold, ambiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, robotEnabled, clicksEnabled, visionSource);
  }

  public GhostMouseConfig withClickCooldown(Duration cooldown) {
    return new GhostMouseConfig(tapThreshold, smoothingFactor, cooldown, framesPerSecond, mirrorX, inputMinX, inputMaxX, inputMinY, inputMaxY, tapReleaseThreshold, clickAmbiguityMargin, clickPressFrames, clickReleaseFrames, socketPort, robotEnabled, clicksEnabled, visionSource);
  }
}
