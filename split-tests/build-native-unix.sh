#!/usr/bin/env sh
set -eu

# Build a native binary on macOS or Ubuntu using GraalVM.
# Output: ./SplitTestCase (or ./SplitTestCase.exe on Windows)

APP_NAME="SplitTestCase"
MAIN_CLASS="SplitTestCase"
ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
BUILD_DIR="$ROOT_DIR/build"
BINARY_DIR="$ROOT_DIR/binary"
JAR_PATH="$BUILD_DIR/$APP_NAME.jar"

command -v native-image >/dev/null 2>&1 || {
  echo "ERROR: native-image not found. Install GraalVM and run: gu install native-image" >&2
  exit 1
}

command -v javac >/dev/null 2>&1 || {
  echo "ERROR: javac not found. Install a JDK (GraalVM recommended)." >&2
  exit 1
}

mkdir -p "$BUILD_DIR"

echo "Compiling..."
javac -d "$BUILD_DIR" "$ROOT_DIR/$MAIN_CLASS.java"

echo "Creating JAR..."
jar --create --file "$JAR_PATH" --main-class "$MAIN_CLASS" -C "$BUILD_DIR" .

echo "Building native binary..."
native-image --no-fallback -jar "$JAR_PATH" -H:Name="$APP_NAME"

echo ""
echo "Done. Native binary:"
mkdir -p "$BINARY_DIR"
mv "$ROOT_DIR/$APP_NAME" "$BINARY_DIR/$APP_NAME"
echo "  $BINARY_DIR/$APP_NAME"