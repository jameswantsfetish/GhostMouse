package com.ghostmouse.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ghostmouse.model.ScreenPoint;
import org.junit.jupiter.api.Test;

final class AdaptivePointerFilterTest {
  @Test
  void smallMotionUsesCalmSmoothing() {
    AdaptivePointerFilter filter = new AdaptivePointerFilter(0.8);

    assertEquals(new ScreenPoint(100, 100), filter.apply(new ScreenPoint(100, 100)));
    assertEquals(new ScreenPoint(104, 100), filter.apply(new ScreenPoint(120, 100)));
  }

  @Test
  void largeMotionMovesMoreResponsively() {
    AdaptivePointerFilter filter = new AdaptivePointerFilter(0.8);

    assertEquals(new ScreenPoint(100, 100), filter.apply(new ScreenPoint(100, 100)));
    assertEquals(new ScreenPoint(685, 100), filter.apply(new ScreenPoint(1000, 100)));
  }
}
