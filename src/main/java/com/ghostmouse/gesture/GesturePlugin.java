package com.ghostmouse.gesture;

import com.ghostmouse.model.HandFrame;

public interface GesturePlugin {
  String name();

  void onFrame(HandFrame frame, GestureContext context);

  default void reset() {}

  default void reset(GestureContext context) {
    reset();
  }
}
