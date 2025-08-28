# 📊 Test Automation Project Statistics Report

- **📊 Report Generated:** 2025-08-28 23:50:40
- **📁 Database:** healthcare-hyperexecute.db
- **🏗️ Project based:** Katalon Studio

## 📈 Executive Summary

- **Total Test Cases:** 4
- **Total Test Suites:** 3
- **Test Suite Coverage:** 3/4 (75.0%)
- **Total Test Executions:** 4

<details>
<summary><strong>📊 Metrics Explanation</strong></summary>

**Total Test Cases:** The complete count of individual test case files (.tc) in the project, representing all available test scenarios that can be executed.

**Total Test Suites:** The count of all test suite configurations including:
- Static Test Suites (explicitly defined test case lists)
- Dynamic Test Suites (filter-based test case selection)
- Test Suite Collections (orchestrated execution of multiple suites)

**Test Suite Coverage:** Shows how many test cases are included in at least one test suite. This indicates the percentage of test cases that are organized and ready for execution through test suites.

**Total Test Executions:** The total count of test case references across all test suites. A test case appearing in multiple test suites is counted multiple times, reflecting potential execution instances. This includes explicit links from static test suites and filter-based matches from dynamic test suites.

</details>

## 🔬 Automation Maturity Analysis

### Test Case Complexity
- **Parameterized Tests:** 2 (50.0%)
- **Data-Driven Tests:** 0 (0.0%)
- **Avg Description Length:** 168 chars

### Test Suite Sophistication
- **Dynamic Test Suite:** 1 (Avg Filter: 10 chars)
- **Test Suite:** 1
- **Test Suite Collection:** 1

## 📊 Test Coverage Distribution

### Coverage by Module

Coverage analysis organized by folder structure under Test Cases directory. This breakdown shows how test cases are distributed across different organizational folders and their respective test suite coverage rates. The depth level can be configured using the --module-depth parameter to show different levels of folder hierarchy.

<details>
<summary><strong>📊 Module Coverage Breakdown</strong> (2 modules)</summary>

Coverage statistics showing test cases present in test suites versus total available test cases per folder path:

| Module | Present in TS | Total | Coverage % |
|--------|---------|-------|------------|
| Test Cases/Main Test Cases | 4 | 4 | 100.0% |
| Test Cases/Common Test Cases | 0 | 1 | 0.0% |

</details>

## ⚠️ Priority & Risk Analysis

### Test Priority Distribution

- **Unclassified:** 5 tests (60.0% in suites)

### Test Type Distribution

- **Other:** 4 tests

### Test Tag Distribution

Tags provide valuable insights into test categorization, functionality coverage, and team organization patterns. This analysis shows the most frequently used tags across all test cases.

<details>
<summary><strong>📊 Top 20 Most Used Tags</strong></summary>

| Tag | Usage Count | Percentage |
|-----|-------------|------------|

</details>

## ⚡ Automation Efficiency Metrics

### Most Reused Test Cases

Test cases that appear in multiple test suites (including dynamic test suites) demonstrate good reusability or might need to review for optimization to avoid duplicated execution.

**Total Reused Test Cases:** 1

<details>
<summary><strong>📋 View All Reused Test Cases Details</strong> (1 test cases)</summary>

<details>
<summary><strong>🔄 TC1_Verify Successful Login</strong> (used in 2 suites)</summary>

**Test Case Path:** `Test Cases/Main Test Cases/TC1_Verify Successful Login.tc`

**Used in Test Suites:**

| Test Suite | Type | Path |
|------------|------|------|
| `Dynamic Test Suite` | Dynamic Test Suite | `Test Suites/Dynamic Test Suite.ts` |
| `healthcare-tests - TS_RegressionTest` | Test Suite | `Test Suites/healthcare-tests - TS_RegressionTest.ts` |

</details>

</details>


### Complete Test Suite Inventory

This comprehensive inventory provides detailed information about all test suites in the project, including their types, test case counts, and filtering criteria. Test suites are organized by their content status to help identify potential maintenance needs.

<details>
<summary><strong>✅ Active Test Suites</strong> (2 suites with test cases)</summary>

These test suites contain test cases and are actively used for test execution.

| Test Suite Name | Type | Test Cases | Path | Filter Criteria |
|-----------------|------|------------|------|------------------|
| **Dynamic Test Suite** | Dynamic Test Suite | 1 | `Test Suites/Dynamic Test Suite.ts` | `name=(TC1)` |
| **healthcare-tests - TS_RegressionTest** | Test Suite | 3 | `Test Suites/healthcare-tests - TS_RegressionTest.ts` | `N/A` |

</details>

### Complete Test Suite Collection Inventory

Test Suite Collections are orchestrated execution configurations that run multiple test suites with specific profiles and settings. This inventory provides comprehensive details about all collections, organized by their content status to help identify configuration issues and optimization opportunities.

<details>
<summary><strong>✅ Active Test Suite Collections</strong> (1 collections with test suites)</summary>

These collections contain test suite references and are configured for execution.

| Collection | Path | Total Suites | Exec Mode | Max Concurrent | Delay (sec) |
|------------|------|--------------|-----------|----------------|-------------|
| **healthcare-tests - TS_RegressionTestCollection** | `Test Suites/healthcare-tests - TS_RegressionTestCollection.ts` | 2 | SEQUENTIAL | 1 | 0 |

</details>

<details>
<summary><strong>📋 Detailed Collection Configurations</strong></summary>

<details>
<summary><strong>📦 healthcare-tests - TS_RegressionTestCollection</strong> (2 suites)</summary>

**Path:** `Test Suites/healthcare-tests - TS_RegressionTestCollection.ts`
**Execution Mode:** SEQUENTIAL
**Max Concurrent Instances:** 1
**Delay Between Instances:** 0 seconds

**Referenced Test Suites:**

| Test Suite | Enabled | Group | Profile | Browser/Config |
|------------|---------|-------|---------|----------------|
| `Test Suites/healthcare-tests - TS_RegressionTest` | ✅ Yes | Web Desktop | default | Chrome |
| `Test Suites/healthcare-tests - TS_RegressionTest` | ✅ Yes | Web Desktop | default | Firefox |

**Total Referenced Suites:** 2

</details>

</details>

## 📈 Trend Analysis

### Recent Test Case Activity

- **2025-08-25:** 4 test cases updated

## 💡 Strategic Recommendations


#### Uncovered Test Cases

Test cases that are not included in any test suite represent potential gaps in automation coverage. These cases may need to be organized into appropriate test suites or evaluated for relevance to current testing strategies.

<details>
<summary><strong>📋 Detailed Uncovered Test Cases</strong> (1 test cases)</summary>

The following test cases are not referenced by any test suite and may require attention:

| Test Case Name | Tags | Path |
|----------------|------|------|
| **Login** | No tags | `Test Cases/Common Test Cases/Login.tc` |

</details>

1. 🎯 1 test cases are not included in any test suite - consider creating focused suites
2. ⚠️  P1 test coverage is 0.0% - ensure critical tests are in smoke/regression suites

## 📋 Detailed Metrics Export

**Detailed metrics exported to:** `healthcare-hyperexecute_archived/healthcare-hyperexecute_metrics_20250828.json`

---

✅ **Automation Progress Report Completed**