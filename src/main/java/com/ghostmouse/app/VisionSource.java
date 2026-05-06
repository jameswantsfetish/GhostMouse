package com.ghostmouse.app;

public enum VisionSource {
  DEMO,
  SOCKET;

  public static VisionSource parse(String value) {
    return switch (value.trim().toLowerCase()) {
      case "demo" -> DEMO;
      case "socket" -> SOCKET;
      default -> throw new IllegalArgumentException("Unknown source '" + value + "'. Use demo or socket.");
    };
  }
}
