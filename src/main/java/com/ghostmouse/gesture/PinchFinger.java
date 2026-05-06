package com.ghostmouse.gesture;

public enum PinchFinger {
  INDEX(8, "index"),
  MIDDLE(12, "middle"),
  RING(16, "ring"),
  PINKY(20, "pinky");

  private final int landmarkIndex;
  private final String displayName;

  PinchFinger(int landmarkIndex, String displayName) {
    this.landmarkIndex = landmarkIndex;
    this.displayName = displayName;
  }

  public int landmarkIndex() {
    return landmarkIndex;
  }

  public String displayName() {
    return displayName;
  }
}
