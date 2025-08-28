# 📊 Test Automation Project Statistics Report

- **📊 Report Generated:** 2025-08-28 23:51:34
- **📁 Database:** rest-assured-demo.db
- **🏗️ Project based:** Katalon Studio

## 📈 Executive Summary

- **Total Test Cases:** 5
- **Total Test Suites:** 2
- **Test Suite Coverage:** 3/5 (60.0%)
- **Total Test Executions:** 3

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
- **Parameterized Tests:** 2 (40.0%)
- **Data-Driven Tests:** 0 (0.0%)
- **Avg Description Length:** 0 chars

### Test Suite Sophistication
- **Test Suite:** 2

## 📊 Test Coverage Distribution

### Coverage by Module

Coverage analysis organized by folder structure under Test Cases directory. This breakdown shows how test cases are distributed across different organizational folders and their respective test suite coverage rates. The depth level can be configured using the --module-depth parameter to show different levels of folder hierarchy.

<details>
<summary><strong>📊 Module Coverage Breakdown</strong> (1 modules)</summary>

Coverage statistics showing test cases present in test suites versus total available test cases per folder path:

| Module | Present in TS | Total | Coverage % |
|--------|---------|-------|------------|
| Other | 3 | 5 | 60.0% |

</details>

## ⚠️ Priority & Risk Analysis

### Test Priority Distribution

- **Unclassified:** 5 tests (60.0% in suites)

### Test Type Distribution

- **Other:** 5 tests

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

No test cases are reused across multiple suites.

### Complete Test Suite Inventory

This comprehensive inventory provides detailed information about all test suites in the project, including their types, test case counts, and filtering criteria. Test suites are organized by their content status to help identify potential maintenance needs.

<details>
<summary><strong>✅ Active Test Suites</strong> (2 suites with test cases)</summary>

These test suites contain test cases and are actively used for test execution.

| Test Suite Name | Type | Test Cases | Path | Filter Criteria |
|-----------------|------|------------|------|------------------|
| **REST Assured Suite** | Test Suite | 1 | `Test Suites/REST Assured Suite.ts` | `N/A` |
| **web-service-tests - All Test Cases** | Test Suite | 2 | `Test Suites/web-service-tests - All Test Cases.ts` | `N/A` |

</details>

### Complete Test Suite Collection Inventory

*No Test Suite Collections found in the database.*

## 📈 Trend Analysis

### Recent Test Case Activity

- **2025-08-25:** 5 test cases updated

## 💡 Strategic Recommendations


#### Uncovered Test Cases

Test cases that are not included in any test suite represent potential gaps in automation coverage. These cases may need to be organized into appropriate test suites or evaluated for relevance to current testing strategies.

<details>
<summary><strong>📋 Detailed Uncovered Test Cases</strong> (2 test cases)</summary>

The following test cases are not referenced by any test suite and may require attention:

| Test Case Name | Tags | Path |
|----------------|------|------|
| **Find User - Basic** | No tags | `Test Cases/Find User - Basic.tc` |
| **REST Assured - Basic** | No tags | `Test Cases/REST Assured - Basic.tc` |

</details>

1. 🎯 2 test cases are not included in any test suite - consider creating focused suites
2. ⚠️  P1 test coverage is 0.0% - ensure critical tests are in smoke/regression suites
3. 🔄 Consider more Dynamic Test Suites for better maintenance (0.0% currently)

## 📋 Detailed Metrics Export

**Detailed metrics exported to:** `rest-assured-demo_archived/rest-assured-demo_metrics_20250828.json`

---

✅ **Automation Progress Report Completed**