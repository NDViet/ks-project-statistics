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
    private static final int DEFAULT_STEPS_PER_SPLIT = 350;

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
        System.out.println();
        System.out.println("Usage:");
        System.out.println("For Windows:        SplitTestCase.exe [--project-root=<path>] <test-case-id> [steps-per-split]");
        System.out.println("For Linux/MacOS:    SplitTestCase [--project-root=<path>] <test-case-id> [steps-per-split]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  test-case-id      Path to test case relative to project root (without .tc extension)");
        System.out.println("                    On Katalon Studio, you can right click on test case and select 'Copy ID'");
        System.out.println("                    Example: \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\"");
        System.out.println();
        System.out.println("  steps-per-split   Optional: Number of steps per split part (default: 350)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --project-root=<path>");
        System.out.println("                    Optional: Explicitly specify the project root directory");
        System.out.println("                    If not provided, uses the current working directory");
        System.out.println("                    Example: --project-root=/Users/username/my-katalon-project");
        System.out.println();
        System.out.println("Examples:");
        System.out.println();
        System.out.println("  Run from project root directory:");
        System.out.println("    cd \"C:\\path\\to\\katalon-project\"");
        System.out.println("    SplitTestCase.exe \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\"");
        System.out.println();
        System.out.println("  Run from any directory with explicit project root:");
        System.out.println("    SplitTestCase.exe --project-root=\"C:\\path\\to\\katalon-project\" \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\"");
        System.out.println();
        System.out.println("  With custom steps per split:");
        System.out.println("    SplitTestCase.exe \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\" 500");
        System.out.println("    SplitTestCase.exe \"Test Cases/AI-Generated/UAT/TC4-Complete Application Process\" 500 --project-root=\"C:\\path\\to\\project\"");
        System.out.println();
        System.out.println("Note:");
        System.out.println("  - The --project-root flag can be placed anywhere in the command (beginning, middle, or end)");
        System.out.println("  - On Linux/MacOS, omit the .exe extension (use SplitTestCase instead of SplitTestCase.exe)");
        System.out.println("  - On Linux/MacOS, execute `chmod +x SplitTestCase` if you get permission denied");
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
        Path scriptDir = findScriptDirectory(testCasePath, testCaseName);

        if (scriptDir == null || !Files.exists(scriptDir)) {
            throw new IOException("Script directory not found for test case: " + testCaseName);
        }

        Path scriptPath = findGroovyScript(scriptDir);
        if (scriptPath == null) {
            throw new IOException("Groovy script not found in: " + scriptDir);
        }

        System.out.println("Test case file: " + tcPath);
        System.out.println("Script file: " + scriptPath);
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

        // Read original test case content
        String tcContent = Files.readString(tcPath);

        // Read original script
        List<String> scriptLines = Files.readAllLines(scriptPath);

        // Calculate splits
        List<SplitInfo> splits = calculateSplits(steps);
        System.out.println("Creating " + splits.size() + " split test cases...");
        System.out.println();

        // Extract prefix (imports and setup code before first step)
        int prefixEnd = steps.get(0).lineStart;
        List<String> prefixLines = scriptLines.subList(0, prefixEnd);

        // Find where the suffix actually starts (after the last step's content)
        int suffixStart = findSuffixStart(scriptLines, steps.get(steps.size() - 1).lineStart);
        List<String> suffixLines = scriptLines.subList(suffixStart, scriptLines.size());

        // Adjust the last step's lineEnd to exclude the suffix
        if (!steps.isEmpty()) {
            steps.get(steps.size() - 1).lineEnd = suffixStart;
        }

        // Separate prefix and suffix into imports, setup/teardown, and other code
        PrefixParts prefixParts = separatePrefixParts(prefixLines);
        PrefixParts suffixParts = separatePrefixParts(suffixLines);

        // Merge setup/teardown from both prefix and suffix
        List<String> allSetupLines = new ArrayList<>(prefixParts.setupBlock);
        List<String> allTeardownLines = new ArrayList<>(prefixParts.teardownBlock);
        allTeardownLines.addAll(suffixParts.teardownBlock);

        PrefixParts mergedParts = new PrefixParts(prefixParts.importsAndOther, allSetupLines, allTeardownLines);

        // Get the directory where test case lives
        Path tcDir = tcPath.getParent();
        Path scriptParentDir = scriptDir.getParent();

        // Create split test cases (without setup/teardown)
        List<String> splitTestNames = new ArrayList<>();
        for (SplitInfo split : splits) {
            String splitName = createSplitTestCase(
                tcDir, scriptParentDir, testCaseName, split,
                tcContent, prefixParts.importsAndOther, scriptLines, steps
            );
            splitTestNames.add(splitName);
        }

        // Create Parent test case (with setup/teardown)
        createSplitCallTestCase(tcDir, scriptParentDir, testCaseName, splitTestNames,
                                testCasePath, mergedParts);

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("Split completed successfully!");
        System.out.println("Original test case preserved: " + testCaseName);
        System.out.println("Created " + splits.size() + " split test cases");
        System.out.println("Created 1 Parent test case");
        System.out.println("=".repeat(80));
    }

    private int findSuffixStart(List<String> scriptLines, int lastStepStart) {
        // Search backwards from the end of the file to find where suffix content starts
        // Suffix includes @TearDown, comments, or any other non-step code after the last step

        for (int i = scriptLines.size() - 1; i > lastStepStart; i--) {
            String line = scriptLines.get(i).trim();

            // Found @TearDown annotation - now search backwards for comments before it
            if (line.startsWith("@com.kms.katalon.core.annotation.TearDown") ||
                line.startsWith("@TearDown")) {
                // Look backwards to find any comments/empty lines before @TearDown
                int suffixStartLine = i;
                for (int j = i - 1; j > lastStepStart; j--) {
                    String prevLine = scriptLines.get(j).trim();
                    // Include empty lines or comments
                    if (prevLine.isEmpty() ||
                        prevLine.startsWith("'") ||
                        prevLine.startsWith("//") ||
                        prevLine.startsWith("/*")) {
                        suffixStartLine = j;
                    } else {
                        // Stop when we hit non-comment code
                        break;
                    }
                }
                return suffixStartLine;
            }

            // Found comment that looks like a step separator or termination marker
            if (line.startsWith("'Terminate") || line.startsWith("'Close") ||
                line.startsWith("//Terminate") || line.startsWith("//Close")) {
                return i;
            }
        }

        // If no suffix markers found, search forward from last step to find first non-step line
        for (int i = lastStepStart; i < scriptLines.size(); i++) {
            String line = scriptLines.get(i).trim();

            // Skip empty lines
            if (line.isEmpty()) {
                continue;
            }

            // If we find a step pattern, continue
            if (STEP_PATTERN.matcher(line).matches()) {
                continue;
            }

            // If we find WebUI calls or other step content, continue
            if (line.startsWith("WebUI.") || line.startsWith("Mobile.") ||
                line.contains("findTestObject") || line.contains("findTestData")) {
                continue;
            }

            // Found non-step content (likely start of suffix)
            if (line.startsWith("@") || line.startsWith("def ") ||
                line.startsWith("'Terminate") || line.startsWith("//")) {
                return i;
            }
        }

        // Default: suffix starts at end of file
        return scriptLines.size();
    }

    private PrefixParts separatePrefixParts(List<String> prefixLines) {
        List<String> importsAndOther = new ArrayList<>();
        List<String> setupBlock = new ArrayList<>();
        List<String> teardownBlock = new ArrayList<>();

        boolean inSetupBlock = false;
        boolean inTeardownBlock = false;
        int braceDepth = 0;
        List<String> pendingComments = new ArrayList<>();

        for (int i = 0; i < prefixLines.size(); i++) {
            String line = prefixLines.get(i);
            String trimmed = line.trim();

            // Check for @SetUp annotation
            if (trimmed.startsWith("@com.kms.katalon.core.annotation.SetUp") ||
                trimmed.startsWith("@SetUp")) {
                inSetupBlock = true;
                // Add any pending comments before the annotation
                setupBlock.addAll(pendingComments);
                pendingComments.clear();
                setupBlock.add(line);
                continue;
            }

            // Check for @TearDown annotation
            if (trimmed.startsWith("@com.kms.katalon.core.annotation.TearDown") ||
                trimmed.startsWith("@TearDown")) {
                inTeardownBlock = true;
                // Add any pending comments before the annotation
                teardownBlock.addAll(pendingComments);
                pendingComments.clear();
                teardownBlock.add(line);
                continue;
            }

            // Track brace depth for setup/teardown blocks
            if (inSetupBlock || inTeardownBlock) {
                if (trimmed.contains("{")) {
                    braceDepth += countChar(line, '{');
                }
                if (trimmed.contains("}")) {
                    braceDepth -= countChar(line, '}');
                }

                if (inSetupBlock) {
                    setupBlock.add(line);
                    if (braceDepth == 0 && trimmed.contains("}")) {
                        inSetupBlock = false;
                    }
                } else if (inTeardownBlock) {
                    teardownBlock.add(line);
                    if (braceDepth == 0 && trimmed.contains("}")) {
                        inTeardownBlock = false;
                    }
                }
            } else {
                // Check if this is a comment line that might precede @SetUp or @TearDown
                boolean isComment = trimmed.startsWith("'") || trimmed.startsWith("//") ||
                                   trimmed.startsWith("/*") || trimmed.startsWith("*");
                boolean isEmpty = trimmed.isEmpty();

                // Look ahead to see if next non-empty line is @SetUp or @TearDown
                boolean nextIsAnnotation = false;
                for (int j = i + 1; j < prefixLines.size(); j++) {
                    String nextTrimmed = prefixLines.get(j).trim();
                    if (nextTrimmed.isEmpty()) continue;
                    if (nextTrimmed.startsWith("@com.kms.katalon.core.annotation.SetUp") ||
                        nextTrimmed.startsWith("@SetUp") ||
                        nextTrimmed.startsWith("@com.kms.katalon.core.annotation.TearDown") ||
                        nextTrimmed.startsWith("@TearDown")) {
                        nextIsAnnotation = true;
                    }
                    break;
                }

                if ((isComment || isEmpty) && nextIsAnnotation) {
                    // This is a comment/empty line before an annotation - hold it
                    pendingComments.add(line);
                } else {
                    // Regular code - add pending comments to importsAndOther
                    importsAndOther.addAll(pendingComments);
                    pendingComments.clear();
                    importsAndOther.add(line);
                }
            }
        }

        // Add any remaining pending comments to importsAndOther
        importsAndOther.addAll(pendingComments);

        return new PrefixParts(importsAndOther, setupBlock, teardownBlock);
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

        // Set end line for each step
        for (int i = 0; i < steps.size() - 1; i++) {
            steps.get(i).lineEnd = steps.get(i + 1).lineStart;
        }
        if (!steps.isEmpty()) {
            steps.get(steps.size() - 1).lineEnd = lines.size();
        }

        return steps;
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

    private String createSplitTestCase(Path tcDir, Path scriptParentDir, String originalName,
                                      SplitInfo split, String tcContent, List<String> prefixLines,
                                      List<String> allScriptLines, List<StepInfo> allSteps) throws IOException {

        String splitName = originalName + "-" + split.suffix;
        System.out.println("Creating: " + splitName);

        // Build script content first to scan for variable usage
        List<String> scriptContent = new ArrayList<>(prefixLines);

        // Add steps for this split
        for (StepInfo step : allSteps) {
            if (step.number >= split.stepStart && step.number <= split.stepEnd) {
                scriptContent.addAll(allScriptLines.subList(step.lineStart, step.lineEnd));
            }
        }

        // Scan script for used variables
        Set<String> usedVariables = findUsedVariables(scriptContent);

        // Create .tc file
        Path newTcPath = tcDir.resolve(splitName + ".tc");

        // Modify XML content and filter variables
        String newTcContent = tcContent;
        newTcContent = newTcContent.replaceFirst(
            "(<name>)(.*?)(</name>)",
            "$1" + splitName + "$3"
        );
        newTcContent = newTcContent.replaceFirst(
            "(<testCaseGuid>)(.*?)(</testCaseGuid>)",
            "$1" + UUID.randomUUID().toString() + "$3"
        );

        // Update description
        newTcContent = newTcContent.replaceFirst(
            "(<description>)(.*?)(</description>)",
            "$1$2 (Split " + split.suffix + ", steps " + split.stepStart + "-" + split.stepEnd + ")$3"
        );

        // Filter variables to only include used ones
        newTcContent = filterVariables(newTcContent, usedVariables);

        Files.writeString(newTcPath, newTcContent);

        // Create script
        Path newScriptDir = scriptParentDir.resolve(splitName);
        Files.createDirectories(newScriptDir);

        Path newScriptPath = newScriptDir.resolve("Script" + System.currentTimeMillis() + ".groovy");

        // Write script content (already built above)
        Files.write(newScriptPath, scriptContent);

        System.out.println("  Created: " + newTcPath.getFileName());
        System.out.println("  Script:  " + newScriptPath);
        System.out.println("  Variables: " + usedVariables.size() + " used (filtered from original)");

        return splitName;
    }

    private Set<String> findUsedVariables(List<String> scriptLines) {
        Set<String> usedVariables = new HashSet<>();

        // Join all script lines to search
        String scriptContent = String.join("\n", scriptLines);

        // Pattern to match variable references in Groovy/Katalon scripts
        // Looks for: variable_name or ${variable_name} or GlobalVariable.variable_name
        Pattern variablePattern = Pattern.compile(
            "\\b([a-zA-Z_][a-zA-Z0-9_]*_[a-zA-Z0-9_]+)\\b|" +  // snake_case variables
            "\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}|" +              // ${variable}
            "GlobalVariable\\.([a-zA-Z_][a-zA-Z0-9_]*)"         // GlobalVariable.name
        );

        Matcher matcher = variablePattern.matcher(scriptContent);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String var = matcher.group(i);
                if (var != null && !var.isEmpty()) {
                    usedVariables.add(var);
                }
            }
        }

        return usedVariables;
    }

    private String filterVariables(String tcContent, Set<String> usedVariables) {
        // Parse XML and filter <variable> elements
        StringBuilder result = new StringBuilder();
        String[] lines = tcContent.split("\n", -1);  // -1 to preserve trailing empty strings

        boolean inVariableBlock = false;
        StringBuilder currentVariable = new StringBuilder();
        String variableName = null;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();

            // Check if entering a <variable> block
            if (trimmed.startsWith("<variable>")) {
                inVariableBlock = true;
                currentVariable = new StringBuilder();
                currentVariable.append(line);
                if (i < lines.length - 1) currentVariable.append("\n");
                variableName = null;
                continue;
            }

            if (inVariableBlock) {
                currentVariable.append(line);
                if (i < lines.length - 1) currentVariable.append("\n");

                // Extract variable name
                if (trimmed.startsWith("<name>") && variableName == null) {
                    variableName = trimmed.replaceAll("</?name>", "").trim();
                }

                // Check if closing </variable>
                if (trimmed.equals("</variable>")) {
                    inVariableBlock = false;

                    // Only include if variable is used
                    if (variableName != null && usedVariables.contains(variableName)) {
                        result.append(currentVariable);
                    }

                    currentVariable = new StringBuilder();
                    variableName = null;
                }
            } else {
                result.append(line);
                if (i < lines.length - 1) result.append("\n");
            }
        }

        return result.toString();
    }

    private void createSplitCallTestCase(Path tcDir, Path scriptParentDir, String originalName,
                                        List<String> splitNames, String originalTestCasePath,
                                        PrefixParts prefixParts) throws IOException {

        String splitCallName = originalName + "-Parent";
        System.out.println();
        System.out.println("Creating Parent: " + splitCallName);

        // Create .tc file
        Path tcPath = tcDir.resolve(splitCallName + ".tc");

        String tcContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<TestCaseEntity>\n" +
            "   <description>Calls all split test cases for " + originalName + " in sequence</description>\n" +
            "   <name>" + splitCallName + "</name>\n" +
            "   <tag></tag>\n" +
            "   <comment></comment>\n" +
            "   <recordOption>OTHER</recordOption>\n" +
            "   <testCaseGuid>" + UUID.randomUUID().toString() + "</testCaseGuid>\n" +
            "</TestCaseEntity>\n";

        Files.writeString(tcPath, tcContent);

        // Create script
        Path scriptDir = scriptParentDir.resolve(splitCallName);
        Files.createDirectories(scriptDir);

        Path scriptPath = scriptDir.resolve("Script" + System.currentTimeMillis() + ".groovy");

        // Build script with imports, setup, callTestCase statements, and teardown
        StringBuilder script = new StringBuilder();

        // Add imports
        script.append("import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint\n");
        script.append("import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase\n");
        script.append("import static com.kms.katalon.core.testdata.TestDataFactory.findTestData\n");
        script.append("import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject\n");
        script.append("import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject\n");
        script.append("import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint\n");
        script.append("import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW\n");
        script.append("import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile\n");
        script.append("import com.kms.katalon.core.model.FailureHandling as FailureHandling\n");
        script.append("import com.kms.katalon.core.testcase.TestCase as TestCase\n");
        script.append("import com.kms.katalon.core.testdata.TestData as TestData\n");
        script.append("import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW\n");
        script.append("import com.kms.katalon.core.testobject.TestObject as TestObject\n");
        script.append("import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS\n");
        script.append("import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI\n");
        script.append("import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows\n");
        script.append("import internal.GlobalVariable as GlobalVariable\n");
        script.append("import org.openqa.selenium.Keys as Keys\n");
        script.append("\n");

        // Add @SetUp block if present
        if (!prefixParts.setupBlock.isEmpty()) {
            for (String line : prefixParts.setupBlock) {
                script.append(line).append("\n");
            }
            script.append("\n");
        }

        // Add callTestCase for each split
        for (String splitName : splitNames) {
            String suffix = splitName.substring(splitName.lastIndexOf("-") + 1);
            String tcPathForCall = originalTestCasePath + "-" + suffix;
            script.append("WebUI.callTestCase(findTestCase('").append(tcPathForCall).append("'),\n");
            script.append("    [:], FailureHandling.STOP_ON_FAILURE)\n");
            script.append("\n");
        }

        // Add @TearDown block if present
        if (!prefixParts.teardownBlock.isEmpty()) {
            for (String line : prefixParts.teardownBlock) {
                script.append(line).append("\n");
            }
        }

        Files.writeString(scriptPath, script.toString());

        System.out.println("  Created: " + tcPath.getFileName());
        System.out.println("  Script:  " + scriptPath);
    }

    // Inner classes
    static class PrefixParts {
        List<String> importsAndOther;
        List<String> setupBlock;
        List<String> teardownBlock;

        PrefixParts(List<String> importsAndOther, List<String> setupBlock, List<String> teardownBlock) {
            this.importsAndOther = importsAndOther;
            this.setupBlock = setupBlock;
            this.teardownBlock = teardownBlock;
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
