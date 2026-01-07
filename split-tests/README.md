# SplitTestCase

A native command-line tool for splitting large Katalon Studio test cases into smaller, manageable parts.

## Overview

When Katalon Studio test cases grow too large (hundreds or thousands of steps), they become difficult to maintain and execute. `SplitTestCase` automatically splits large test cases into multiple smaller test cases, each containing a specified number of steps.

### Features

- **Automatic splitting** - Splits test cases by a configurable number of steps (default: 350)
- **Setup/Teardown preservation** - Automatically extracts and preserves `@SetUp` and `@TearDown` blocks
- **Variable filtering** - Only includes variables that are actually used in each split
- **Parent test case generation** - Creates a Parent test case that calls all splits in sequence
- **Cross-platform** - Native binaries for Windows, Linux, and macOS
- **No dependencies** - Standalone executables, no JVM required at runtime

## Installation

### Download Pre-built Binaries

Download the latest release from [GitHub Releases](/../../../releases):

- **Windows**: `SplitTestCase.exe`
- **Linux/macOS**: `SplitTestCase`
- **All platforms**: `SplitTestCase-all-platforms.zip`

### Linux/macOS Setup

After downloading, make the binary executable:

```bash
chmod +x SplitTestCase
```

Optionally, move it to your PATH:

```bash
# Example: Move to ~/bin or /usr/local/bin
mv SplitTestCase ~/bin/
```

### Windows Setup

Simply download `SplitTestCase.exe` and place it in a directory of your choice. You can add it to your PATH for easier access.

## Usage

### Basic Usage

Run from your Katalon project root directory:

```bash
# Windows
cd C:\path\to\katalon-project
SplitTestCase.exe "Test Cases/AI-Generated/UAT/TC4-Complete Application Process"

# Linux/macOS
cd /path/to/katalon-project
SplitTestCase "Test Cases/AI-Generated/UAT/TC4-Complete Application Process"
```

### With Custom Split Size

Specify a custom number of steps per split:

```bash
SplitTestCase.exe "Test Cases/MyLargeTest" 500
```

### From Any Directory

Use the `--project-root` flag to specify the project directory:

```bash
SplitTestCase.exe --project-root="C:\path\to\katalon-project" "Test Cases/MyTest"

# Flag can be placed anywhere
SplitTestCase.exe "Test Cases/MyTest" 500 --project-root="C:\path\to\project"
```

### Getting Help

Display usage information:

```bash
SplitTestCase.exe
```

## Command-Line Options

```
Usage:
  SplitTestCase.exe [--project-root=<path>] <test-case-id> [steps-per-split]

Arguments:
  test-case-id      Path to test case relative to project root (without .tc extension)
                    In Katalon Studio: Right-click test case → "Copy ID"
                    Example: "Test Cases/AI-Generated/UAT/TC4-Complete Application Process"

  steps-per-split   Optional: Number of steps per split part (default: 350)

Options:
  --project-root=<path>
                    Optional: Explicitly specify the project root directory
                    If not provided, uses the current working directory
```

## How It Works

Given a test case with 1000 steps and a split size of 350:

1. **Analyzes the test case**
   - Locates the `.tc` file and corresponding Groovy script
   - Extracts all step definitions (e.g., `"Step 1: Navigate to login page"`)
   - Identifies setup/teardown blocks and imports

2. **Creates split test cases** (without setup/teardown)
   - `OriginalTest-P1` - Steps 1-350
   - `OriginalTest-P2` - Steps 351-700
   - `OriginalTest-P3` - Steps 701-1000

3. **Creates Parent test case** (with setup/teardown)
   - `OriginalTest-Parent` - Calls P1, P2, P3 in sequence
   - Contains all `@SetUp` and `@TearDown` methods
   - Ensures proper initialization and cleanup

4. **Filters variables**
   - Each split only includes variables it actually uses
   - Reduces memory footprint and improves performance

5. **Preserves original**
   - Original test case remains unchanged
   - Can be safely archived or deleted after verification

## Output Example

```
================================================================================
Splitting test case: Test Cases/AI-Generated/UAT/TC4-Complete Application Process
================================================================================
Test case file: /path/to/Test Cases/AI-Generated/UAT/TC4-Complete Application Process.tc
Script file: /path/to/Scripts/AI-Generated/UAT/TC4-Complete Application Process/Script123456.groovy

Total steps found: 1000
Steps per split: 350

Creating 3 split test cases...

Creating: TC4-Complete Application Process-P1
  Created: TC4-Complete Application Process-P1.tc
  Script:  /path/to/Scripts/TC4-Complete Application Process-P1/Script123457.groovy
  Variables: 45 used (filtered from original)

Creating: TC4-Complete Application Process-P2
  Created: TC4-Complete Application Process-P2.tc
  Script:  /path/to/Scripts/TC4-Complete Application Process-P2/Script123458.groovy
  Variables: 38 used (filtered from original)

Creating: TC4-Complete Application Process-P3
  Created: TC4-Complete Application Process-P3.tc
  Script:  /path/to/Scripts/TC4-Complete Application Process-P3/Script123459.groovy
  Variables: 42 used (filtered from original)

Creating Parent: TC4-Complete Application Process-Parent
  Created: TC4-Complete Application Process-Parent.tc
  Script:  /path/to/Scripts/TC4-Complete Application Process-Parent/Script123460.groovy

================================================================================
Split completed successfully!
Original test case preserved: TC4-Complete Application Process
Created 3 split test cases
Created 1 Parent test case
================================================================================
```

## No Split Required

If the test case is already small enough:

```
Total steps found: 253
Steps per split: 350

================================================================================
Splitting not required!
Test case has 253 steps, which is within the limit of 350 steps per split.
No split test cases will be created.
================================================================================
```

## Building from Source

### Prerequisites

- **Java 11+** (for compilation)
- **GraalVM** (for native image compilation)

### Install GraalVM

1. Download [GraalVM](https://www.graalvm.org/downloads/)
2. Install native-image:
   ```bash
   gu install native-image
   ```

### Build Native Binaries

#### Windows

```cmd
cd split-tests
build-native-windows.bat
```

Output: `binary/SplitTestCase.exe`

#### Linux/macOS

```bash
cd split-tests
chmod +x build-native-unix.sh
./build-native-unix.sh
```

Output: `binary/SplitTestCase`

### Run from Source (Without Compilation)

Java 11+ supports running `.java` files directly:

```bash
java SplitTestCase.java "Test Cases/MyTest"
```

## CI/CD Integration

### GitHub Actions

This project includes automated workflows:

#### Build Workflow

Triggers on push/PR to master:
- Builds binaries for all platforms
- Uploads artifacts (30-day retention)
- Verifies binaries work correctly

#### Release Workflow

Triggers on version tag push:
- Builds binaries for all platforms
- Creates GitHub Release
- Attaches binaries as release assets

**Create a release:**

```bash
git tag v1.0.0
git push origin v1.0.0
```

## Project Structure

```
split-tests/
├── SplitTestCase.java          # Main source code
├── build-native-unix.sh        # Build script for Linux/macOS
├── build-native-windows.bat    # Build script for Windows
├── build/                      # Compilation output (created during build)
├── binary/                     # Native binaries (created during build)
└── README.md                   # This file
```

## Troubleshooting

### "Test case file not found"

- Ensure you're running from the Katalon project root, or use `--project-root`
- Verify the test case ID is correct (right-click → "Copy ID" in Katalon Studio)
- Don't include the `.tc` extension

### "Script directory not found"

- The tool looks for the Groovy script in the `Scripts/` directory
- Ensure your test case has an associated script file
- Check that the directory structure matches Katalon conventions

### "No steps found in script"

- Verify your script contains step comments in the format: `"Step N: description"`
- Steps must be numbered sequentially
- Check that the script file is not empty

### Binary not executing (Linux/macOS)

- Make the file executable: `chmod +x SplitTestCase`
- Verify you're using the correct binary for your platform
- Check file permissions and ownership

### "Permission denied" (Windows)

- Run as Administrator if necessary
- Check antivirus/security software isn't blocking execution
- Ensure the file is not marked as blocked (Properties → Unblock)

## License

[Apache License 2.0](../LICENSE)

## Contributing

Issues and pull requests are welcome at the [GitHub repository](../..).

## Related Tools

- [Katalon Studio](https://www.katalon.com/) - Test automation platform
- [GraalVM](https://www.graalvm.org/) - High-performance JDK with native image compilation
