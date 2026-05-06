package com.ghostmouse.control;

import com.ghostmouse.model.ScreenPoint;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public final class RobotMouseController implements MouseController {
  private final Robot robot;
  private boolean leftButtonDown;

  public RobotMouseController() {
    try {
      robot = new Robot();
      robot.setAutoDelay(4);
    } catch (AWTException exception) {
      throw new IllegalStateException("Could not create java.awt.Robot. Check desktop/accessibility permissions.", exception);
    }
  }

  @Override
  public void moveTo(ScreenPoint point) {
    robot.mouseMove(point.x(), point.y());
  }

  @Override
  public void leftClick() {
    click(InputEvent.BUTTON1_DOWN_MASK);
  }

  @Override
  public void rightClick() {
    click(InputEvent.BUTTON3_DOWN_MASK);
  }

  @Override
  public void leftDown() {
    if (!leftButtonDown) {
      robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
      leftButtonDown = true;
    }
  }

  @Override
  public void leftUp() {
    if (leftButtonDown) {
      robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
      leftButtonDown = false;
    }
  }

  @Override
  public void scroll(int wheelUnits) {
    robot.mouseWheel(wheelUnits);
  }

  @Override
  public void close() {
    leftUp();
  }

  private void click(int mask) {
    robot.mousePress(mask);
    robot.mouseRelease(mask);
  }
}
