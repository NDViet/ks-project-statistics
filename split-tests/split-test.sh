#!/bin/bash

# Katalon Test Case Splitter - Pure Java Version (Unix/Mac)
# Usage: ./split-test-java.sh "Test Cases/path/to/TestCase" [steps-per-split]

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if java is available
if ! command -v java &> /dev/null; then
    echo -e "${RED}ERROR: Java not found!${NC}"
    echo "Please install Java 11 or higher."
    echo ""
    echo "Check: java -version"
    exit 1
fi

# Check Java version (need 11+)
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ] 2>/dev/null; then
    echo -e "${YELLOW}WARNING: Java 11+ recommended (detected: Java $JAVA_VERSION)${NC}"
    echo "The script may not work with older Java versions."
    echo ""
fi

# Check if script file exists
SCRIPT_PATH="$(dirname "$0")/SplitTestCase.java"
if [ ! -f "$SCRIPT_PATH" ]; then
    echo -e "${RED}ERROR: SplitTestCase.java not found!${NC}"
    echo "Expected location: $SCRIPT_PATH"
    exit 1
fi

# Show usage if no arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 <test-case-path> [steps-per-split]"
    echo ""
    echo "Example:"
    echo "  $0 \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\""
    echo "  $0 \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\" 500"
    exit 1
fi

# Run the Java script
echo ""
echo -e "${GREEN}Running Pure Java splitter...${NC}"
java "$SCRIPT_PATH" "$@"
EXIT_CODE=$?

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ Test case split completed successfully!${NC}"
else
    echo -e "${RED}✗ Test case split failed with exit code: $EXIT_CODE${NC}"
fi

exit $EXIT_CODE
