# 📊 Test Automation Project Statistics Utility

A comprehensive analysis toolkit for Katalon Studio automation projects, designed to provide actionable insights for Lead Quality Engineers and project managers. This utility analyzes your Katalon Studio project structure and generates detailed reports on test automation coverage, organization, and quality metrics.

## 🎯 Motivation

As a **Lead Quality Engineer** or similar management role, you need visibility into your test automation project's health and progress. Recognizing this specific demand, and rely on **vibe coding** techniques to create a tailored script solution that addresses these exact needs. This utility provides essential metrics to answer critical questions:

- **How many test cases are automated?** - Track automation coverage and growth
- **Are automated tests properly organized?** - Identify tests added to test suites for execution
- **What's the inactive test script count?** - Find orphaned tests not belonging to any test suite
- **How are test tags being used?** - Analyze test classification and categorization
- **What's the test suite coverage?** - Understand execution scope and organization
- **How are test suite collections configured?** - Analyze orchestration and execution strategies
- **How is the project evolving?** - Capture status continuously for trend analysis

This toolkit is specifically designed for **Katalon Studio** automation projects and provides comprehensive technical reports with executive-level summaries, supporting all Katalon test suite types including Test Suites, Dynamic Test Suites, and Test Suite Collections.

## 🚀 Quick Start

### Prerequisites
- Python 3.7+
- Katalon Studio project structure
- No external dependencies required (uses Python standard library only)

### Installation
1. Clone or download this repository
2. No additional installation needed - all dependencies are part of Python standard library

### Basic Usage
Run the complete analysis pipeline with a single command:

```bash
# Default folder depth (2)
./bootstrap.sh /path/to/your/katalon/project

# Custom folder depth (3)
./bootstrap.sh /path/to/your/katalon/project 3
```

_Noted: `bootstrap.sh` shell script compatible with Unix-based systems (Linux, macOS). For Windows users, you can use the `bootstrap.bat` batch file instead._

This will:
1. Extract all test cases and metadata
2. Extract all test suites and relationships  
3. Generate comprehensive statistics report
4. Generate test case browser report with folder grouping

### Quick Demo with Sample Project

Try the utility immediately with a Katalon Studio sample project:

```bash
# Clone the sample healthcare project
git clone git@github.com:katalon-studio-samples/healthcare-hyperexecute.git

# Run analysis with default folder depth (2)
./bootstrap.sh healthcare-hyperexecute

# Or run with custom folder depth (3) for more detailed grouping
./bootstrap.sh healthcare-hyperexecute 3
```

**Expected Output Files:**
- `healthcare-hyperexecute.db` - SQLite database with extracted data
- `healthcare-hyperexecute.md` - Comprehensive automation progress report
- `healthcare-hyperexecute_list_test_cases.md` - Test case browser with folder grouping

**Windows Users:**
```cmd
git clone git@github.com:katalon-studio-samples/healthcare-hyperexecute.git
bootstrap.bat healthcare-hyperexecute
```

This demo will analyze the sample project's test structure and generate reports showing test automation coverage, organization, and detailed test case listings grouped by folder structure.

## 📋 What You Get

### Executive Summary Metrics

- **Total test cases** automated in the project
- **Test suite coverage** - cases included in at least one test suite
- **Total test executions** - count of test case references across all suites
- **Inactive test scripts** - orphaned cases not in any test suite
- **Test organization** by modules and directories
- **Tag usage analysis** for test classification and priority distribution
- **Coverage percentages** and automation maturity indicators

### Detailed Analysis Reports

- **Test Case Analysis**: Names, descriptions, tags, file paths, and metadata
- **Test Case Browser**: Organized test case listing grouped by folder structure with collapsible sections
- **Test Suite Analysis**: All suite types (Test Suites, Dynamic Test Suites, Test Suite Collections)
- **Test Suite Collection Inventory**: Orchestration configurations and execution settings
- **Coverage by Module**: Breakdown by project structure with configurable depth
- **Test Priority Distribution**: Analysis of P1/P2/P3 test classifications
- **Test Type Distribution**: UI, API, Smoke, Regression test categorization
- **Tag Distribution**: Most frequently used tags and classification patterns
- **Reused Test Cases**: Tests appearing in multiple suites for optimization analysis
- **Strategic Recommendations**: Data-driven suggestions for improvement

## 🛠️ Technical Architecture

### Core Components

#### 1. Test Case Extractor (`tc_extractor.py`)
- Parses `.tc` XML files from `Test Cases/` directory
- Extracts metadata: names, descriptions, tags, GUIDs
- Stores structured data in SQLite database

#### 2. Test Suite Extractor (`ts_extractor.py`)
- Parses `.ts` XML files from `Test Suites/` directory
- Handles all Katalon suite types: TestSuiteEntity, FilteringTestSuiteEntity, TestSuiteCollectionEntity
- Maps test case relationships, filtering criteria, and collection orchestration
- Creates proper test_suite_case_links for dynamic test suites

#### 3. Progress Report Generator (`automation_progress_report.py`)
- Generates comprehensive executive and detailed reports
- Supports markdown and console output formats
- Provides coverage analysis, automation maturity metrics, and strategic recommendations
- Features configurable priority tags, test type classifications, and module depth analysis
- Includes collapsible sections and detailed explanations for better readability

#### 4. Test Case Browser (`test_case_browser.py`)
- Generates organized test case listings grouped by folder structure
- Creates collapsible/expandable sections for each folder partition
- Displays test case names, descriptions, and tags in clean tables
- Supports configurable folder depth levels for flexible organization
- Auto-generates filename based on database name (`{database}_list_test_cases.md`)

### Database Schema

The utility creates a SQLite database with structured tables:

- **test_cases**: Core test case information, metadata, tags, and file paths
- **test_suites**: All suite types with configurations, filtering criteria, and execution settings
- **test_suite_case_links**: Relationships between test cases and suites (including dynamic filtering)
- **test_suite_collection_links**: Test Suite Collection orchestration and execution configurations
- **tags**: Tag definitions and classifications
- **test_case_tags**: Many-to-many relationships between test cases and tags
- **suite_tags**: Tag associations for test suites
- **test_suite_variables**: Variable definitions and configurations

## 📊 Usage Examples

### Generate Standard Report
```bash
# Analyze specific project with default folder depth (2)
./bootstrap.sh /path/to/katalon/project

# Analyze with custom folder depth (3)
./bootstrap.sh /path/to/katalon/project 3

# Generate report from existing database
python3 scripts/automation_progress_report.py project.db
```

### Generate Reports with Different Options
```bash
# Generate markdown report (default)
python3 scripts/automation_progress_report.py project.db

# Generate console report
python3 scripts/automation_progress_report.py project.db --format console

# Generate report with custom module depth
python3 scripts/automation_progress_report.py project.db --module-depth 3

# Scan all databases in current directory
python3 scripts/automation_progress_report.py --scan-all
```

### Generate Test Case Browser Report
```bash
# Generate test case browser with default depth (2)
python3 scripts/test_case_browser.py project.db

# Generate with custom folder depth (3)
python3 scripts/test_case_browser.py project.db --module-depth 3

# Generate console output instead of markdown
python3 scripts/test_case_browser.py project.db --output console

# Save to custom filename
python3 scripts/test_case_browser.py project.db --save-to custom_report.md
```

### Extract Data Only
```bash
# Extract test cases only
python3 scripts/tc_extractor.py /path/to/katalon/project

# Extract test suites only (includes all types: Test Suites, Dynamic Test Suites, Test Suite Collections)
python3 scripts/ts_extractor.py /path/to/katalon/project
```

## 📈 Continuous Monitoring

### Tracking Project Evolution

Run the analysis regularly to track:

- **Growth trends** in test automation coverage
- **Quality improvements** in test organization
- **Tag usage evolution** and classification maturity
- **Test suite optimization** and execution efficiency

### Integration Options

- **CI/CD Integration**: Add to build pipelines for automated reporting
- **Scheduled Analysis**: Set up regular runs for trend tracking
- **Dashboard Integration**: Export data for visualization tools
- **Team Reporting**: Generate regular reports for stakeholders

## 🎯 Management Benefits

### For Lead Quality Engineers

- **Project Health Visibility**: Clear metrics on automation maturity
- **Resource Planning**: Identify areas needing attention or improvement
- **Quality Assurance**: Ensure tests are properly organized for execution
- **Trend Analysis**: Track progress over time and identify patterns

### For Project Managers

- **Executive Summaries**: High-level metrics without technical details
- **Progress Tracking**: Quantifiable automation coverage and growth
- **Risk Identification**: Spot orphaned tests and organizational issues
- **ROI Demonstration**: Show automation investment value and impact

## 🔧 Configuration

### Output Formats

- **Markdown**: Structured reports with collapsible sections for documentation
- **Console**: Quick terminal-based summaries for CI/CD integration

### Customization Options

- **Folder Depth**: Configure folder hierarchy depth for both coverage analysis and test case browser (`--module-depth` or bootstrap second argument)
- **Priority Tags**: Customize P1/P2/P3 priority classifications in the script configuration
- **Test Type Tags**: Configure UI/API/Smoke/Regression test type classifications
- **Tag Analysis**: Customize top tags limit and grouping patterns
- **Report Sections**: All sections included with detailed explanations and collapsible content
- **Output Formats**: Choose between markdown (with collapsible sections) and console output

## 📚 Advanced Features

### Comprehensive Test Suite Support

The utility supports all Katalon Studio test suite types:

- **Test Suites**: Static test case collections with explicit test case links
- **Dynamic Test Suites**: Filter-based test case selection using tag and name criteria
- **Test Suite Collections**: Orchestration configurations for running multiple test suites with specific profiles and execution settings

### Test Case Browser Features

- **Folder-based Organization**: Groups test cases by configurable folder depth levels
- **Collapsible Sections**: Each folder becomes an expandable section in markdown output
- **Clean Table Format**: Simplified 3-column layout (Test Case, Description, Tags)
- **Description Cleaning**: Automatically removes step details to focus on main descriptions
- **Auto-filename Generation**: Creates files named `{database}_list_test_cases.md`

### Intelligent Analysis Features

- **Dynamic Test Suite Processing**: Automatically creates test_suite_case_links for filtering test suites
- **Test Reusability Analysis**: Identifies test cases used across multiple suites
- **Priority and Type Classification**: Configurable tag-based categorization (P1/P2/P3, UI/API/Smoke/Regression)
- **Strategic Recommendations**: Data-driven suggestions based on automation maturity metrics
- **Collapsible Report Sections**: Organized markdown output with detailed explanations
- **Flexible Folder Grouping**: Configurable depth levels for both analysis and browsing (1-4+ levels)
- **Bootstrap Integration**: Single command execution with customizable folder depth parameter

### Database Integration

- **SQLite Storage**: Lightweight, portable database format with comprehensive schema
- **Query Interface**: Direct SQL access for custom analysis using provided schema
- **Structured Data**: Normalized tables for test cases, suites, collections, tags, and relationships
- **Historical Tracking**: Compare snapshots over time by maintaining separate database files

## 🤝 Contributing

This utility is designed to be extensible and customizable for different Katalon Studio project structures and reporting needs.

## 📄 License

This project is designed for Katalon Studio automation projects based.

---

**Built for Lead Quality Engineers who need actionable insights into their test automation projects.**
