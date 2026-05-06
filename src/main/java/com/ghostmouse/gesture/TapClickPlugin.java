package com.ghostmouse.gesture;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.model.HandFrame;
import java.time.Duration;
import java.time.Instant;

public final class TapClickPlugin implements GesturePlugin {
  private final PinchState leftClick = new PinchState();
  private final PinchState rightClick = new PinchState();

  @Override
  public String name() {
    return "tap-clicks";
  }

  @Override
  public void onFrame(HandFrame frame, GestureContext context) {
    Instant now = context.clock().instant();
    GhostMouseConfig config = context.config();
    PinchMeasurement index = PinchMeasurements.thumbTo(frame, PinchFinger.INDEX);
    PinchMeasurement pinky = PinchMeasurements.thumbTo(frame, PinchFinger.PINKY);

    boolean indexPressed = index.ratio() < config.tapThreshold();
    boolean pinkyPressed = pinky.ratio() < config.tapThreshold();
    // If two fingers are close at once, require one pinch to be clearly smaller
    // so a left click and right click cannot fire from the same uncertain pose.
    boolean indexWins = indexPressed
        && (!pinkyPressed || index.ratio() + config.clickAmbiguityMargin() < pinky.ratio());
    boolean pinkyWins = pinkyPressed
        && (!indexPressed || pinky.ratio() + config.clickAmbiguityMargin() < index.ratio());

    if (leftClick.update(index.ratio(), indexWins && !rightClick.isActive(), now, config)) {
      context.mouseController().leftClick();
    }

    if (rightClick.update(pinky.ratio(), pinkyWins && !leftClick.isActive(), now, config)) {
      context.mouseController().rightClick();
    }
  }

  @Override
  public void reset() {
    leftClick.reset();
    rightClick.reset();
  }

  private static final class PinchState {
    private boolean active;
    private int pressFrames;
    private int releaseFrames;
    private Instant lastClickAt = Instant.EPOCH;

    boolean isActive() {
      return active;
    }

    boolean update(double ratio, boolean canStart, Instant now, GhostMouseConfig config) {
      if (active) {
        if (ratio > config.tapReleaseThreshold()) {
          releaseFrames++;
        } else {
          releaseFrames = 0;
        }
        if (releaseFrames >= config.clickReleaseFrames()) {
          active = false;
          releaseFrames = 0;
        }
        return false;
      }

      if (canStart && ratio < config.tapThreshold()) {
        pressFrames++;
      } else {
        pressFrames = 0;
      }

      if (pressFrames >= config.clickPressFrames() && isCooldownOver(now, config.clickCooldown())) {
        // A click starts only after enough pressed frames and then stays active
        // until enough released frames pass, which prevents repeated click bursts.
        active = true;
        pressFrames = 0;
        releaseFrames = 0;
        lastClickAt = now;
        return true;
      }

      return false;
    }

    void reset() {
      active = false;
      pressFrames = 0;
      releaseFrames = 0;
      lastClickAt = Instant.EPOCH;
    }

    private boolean isCooldownOver(Instant now, Duration cooldown) {
      return !now.minus(cooldown).isBefore(lastClickAt);
    }
  }
}
