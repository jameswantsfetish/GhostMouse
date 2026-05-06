package com.ghostmouse.gesture;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.control.MouseController;
import com.ghostmouse.control.ScreenMapper;
import java.time.Clock;

public record GestureContext(
    GhostMouseConfig config,
    MouseController mouseController,
    ScreenMapper screenMapper,
    Clock clock) {}
