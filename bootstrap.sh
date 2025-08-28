#!/bin/bash

# Automation Test Project Analysis Bootstrap Script
# This script runs the complete analysis pipeline for a Katalon Studio project
# Usage: ./bootstrap.sh <project_path>

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to print usage
usage() {
    echo "Usage: $0 <project_path> [folder_depth]"
    echo ""
    echo "Arguments:"
    echo "  project_path    Path to the Katalon Studio project directory"
    echo "  folder_depth    Optional folder depth level for grouping (default: 2)"
    echo ""
    echo "Examples:"
    echo "  $0 ../project_path"
    echo "  $0 /path/to/katalon/project 3"
    echo "  $0 ../project_path 2"
}

# Check if argument is provided
if [ $# -eq 0 ]; then
    print_error "No project path provided"
    usage
    exit 1
fi

PROJECT_PATH="$1"
FOLDER_DEPTH="${2:-2}"  # Default to 2 if not provided

# Validate folder depth is a number
if ! [[ "$FOLDER_DEPTH" =~ ^[0-9]+$ ]]; then
    print_error "Folder depth must be a positive integer: $FOLDER_DEPTH"
    usage
    exit 1
fi

# Check if project path exists
if [ ! -d "$PROJECT_PATH" ]; then
    print_error "Project path does not exist: $PROJECT_PATH"
    exit 1
fi

# Get absolute path
PROJECT_PATH=$(realpath "$PROJECT_PATH")
PROJECT_NAME=$(basename "$PROJECT_PATH")

print_status "Starting Automation Test Project Analysis Pipeline"
print_status "Project: $PROJECT_NAME"
print_status "Path: $PROJECT_PATH"
print_status "Folder Depth: $FOLDER_DEPTH"
echo "=================================================="

# Get script directory (where this bootstrap script is located)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPTS_PATH="$SCRIPT_DIR/scripts"

# Check if scripts directory exists
if [ ! -d "$SCRIPTS_PATH" ]; then
    print_error "Scripts directory not found: $SCRIPTS_PATH"
    exit 1
fi

# Step 1: Extract Test Cases
print_status "Step 1/4: Extracting Test Cases (.tc files)"
if python3 "$SCRIPTS_PATH/tc_extractor.py" "$PROJECT_PATH"; then
    print_success "Test Cases extraction completed"
else
    print_error "Test Cases extraction failed"
    exit 1
fi

echo ""

# Step 2: Extract Test Suites
print_status "Step 2/4: Extracting Test Suites (.ts files)"
if python3 "$SCRIPTS_PATH/ts_extractor.py" "$PROJECT_PATH"; then
    print_success "Test Suites extraction completed"
else
    print_error "Test Suites extraction failed"
    exit 1
fi

echo ""

# Step 3: Generate Automation Progress Report
print_status "Step 3/4: Generating Automation Progress Report"
if python3 "$SCRIPTS_PATH/automation_progress_report.py" "${PROJECT_NAME}.db" --module-depth "$FOLDER_DEPTH"; then
    print_success "Automation Progress Report generated"
else
    print_error "Automation Progress Report generation failed"
    exit 1
fi

echo ""

# Step 4: Generate Test Case Browser Report
print_status "Step 4/4: Generating Test Case Browser Report"
if python3 "$SCRIPTS_PATH/test_case_browser.py" "${PROJECT_NAME}.db" --module-depth "$FOLDER_DEPTH"; then
    print_success "Test Case Browser Report generated"
else
    print_error "Test Case Browser Report generation failed"
    exit 1
fi

echo ""
echo "=================================================="
print_success "Katalon Studio Project Analysis Pipeline completed successfully!"
print_status "Database created: ${PROJECT_NAME}.db"
print_status "Reports generated: ${PROJECT_NAME}.md, ${PROJECT_NAME}_list_test_cases.md"
print_status "Check the generated reports and database for analysis results"
