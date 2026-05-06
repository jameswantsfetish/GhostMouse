package com.ghostmouse.gesture;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.math.AdaptivePointerFilter;
import com.ghostmouse.model.HandFrame;
import com.ghostmouse.model.ScreenPoint;
import java.time.Duration;
import java.time.Instant;

public final class GhostMouseControlPlugin implements GesturePlugin {
  private static final int DRAG_PRESS_FRAMES = 3;
  private static final int DRAG_RELEASE_FRAMES = 2;
  private static final int SCROLL_PRESS_FRAMES = 2;
  private static final double SCROLL_DEAD_ZONE = 0.012;
  private static final double SCROLL_UNITS_PER_NORMALIZED_Y = 70.0;

  private final AdaptivePointerFilter filter;
  private final GestureIntentAnalyzer analyzer = new GestureIntentAnalyzer();
  private final ReleaseClickState leftClick = new ReleaseClickState();
  private final ReleaseClickState rightClick = new ReleaseClickState();

  private boolean dragActive;
  private int dragPressFrames;
  private int dragReleaseFrames;
  private int scrollPressFrames;
  private Double lastScrollY;
  private double scrollCarry;

  public GhostMouseControlPlugin(double smoothingFactor) {
    this.filter = new AdaptivePointerFilter(smoothingFactor);
  }

  @Override
  public String name() {
    return "ghostmouse-control";
  }

  @Override
  public void onFrame(HandFrame frame, GestureContext context) {
    GhostMouseConfig config = context.config();
    GestureSignal signal = analyzer.analyze(frame, config);

    if (!config.clicksEnabled()) {
      movePointer(frame, context);
      return;
    }

    if (handleScroll(frame, signal, context)) {
      return;
    }

    movePointer(frame, context);
    handleDrag(signal, context);

    if (dragActive || dragPressFrames > 0 || signal.dragPose() || signal.scrollPose()) {
      leftClick.cancel();
      rightClick.cancel();
      return;
    }

    Instant now = context.clock().instant();
    if (leftClick.update(signal.leftClickPose(), signal.thumbIndexRatio(), now, config)) {
      context.mouseController().leftClick();
    }
    if (rightClick.update(signal.rightClickPose(), signal.thumbPinkyRatio(), now, config)) {
      context.mouseController().rightClick();
    }
  }

  @Override
  public void reset() {
    filter.reset();
    leftClick.reset();
    rightClick.reset();
    dragActive = false;
    dragPressFrames = 0;
    dragReleaseFrames = 0;
    scrollPressFrames = 0;
    lastScrollY = null;
    scrollCarry = 0.0;
  }

  @Override
  public void reset(GestureContext context) {
    if (dragActive) {
      context.mouseController().leftUp();
    }
    reset();
  }

  private void movePointer(HandFrame frame, GestureContext context) {
    ScreenPoint target = context.screenMapper().toScreenPoint(frame.palmCenter());
    context.mouseController().moveTo(filter.apply(target));
  }

  private boolean handleScroll(HandFrame frame, GestureSignal signal, GestureContext context) {
    if (!signal.scrollPose() || dragActive) {
      scrollPressFrames = 0;
      lastScrollY = null;
      scrollCarry = 0.0;
      return false;
    }

    scrollPressFrames++;
    if (scrollPressFrames < SCROLL_PRESS_FRAMES) {
      return true;
    }

    double currentY = frame.palmCenter().y();
    if (lastScrollY == null) {
      lastScrollY = currentY;
      return true;
    }

    double deltaY = currentY - lastScrollY;
    lastScrollY = currentY;
    if (Math.abs(deltaY) < SCROLL_DEAD_ZONE) {
      return true;
    }

    scrollCarry += deltaY * SCROLL_UNITS_PER_NORMALIZED_Y;
    int wheelUnits = (int) scrollCarry;
    if (wheelUnits != 0) {
      context.mouseController().scroll(wheelUnits);
      scrollCarry -= wheelUnits;
    }
    return true;
  }

  private void handleDrag(GestureSignal signal, GestureContext context) {
    if (signal.dragPose()) {
      dragPressFrames++;
      dragReleaseFrames = 0;
      if (!dragActive && dragPressFrames >= DRAG_PRESS_FRAMES) {
        context.mouseController().leftDown();
        dragActive = true;
      }
      return;
    }

    dragPressFrames = 0;
    if (!dragActive) {
      dragReleaseFrames = 0;
      return;
    }

    dragReleaseFrames++;
    if (dragReleaseFrames >= DRAG_RELEASE_FRAMES) {
      context.mouseController().leftUp();
      dragActive = false;
      dragReleaseFrames = 0;
    }
  }

  private static final class ReleaseClickState {
    private boolean armed;
    private int pressFrames;
    private int releaseFrames;
    private Instant lastClickAt = Instant.EPOCH;

    boolean update(boolean pressed, double ratio, Instant now, GhostMouseConfig config) {
      if (armed) {
        if (ratio > config.tapReleaseThreshold()) {
          releaseFrames++;
        } else {
          releaseFrames = 0;
        }
        if (releaseFrames >= config.clickReleaseFrames()) {
          boolean cooldownOver = cooldownOver(now, config.clickCooldown());
          reset();
          if (cooldownOver) {
            lastClickAt = now;
            return true;
          }
        }
        return false;
      }

      if (pressed) {
        pressFrames++;
      } else {
        pressFrames = 0;
      }

      if (pressFrames >= config.clickPressFrames()) {
        armed = true;
        releaseFrames = 0;
      }
      return false;
    }

    void cancel() {
      armed = false;
      pressFrames = 0;
      releaseFrames = 0;
    }

    void reset() {
      cancel();
      lastClickAt = Instant.EPOCH;
    }

    private boolean cooldownOver(Instant now, Duration cooldown) {
      return !now.minus(cooldown).isBefore(lastClickAt);
    }
  }
}
