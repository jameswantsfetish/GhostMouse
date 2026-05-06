package com.ghostmouse.gesture;

import com.ghostmouse.math.AdaptivePointerFilter;
import com.ghostmouse.model.HandFrame;
import com.ghostmouse.model.ScreenPoint;

public final class MovementPlugin implements GesturePlugin {
  private final AdaptivePointerFilter filter;

  public MovementPlugin(double smoothingFactor) {
    this.filter = new AdaptivePointerFilter(smoothingFactor);
  }

  @Override
  public String name() {
    return "movement";
  }

  @Override
  public void onFrame(HandFrame frame, GestureContext context) {
    ScreenPoint target = context.screenMapper().toScreenPoint(frame.palmCenter());
    context.mouseController().moveTo(filter.apply(target));
  }

  @Override
  public void reset() {
    filter.reset();
  }
}
