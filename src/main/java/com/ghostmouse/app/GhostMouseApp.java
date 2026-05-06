package com.ghostmouse.app;

import com.ghostmouse.control.MouseController;
import com.ghostmouse.control.NoopMouseController;
import com.ghostmouse.control.RobotMouseController;
import com.ghostmouse.control.ScreenMapper;
import com.ghostmouse.gesture.GestureContext;
import com.ghostmouse.gesture.GestureManager;
import com.ghostmouse.gesture.GhostMouseControlPlugin;
import com.ghostmouse.tray.TrayIconManager;
import com.ghostmouse.ui.GhostMouseWindow;
import com.ghostmouse.vision.DemoHandFrameProvider;
import com.ghostmouse.vision.HandFrameProvider;
import com.ghostmouse.vision.SocketHandFrameProvider;
import java.awt.GraphicsEnvironment;
import java.time.Clock;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GhostMouseApp {
  private static final Logger LOGGER = Logger.getLogger(GhostMouseApp.class.getName());

  private GhostMouseApp() {}

  public static void main(String[] args) throws InterruptedException {
    configureLogging();
    GhostMouseConfig config = parseArgs(args);
    if (Arrays.asList(args).contains("--help")) {
      printHelp();
      return;
    }

    MouseController mouseController = config.robotEnabled()
        ? new RobotMouseController()
        : new NoopMouseController();
    HandFrameProvider frameProvider = createFrameProvider(config);
    // Keep the vision source behind one interface so the Java gesture code does
    // not care whether landmarks come from the demo stream or MediaPipe socket.
    ScreenMapper screenMapper = new ScreenMapper(
        config.mirrorX(),
        config.inputMinX(),
        config.inputMaxX(),
        config.inputMinY(),
        config.inputMaxY());
    GestureContext context = new GestureContext(config, mouseController, screenMapper, Clock.systemDefaultZone());
    GestureManager gestureManager = new GestureManager(context)
        .add(new GhostMouseControlPlugin(config.smoothingFactor()));
    GhostMouseService service = new GhostMouseService(frameProvider, gestureManager);
    GhostMouseWindow window = GraphicsEnvironment.isHeadless()
        ? null
        : new GhostMouseWindow(service, config);
    TrayIconManager tray = new TrayIconManager(service, () -> {
      if (window != null) {
        window.showWindow();
      }
    });
    CountDownLatch shutdown = new CountDownLatch(1);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      tray.close();
      service.close();
      mouseController.close();
      shutdown.countDown();
    }, "ghostmouse-shutdown"));

    boolean trayInstalled = tray.install();
    if (window != null) {
      window.showWindow();
    }
    service.start();

    LOGGER.info(() -> "GhostMouse is running. Source=" + config.visionSource().name().toLowerCase(Locale.ROOT)
        + ", robot=" + config.robotEnabled()
        + ", tray=" + trayInstalled);
    shutdown.await();
  }

  private static HandFrameProvider createFrameProvider(GhostMouseConfig config) {
    return switch (config.visionSource()) {
      case DEMO -> new DemoHandFrameProvider(config.framesPerSecond());
      case SOCKET -> new SocketHandFrameProvider(config.socketPort());
    };
  }

  private static GhostMouseConfig parseArgs(String[] args) {
    GhostMouseConfig config = GhostMouseConfig.defaults();
    for (String arg : args) {
      if (arg.equals("--enable-robot")) {
        config = config.withRobotEnabled(true);
      } else if (arg.equals("--disable-clicks")) {
        config = config.withClicksEnabled(false);
      } else if (arg.startsWith("--active-zone=")) {
        config = withActiveZone(config, valueOf(arg));
      } else if (arg.startsWith("--smoothing=")) {
        config = config.withSmoothingFactor(Double.parseDouble(valueOf(arg)));
      } else if (arg.startsWith("--tap-threshold=")) {
        config = config.withTapThreshold(Double.parseDouble(valueOf(arg)));
      } else if (arg.startsWith("--source=")) {
        config = config.withVisionSource(VisionSource.parse(valueOf(arg)));
      } else if (arg.startsWith("--port=")) {
        config = config.withSocketPort(Integer.parseInt(valueOf(arg)));
      } else if (arg.equals("--help")) {
        return config;
      } else {
        throw new IllegalArgumentException("Unknown argument: " + arg);
      }
    }
    return config;
  }

  private static String valueOf(String arg) {
    int equals = arg.indexOf('=');
    if (equals < 0 || equals == arg.length() - 1) {
      throw new IllegalArgumentException("Expected --key=value format: " + arg);
    }
    return arg.substring(equals + 1);
  }

  private static GhostMouseConfig withActiveZone(GhostMouseConfig config, String value) {
    String[] parts = value.split(",");
    if (parts.length != 4) {
      throw new IllegalArgumentException("Expected --active-zone=minX,maxX,minY,maxY");
    }
    return config.withActiveZone(
        Double.parseDouble(parts[0]),
        Double.parseDouble(parts[1]),
        Double.parseDouble(parts[2]),
        Double.parseDouble(parts[3]));
  }

  private static void printHelp() {
    System.out.println("""
        GhostMouse

        Options:
          --source=demo|socket     Landmark source. Default: demo
          --port=8765              Socket port for --source=socket
          --enable-robot           Move and click the real OS mouse
          --disable-clicks         Move the cursor but suppress tap clicks
          --active-zone=a,b,c,d    Map camera sub-rectangle to full screen
          --smoothing=0.80         Cursor smoothing; lower is faster, higher is calmer
          --tap-threshold=0.35     Scale-aware thumb-to-finger pinch ratio for clicks
          --help                   Show this help

        Examples:
          java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=demo
          java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot
          java -jar target/ghostmouse-0.1.0-SNAPSHOT.jar --source=socket --enable-robot --disable-clicks
        """);
  }

  private static void configureLogging() {
    Logger root = Logger.getLogger("");
    root.setLevel(Level.INFO);
    for (java.util.logging.Handler handler : root.getHandlers()) {
      root.removeHandler(handler);
    }
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.INFO);
    root.addHandler(handler);
  }
}
