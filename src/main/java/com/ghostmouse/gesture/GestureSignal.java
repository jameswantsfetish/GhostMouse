package com.ghostmouse.gesture;

public record GestureSignal(
    double thumbIndexRatio,
    double thumbMiddleRatio,
    double thumbRingRatio,
    double thumbPinkyRatio,
    double indexMiddleRatio,
    boolean leftClickPose,
    boolean rightClickPose,
    boolean dragPose,
    boolean scrollPose) {}
