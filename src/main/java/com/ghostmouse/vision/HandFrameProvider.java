package com.ghostmouse.vision;

import com.ghostmouse.model.HandFrame;
import java.util.function.Consumer;

public interface HandFrameProvider extends AutoCloseable {
  String name();

  void start(Consumer<HandFrame> frameConsumer);

  void stop();

  @Override
  default void close() {
    stop();
  }
}
