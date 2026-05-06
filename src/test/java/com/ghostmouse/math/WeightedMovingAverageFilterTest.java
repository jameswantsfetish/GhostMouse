package com.ghostmouse.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ghostmouse.model.ScreenPoint;
import org.junit.jupiter.api.Test;

final class WeightedMovingAverageFilterTest {
  @Test
  void firstPointPassesThroughThenSmooths() {
    WeightedMovingAverageFilter filter = new WeightedMovingAverageFilter(0.8);

    assertEquals(new ScreenPoint(100, 100), filter.apply(new ScreenPoint(100, 100)));
    assertEquals(new ScreenPoint(120, 120), filter.apply(new ScreenPoint(200, 200)));
  }
}
