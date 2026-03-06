import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Katalon Test Case Splitter - Pure Java Implementation
 *
 * Runs with Java 11+ without requiring compilation:
 *   java SplitTestCase.java "Test Cases/AI-Generated/UAT/TC4-Complete Application..."
 *
 * Or compile and run:
 *   javac SplitTestCase.java
 *   java SplitTestCase "Test Cases/AI-Generated/UAT/TC4-Complete Application..."
 */
public class SplitTestCase {

    private static final Pattern STEP_PATTERN = Pattern.compile("^\\s*\"Step\\s+(\\d+):\\s*(.+)\"\\s*$");
    private static final int DEFAULT_STEPS_PER_SPLIT = 250;

    private final Path projectRoot;
    private final int stepsPerSplit;

    public SplitTestCase(Path projectRoot, int stepsPerSplit) {
        this.projectRoot = projectRoot;
        this.stepsPerSplit = stepsPerSplit;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }

        // Parse --project-root flag and filter it out from args
        Path projectRoot = null;
        List<String> positionalArgs = new ArrayList<>();

        for (String arg : args) {
            if (arg.startsWith("--project-root=")) {
                String rootPath = arg.substring("--project-root=".length());
                projectRoot = Paths.get(rootPath);
            } else if (arg.equals("--project-root")) {
                System.err.println("ERROR: --project-root requires a value. Use --project-root=/path/to/project");
                System.exit(1);
            } else {
                positionalArgs.add(arg);
            }
        }

        // Use current directory if --project-root not specified
        if (projectRoot == null) {
            projectRoot = Paths.get(System.getProperty("user.dir"));
        }

        // Validate project root exists
        if (!Files.exists(projectRoot)) {
            System.err.println("ERROR: Project root does not exist: " + projectRoot);
            System.exit(1);
        }

        // Parse positional arguments
        if (positionalArgs.isEmpty()) {
            System.err.println("ERROR: Missing required argument: test-case-id");
            printUsage();
            System.exit(1);
        }

        String testCasePath = positionalArgs.get(0);
        int stepsPerSplit = positionalArgs.size() > 1 ? Integer.parseInt(positionalArgs.get(1)) : DEFAULT_STEPS_PER_SPLIT;

        System.out.println("Project root: " + projectRoot);
        System.out.println("Steps per split: " + stepsPerSplit);
        System.out.println();

        try {
            SplitTestCase splitter = new SplitTestCase(projectRoot, stepsPerSplit);
            splitter.splitTestCase(testCasePath);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printUsage() {
        // Detect OS and determine binary name
        String osName = System.getProperty("os.name").toLowerCase();
        String binaryName;
        String osLabel;

        if (osName.contains("win")) {
            binaryName = "SplitTestCase.exe";
            osLabel = "Windows";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            binaryName = "SplitTestCase-macos";
            osLabel = "macOS";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            binaryName = "SplitTestCase-linux";
            osLabel = "Linux";
        } else {
            binaryName = "SplitTestCase";
            osLabel = "Unix";
        }

        System.out.println();
        System.out.println("Usage:");
        System.out.println("  " + binaryName + " [--project-root=<path>] <test-case-id> [steps-per-split]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  test-case-id      Path to test case relative to project root (without .tc extension)");
        System.out.println("                    On Katalon Studio, you can right click on test case and select 'Copy ID'");
        System.out.println("                    Example: \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\"");
        System.out.println();
        System.out.println("  steps-per-split   Optional: Number of steps per split part (default: " + DEFAULT_STEPS_PER_SPLIT + ")");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --project-root=<path>");
        System.out.println("                    Optional: Explicitly specify the project root directory");
        System.out.println("                    If not provided, uses the current working directory");
        System.out.println("                    Example: --project-root=C:\\Users\\username\\my-katalon-project");
        System.out.println();
        System.out.println("Examples:");
        System.out.println();
        System.out.println("  Run from project root directory:");
        System.out.println("    cd \"C:\\path\\to\\katalon-project\"");
        System.out.println("    " + binaryName + " \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\"");
        System.out.println();
        System.out.println("  Run from any directory with explicit project root:");
        System.out.println("    " + binaryName + " --project-root=\"C:\\path\\to\\katalon-project\" \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\"");
        System.out.println();
        System.out.println("  With custom steps per split:");
        System.out.println("    " + binaryName + " \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\" 500");
        System.out.println("    " + binaryName + " \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\" 500 --project-root=\"C:\\path\\to\\project\"");

        System.out.println();
        System.out.println("Note:");
        System.out.println("  - The --project-root flag can be placed anywhere in the command (beginning, middle, or end)");

        if (osName.contains("mac") || osName.contains("darwin")) {
            System.out.println();
            System.out.println("macOS users:");
            System.out.println("  - If blocked by Gatekeeper security, run: xattr -d com.apple.quarantine " + binaryName);
            System.out.println("  - Make executable if needed: chmod +x " + binaryName);
        } else if (!osName.contains("win")) {
            System.out.println();
            System.out.println("Linux users:");
            System.out.println("  - Make executable if needed: chmod +x " + binaryName);
        }

        System.out.println();
    }

    public void splitTestCase(String testCasePath) throws IOException {
        System.out.println("=".repeat(80));
        System.out.println("Splitting test case: " + testCasePath);
        System.out.println("=".repeat(80));

        // Resolve paths
        Path tcPath = projectRoot.resolve(testCasePath + ".tc");
        if (!Files.exists(tcPath)) {
            throw new IOException("Test case file not found: " + tcPath);
        }

        // Find the script
        String testCaseName = tcPath.getFileName().toString().replace(".tc", "");
        String updatedTestCaseName = testCaseName + "-Updated";
        Path scriptDir = findScriptDirectory(testCasePath, testCaseName);

        if (scriptDir == null || !Files.exists(scriptDir)) {
            throw new IOException("Script directory not found for test case: " + testCaseName);
        }

        Path scriptPath = findGroovyScript(scriptDir);
        if (scriptPath == null) {
            throw new IOException("Groovy script not found in: " + scriptDir);
        }

        // Compute paths for the updated (output) test case
        Path updatedTcPath = tcPath.getParent().resolve(updatedTestCaseName + ".tc");
        Path updatedScriptDir = scriptDir.getParent().resolve(updatedTestCaseName);
        Path updatedScriptPath = updatedScriptDir.resolve(scriptPath.getFileName());

        System.out.println("Original test case file: " + tcPath);
        System.out.println("Original script file:    " + scriptPath);
        System.out.println("Updated test case file:  " + updatedTcPath);
        System.out.println("Updated script file:     " + updatedScriptPath);
        System.out.println();

        // Extract steps
        List<StepInfo> steps = extractSteps(scriptPath);
        if (steps.isEmpty()) {
            throw new IOException("No steps found in script");
        }

        System.out.println("Total steps found: " + steps.size());
        System.out.println("Steps per split: " + stepsPerSplit);
        System.out.println();

        // Check if splitting is necessary
        if (steps.size() <= stepsPerSplit) {
            System.out.println("=" .repeat(80));
            System.out.println("Splitting not required!");
            System.out.println("Test case has " + steps.size() + " steps, which is within the limit of " + stepsPerSplit + " steps per split.");
            System.out.println("No split test cases will be created.");
            System.out.println("=" .repeat(80));
            return;
        }

        // Create updated script directory and copy .tc file (with updated name inside)
        Files.createDirectories(updatedScriptDir);

        String tcContent = new String(Files.readAllBytes(tcPath));
        tcContent = tcContent.replace("<name>" + testCaseName + "</name>",
                                      "<name>" + updatedTestCaseName + "</name>");
        Files.write(updatedTcPath, tcContent.getBytes());
        System.out.println("Copied .tc file: " + updatedTcPath);

        // Copy groovy script to updated location (will be overwritten by rewrite)
        Files.copy(scriptPath, updatedScriptPath, StandardCopyOption.REPLACE_EXISTING);

        // Read original script
        List<String> scriptLines = Files.readAllLines(scriptPath);

        // Calculate splits
        List<SplitInfo> splits = calculateSplits(steps);
        System.out.println("Splitting into " + splits.size() + " closure parts...");
        System.out.println();

        // NEW APPROACH: Find "Initialize test session" marker
        int testBodyStart = findTestBodyStart(scriptLines);

        // Extract static prefix (imports, declarations before test body)
        List<String> staticPrefix = scriptLines.subList(0, testBodyStart);

        // Extract @SetUp and @TearDown blocks
        List<BlockInfo> setupBlocks = extractAnnotatedBlocks(scriptLines, "@SetUp");
        List<BlockInfo> teardownBlocks = extractAnnotatedBlocks(scriptLines, "@TearDown");

        // Extract pre-step executable code (between test body start and first step, excluding @SetUp/@TearDown)
        int firstStepLine = steps.isEmpty() ? scriptLines.size() : steps.get(0).lineStart;
        List<String> preStepCode = extractPreStepCode(scriptLines, testBodyStart, setupBlocks, teardownBlocks, firstStepLine);

        // Extract @SetUp/@TearDown block content for Parent
        List<String> setupLines = new ArrayList<>();
        for (BlockInfo block : setupBlocks) {
            setupLines.addAll(scriptLines.subList(block.start, block.end));
        }

        List<String> teardownLines = new ArrayList<>();
        for (BlockInfo block : teardownBlocks) {
            teardownLines.addAll(scriptLines.subList(block.start, block.end));
        }

        // Detect 'Initialize test session' marker line content
        String initMarkerLine = null;
        if (testBodyStart < scriptLines.size()) {
            String trimmed = scriptLines.get(testBodyStart).trim();
            if (trimmed.startsWith("'Initialize test session") || trimmed.startsWith("\"Initialize test session")) {
                initMarkerLine = scriptLines.get(testBodyStart);
            }
        }

        // Detect 'Terminate test session' marker line content
        String terminateMarkerLine = null;
        int testBodyEnd = steps.isEmpty() ? scriptLines.size() : steps.get(steps.size() - 1).lineEnd;
        if (testBodyEnd < scriptLines.size()) {
            String trimmed = scriptLines.get(testBodyEnd).trim();
            if (trimmed.startsWith("'Terminate test session") || trimmed.startsWith("\"Terminate test session")) {
                terminateMarkerLine = scriptLines.get(testBodyEnd);
            }
        }

        // Rewrite script with closure-based approach into the updated copy
        rewriteScriptWithClosures(updatedScriptPath, staticPrefix, initMarkerLine, preStepCode,
                                  setupLines, teardownLines, splits, steps, scriptLines,
                                  terminateMarkerLine);

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("Split completed successfully!");
        System.out.println("Original test case preserved: " + tcPath);
        System.out.println("Updated test case created:    " + updatedTcPath);
        System.out.println("Script rewritten with " + splits.size() + " closure parts");
        System.out.println("=".repeat(80));
    }

    private int countChar(String str, char c) {
        int count = 0;
        for (char ch : str.toCharArray()) {
            if (ch == c) count++;
        }
        return count;
    }

    private Path findScriptDirectory(String testCasePath, String testCaseName) {
        // Convert Test Cases path to Scripts path
        String scriptsPath = testCasePath.replace("Test Cases/", "Scripts/").replace("Test Cases\\", "Scripts\\");
        Path scriptDir = projectRoot.resolve(scriptsPath + "/" + testCaseName);

        if (Files.exists(scriptDir)) {
            return scriptDir;
        }

        // Try without appending test case name (path already includes it)
        scriptDir = projectRoot.resolve(scriptsPath);
        if (Files.exists(scriptDir)) {
            return scriptDir;
        }

        // Try alternative location
        scriptDir = projectRoot.resolve("Scripts/" + testCaseName);
        return Files.exists(scriptDir) ? scriptDir : null;
    }

    private Path findGroovyScript(Path scriptDir) throws IOException {
        try (Stream<Path> paths = Files.list(scriptDir)) {
            return paths.filter(p -> p.toString().endsWith(".groovy"))
                       .findFirst()
                       .orElse(null);
        }
    }

    private List<StepInfo> extractSteps(Path scriptPath) throws IOException {
        List<StepInfo> steps = new ArrayList<>();
        List<String> lines = Files.readAllLines(scriptPath);

        for (int i = 0; i < lines.size(); i++) {
            Matcher matcher = STEP_PATTERN.matcher(lines.get(i));
            if (matcher.matches()) {
                int stepNum = Integer.parseInt(matcher.group(1));
                String desc = matcher.group(2).trim();
                steps.add(new StepInfo(stepNum, desc, i));
            }
        }

        // Find where test body ends (before "Terminate test session" or @TearDown)
        int testBodyEnd = findTestBodyEnd(lines);

        // Set end line for each step
        for (int i = 0; i < steps.size() - 1; i++) {
            steps.get(i).lineEnd = steps.get(i + 1).lineStart;
        }
        if (!steps.isEmpty()) {
            // Last step ends at the test body end, not at end of file
            steps.get(steps.size() - 1).lineEnd = testBodyEnd;
        }

        return steps;
    }

    private int findTestBodyEnd(List<String> scriptLines) {
        // Look for "Terminate test session" marker or @TearDown annotation
        for (int i = 0; i < scriptLines.size(); i++) {
            String trimmed = scriptLines.get(i).trim();

            // Found "Terminate test session" marker
            if (trimmed.startsWith("'Terminate test session") ||
                trimmed.startsWith("\"Terminate test session")) {
                return i;
            }

            // Found @TearDown annotation
            if (trimmed.startsWith("@com.kms.katalon.core.annotation.TearDown") ||
                trimmed.startsWith("@TearDown")) {
                // Look backwards to include any comments before @TearDown
                int endLine = i;
                for (int j = i - 1; j >= 0; j--) {
                    String prevTrimmed = scriptLines.get(j).trim();
                    if (prevTrimmed.isEmpty() ||
                        prevTrimmed.startsWith("'") ||
                        prevTrimmed.startsWith("//") ||
                        prevTrimmed.startsWith("/*")) {
                        endLine = j;
                    } else {
                        break;
                    }
                }
                return endLine;
            }
        }

        // If no marker found, return end of file
        return scriptLines.size();
    }

    private List<SplitInfo> calculateSplits(List<StepInfo> steps) {
        List<SplitInfo> splits = new ArrayList<>();
        int currentStep = steps.get(0).number;
        int maxStep = steps.get(steps.size() - 1).number;
        int splitIndex = 1;

        while (currentStep <= maxStep) {
            int stepEnd = Math.min(currentStep + stepsPerSplit - 1, maxStep);
            splits.add(new SplitInfo(splitIndex, "P" + splitIndex, currentStep, stepEnd));
            currentStep = stepEnd + 1;
            splitIndex++;
        }

        return splits;
    }

    private void rewriteScriptWithClosures(Path scriptPath, List<String> staticPrefix,
                                           String initMarkerLine, List<String> preStepCode,
                                           List<String> setupLines, List<String> teardownLines,
                                           List<SplitInfo> splits, List<StepInfo> steps,
                                           List<String> allScriptLines,
                                           String terminateMarkerLine) throws IOException {
        List<String> newScript = new ArrayList<>();

        // 1. Static prefix (imports, declarations) - strip trailing blank lines
        int prefixEnd = staticPrefix.size();
        while (prefixEnd > 0 && staticPrefix.get(prefixEnd - 1).trim().isEmpty()) {
            prefixEnd--;
        }
        newScript.addAll(staticPrefix.subList(0, prefixEnd));

        // 2. 'Initialize test session' marker
        if (initMarkerLine != null) {
            newScript.add("");
            newScript.add(initMarkerLine);
        }

        // 3. @SetUp blocks
        if (!setupLines.isEmpty()) {
            newScript.add("");
            newScript.addAll(setupLines);
        }

        // 4. Pre-step code
        if (!preStepCode.isEmpty()) {
            newScript.add("");
            newScript.addAll(preStepCode);
        }

        // 5. Define a closure for each split except the last
        List<String> closureNames = new ArrayList<>();
        for (int i = 0; i < splits.size() - 1; i++) {
            SplitInfo split = splits.get(i);
            String closureName = "part" + split.index;
            closureNames.add(closureName);

            newScript.add("");
            newScript.add("def " + closureName + " = {");
            for (StepInfo step : steps) {
                if (step.number >= split.stepStart && step.number <= split.stepEnd) {
                    for (String line : allScriptLines.subList(step.lineStart, step.lineEnd)) {
                        newScript.add(line.isEmpty() ? "" : "\t" + line);
                    }
                }
            }
            newScript.add("}");
        }

        // 6. Call each closure in sequence
        newScript.add("");
        for (String closureName : closureNames) {
            newScript.add(closureName + "()");
            newScript.add("");
        }

        // 7. Last split steps written directly in the main script body
        SplitInfo lastSplit = splits.get(splits.size() - 1);
        for (StepInfo step : steps) {
            if (step.number >= lastSplit.stepStart && step.number <= lastSplit.stepEnd) {
                newScript.addAll(allScriptLines.subList(step.lineStart, step.lineEnd));
            }
        }

        // 8. 'Terminate test session' marker
        if (terminateMarkerLine != null) {
            newScript.add("");
            newScript.add(terminateMarkerLine);
        }

        // 9. @TearDown blocks
        if (!teardownLines.isEmpty()) {
            newScript.add("");
            newScript.addAll(teardownLines);
        }

        Files.write(scriptPath, newScript);
        System.out.println("  Rewritten: " + scriptPath);
    }

    private int findTestBodyStart(List<String> scriptLines) {
        // Look for "Initialize test session" marker (with single or double quotes)
        for (int i = 0; i < scriptLines.size(); i++) {
            String trimmed = scriptLines.get(i).trim();
            if (trimmed.startsWith("'Initialize test session") ||
                trimmed.startsWith("\"Initialize test session")) {
                return i;
            }
        }
        // If no marker found, default to line 0 (entire file is test body)
        return 0;
    }

    private List<BlockInfo> extractAnnotatedBlocks(List<String> scriptLines, String annotation) {
        List<BlockInfo> blocks = new ArrayList<>();
        int i = 0;

        while (i < scriptLines.size()) {
            String trimmed = scriptLines.get(i).trim();

            // Found @SetUp or @TearDown annotation
            if (trimmed.startsWith(annotation) ||
                trimmed.startsWith("@com.kms.katalon.core.annotation." + annotation.substring(1))) {

                int blockStart = i;
                i++; // Move to next line (should be def method)

                // Find the end of the block by tracking braces
                int braceDepth = 0;
                while (i < scriptLines.size()) {
                    String line = scriptLines.get(i);
                    for (char c : line.toCharArray()) {
                        if (c == '{') braceDepth++;
                        if (c == '}') braceDepth--;
                    }
                    i++;
                    if (braceDepth == 0 && line.trim().endsWith("}")) {
                        break;
                    }
                }

                blocks.add(new BlockInfo(blockStart, i));
            } else {
                i++;
            }
        }

        return blocks;
    }

    private List<String> extractPreStepCode(List<String> scriptLines, int testBodyStart,
                                            List<BlockInfo> setupBlocks, List<BlockInfo> teardownBlocks,
                                            int firstStepLine) {
        List<String> preStepCode = new ArrayList<>();

        // Collect all @SetUp/@TearDown line ranges
        Set<Integer> excludedLines = new HashSet<>();
        for (BlockInfo block : setupBlocks) {
            for (int i = block.start; i < block.end; i++) {
                excludedLines.add(i);
            }
        }
        for (BlockInfo block : teardownBlocks) {
            for (int i = block.start; i < block.end; i++) {
                excludedLines.add(i);
            }
        }

        // Extract lines between testBodyStart and firstStepLine, excluding @SetUp/@TearDown
        for (int i = testBodyStart; i < firstStepLine; i++) {
            if (!excludedLines.contains(i)) {
                String line = scriptLines.get(i);
                String trimmed = line.trim();
                // Skip the marker line itself and empty lines
                if (!trimmed.startsWith("'Initialize test session") &&
                    !trimmed.startsWith("\"Initialize test session") &&
                    !trimmed.isEmpty()) {
                    preStepCode.add(line);
                }
            }
        }

        return preStepCode;
    }

    // Inner classes
    static class BlockInfo {
        int start;
        int end;

        BlockInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    static class StepInfo {
        int number;
        String description;
        int lineStart;
        int lineEnd;

        StepInfo(int number, String description, int lineStart) {
            this.number = number;
            this.description = description;
            this.lineStart = lineStart;
        }
    }

    static class SplitInfo {
        int index;
        String suffix;
        int stepStart;
        int stepEnd;

        SplitInfo(int index, String suffix, int stepStart, int stepEnd) {
            this.index = index;
            this.suffix = suffix;
            this.stepStart = stepStart;
            this.stepEnd = stepEnd;
        }
    }
}
