package com.ghostmouse.ui;

import com.ghostmouse.app.GhostMouseConfig;
import com.ghostmouse.app.GhostMouseService;
import com.ghostmouse.app.ServiceListener;
import com.ghostmouse.gesture.GestureIntentAnalyzer;
import com.ghostmouse.gesture.GestureSignal;
import com.ghostmouse.model.HandFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class GhostMouseWindow extends JFrame implements ServiceListener {
  private static final DateTimeFormatter TIME_FORMAT =
      DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

  private final GhostMouseService service;
  private final GhostMouseConfig config;
  private final LandmarkPanel landmarkPanel = new LandmarkPanel();
  private final JLabel runState = new JLabel("Stopped");
  private final JLabel sourceState = new JLabel();
  private final JLabel robotState = new JLabel();
  private final JLabel clickState = new JLabel();
  private final JLabel clickModeState =
      new JLabel("Click mode: thumb-index left, thumb-pinky right, three-finger drag, peace-scroll");
  private final JLabel pointerState = new JLabel("Pointer: No hand");
  private final JLabel gestureState = new JLabel("Gesture: No hand");
  private final JLabel handState = new JLabel("Hand: none");
  private final JLabel activeZoneState = new JLabel();
  private final JLabel frameState = new JLabel("Last frame: none");
  private Instant lastFrameAt;
  private final GestureIntentAnalyzer gestureAnalyzer = new GestureIntentAnalyzer();

  public GhostMouseWindow(GhostMouseService service, GhostMouseConfig config) {
    super("GhostMouse");
    this.service = service;
    this.config = config;
    service.addListener(this);
    sourceState.setText("Source: " + config.visionSource().name().toLowerCase() + socketSuffix(config));
    robotState.setText("Mouse control: " + (config.robotEnabled() ? "ON" : "OFF"));
    clickState.setText("Tap clicks: " + (config.clicksEnabled() ? "ON" : "OFF"));
    activeZoneState.setText("Active zone: " + activeZone(config));
    buildUi();
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setMinimumSize(new Dimension(620, 500));
    pack();
    setLocationRelativeTo(null);

    Timer timer = new Timer(1000, event -> refreshFrameAge());
    timer.start();
  }

  public void showWindow() {
    SwingUtilities.invokeLater(() -> {
      setVisible(true);
      toFront();
      requestFocus();
    });
  }

  @Override
  public void onStarted(String sourceName) {
    SwingUtilities.invokeLater(() -> {
      runState.setText("Running");
      runState.setForeground(new Color(24, 135, 84));
      sourceState.setText("Source: " + sourceName);
    });
  }

  @Override
  public void onStopped() {
    SwingUtilities.invokeLater(() -> {
      runState.setText("Stopped");
      runState.setForeground(new Color(170, 60, 60));
      frameState.setText("Last frame: none");
      pointerState.setText("Pointer: No hand");
      gestureState.setText("Gesture: No hand");
      handState.setText("Hand: none");
      lastFrameAt = null;
      landmarkPanel.clearFrame();
    });
  }

  @Override
  public void onFrame(HandFrame frame) {
    SwingUtilities.invokeLater(() -> {
      lastFrameAt = Instant.now();
      frameState.setText("Last frame: " + TIME_FORMAT.format(lastFrameAt));
      pointerState.setText("Pointer: Palm center tracking");
      gestureState.setText("Gesture: " + gestureLabel(frame));
      handState.setText("Hand: " + handLabel(frame));
      landmarkPanel.updateFrame(frame);
    });
  }

  private void buildUi() {
    JPanel root = new JPanel(new BorderLayout(16, 16));
    root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    root.setBackground(new Color(245, 247, 250));
    setContentPane(root);

    JLabel title = new JLabel("GhostMouse");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(title, BorderLayout.WEST);
    header.add(statusPill(runState), BorderLayout.EAST);
    root.add(header, BorderLayout.NORTH);

    JPanel center = new JPanel(new BorderLayout(12, 12));
    center.setOpaque(false);
    center.add(landmarkPanel, BorderLayout.CENTER);
    center.add(infoPanel(), BorderLayout.SOUTH);
    root.add(center, BorderLayout.CENTER);

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    controls.setOpaque(false);
    JButton start = new JButton("Start");
    JButton stop = new JButton("Stop");
    JButton hide = new JButton("Hide");
    JButton exit = new JButton("Exit");

    start.addActionListener(event -> service.start());
    stop.addActionListener(event -> service.stop());
    hide.addActionListener(event -> setVisible(false));
    exit.addActionListener(event -> {
      service.close();
      System.exit(0);
    });

    controls.add(start);
    controls.add(stop);
    controls.add(hide);
    controls.add(exit);
    root.add(controls, BorderLayout.SOUTH);
  }

  private JPanel infoPanel() {
    JPanel panel = new JPanel();
    panel.setOpaque(false);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(sourceState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(robotState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(clickState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(clickModeState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(pointerState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(gestureState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(handState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(activeZoneState);
    panel.add(Box.createVerticalStrut(4));
    panel.add(frameState);
    return panel;
  }

  private JLabel statusPill(JLabel label) {
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(210, 216, 224)),
        BorderFactory.createEmptyBorder(7, 14, 7, 14)));
    label.setOpaque(true);
    label.setBackground(Color.WHITE);
    return label;
  }

  private void refreshFrameAge() {
    if (lastFrameAt == null || !service.isRunning()) {
      return;
    }
    long seconds = Duration.between(lastFrameAt, Instant.now()).toSeconds();
    if (seconds > 2) {
      frameState.setText("Last frame: " + seconds + "s ago");
      pointerState.setText("Pointer: No hand");
      gestureState.setText("Gesture: No hand");
      handState.setText("Hand: none");
    }
  }

  private String gestureLabel(HandFrame frame) {
    GestureSignal signal = gestureAnalyzer.analyze(frame, config);
    if (signal.scrollPose()) {
      return String.format("Scroll %.2f", signal.indexMiddleRatio());
    }
    if (signal.dragPose()) {
      return String.format("Drag %.2f/%.2f", signal.thumbIndexRatio(), signal.thumbMiddleRatio());
    }
    if (signal.leftClickPose()) {
      return String.format("Left click %.2f", signal.thumbIndexRatio());
    }
    if (signal.rightClickPose()) {
      return String.format("Right click %.2f", signal.thumbPinkyRatio());
    }
    return String.format(
        "Tracking L %.2f / R %.2f / scroll %.2f",
        signal.thumbIndexRatio(),
        signal.thumbPinkyRatio(),
        signal.indexMiddleRatio());
  }

  private String handLabel(HandFrame frame) {
    String source = frame.hasWorldLandmarks() ? "world landmarks" : "image landmarks";
    if (frame.handedness().isBlank()) {
      return source;
    }
    return String.format("%s %.0f%%, %s", frame.handedness(), frame.handednessScore() * 100.0, source);
  }

  private String socketSuffix(GhostMouseConfig config) {
    return switch (config.visionSource()) {
      case SOCKET -> ":" + config.socketPort();
      case DEMO -> "";
    };
  }

  private String activeZone(GhostMouseConfig config) {
    return String.format(
        "%.2f,%.2f,%.2f,%.2f",
        config.inputMinX(),
        config.inputMaxX(),
        config.inputMinY(),
        config.inputMaxY());
  }
}
