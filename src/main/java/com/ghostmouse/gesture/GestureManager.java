package com.ghostmouse.gesture;

import com.ghostmouse.model.HandFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GestureManager {
  private static final Logger LOGGER = Logger.getLogger(GestureManager.class.getName());
  private final List<GesturePlugin> plugins = new ArrayList<>();
  private final GestureContext context;

  public GestureManager(GestureContext context) {
    this.context = context;
  }

  public GestureManager add(GesturePlugin plugin) {
    plugins.add(plugin);
    return this;
  }

  public void onFrame(HandFrame frame) {
    for (GesturePlugin plugin : plugins) {
      try {
        // Each plugin gets the same frame and shared context, which keeps new
        // gestures independent from the service loop and from each other.
        plugin.onFrame(frame, context);
      } catch (RuntimeException exception) {
        LOGGER.log(Level.WARNING, "Gesture plugin failed: " + plugin.name(), exception);
      }
    }
  }

  public void reset() {
    plugins.forEach(plugin -> plugin.reset(context));
  }
}
