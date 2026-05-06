package com.ghostmouse.vision;

import com.ghostmouse.model.HandFrame;
import com.ghostmouse.model.Landmark;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SocketHandFrameProvider implements HandFrameProvider {
  private static final Logger LOGGER = Logger.getLogger(SocketHandFrameProvider.class.getName());
  private final int port;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private volatile boolean running;
  private ServerSocket serverSocket;
  private Future<?> listener;
  private long frameCount;

  public SocketHandFrameProvider(int port) {
    this.port = port;
  }

  @Override
  public String name() {
    return "socket:" + port;
  }

  @Override
  public synchronized void start(Consumer<HandFrame> frameConsumer) {
    if (running) {
      return;
    }
    running = true;
    listener = executor.submit(() -> listen(frameConsumer));
  }

  @Override
  public synchronized void stop() {
    running = false;
    closeServerSocket();
    if (listener != null) {
      listener.cancel(true);
      listener = null;
    }
  }

  @Override
  public void close() {
    stop();
    executor.shutdownNow();
  }

  private void listen(Consumer<HandFrame> frameConsumer) {
    try (ServerSocket socket = new ServerSocket(port)) {
      serverSocket = socket;
      LOGGER.info(() -> "Waiting for MediaPipe landmark stream on 127.0.0.1:" + port);
      while (running) {
        try (Socket client = socket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
          LOGGER.info(() -> "Landmark client connected: " + client.getRemoteSocketAddress());
          String line;
          while (running && (line = reader.readLine()) != null) {
            try {
              parseLine(line).ifPresent(frame -> {
                frameConsumer.accept(frame);
                frameCount++;
                if (frameCount == 1 || frameCount % 120 == 0) {
                  LOGGER.info(() -> "Received MediaPipe hand frame #" + frameCount
                      + " handedness=" + (frame.handedness().isBlank() ? "unknown" : frame.handedness()));
                }
              });
            } catch (RuntimeException exception) {
              if (running) {
                LOGGER.log(Level.WARNING, "Ignoring malformed landmark frame without stopping tracking.", exception);
              }
            }
          }
          if (running) {
            LOGGER.info("Landmark client ended stream; waiting for reconnect.");
          }
        } catch (IOException exception) {
          if (running) {
            LOGGER.log(Level.WARNING, "Landmark client disconnected.", exception);
          }
        } catch (RuntimeException exception) {
          if (running) {
            LOGGER.log(Level.WARNING, "Landmark listener recovered after unexpected client error.", exception);
          }
        }
      }
    } catch (IOException exception) {
      if (running) {
        LOGGER.log(Level.SEVERE, "Could not listen for landmarks on port " + port, exception);
      }
    } catch (RuntimeException exception) {
      if (running) {
        LOGGER.log(Level.SEVERE, "Landmark listener stopped unexpectedly.", exception);
      }
    } finally {
      serverSocket = null;
    }
  }

  private java.util.Optional<HandFrame> parseLine(String line) {
    String trimmed = line.trim();
    if (trimmed.isEmpty()) {
      return java.util.Optional.empty();
    }

    if (trimmed.startsWith("v2|")) {
      return parseVersionTwoLine(trimmed);
    }

    return parseLandmarkList(trimmed, "legacy").map(landmarks -> new HandFrame(landmarks, Instant.now()));
  }

  private java.util.Optional<HandFrame> parseVersionTwoLine(String line) {
    Map<String, String> sections = new HashMap<>();
    String[] parts = line.split("\\|");
    for (int i = 1; i < parts.length; i++) {
      int separator = parts[i].indexOf('=');
      if (separator > 0) {
        sections.put(parts[i].substring(0, separator), parts[i].substring(separator + 1));
      }
    }

    java.util.Optional<List<Landmark>> normalized = parseLandmarkList(sections.get("n"), "normalized");
    if (normalized.isEmpty()) {
      return java.util.Optional.empty();
    }

    List<Landmark> worldLandmarks = parseLandmarkList(sections.get("w"), "world").orElse(List.of());
    String handedness = "";
    double handednessScore = 0.0;
    String handednessSection = sections.get("h");
    if (handednessSection != null && !handednessSection.isBlank()) {
      String[] handednessParts = handednessSection.split(",", 2);
      handedness = handednessParts[0];
      if (handednessParts.length == 2) {
        try {
          handednessScore = Double.parseDouble(handednessParts[1]);
        } catch (NumberFormatException exception) {
          handednessScore = 0.0;
        }
      }
    }

    return java.util.Optional.of(new HandFrame(
        normalized.get(),
        worldLandmarks,
        handedness,
        handednessScore,
        Instant.now()));
  }

  private java.util.Optional<List<Landmark>> parseLandmarkList(String encoded, String label) {
    if (encoded == null || encoded.isBlank()) {
      return java.util.Optional.empty();
    }

    String[] parts = encoded.split(";");
    if (parts.length != HandFrame.MEDIA_PIPE_HAND_LANDMARK_COUNT) {
      LOGGER.warning(() -> "Ignoring " + label + " landmark frame with " + parts.length + " points.");
      return java.util.Optional.empty();
    }

    List<Landmark> landmarks = new ArrayList<>(HandFrame.MEDIA_PIPE_HAND_LANDMARK_COUNT);
    for (String part : parts) {
      String[] coordinates = part.split(",");
      if (coordinates.length != 3) {
        LOGGER.warning(() -> "Ignoring malformed " + label + " landmark frame.");
        return java.util.Optional.empty();
      }
      try {
        landmarks.add(new Landmark(
            Double.parseDouble(coordinates[0]),
            Double.parseDouble(coordinates[1]),
            Double.parseDouble(coordinates[2])));
      } catch (NumberFormatException exception) {
        LOGGER.warning(() -> "Ignoring " + label + " landmark frame with non-numeric coordinates.");
        return java.util.Optional.empty();
      }
    }
    return java.util.Optional.of(landmarks);
  }

  private void closeServerSocket() {
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException ignored) {
      }
    }
  }
}
