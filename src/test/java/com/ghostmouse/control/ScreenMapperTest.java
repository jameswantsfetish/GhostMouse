package com.ghostmouse.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ghostmouse.model.Landmark;
import com.ghostmouse.model.ScreenPoint;
import java.awt.Dimension;
import org.junit.jupiter.api.Test;

final class ScreenMapperTest {
  @Test
  void activeZoneMapsToScreenCorners() {
    ScreenMapper mapper = new ScreenMapper(new Dimension(100, 100), false, 0.20, 0.80, 0.10, 0.90);

    assertEquals(new ScreenPoint(0, 0), mapper.toScreenPoint(new Landmark(0.20, 0.10, 0.0)));
    assertEquals(new ScreenPoint(99, 99), mapper.toScreenPoint(new Landmark(0.80, 0.90, 0.0)));
  }

  @Test
  void activeZoneClampsOutsideBounds() {
    ScreenMapper mapper = new ScreenMapper(new Dimension(100, 100), false, 0.20, 0.80, 0.10, 0.90);

    assertEquals(new ScreenPoint(0, 0), mapper.toScreenPoint(new Landmark(0.10, 0.00, 0.0)));
    assertEquals(new ScreenPoint(99, 99), mapper.toScreenPoint(new Landmark(0.90, 1.00, 0.0)));
  }
}
