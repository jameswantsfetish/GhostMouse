package com.ghostmouse.vision;

import com.ghostmouse.model.HandFrame;
import com.ghostmouse.model.Landmark;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class DemoHandFrameProvider implements HandFrameProvider {
  private final int framesPerSecond;
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> task;
  private long tick;

  public DemoHandFrameProvider(int framesPerSecond) {
    this.framesPerSecond = framesPerSecond;
  }

  @Override
  public String name() {
    return "demo";
  }

  @Override
  public synchronized void start(Consumer<HandFrame> frameConsumer) {
    if (task != null && !task.isDone()) {
      return;
    }
    long periodMillis = Math.max(1, 1000 / framesPerSecond);
    task = executor.scheduleAtFixedRate(() -> frameConsumer.accept(nextFrame()), 0, periodMillis, TimeUnit.MILLISECONDS);
  }

  @Override
  public synchronized void stop() {
    if (task != null) {
      task.cancel(false);
      task = null;
    }
  }

  @Override
  public void close() {
    stop();
    executor.shutdownNow();
  }

  private HandFrame nextFrame() {
    tick++;
    double phase = (tick % 240) / 240.0;
    double x = 0.15 + 0.70 * phase;
    double y = 0.50 + 0.20 * Math.sin(phase * Math.PI * 2.0);

    List<Landmark> landmarks = new ArrayList<>(HandFrame.MEDIA_PIPE_HAND_LANDMARK_COUNT);
    for (int i = 0; i < HandFrame.MEDIA_PIPE_HAND_LANDMARK_COUNT; i++) {
      landmarks.add(new Landmark(0.5, 0.5, 0.0));
    }

    landmarks.set(4, new Landmark(0.25, 0.85, 0.0));
    landmarks.set(8, new Landmark(x, y, 0.0));
    landmarks.set(20, new Landmark(0.85, 0.85, 0.0));
    return new HandFrame(landmarks, Instant.now());
  }
}
