package com.ghostmouse.control;

import com.ghostmouse.model.ScreenPoint;
import java.util.logging.Logger;

public final class NoopMouseController implements MouseController {
  private static final Logger LOGGER = Logger.getLogger(NoopMouseController.class.getName());

  @Override
  public void moveTo(ScreenPoint point) {
    LOGGER.fine(() -> "Mouse move suppressed: " + point);
  }

  @Override
  public void leftClick() {
    LOGGER.info("Left click suppressed. Start with --enable-robot to control the real mouse.");
  }

  @Override
  public void rightClick() {
    LOGGER.info("Right click suppressed. Start with --enable-robot to control the real mouse.");
  }

  @Override
  public void leftDown() {
    LOGGER.info("Left mouse down suppressed. Start with --enable-robot to control the real mouse.");
  }

  @Override
  public void leftUp() {
    LOGGER.info("Left mouse up suppressed. Start with --enable-robot to control the real mouse.");
  }

  @Override
  public void scroll(int wheelUnits) {
    LOGGER.info(() -> "Scroll suppressed: " + wheelUnits);
  }
}
