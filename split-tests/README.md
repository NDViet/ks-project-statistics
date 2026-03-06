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

Download the latest release from [GitHub Releases](/../../releases):

- **Linux**: `SplitTestCase-linux`
- **macOS**: `SplitTestCase-macos`
- **Windows**: `SplitTestCase.exe`
- **All platforms**: `SplitTestCase-all-platforms.zip` (contains all three binaries)

### Linux Setup

After downloading, make the binary executable:

```bash
chmod +x SplitTestCase-linux
```

Optionally, rename and move it to your PATH:

```bash
# Rename for convenience
mv SplitTestCase-linux SplitTestCase

# Move to PATH
mv SplitTestCase ~/bin/
# or
sudo mv SplitTestCase /usr/local/bin/
```

### macOS Setup

After downloading, make the binary executable:

```bash
chmod +x SplitTestCase-macos
```

**Important: macOS Security Warning**

Since the binary is not notarized with Apple, macOS Gatekeeper may block it. If you see "cannot be opened because it is from an unidentified developer":

**Option 1: Remove quarantine flag (recommended)**
```bash
xattr -d com.apple.quarantine SplitTestCase-macos
```

**Option 2: Allow in System Settings**
1. Try to run the binary
2. Open System Settings → Privacy & Security
3. Scroll down and click "Open Anyway" next to the security warning
4. Run the binary again and confirm

After allowing, you can optionally rename and move it to your PATH:

```bash
# Rename for convenience
mv SplitTestCase-macos SplitTestCase

# Move to PATH
mv SplitTestCase ~/bin/
# or
sudo mv SplitTestCase /usr/local/bin/
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

# Linux (assuming you renamed the binary to SplitTestCase)
cd /path/to/katalon-project
SplitTestCase "Test Cases/AI-Generated/UAT/TC4-Complete Application Process"

# macOS (assuming you renamed the binary to SplitTestCase)
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
# The tool automatically shows OS-specific instructions
SplitTestCase.exe        # Windows
SplitTestCase-linux      # Linux
SplitTestCase-macos      # macOS
```

The usage message automatically adapts to your operating system, showing the correct binary name and path format.

## Command-Line Options

The tool automatically detects your operating system and displays appropriate instructions. Run without arguments to see OS-specific help.

### Syntax

```
[binary-name] [--project-root=<path>] <test-case-id> [steps-per-split]
```

Where `[binary-name]` is:
- `SplitTestCase.exe` on Windows
- `SplitTestCase-linux` on Linux
- `SplitTestCase-macos` on macOS

### Arguments

**test-case-id** (required)
- Path to test case relative to project root (without .tc extension)
- In Katalon Studio: Right-click test case → "Copy ID"
- Example: `"Test Cases/AI-Generated/UAT/TC4-Complete Application Process"`

**steps-per-split** (optional)
- Number of steps per split part
- Default: 300
- Example: `500`

### Options

**--project-root=<path>** (optional)
- Explicitly specify the project root directory
- If not provided, uses the current working directory
- Can be placed anywhere in the command
- Example: `--project-root=/path/to/project`

## How It Works

Given a test case with 1000 steps and a split size of 300 (the default):

1. **Analyzes the test case**
   - Locates the `.tc` file and corresponding Groovy script
   - Extracts all step definitions (e.g., `"Step 1: Navigate to login page"`)
   - Identifies setup/teardown blocks and imports

2. **Creates split test cases** (without setup/teardown)
   - `OriginalTest-P1` - Steps 1-300
   - `OriginalTest-P2` - Steps 301-600
   - `OriginalTest-P3` - Steps 601-900
   - `OriginalTest-P4` - Steps 901-1000

3. **Creates Parent test case** (with setup/teardown)
   - `OriginalTest-Parent` - Calls P1, P2, P3, P4 in sequence
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
Steps per split: 300

Creating 4 split test cases...

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

Creating: TC4-Complete Application Process-P4
  Created: TC4-Complete Application Process-P4.tc
  Script:  /path/to/Scripts/TC4-Complete Application Process-P4/Script123460.groovy
  Variables: 28 used (filtered from original)

Creating Parent: TC4-Complete Application Process-Parent
  Created: TC4-Complete Application Process-Parent.tc
  Script:  /path/to/Scripts/TC4-Complete Application Process-Parent/Script123461.groovy

================================================================================
Split completed successfully!
Original test case preserved: TC4-Complete Application Process
Created 4 split test cases
Created 1 Parent test case
================================================================================
```

## No Split Required

If the test case is already small enough:

```
Total steps found: 253
Steps per split: 300

================================================================================
Splitting not required!
Test case has 253 steps, which is within the limit of 300 steps per split.
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

#### Linux

```bash
cd split-tests
chmod +x build-native-unix.sh
./build-native-unix.sh
```

Output: `binary/SplitTestCase-linux`

#### macOS

```bash
cd split-tests
chmod +x build-native-unix.sh
./build-native-unix.sh
```

Output: `binary/SplitTestCase-macos`

**Note:** The build script automatically detects your OS (Linux or macOS) and names the binary accordingly. A binary built on Linux will not run on macOS and vice versa.

#### Code Signing on macOS

The build script automatically signs the macOS binary:

**Ad-hoc signing (default):**
- Used when no Developer ID certificate is available
- Binary is signed but not trusted by default
- Users will need to allow it via System Settings or remove quarantine flag

**Developer ID signing (recommended for distribution):**
- If you have an Apple Developer account and certificate installed
- The script automatically detects and uses "Developer ID Application" certificate
- Provides better trust but still requires notarization for full Gatekeeper approval

**Full notarization (best for public distribution):**
To fully remove security warnings, notarize the binary after building:

```bash
# After building, submit for notarization (requires Apple Developer account)
xcrun notarytool submit binary/SplitTestCase-macos \
  --apple-id your@email.com \
  --team-id YOUR_TEAM_ID \
  --password your-app-specific-password \
  --wait

# Staple the notarization ticket
xcrun stapler staple binary/SplitTestCase-macos
```

### Run from Source (Without Compilation)

Java 11+ supports running `.java` files directly:

```bash
java SplitTestCase.java "Test Cases/MyTest"
```

## Testing with the Dummy Script

A sample Groovy script (`dummy_script_test.groovy`) is included for testing. It contains 710 steps and mirrors a real Katalon test case. The mock Katalon project structure is already set up under `test-project/`.

### Run

```bash
cd split-tests
./run-test.sh
```

### View Output

The rewritten script is at:

```
split-tests/test-project/Scripts/dummy_script_test/dummy_script_test.groovy
```

### Expected Output

```
================================================================================
Splitting test case: Test Cases/dummy_script_test
================================================================================
Test case file: /tmp/ks-test-project/Test Cases/dummy_script_test.tc
Script file: /tmp/ks-test-project/Scripts/dummy_script_test/dummy_script_test.groovy

Total steps found: 710
Steps per split: 300

Splitting into 3 closure parts...

  Rewritten: /tmp/ks-test-project/Scripts/dummy_script_test/dummy_script_test.groovy

================================================================================
Split completed successfully!
Script rewritten with 3 closure parts
================================================================================
```

The rewritten script groups steps 1–300 and 301–600 into closures (`part1`, `part2`), calls them inline, then writes steps 601–710 directly in the main body:

```groovy
def part1 = {
    "Step 1: ..."
    // ...
}
def part2 = {
    "Step 301: ..."
    // ...
}

part1
part2

"Step 601: ..."
// ... remaining steps written directly
```

## CI/CD Integration

### GitHub Actions

This project includes automated workflows:

#### Build Workflow

Triggers on push/PR to master:
- Builds separate binaries for Linux, macOS, and Windows
- Creates a combined .zip archive with all binaries
- Uploads artifacts (30-day retention)
- Verifies binaries work correctly

Available artifacts:
- `SplitTestCase-linux` - Linux binary
- `SplitTestCase-macos` - macOS binary
- `SplitTestCase.exe` - Windows binary
- `SplitTestCase-all-platforms.zip` - All binaries in one archive

#### Release Workflow

Triggers on version tag push:
- Builds binaries for all platforms (Linux, macOS, Windows)
- Creates GitHub Release with auto-generated release notes
- Attaches all binaries as release assets:
  - `SplitTestCase-linux`
  - `SplitTestCase-macos`
  - `SplitTestCase.exe`
  - `SplitTestCase-all-platforms.zip`

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

- Make the file executable: `chmod +x SplitTestCase-linux` or `chmod +x SplitTestCase-macos`
- Verify you're using the correct binary for your platform
- Check file permissions and ownership

### macOS: "cannot be opened because it is from an unidentified developer"

The binary is signed but not notarized with Apple. Fix this by removing the quarantine flag:

```bash
xattr -d com.apple.quarantine SplitTestCase-macos
```

Or allow it through System Settings → Privacy & Security → "Open Anyway"

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
