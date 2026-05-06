package com.ghostmouse.gesture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.control.MouseController;
import com.ghostmouse.control.ScreenMapper;
import com.ghostmouse.model.ScreenPoint;
import java.awt.Dimension;
import java.time.Clock;
import org.junit.jupiter.api.Test;

final class MovementPluginTest {
  @Test
  void validHandMovesCursorFromPalmCenter() {
    FakeMouseController mouse = new FakeMouseController();
    MovementPlugin plugin = new MovementPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.validPointing(), context(mouse));

    assertEquals(1, mouse.moves);
  }

  @Test
  void upsideDownPoseStillMovesCursor() {
    FakeMouseController mouse = new FakeMouseController();
    MovementPlugin plugin = new MovementPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.upsideDown(), context(mouse));

    assertEquals(1, mouse.moves);
  }

  @Test
  void thumbIndexTapPoseStillMovesCursorFromPalmCenter() {
    FakeMouseController mouse = new FakeMouseController();
    MovementPlugin plugin = new MovementPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.clickArmed(), context(mouse));

    assertEquals(1, mouse.moves);
  }

  private static GestureContext context(FakeMouseController mouse) {
    return new GestureContext(
        GhostMouseConfig.defaults(),
        mouse,
        new ScreenMapper(new Dimension(1920, 1080), true),
        Clock.systemUTC());
  }

  private static final class FakeMouseController implements MouseController {
    int moves;

    @Override
    public void moveTo(ScreenPoint point) {
      moves++;
    }

    @Override
    public void leftClick() {}

    @Override
    public void rightClick() {}

    @Override
    public void leftDown() {}

    @Override
    public void leftUp() {}

    @Override
    public void scroll(int wheelUnits) {}
  }
}
