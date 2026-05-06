package com.ghostmouse.tray;

import com.ghostmouse.app.GhostMouseService;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TrayIconManager implements AutoCloseable {
  private static final Logger LOGGER = Logger.getLogger(TrayIconManager.class.getName());
  private final GhostMouseService service;
  private final Runnable showWindow;
  private TrayIcon trayIcon;

  public TrayIconManager(GhostMouseService service, Runnable showWindow) {
    this.service = service;
    this.showWindow = showWindow;
  }

  public boolean install() {
    if (GraphicsEnvironment.isHeadless() || !SystemTray.isSupported()) {
      LOGGER.info("System tray is not supported in this environment.");
      return false;
    }

    PopupMenu menu = new PopupMenu();
    MenuItem start = new MenuItem("Start");
    MenuItem stop = new MenuItem("Stop");
    MenuItem show = new MenuItem("Show Window");
    MenuItem exit = new MenuItem("Exit");

    start.addActionListener(event -> service.start());
    stop.addActionListener(event -> service.stop());
    show.addActionListener(event -> showWindow.run());
    exit.addActionListener(event -> {
      close();
      service.close();
      System.exit(0);
    });

    menu.add(show);
    menu.addSeparator();
    menu.add(start);
    menu.add(stop);
    menu.addSeparator();
    menu.add(exit);

    trayIcon = new TrayIcon(createIcon(), "GhostMouse", menu);
    trayIcon.setImageAutoSize(true);
    try {
      SystemTray.getSystemTray().add(trayIcon);
      return true;
    } catch (AWTException exception) {
      LOGGER.log(Level.WARNING, "Could not install GhostMouse tray icon.", exception);
      trayIcon = null;
      return false;
    }
  }

  @Override
  public void close() {
    if (trayIcon != null && SystemTray.isSupported()) {
      SystemTray.getSystemTray().remove(trayIcon);
      trayIcon = null;
    }
  }

  private static Image createIcon() {
    int size = 32;
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(new Color(26, 28, 34));
    graphics.fillOval(2, 2, 28, 28);
    graphics.setColor(new Color(88, 214, 141));
    graphics.fillOval(10, 7, 12, 12);
    graphics.fillRoundRect(14, 15, 4, 12, 4, 4);
    graphics.dispose();
    return image;
  }
}
