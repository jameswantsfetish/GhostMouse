package com.ghostmouse.app;

import com.ghostmouse.gesture.GestureManager;
import com.ghostmouse.model.HandFrame;
import com.ghostmouse.vision.HandFrameProvider;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public final class GhostMouseService implements AutoCloseable {
  private static final Logger LOGGER = Logger.getLogger(GhostMouseService.class.getName());
  private final HandFrameProvider frameProvider;
  private final GestureManager gestureManager;
  private final List<ServiceListener> listeners = new CopyOnWriteArrayList<>();
  private final AtomicBoolean running = new AtomicBoolean();

  public GhostMouseService(HandFrameProvider frameProvider, GestureManager gestureManager) {
    this.frameProvider = frameProvider;
    this.gestureManager = gestureManager;
  }

  public void start() {
    if (running.compareAndSet(false, true)) {
      gestureManager.reset();
      frameProvider.start(this::onFrame);
      LOGGER.info(() -> "GhostMouse started with " + frameProvider.name() + " input.");
      listeners.forEach(listener -> listener.onStarted(frameProvider.name()));
    }
  }

  public void stop() {
    if (running.compareAndSet(true, false)) {
      frameProvider.stop();
      gestureManager.reset();
      LOGGER.info("GhostMouse stopped.");
      listeners.forEach(ServiceListener::onStopped);
    }
  }

  public void addListener(ServiceListener listener) {
    listeners.add(listener);
  }

  public boolean isRunning() {
    return running.get();
  }

  private void onFrame(HandFrame frame) {
    gestureManager.onFrame(frame);
    listeners.forEach(listener -> listener.onFrame(frame));
  }

  @Override
  public void close() {
    stop();
    frameProvider.close();
  }
}
