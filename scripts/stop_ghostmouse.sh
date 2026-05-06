#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
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
    log "No running $label found."
    return
  fi

  log "Stopping $label..."
  while IFS= read -r pid; do
    if [[ -n "$pid" && "$pid" != "$$" ]]; then
      kill "$pid" 2>/dev/null || true
    fi
  done <<< "$pids"
}

cd "$PROJECT_DIR"
touch "$LAUNCHER_LOG"

kill_matches "java -jar .*ghostmouse-0.1.0-SNAPSHOT.jar --source=socket" "GhostMouse Java app"
kill_matches "dist/GhostMouse.app/Contents/MacOS/GhostMouse" "GhostMouse app"
kill_matches "GhostMouseCameraSidecar.app/Contents/MacOS/GhostMouseCameraSidecar" "camera sidecar"

log "GhostMouse stopped."
