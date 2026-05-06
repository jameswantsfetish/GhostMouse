package com.ghostmouse.control;

import com.ghostmouse.model.ScreenPoint;

public interface MouseController extends AutoCloseable {
  void moveTo(ScreenPoint point);

  void leftClick();

  void rightClick();

  void leftDown();

  void leftUp();

  void scroll(int wheelUnits);

  @Override
  default void close() {}
}
