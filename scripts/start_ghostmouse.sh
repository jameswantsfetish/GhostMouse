#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR="$PROJECT_DIR/target/ghostmouse-0.1.0-SNAPSHOT.jar"
SIDECAR_APP="$PROJECT_DIR/GhostMouseCameraSidecar.app"
JAVA_LOG="$PROJECT_DIR/ghostmouse-java.log"
LAUNCHER_LOG="$PROJECT_DIR/ghostmouse-launcher.log"

timestamp() {
  date "+%Y-%m-%d %H:%M:%S"
}

log() {
  local message="$1"
  printf "[%s] %s\n" "$(timestamp)" "$message" | tee -a "$LAUNCHER_LOG"
}

kill_matches() {
  local pattern="$1"
  local label="$2"
  local pids
  pids="$(pgrep -f "$pattern" || true)"
  if [[ -z "$pids" ]]; then
    return
  fi

  log "Stopping existing $label..."
  while IFS= read -r pid; do
    if [[ -n "$pid" && "$pid" != "$$" ]]; then
      kill "$pid" 2>/dev/null || true
    fi
  done <<< "$pids"
}

wait_for_socket() {
  local attempts=30
  for _ in $(seq 1 "$attempts"); do
    if nc -z 127.0.0.1 8765 >/dev/null 2>&1; then
      return 0
    fi
    sleep 0.25
  done
  return 1
}

cd "$PROJECT_DIR"
touch "$JAVA_LOG" "$LAUNCHER_LOG"

log "Starting GhostMouse from $PROJECT_DIR"

if [[ ! -f "$JAR" ]] || find pom.xml src -type f -newer "$JAR" | grep -q .; then
  log "Java app package is missing or stale. Building it now..."
  ./mvnw -q -DskipTests package
fi

if [[ ! -d "$SIDECAR_APP" ]]; then
  log "Missing GhostMouseCameraSidecar.app."
  exit 1
fi

kill_matches "java -jar .*ghostmouse-0.1.0-SNAPSHOT.jar --source=socket" "GhostMouse Java app"
kill_matches "dist/GhostMouse.app/Contents/MacOS/GhostMouse" "GhostMouse app"
kill_matches "GhostMouseCameraSidecar.app/Contents/MacOS/GhostMouseCameraSidecar" "camera sidecar"
sleep 0.5

log "Launching GhostMouse Java app..."
java -jar "$JAR" --source=socket --enable-robot >> "$JAVA_LOG" 2>&1 &
JAVA_PID="$!"

if wait_for_socket; then
  log "GhostMouse socket is ready on 127.0.0.1:8765."
else
  log "GhostMouse socket did not open yet. Check $JAVA_LOG if the app does not appear."
fi

log "Launching camera sidecar..."
open -n "$SIDECAR_APP"

log "GhostMouse startup complete."
log "Keep this Terminal window open while using GhostMouse."
log "Launcher log: $LAUNCHER_LOG"

wait "$JAVA_PID"
