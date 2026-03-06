#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cp "$SCRIPT_DIR/dummy_script_test.groovy" "$SCRIPT_DIR/test-project/Scripts/dummy_script_test/dummy_script_test.groovy"
java "$SCRIPT_DIR/SplitTestCase.java" --project-root="$SCRIPT_DIR/test-project" "Test Cases/dummy_script_test"
