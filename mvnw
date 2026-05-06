#!/usr/bin/env sh
set -eu

BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_VERSION="${MAVEN_VERSION:-3.9.14}"
MAVEN_HOME="$BASE_DIR/.mvn/cache/apache-maven-$MAVEN_VERSION"
MAVEN_ARCHIVE="$BASE_DIR/.mvn/cache/apache-maven-$MAVEN_VERSION-bin.tar.gz"
MAVEN_URL="${MAVEN_URL:-https://repo1.maven.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz}"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  mkdir -p "$BASE_DIR/.mvn/cache"
  if [ ! -f "$MAVEN_ARCHIVE" ]; then
    echo "Downloading Apache Maven $MAVEN_VERSION..."
    curl -fsSL "$MAVEN_URL" -o "$MAVEN_ARCHIVE"
  fi
  tar -xzf "$MAVEN_ARCHIVE" -C "$BASE_DIR/.mvn/cache"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
