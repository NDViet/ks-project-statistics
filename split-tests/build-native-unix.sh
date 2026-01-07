#!/usr/bin/env sh
set -eu

# Build a native binary on macOS or Ubuntu using GraalVM.
# Output: ./SplitTestCase-linux or ./SplitTestCase-macos

APP_NAME="SplitTestCase"
MAIN_CLASS="SplitTestCase"
ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
BUILD_DIR="$ROOT_DIR/build"
BINARY_DIR="$ROOT_DIR/binary"
JAR_PATH="$BUILD_DIR/$APP_NAME.jar"

# Detect OS and set binary suffix
OS_TYPE="$(uname -s)"
case "$OS_TYPE" in
  Linux*)
    BINARY_SUFFIX="-linux"
    OS_NAME="Linux"
    ;;
  Darwin*)
    BINARY_SUFFIX="-macos"
    OS_NAME="macOS"
    ;;
  *)
    echo "ERROR: Unsupported OS: $OS_TYPE" >&2
    exit 1
    ;;
esac

BINARY_NAME="$APP_NAME$BINARY_SUFFIX"
echo "Building for $OS_NAME..."
echo ""

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
native-image --no-fallback -jar "$JAR_PATH" -H:Name="$BINARY_NAME"

# Sign the binary on macOS
if [ "$OS_NAME" = "macOS" ]; then
  echo ""
  echo "Signing macOS binary..."

  # Check if a Developer ID certificate is available
  if security find-identity -v -p codesigning | grep -q "Developer ID Application"; then
    echo "Found Developer ID certificate, signing with it..."
    codesign --force --sign "Developer ID Application" --timestamp --options runtime "$ROOT_DIR/$BINARY_NAME"
  else
    echo "No Developer ID certificate found, using ad-hoc signature..."
    echo "Note: Users may need to allow the binary in System Settings > Privacy & Security"
    codesign --force --sign - "$ROOT_DIR/$BINARY_NAME"
  fi

  # Verify signature
  codesign --verify --verbose "$ROOT_DIR/$BINARY_NAME"
  echo "Signature verification: OK"
fi

echo ""
echo "Done. Native binary:"
mkdir -p "$BINARY_DIR"
mv "$ROOT_DIR/$BINARY_NAME" "$BINARY_DIR/$BINARY_NAME"
echo "  $BINARY_DIR/$BINARY_NAME"

if [ "$OS_NAME" = "macOS" ]; then
  echo ""
  echo "Note: This binary is signed but not notarized."
  echo "Users downloading it may need to allow it in System Settings > Privacy & Security."
  echo "Or run: xattr -d com.apple.quarantine $BINARY_DIR/$BINARY_NAME"
fi