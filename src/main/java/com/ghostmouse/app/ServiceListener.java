package com.ghostmouse.app;

import com.ghostmouse.model.HandFrame;

public interface ServiceListener {
  default void onStarted(String sourceName) {}

  default void onStopped() {}

  default void onFrame(HandFrame frame) {}
}
