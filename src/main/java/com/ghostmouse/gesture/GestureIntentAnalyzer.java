package com.ghostmouse.gesture;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.model.HandFrame;

public final class GestureIntentAnalyzer {
  private static final double SCROLL_FINGER_RATIO = 0.22;
  private static final double DRAG_FINGER_RATIO = 0.28;

  public GestureSignal analyze(HandFrame frame, GhostMouseConfig config) {
    double thumbIndex = PinchMeasurements.thumbTo(frame, PinchFinger.INDEX).ratio();
    double thumbMiddle = PinchMeasurements.thumbTo(frame, PinchFinger.MIDDLE).ratio();
    double thumbRing = PinchMeasurements.thumbTo(frame, PinchFinger.RING).ratio();
    double thumbPinky = PinchMeasurements.thumbTo(frame, PinchFinger.PINKY).ratio();
    double indexMiddle = PinchMeasurements.between(frame, 8, 12).ratio();

    double press = config.tapThreshold();
    double release = config.tapReleaseThreshold();

    boolean thumbAwayFromIndexMiddle = thumbIndex > release && thumbMiddle > release;
    boolean scrollPose = indexMiddle < SCROLL_FINGER_RATIO && thumbAwayFromIndexMiddle;

    boolean ringAndPinkyClear = thumbRing > press && thumbPinky > press;
    boolean dragPose = thumbIndex < press
        && thumbMiddle < press
        && indexMiddle < DRAG_FINGER_RATIO
        && ringAndPinkyClear
        && !scrollPose;

    boolean leftClickPose = thumbIndex < press
        && thumbMiddle > press
        && thumbPinky > press
        && !dragPose
        && !scrollPose;

    boolean rightClickPose = thumbPinky < press
        && thumbIndex > press
        && thumbMiddle > press
        && !dragPose
        && !scrollPose;

    return new GestureSignal(
        thumbIndex,
        thumbMiddle,
        thumbRing,
        thumbPinky,
        indexMiddle,
        leftClickPose,
        rightClickPose,
        dragPose,
        scrollPose);
  }
}
