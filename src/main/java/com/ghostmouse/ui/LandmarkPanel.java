package com.ghostmouse.ui;

import com.ghostmouse.model.HandFrame;
import com.ghostmouse.model.Landmark;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;

public final class LandmarkPanel extends JPanel {
  private static final int[][] CONNECTIONS = {
      {0, 1}, {1, 2}, {2, 3}, {3, 4},
      {0, 5}, {5, 6}, {6, 7}, {7, 8},
      {0, 9}, {9, 10}, {10, 11}, {11, 12},
      {0, 13}, {13, 14}, {14, 15}, {15, 16},
      {0, 17}, {17, 18}, {18, 19}, {19, 20},
      {5, 9}, {9, 13}, {13, 17}
  };

  private HandFrame frame;

  public LandmarkPanel() {
    setPreferredSize(new Dimension(520, 330));
    setMinimumSize(new Dimension(360, 240));
    setBackground(new Color(17, 19, 24));
  }

  public void updateFrame(HandFrame frame) {
    this.frame = frame;
    repaint();
  }

  public void clearFrame() {
    this.frame = null;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    Graphics2D g = (Graphics2D) graphics.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (frame == null) {
      paintEmptyState(g);
      g.dispose();
      return;
    }

    List<Landmark> landmarks = frame.landmarks();
    g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.setColor(new Color(88, 214, 141, 190));
    for (int[] connection : CONNECTIONS) {
      Landmark a = landmarks.get(connection[0]);
      Landmark b = landmarks.get(connection[1]);
      g.drawLine(x(a), y(a), x(b), y(b));
    }

    for (int index = 0; index < landmarks.size(); index++) {
      Landmark landmark = landmarks.get(index);
      int radius = switch (index) {
        case 4, 8, 20 -> 8;
        default -> 5;
      };
      g.setColor(colorFor(index));
      g.fillOval(x(landmark) - radius, y(landmark) - radius, radius * 2, radius * 2);
    }
    g.dispose();
  }

  private void paintEmptyState(Graphics2D g) {
    g.setColor(new Color(180, 188, 200));
    String message = "Waiting for hand landmarks";
    FontMetrics metrics = g.getFontMetrics();
    g.drawString(message, (getWidth() - metrics.stringWidth(message)) / 2, getHeight() / 2);
  }

  private Color colorFor(int index) {
    return switch (index) {
      case 4 -> new Color(249, 196, 80);
      case 8 -> new Color(84, 190, 255);
      case 20 -> new Color(255, 111, 145);
      default -> new Color(235, 238, 244);
    };
  }

  private int x(Landmark landmark) {
    return (int) Math.round((1.0 - landmark.x()) * (getWidth() - 1));
  }

  private int y(Landmark landmark) {
    return (int) Math.round(landmark.y() * (getHeight() - 1));
  }
}
