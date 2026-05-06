package com.ghostmouse.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ghostmouse.model.Landmark;
import org.junit.jupiter.api.Test;

final class LandmarkMathTest {
  @Test
  void calculates3dDistance() {
    Landmark a = new Landmark(0.0, 0.0, 0.0);
    Landmark b = new Landmark(0.0, 3.0, 4.0);

    assertEquals(5.0, LandmarkMath.distance3d(a, b), 0.0001);
  }

  @Test
  void clampsValues() {
    assertEquals(0.0, LandmarkMath.clamp(-1.0, 0.0, 1.0));
    assertEquals(0.5, LandmarkMath.clamp(0.5, 0.0, 1.0));
    assertEquals(1.0, LandmarkMath.clamp(2.0, 0.0, 1.0));
  }
}
