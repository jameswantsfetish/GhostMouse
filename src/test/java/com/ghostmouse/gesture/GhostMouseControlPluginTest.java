package com.ghostmouse.gesture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.control.MouseController;
import com.ghostmouse.control.ScreenMapper;
import com.ghostmouse.model.ScreenPoint;
import java.awt.Dimension;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

final class GhostMouseControlPluginTest {
  @Test
  void palmMovementWorksForNormalAndUpsideDownFrames() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.validPointing(), context(mouse));
    plugin.onFrame(HandFrameFixtures.upsideDown(), context(mouse));

    assertEquals(2, mouse.moves);
  }

  @Test
  void leftClickFiresAfterQuickThumbIndexPinchAndRelease() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.clickArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.clickArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.clickReleased(), context(mouse));
    plugin.onFrame(HandFrameFixtures.clickReleased(), context(mouse));

    assertEquals(1, mouse.leftClicks);
    assertEquals(0, mouse.leftDowns);
  }

  @Test
  void rightClickUsesThumbPinkyPinchAndRelease() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.rightClickArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.rightClickArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.clickReleased(), context(mouse));
    plugin.onFrame(HandFrameFixtures.clickReleased(), context(mouse));

    assertEquals(1, mouse.rightClicks);
    assertEquals(0, mouse.leftClicks);
  }

  @Test
  void dragHoldsLeftButtonUntilThreeFingerPinchReleases() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.dragArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.dragArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.dragArmed(), context(mouse));
    plugin.onFrame(HandFrameFixtures.validPointing(), context(mouse));
    plugin.onFrame(HandFrameFixtures.validPointing(), context(mouse));

    assertEquals(1, mouse.leftDowns);
    assertEquals(1, mouse.leftUps);
    assertEquals(0, mouse.leftClicks);
  }

  @Test
  void resetReleasesActiveDragSoMouseCannotStayHeld() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);
    GestureContext context = context(mouse);

    plugin.onFrame(HandFrameFixtures.dragArmed(), context);
    plugin.onFrame(HandFrameFixtures.dragArmed(), context);
    plugin.onFrame(HandFrameFixtures.dragArmed(), context);
    plugin.reset(context);

    assertEquals(1, mouse.leftDowns);
    assertEquals(1, mouse.leftUps);
  }

  @Test
  void scrollFreezesPointerAndScrollsFromPalmYMotion() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.validPointing(), context(mouse));
    plugin.onFrame(HandFrameFixtures.scrollArmed(0.45), context(mouse));
    plugin.onFrame(HandFrameFixtures.scrollArmed(0.45), context(mouse));
    plugin.onFrame(HandFrameFixtures.scrollArmed(0.55), context(mouse));

    assertEquals(1, mouse.moves);
    assertEquals(1, mouse.scrolls);
  }

  @Test
  void indexMiddleTogetherDoesNotScrollWhenThumbIsNear() {
    FakeMouseController mouse = new FakeMouseController();
    GhostMouseControlPlugin plugin = new GhostMouseControlPlugin(0.8);

    plugin.onFrame(HandFrameFixtures.scrollWithThumbNear(), context(mouse));
    plugin.onFrame(HandFrameFixtures.scrollWithThumbNear(), context(mouse));

    assertEquals(2, mouse.moves);
    assertEquals(0, mouse.scrolls);
  }

  private static GestureContext context(FakeMouseController mouse) {
    return new GestureContext(
        GhostMouseConfig.defaults(),
        mouse,
        new ScreenMapper(new Dimension(1920, 1080), true),
        Clock.fixed(Instant.parse("2026-05-06T06:00:00Z"), ZoneId.of("UTC")));
  }

  private static final class FakeMouseController implements MouseController {
    int moves;
    int leftClicks;
    int rightClicks;
    int leftDowns;
    int leftUps;
    int scrolls;

    @Override
    public void moveTo(ScreenPoint point) {
      moves++;
    }

    @Override
    public void leftClick() {
      leftClicks++;
    }

    @Override
    public void rightClick() {
      rightClicks++;
    }

    @Override
    public void leftDown() {
      leftDowns++;
    }

    @Override
    public void leftUp() {
      leftUps++;
    }

    @Override
    public void scroll(int wheelUnits) {
      scrolls++;
    }
  }
}
