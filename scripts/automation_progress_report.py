#!/usr/bin/env python3
"""
Katalon Studio Project Statistics
==================================
Comprehensive reporting script for Lead Quality Engineers to analyze test automation progress,
coverage, quality metrics, and strategic insights from Katalon test data.

Usage: python3 automation_progress_report.py [database_path]
"""

import sqlite3
import argparse
import json
import re
import os
import shutil
from datetime import datetime, timedelta
from pathlib import Path
import sys

# Global configuration for Test Priority Distribution
# Users can modify these priority tags to match their project's priority system
PRIORITY_TAGS = [
    {'tag': 'p1', 'label': 'P1 (Critical)', 'order': 1},
    {'tag': 'p2', 'label': 'P2 (High)', 'order': 2},
    {'tag': 'p3', 'label': 'P3 (Medium)', 'order': 3}
]

# Global configuration for Test Type Distribution
# Users can modify these test type tags to match their project's test categorization
TEST_TYPE_TAGS = [
    {'tag': 'ui', 'label': 'UI Tests'},
    {'tag': 'api', 'label': 'API Tests'},
    {'tag': 'smoke', 'label': 'Smoke Tests'},
    {'tag': 'regression', 'label': 'Regression Tests'},
    {'tag': 'integration', 'label': 'Integration Tests'}
]

# Global configuration for Test Tag Distribution
# Users can modify this limit to control how many top tags are displayed
TOP_TAGS_LIMIT = 20


class AutomationProgressReporter:
    """Generate comprehensive automation progress reports for Lead Quality Engineers."""
    
    def __init__(self, db_path, output_format='console', module_depth=2):
        """Initialize reporter with database connection.
        
        Args:
            db_path: Path to the SQLite database
            output_format: 'console' or 'markdown'
            module_depth: Number of directory levels deep from 'Test Cases/' to extract module names
                         Default 3: Test Cases/Platform/Admin/ -> 'Admin'
                         Depth 2: Test Cases/Platform/ -> 'Platform'
                         Depth 4: Test Cases/Platform/Admin/API/ -> 'API'
        """
        self.db_path = Path(db_path)
        if not self.db_path.exists():
            raise FileNotFoundError(f"Database not found: {db_path}")
        
        self.conn = sqlite3.connect(db_path)
        self.conn.row_factory = sqlite3.Row  # Enable column access by name
        self.output_format = output_format
        self.module_depth = module_depth
        self.report_content = []  # Store report content for markdown output
    
    def _output(self, text, level=0):
        """Output text to console or store for markdown."""
        if self.output_format == 'console':
            print(text)
        else:
            self.report_content.append(text)
    
    def _save_markdown_report(self, custom_filename=None, archive_folder=None):
        """Save accumulated report content to markdown file."""
        if self.output_format == 'markdown':
            # Create archive folder if specified
            if archive_folder:
                archive_path = Path(archive_folder)
                archive_path.mkdir(exist_ok=True)
                if custom_filename:
                    # For archived files, add timestamp
                    timestamp = datetime.now().strftime('%Y%m%d')
                    base_name = Path(custom_filename).stem
                    archived_file = archive_path / f"{base_name}_{timestamp}.md"
                    
                    with open(archived_file, 'w', encoding='utf-8') as f:
                        f.write('\n'.join(self.report_content))
                    print(f"📄 Archived report saved to: {archived_file}")
            
            # Save primary report file only if custom filename is provided
            if custom_filename:
                markdown_file = Path(custom_filename)
                with open(markdown_file, 'w', encoding='utf-8') as f:
                    f.write('\n'.join(self.report_content))
                print(f"📄 Markdown report saved to: {markdown_file}")
                return markdown_file
            else:
                # No timestamped files generated - only save when explicitly requested
                print("📄 Markdown content generated (use --scan-all for organized file output)")
                return None
    
    def _extract_module_from_path(self, relative_path):
        """Extract module name from relative path based on configured depth.
        
        Args:
            relative_path: Test case path like 'Test Cases/Platform/Admin/API/test.tc'
            
        Returns:
            Module name based on depth configuration, intelligently handling shorter paths
            
        Examples:
            depth=3: 'Test Cases/Platform/Admin/API/test.tc' -> 'Admin'
            depth=3: 'Test Cases/Website/test.tc' -> 'Website' (uses available depth)
            depth=4: 'Test Cases/Platform/Admin/test.tc' -> 'Admin' (uses available depth)
        """
        if not relative_path or not relative_path.startswith('Test Cases/'):
            return 'Other'
        
        # Split path and remove 'Test Cases' prefix
        path_parts = relative_path.split('/')
        if len(path_parts) < 2:
            return 'Other'
        
        # Remove the .tc file from consideration (last element if it ends with .tc)
        directory_parts = path_parts[:-1] if path_parts[-1].endswith('.tc') else path_parts
        
        # Calculate available directory depth (excluding 'Test Cases' at index 0)
        available_depth = len(directory_parts) - 1
        
        if available_depth <= 0:
            return 'Other'
        
        # Use the minimum of configured depth and available depth
        effective_depth = min(self.module_depth, available_depth)
        
        # Extract module at effective depth (index 0 is 'Test Cases', so we use effective_depth as index)
        return directory_parts[effective_depth]
    
    def _extract_module_path_from_path(self, relative_path):
        """Extract full directory path from relative path based on configured depth.
        
        Args:
            relative_path: Test case path like 'Test Cases/Platform/Admin/API/test.tc'
            
        Returns:
            Full directory path based on depth configuration, intelligently handling shorter paths
            
        Examples:
            depth=3: 'Test Cases/Platform/Admin/API/test.tc' -> 'Test Cases/Platform/Admin'
            depth=3: 'Test Cases/Website/test.tc' -> 'Test Cases/Website' (uses available depth)
            depth=4: 'Test Cases/Platform/Admin/test.tc' -> 'Test Cases/Platform/Admin' (uses available depth)
        """
        if not relative_path or not relative_path.startswith('Test Cases/'):
            return 'Other'
        
        # Split path
        path_parts = relative_path.split('/')
        if len(path_parts) < 2:
            return 'Other'
        
        # Remove the .tc file from consideration (last element if it ends with .tc)
        directory_parts = path_parts[:-1] if path_parts[-1].endswith('.tc') else path_parts
        
        # Calculate available directory depth (excluding 'Test Cases' at index 0)
        available_depth = len(directory_parts) - 1
        
        if available_depth <= 0:
            return 'Other'
        
        # Use the minimum of configured depth and available depth
        effective_depth = min(self.module_depth, available_depth)
        
        # Return the full path up to effective depth (including 'Test Cases')
        return '/'.join(directory_parts[:effective_depth + 1])
    
    def _get_coverage_by_module_dynamic(self):
        """Get coverage statistics by module using consistent approach with overall coverage calculation."""
        cursor = self.conn.cursor()
        
        # Get all test cases with their paths and coverage info using the same logic as overall coverage
        cursor.execute("""
            SELECT 
                tc.relative_path,
                tc.id,
                CASE WHEN tscl.test_case_db_id IS NOT NULL THEN 1 ELSE 0 END as is_covered
            FROM test_cases tc
            LEFT JOIN test_suite_case_links tscl ON tc.id = tscl.test_case_db_id
        """)
        
        test_cases = cursor.fetchall()
        
        # Group by module path
        module_stats = {}
        
        for test_case in test_cases:
            # Extract the directory path up to the configured depth
            module_path = self._extract_module_path_from_path(test_case['relative_path'])
            
            if module_path not in module_stats:
                module_stats[module_path] = {
                    'total_cases': 0,
                    'total_covered': 0
                }
            
            module_stats[module_path]['total_cases'] += 1
            
            # Use the same coverage logic as overall calculation - if test case is in test_suite_case_links, it's covered
            if test_case['is_covered']:
                module_stats[module_path]['total_covered'] += 1
        
        return module_stats
    
    def _count_filtering_covered_cases_for_module(self, test_case_ids):
        """Count test cases covered by filtering criteria for specific module."""
        if not test_case_ids:
            return 0
        
        cursor = self.conn.cursor()
        
        # Get all filtering test suites
        cursor.execute("SELECT filtering_text FROM test_suites WHERE suite_type = 'FilteringTestSuiteEntity' AND filtering_text IS NOT NULL")
        filtering_suites = cursor.fetchall()
        
        covered_ids = set()
        
        for suite in filtering_suites:
            criteria = self._parse_filtering_criteria(suite['filtering_text'])
            if criteria:
                matching_ids = self._get_matching_test_case_ids(criteria)
                # Only count test cases that belong to this module
                module_matching_ids = [tc_id for tc_id in matching_ids if tc_id in test_case_ids]
                covered_ids.update(module_matching_ids)
        
        return len(covered_ids)
    
    def _get_explicitly_covered_case_ids_for_module(self, test_case_ids):
        """Get explicitly covered test case IDs for specific module."""
        if not test_case_ids:
            return []
        
        cursor = self.conn.cursor()
        placeholders = ','.join('?' * len(test_case_ids))
        cursor.execute(f"""
            SELECT DISTINCT tscl.test_case_db_id
            FROM test_suite_case_links tscl
            WHERE tscl.test_case_db_id IN ({placeholders})
        """, test_case_ids)
        
        return [row['test_case_db_id'] for row in cursor.fetchall()]
    
    def _get_filtering_covered_case_ids_for_module(self, test_case_ids):
        """Get filtering covered test case IDs for specific module."""
        if not test_case_ids:
            return []
        
        cursor = self.conn.cursor()
        
        # Get all filtering test suites
        cursor.execute("SELECT filtering_text FROM test_suites WHERE suite_type = 'FilteringTestSuiteEntity' AND filtering_text IS NOT NULL")
        filtering_suites = cursor.fetchall()
        
        covered_ids = set()
        
        for suite in filtering_suites:
            criteria = self._parse_filtering_criteria(suite['filtering_text'])
            if criteria:
                matching_ids = self._get_matching_test_case_ids(criteria)
                # Only include test cases that belong to this module
                module_matching_ids = [tc_id for tc_id in matching_ids if tc_id in test_case_ids]
                covered_ids.update(module_matching_ids)
        
        return list(covered_ids)
    
    def _parse_filtering_criteria(self, filtering_text):
        """Parse filtering text from Dynamic Test Suite to extract criteria."""
        if not filtering_text:
            return {}
        
        criteria = {}
        
        # Pattern to match name=(value) or tag=(value1,value2)
        pattern = r'(\w+)=\(([^)]*)\)'
        matches = re.findall(pattern, filtering_text)
        
        for key, value in matches:
            values = [v.strip() for v in value.split(',') if v.strip()]
            if values:
                criteria[key] = values
        
        return criteria
    
    def _count_matching_test_cases(self, filtering_criteria):
        """Count how many test cases match the filtering criteria."""
        if not filtering_criteria:
            return 0
        
        cursor = self.conn.cursor()
        
        # Build WHERE clause based on criteria
        where_conditions = []
        params = []
        
        # Handle name criteria (usually prefix matching)
        if 'name' in filtering_criteria:
            name_conditions = []
            for name_pattern in filtering_criteria['name']:
                if name_pattern:
                    name_conditions.append("name LIKE ?")
                    params.append(f"{name_pattern}%")
            
            if name_conditions:
                where_conditions.append(f"({' OR '.join(name_conditions)})")
        
        # Handle tag criteria (must contain all specified tags)
        if 'tag' in filtering_criteria:
            tag_conditions = []
            for tag in filtering_criteria['tag']:
                if tag:
                    tag_conditions.append("(tags LIKE ? OR tags LIKE ? OR tags LIKE ? OR tags = ?)")
                    # Handle tag at beginning, middle, end, or as only tag
                    params.extend([f"{tag},%", f"%,{tag},%", f"%,{tag}", tag])
            
            if tag_conditions:
                where_conditions.append(f"({' AND '.join(tag_conditions)})")
        
        if not where_conditions:
            return 0
        
        # Execute query
        query = f"SELECT COUNT(*) FROM test_cases WHERE {' AND '.join(where_conditions)}"
        cursor.execute(query, params)
        return cursor.fetchone()[0]
    
    def _count_filtering_covered_cases(self):
        """Count unique test cases covered by Dynamic Test Suite suites."""
        cursor = self.conn.cursor()
        
        # Get all Dynamic Test Suite test suites with filtering criteria
        cursor.execute("""
            SELECT filtering_text 
            FROM test_suites 
            WHERE suite_type = 'FilteringTestSuiteEntity' 
            AND filtering_text IS NOT NULL AND filtering_text != ''
        """)
        
        filtering_suites = cursor.fetchall()
        covered_test_case_ids = set()
        
        for suite in filtering_suites:
            criteria = self._parse_filtering_criteria(suite['filtering_text'])
            if criteria:
                # Get test case IDs that match this filtering criteria
                test_case_ids = self._get_matching_test_case_ids(criteria)
                covered_test_case_ids.update(test_case_ids)
        
        return len(covered_test_case_ids)
    
    def _get_matching_test_case_ids(self, filtering_criteria):
        """Get test case IDs that match the filtering criteria."""
        if not filtering_criteria:
            return []
        
        cursor = self.conn.cursor()
        
        # Build WHERE clause based on criteria
        where_conditions = []
        params = []
        
        # Handle name criteria (usually prefix matching)
        if 'name' in filtering_criteria:
            name_conditions = []
            for name_pattern in filtering_criteria['name']:
                if name_pattern:
                    name_conditions.append("name LIKE ?")
                    params.append(f"{name_pattern}%")
            
            if name_conditions:
                where_conditions.append(f"({' OR '.join(name_conditions)})")
        
        # Handle tag criteria (must contain all specified tags)
        if 'tag' in filtering_criteria:
            tag_conditions = []
            for tag in filtering_criteria['tag']:
                if tag:
                    tag_conditions.append("(tags LIKE ? OR tags LIKE ? OR tags LIKE ? OR tags = ?)")
                    # Handle tag at beginning, middle, end, or as only tag
                    params.extend([f"{tag},%", f"%,{tag},%", f"%,{tag}", tag])
            
            if tag_conditions:
                where_conditions.append(f"({' AND '.join(tag_conditions)})")
        
        if not where_conditions:
            return []
        
        # Execute query to get IDs
        query = f"SELECT id FROM test_cases WHERE {' AND '.join(where_conditions)}"
        cursor.execute(query, params)
        return [row[0] for row in cursor.fetchall()]
    
    def _build_filtering_union_query(self):
        """Build a UNION query for all Dynamic Test Suite criteria."""
        cursor = self.conn.cursor()
        
        # Get all Dynamic Test Suite test suites with filtering criteria
        cursor.execute("""
            SELECT filtering_text 
            FROM test_suites 
            WHERE suite_type = 'FilteringTestSuiteEntity' 
            AND filtering_text IS NOT NULL AND filtering_text != ''
        """)
        
        filtering_suites = cursor.fetchall()
        union_conditions = []
        
        for suite in filtering_suites:
            criteria = self._parse_filtering_criteria(suite['filtering_text'])
            if criteria:
                where_conditions = []
                
                # Handle name criteria
                if 'name' in criteria:
                    name_conditions = []
                    for name_pattern in criteria['name']:
                        if name_pattern:
                            name_conditions.append(f"name LIKE '{name_pattern}%'")
                    
                    if name_conditions:
                        where_conditions.append(f"({' OR '.join(name_conditions)})")
                
                # Handle tag criteria
                if 'tag' in criteria:
                    tag_conditions = []
                    for tag in criteria['tag']:
                        if tag:
                            tag_conditions.append(f"(tags LIKE '{tag},%' OR tags LIKE '%,{tag},%' OR tags LIKE '%,{tag}' OR tags = '{tag}')")
                    
                    if tag_conditions:
                        where_conditions.append(f"({' AND '.join(tag_conditions)})")
                
                if where_conditions:
                    union_conditions.append(' AND '.join(where_conditions))
        
        if union_conditions:
            return ' OR '.join([f"({condition})" for condition in union_conditions])
        else:
            return "1=0"  # No matches if no valid conditions
    
    def _get_total_unique_covered_cases(self):
        """Get total unique test cases covered by both Test Suite and Dynamic Test Suite."""
        cursor = self.conn.cursor()
        
        # Get explicitly linked test case IDs (Test Suite)
        cursor.execute("""
            SELECT DISTINCT tscl.test_case_db_id as test_case_id
            FROM test_suite_case_links tscl 
            WHERE tscl.test_case_db_id IS NOT NULL
        """)
        explicit_ids = {row['test_case_id'] for row in cursor.fetchall()}
        
        # Get dynamically covered test case IDs (Dynamic Test Suite)
        cursor.execute("""
            SELECT filtering_text 
            FROM test_suites 
            WHERE suite_type = 'FilteringTestSuiteEntity' 
            AND filtering_text IS NOT NULL AND filtering_text != ''
        """)
        
        filtering_suites = cursor.fetchall()
        filtering_ids = set()
        
        for suite in filtering_suites:
            criteria = self._parse_filtering_criteria(suite['filtering_text'])
            if criteria:
                test_case_ids = self._get_matching_test_case_ids(criteria)
                filtering_ids.update(test_case_ids)
        
        # Combine both sets to get unique coverage
        total_unique_ids = explicit_ids.union(filtering_ids)
        return len(total_unique_ids)
        
    def generate_executive_summary(self):
        """Generate high-level executive summary for leadership."""
        if self.output_format == 'markdown':
            self._output("# 📊 Test Automation Project Statistics Report")
            self._output("")
            self._output(f"- **📊 Report Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            self._output(f"- **📁 Database:** {self.db_path.name}")
            self._output(f"- **🏗️ Project based:** Katalon Studio")
            self._output("")
        else:
            self._output("=" * 80)
            self._output("📊 TEST AUTOMATION PROJECT STATISTICS REPORT")
            self._output("=" * 80)
            self._output(f"📊 Report Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            self._output(f"📁 Database: {self.db_path.name}")
            self._output(f"🏗️ Project based: Katalon Studio")
            self._output("")
        
        # Overall metrics
        cursor = self.conn.cursor()
        
        # Test case metrics
        cursor.execute("SELECT COUNT(*) as total FROM test_cases")
        total_test_cases = cursor.fetchone()['total']
        
        cursor.execute("SELECT COUNT(*) as total FROM test_suites")
        total_test_suites = cursor.fetchone()['total']
        
        cursor.execute("SELECT COUNT(*) as total FROM test_suite_case_links")
        total_links = cursor.fetchone()['total']
        
        # Coverage metrics - count both explicit links and filtering matches
        # Get explicitly linked test cases (Test Suite)
        cursor.execute("""
            SELECT COUNT(DISTINCT tc.id) as explicit_covered_cases
            FROM test_cases tc
            JOIN test_suite_case_links tscl ON tc.id = tscl.test_case_db_id
        """)
        explicit_covered_cases = cursor.fetchone()['explicit_covered_cases']
        
        # Get dynamically covered test cases (Dynamic Test Suite)
        filtering_covered_cases = self._count_filtering_covered_cases()
        
        # Calculate total unique covered cases (combine both types)
        covered_cases = self._get_total_unique_covered_cases()
        coverage_percentage = (covered_cases / total_test_cases * 100) if total_test_cases > 0 else 0
        
        if self.output_format == 'markdown':
            self._output("## 📈 Executive Summary")
            self._output("")
            self._output(f"- **Total Test Cases:** {total_test_cases:,}")
            self._output(f"- **Total Test Suites:** {total_test_suites:,}")
            self._output(f"- **Test Suite Coverage:** {covered_cases:,}/{total_test_cases:,} ({coverage_percentage:.1f}%)")
            self._output(f"- **Total Test Executions:** {total_links:,}")
            self._output("")
            self._output("<details>")
            self._output("<summary><strong>📊 Metrics Explanation</strong></summary>")
            self._output("")
            self._output("**Total Test Cases:** The complete count of individual test case files (.tc) in the project, representing all available test scenarios that can be executed.")
            self._output("")
            self._output("**Total Test Suites:** The count of all test suite configurations including:")
            self._output("- Static Test Suites (explicitly defined test case lists)")
            self._output("- Dynamic Test Suites (filter-based test case selection)")
            self._output("- Test Suite Collections (orchestrated execution of multiple suites)")
            self._output("")
            self._output("**Test Suite Coverage:** Shows how many test cases are included in at least one test suite. This indicates the percentage of test cases that are organized and ready for execution through test suites.")
            self._output("")
            self._output("**Total Test Executions:** The total count of test case references across all test suites. A test case appearing in multiple test suites is counted multiple times, reflecting potential execution instances. This includes explicit links from static test suites and filter-based matches from dynamic test suites.")
            self._output("")
            self._output("</details>")
            self._output("")
        else:
            self._output("📈 EXECUTIVE SUMMARY")
            self._output("-" * 40)
            self._output(f"Total Test Cases: {total_test_cases:,}")
            self._output(f"Total Test Suites: {total_test_suites:,}")
            self._output(f"Test Suite Coverage: {covered_cases:,}/{total_test_cases:,} ({coverage_percentage:.1f}%)")
            self._output(f"Total Test Executions: {total_links:,}")
            self._output("")
            self._output("📊 METRICS EXPLANATION")
            self._output("-" * 40)
            self._output("Total Test Cases: Complete count of individual test case files (.tc) in the project,")
            self._output("                  representing all available test scenarios that can be executed.")
            self._output("")
            self._output("Total Test Suites: Count of all test suite configurations including:")
            self._output("                   • Static Test Suites (explicitly defined test case lists)")
            self._output("                   • Dynamic Test Suites (filter-based test case selection)")
            self._output("                   • Test Suite Collections (orchestrated execution of multiple suites)")
            self._output("")
            self._output("Test Suite Coverage: Shows how many test cases are included in at least one test suite.")
            self._output("                     This indicates the percentage of test cases that are organized")
            self._output("                     and ready for execution through test suites.")
            self._output("")
            self._output("Total Test Executions: Total count of test case references across all test suites.")
            self._output("                       A test case appearing in multiple test suites is counted")
            self._output("                       multiple times, reflecting potential execution instances.")
            self._output("                       This includes explicit links from static test suites and")
            self._output("                       filter-based matches from dynamic test suites.")
            self._output("")
        
    def analyze_test_automation_maturity(self):
        """Analyze automation maturity and quality indicators."""
        if self.output_format == 'markdown':
            self._output("## 🔬 Automation Maturity Analysis")
            self._output("")
        else:
            self._output("🔬 AUTOMATION MATURITY ANALYSIS")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        # Test case complexity analysis
        cursor.execute("""
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN has_variables THEN 1 ELSE 0 END) as with_variables,
                SUM(CASE WHEN has_test_data_links THEN 1 ELSE 0 END) as with_data_links,
                AVG(LENGTH(description)) as avg_description_length
            FROM test_cases
        """)
        complexity = cursor.fetchone()
        
        variables_percentage = (complexity['with_variables'] / complexity['total'] * 100) if complexity['total'] > 0 else 0
        data_links_percentage = (complexity['with_data_links'] / complexity['total'] * 100) if complexity['total'] > 0 else 0
        
        if self.output_format == 'markdown':
            self._output("### Test Case Complexity")
            self._output(f"- **Parameterized Tests:** {complexity['with_variables']:,} ({variables_percentage:.1f}%)")
            self._output(f"- **Data-Driven Tests:** {complexity['with_data_links']:,} ({data_links_percentage:.1f}%)")
            self._output(f"- **Avg Description Length:** {complexity['avg_description_length']:.0f} chars")
            self._output("")
        else:
            self._output(f"Test Case Complexity:")
            self._output(f"  • Parameterized Tests: {complexity['with_variables']:,} ({variables_percentage:.1f}%)")
            self._output(f"  • Data-Driven Tests: {complexity['with_data_links']:,} ({data_links_percentage:.1f}%)")
            self._output(f"  • Avg Description Length: {complexity['avg_description_length']:.0f} chars")
        
        # Test suite sophistication
        cursor.execute("""
            SELECT 
                suite_type_alias,
                COUNT(*) as count,
                AVG(LENGTH(filtering_text)) as avg_filter_complexity
            FROM test_suites 
            WHERE suite_type_alias IS NOT NULL
            GROUP BY suite_type_alias
        """)
        suite_types = cursor.fetchall()
        
        if self.output_format == 'markdown':
            self._output("### Test Suite Sophistication")
            for suite_type in suite_types:
                filter_info = f" (Avg Filter: {suite_type['avg_filter_complexity']:.0f} chars)" if suite_type['avg_filter_complexity'] else ""
                self._output(f"- **{suite_type['suite_type_alias']}:** {suite_type['count']:,}{filter_info}")
            self._output("")
        else:
            self._output(f"\nTest Suite Sophistication:")
            for suite_type in suite_types:
                filter_info = f" (Avg Filter: {suite_type['avg_filter_complexity']:.0f} chars)" if suite_type['avg_filter_complexity'] else ""
                self._output(f"  • {suite_type['suite_type_alias']}: {suite_type['count']:,}{filter_info}")
            self._output("")
        
    def analyze_test_coverage_distribution(self):
        """Analyze test coverage across different areas."""
        if self.output_format == 'markdown':
            self._output("## 📊 Test Coverage Distribution")
            self._output("")
        else:
            self._output("📊 TEST COVERAGE DISTRIBUTION")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        # Coverage by directory/module using dynamic approach
        module_stats = self._get_coverage_by_module_dynamic()
        
        # Convert to list format for compatibility with existing output code
        coverage_data = []
        for module, stats in sorted(module_stats.items(), key=lambda x: x[1]['total_cases'], reverse=True):
            coverage_data.append({
                'module': module,
                'total_cases': stats['total_cases'],
                'covered_cases': stats['total_covered']
            })
        
        if self.output_format == 'markdown':
            self._output("### Coverage by Module")
            self._output("")
            self._output("Coverage analysis organized by folder structure under Test Cases directory. This breakdown shows how test cases are distributed across different organizational folders and their respective test suite coverage rates. The depth level can be configured using the --module-depth parameter to show different levels of folder hierarchy.")
            self._output("")
            self._output("<details>")
            self._output(f"<summary><strong>📊 Module Coverage Breakdown</strong> ({len(coverage_data)} modules)</summary>")
            self._output("")
            self._output("Coverage statistics showing test cases present in test suites versus total available test cases per folder path:")
            self._output("")
            self._output("| Module | Present in TS | Total | Coverage % |")
            self._output("|--------|---------|-------|------------|")
            for row in coverage_data:
                coverage_pct = (row['covered_cases'] / row['total_cases'] * 100) if row['total_cases'] > 0 else 0
                self._output(f"| {row['module']} | {row['covered_cases']} | {row['total_cases']} | {coverage_pct:.1f}% |")
            self._output("")
            self._output("</details>")
            self._output("")
        else:
            self._output("Coverage by Module:")
            for row in coverage_data:
                coverage_pct = (row['covered_cases'] / row['total_cases'] * 100) if row['total_cases'] > 0 else 0
                self._output(f"  • {row['module']:<12}: {row['covered_cases']:>3}/{row['total_cases']:<3} ({coverage_pct:>5.1f}%)")
            self._output("")
        
    def analyze_priority_and_risk(self):
        """Analyze test priority distribution and risk indicators."""
        if self.output_format == 'markdown':
            self._output("## ⚠️ Priority & Risk Analysis")
            self._output("")
        else:
            self._output("⚠️  PRIORITY & RISK ANALYSIS")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        # Priority tag analysis using global PRIORITY_TAGS configuration
        # Build dynamic CASE statement for priority classification
        case_conditions = []
        order_conditions = []
        
        for priority in PRIORITY_TAGS:
            case_conditions.append(f"WHEN tags LIKE '%{priority['tag']}%' THEN '{priority['label']}'")
            order_conditions.append(f"WHEN '{priority['label']}' THEN {priority['order']}")
        
        case_statement = "\n                    ".join(case_conditions)
        order_statement = "\n                    ".join(order_conditions)
        
        priority_sql = f"""
            SELECT 
                CASE 
                    {case_statement}
                    ELSE 'Unclassified'
                END as priority,
                COUNT(*) as count,
                COUNT(DISTINCT tscl.test_case_db_id) as covered
            FROM test_cases tc
            LEFT JOIN test_suite_case_links tscl ON tc.id = tscl.test_case_db_id
            GROUP BY priority
            ORDER BY 
                CASE priority
                    {order_statement}
                    ELSE 999
                END
        """
        
        cursor.execute(priority_sql)
        
        priority_data = cursor.fetchall()
        
        if self.output_format == 'markdown':
            self._output("### Test Priority Distribution")
            self._output("")
            # Generate dynamic note based on configured priority tags
            tag_list = ", ".join([f"`{priority['tag']}`" for priority in PRIORITY_TAGS])
            for row in priority_data:
                coverage_pct = (row['covered'] / row['count'] * 100) if row['count'] > 0 else 0
                self._output(f"- **{row['priority']}:** {row['count']} tests ({coverage_pct:.1f}% in suites)")
            self._output("")
        else:
            self._output("Test Priority Distribution:")
            for row in priority_data:
                coverage_pct = (row['covered'] / row['count'] * 100) if row['count'] > 0 else 0
                self._output(f"  • {row['priority']:<15}: {row['count']:>3} tests ({coverage_pct:>5.1f}% in suites)")
        
        # Test type analysis using global TEST_TYPE_TAGS configuration
        # Build dynamic CASE statement for test type classification
        type_case_conditions = []
        
        for test_type in TEST_TYPE_TAGS:
            type_case_conditions.append(f"WHEN tags LIKE '%{test_type['tag']}%' THEN '{test_type['label']}'")
        
        type_case_statement = "\n                    ".join(type_case_conditions)
        
        test_type_sql = f"""
            SELECT 
                CASE 
                    {type_case_statement}
                    ELSE 'Other'
                END as test_type,
                COUNT(*) as count
            FROM test_cases
            WHERE tags IS NOT NULL
            GROUP BY test_type
            ORDER BY count DESC
            LIMIT 10
        """
        
        cursor.execute(test_type_sql)
        
        test_types = cursor.fetchall()
        
        if self.output_format == 'markdown':
            self._output("### Test Type Distribution")
            self._output("")
            for row in test_types:
                self._output(f"- **{row['test_type']}:** {row['count']} tests")
            self._output("")
        else:
            self._output(f"\nTest Type Distribution:")
            for row in test_types:
                self._output(f"  • {row['test_type']:<18}: {row['count']:>3} tests")
            self._output("")
        
        # Test tag distribution analysis
        cursor.execute(f"""
            SELECT tag_name, COUNT(*) as usage_count
            FROM tags t
            JOIN test_case_tags tct ON t.id = tct.tag_id
            GROUP BY tag_name
            ORDER BY usage_count DESC
            LIMIT {TOP_TAGS_LIMIT}
        """)
        
        tag_distribution = cursor.fetchall()
        
        if self.output_format == 'markdown':
            self._output("### Test Tag Distribution")
            self._output("")
            self._output("Tags provide valuable insights into test categorization, functionality coverage, and team organization patterns. This analysis shows the most frequently used tags across all test cases.")
            self._output("")
            self._output("<details>")
            self._output(f"<summary><strong>📊 Top {TOP_TAGS_LIMIT} Most Used Tags</strong></summary>")
            self._output("")
            self._output("| Tag | Usage Count | Percentage |")
            self._output("|-----|-------------|------------|")
            
            total_test_cases = cursor.execute("SELECT COUNT(*) FROM test_cases").fetchone()[0]
            for row in tag_distribution:
                percentage = (row['usage_count'] / total_test_cases * 100)
                self._output(f"| **{row['tag_name']}** | {row['usage_count']} | {percentage:.1f}% |")
            self._output("")
            self._output("</details>")
            self._output("")
        else:
            self._output(f"\nTest Tag Distribution:")
            total_test_cases = cursor.execute("SELECT COUNT(*) FROM test_cases").fetchone()[0]
            for row in tag_distribution:
                percentage = (row['usage_count'] / total_test_cases * 100)
                self._output(f"  • {row['tag_name']:<15}: {row['usage_count']:>3} tests ({percentage:>5.1f}%)")
            self._output("")
        
    def analyze_automation_efficiency(self):
        """Analyze automation efficiency and reusability metrics."""
        if self.output_format == 'markdown':
            self._output("## ⚡ Automation Efficiency Metrics")
            self._output("")
        else:
            self._output("⚡ AUTOMATION EFFICIENCY METRICS")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        # Test reusability analysis - get all test cases that appear in more than 1 test suite
        cursor.execute("""
            SELECT 
                tc.id,
                tc.name,
                tc.relative_path,
                tc.tags,
                COUNT(tscl.id) as reuse_count
            FROM test_cases tc
            JOIN test_suite_case_links tscl ON tc.id = tscl.test_case_db_id
            GROUP BY tc.id
            HAVING COUNT(tscl.id) > 1
            ORDER BY reuse_count DESC, tc.name
        """)
        
        reusable_tests = cursor.fetchall()
        
        if self.output_format == 'markdown':
            self._output("### Most Reused Test Cases")
            self._output("")
            self._output("Test cases that appear in multiple test suites (including dynamic test suites) demonstrate good reusability or might need to review for optimization to avoid duplicated execution.")
            self._output("")
            
            if reusable_tests:
                self._output(f"**Total Reused Test Cases:** {len(reusable_tests)}")
                self._output("")
                
                # Create higher-level collapsible wrapper around all detailed sections
                self._output("<details>")
                self._output(f"<summary><strong>📋 View All Reused Test Cases Details</strong> ({len(reusable_tests)} test cases)</summary>")
                self._output("")
                
                for test in reusable_tests:
                    # Get the specific test suites this test case appears in
                    cursor.execute("""
                        SELECT DISTINCT
                            ts.name as suite_name,
                            ts.suite_type_alias,
                            ts.relative_path as suite_path
                        FROM test_suite_case_links tscl
                        JOIN test_suites ts ON tscl.test_suite_id = ts.id
                        WHERE tscl.test_case_db_id = ?
                        ORDER BY ts.name
                    """, (test['id'],))
                    
                    suite_usage = cursor.fetchall()
                    
                    self._output(f"<details>")
                    self._output(f"<summary><strong>🔄 {test['name']}</strong> (used in {test['reuse_count']} suites)</summary>")
                    self._output("")
                    self._output(f"**Test Case Path:** `{test['relative_path']}`")
                    if test['tags']:
                        self._output(f"**Tags:** `{test['tags']}`")
                    self._output("")
                    self._output("**Used in Test Suites:**")
                    self._output("")
                    self._output("| Test Suite | Type | Path |")
                    self._output("|------------|------|------|")
                    
                    for suite in suite_usage:
                        suite_type_friendly = suite['suite_type_alias'] or 'Unknown'
                        
                        self._output(f"| `{suite['suite_name']}` | {suite_type_friendly} | `{suite['suite_path']}` |")
                    
                    self._output("")
                    self._output("</details>")
                    self._output("")
                
                # Close the higher-level collapsible wrapper
                self._output("</details>")
                self._output("")
            else:
                self._output("No test cases are reused across multiple suites.")
            self._output("")
        else:
            self._output("Most Reused Test Cases:")
            if reusable_tests:
                for i, test in enumerate(reusable_tests, 1):
                    self._output(f"  {i:>2}. {test['name'][:50]:<50} ({test['reuse_count']} suites)")
            else:
                self._output("  No test cases are reused across multiple suites.")
        
        # Complete test suite inventory with correct test counts for filtering suites
        cursor.execute("""
            SELECT 
                ts.name,
                ts.suite_type,
                ts.suite_type_alias,
                ts.filtering_text,
                ts.relative_path,
                COUNT(tscl.id) as explicit_test_count
            FROM test_suites ts
            LEFT JOIN test_suite_case_links tscl ON ts.id = tscl.test_suite_id
            GROUP BY ts.id, ts.name, ts.suite_type, ts.suite_type_alias, ts.filtering_text, ts.relative_path
            ORDER BY ts.name ASC
        """)
        
        all_suites_raw = cursor.fetchall()
        
        # Calculate correct test counts for each suite
        all_suites = []
        for suite in all_suites_raw:
            if suite['suite_type'] == 'FilteringTestSuiteEntity' and suite['filtering_text']:
                # For Dynamic Test Suites, count matching test cases based on filtering criteria
                criteria = self._parse_filtering_criteria(suite['filtering_text'])
                filtering_count = len(self._get_matching_test_case_ids(criteria)) if criteria else 0
                test_count = filtering_count
            else:
                # For regular Test Suites, use explicit test case links
                test_count = suite['explicit_test_count']
            
            all_suites.append({
                'name': suite['name'],
                'suite_type': suite['suite_type'],
                'suite_type_friendly': suite['suite_type_alias'] or 'Unknown',
                'test_count': test_count,
                'relative_path': suite['relative_path'],
                'filtering_text': suite['filtering_text'] if suite['suite_type'] == 'FilteringTestSuiteEntity' else None
            })
        
        if self.output_format == 'markdown':
            self._output("### Complete Test Suite Inventory")
            self._output("")
            self._output("This comprehensive inventory provides detailed information about all test suites in the project, including their types, test case counts, and filtering criteria. Test suites are organized by their content status to help identify potential maintenance needs.")
            self._output("")
            
            # Separate empty and non-empty test suites (exclude Test Suite Collections from both categories)
            empty_suites = [suite for suite in all_suites if suite['test_count'] == 0 and suite['suite_type'] != 'TestSuiteCollectionEntity']
            non_empty_suites = [suite for suite in all_suites if suite['test_count'] > 0 and suite['suite_type'] != 'TestSuiteCollectionEntity']
            
            # Display empty test suites if any exist
            if empty_suites:
                self._output("<details>")
                self._output(f"<summary><strong>⚠️ Empty Test Suites</strong> ({len(empty_suites)} suites with 0 test cases)</summary>")
                self._output("")
                self._output("These test suites contain no test cases and may require attention or cleanup.")
                self._output("")
                self._output("| Test Suite Name | Type | Path | Filter Criteria |")
                self._output("|-----------------|------|------|------------------|")
                for suite in empty_suites:
                    path = suite['relative_path'] or 'N/A'
                    filter_criteria = suite['filtering_text'] or 'N/A'
                    # Escape pipe characters in filter criteria for markdown table
                    filter_criteria = filter_criteria.replace('|', '\\|') if filter_criteria != 'N/A' else 'N/A'
                    self._output(f"| **{suite['name']}** | {suite['suite_type_friendly']} | `{path}` | `{filter_criteria}` |")
                self._output("")
                self._output("</details>")
                self._output("")
            
            # Display non-empty test suites
            if non_empty_suites:
                self._output("<details>")
                self._output(f"<summary><strong>✅ Active Test Suites</strong> ({len(non_empty_suites)} suites with test cases)</summary>")
                self._output("")
                self._output("These test suites contain test cases and are actively used for test execution.")
                self._output("")
                self._output("| Test Suite Name | Type | Test Cases | Path | Filter Criteria |")
                self._output("|-----------------|------|------------|------|------------------|")
                for suite in non_empty_suites:
                    path = suite['relative_path'] or 'N/A'
                    filter_criteria = suite['filtering_text'] or 'N/A'
                    # Escape pipe characters in filter criteria for markdown table
                    filter_criteria = filter_criteria.replace('|', '\\|') if filter_criteria != 'N/A' else 'N/A'
                    self._output(f"| **{suite['name']}** | {suite['suite_type_friendly']} | {suite['test_count']} | `{path}` | `{filter_criteria}` |")
                self._output("")
                self._output("</details>")
                self._output("")
        
    def generate_test_suite_collection_inventory(self):
        """Generate Complete Test Suite Collection Inventory section."""
        cursor = self.conn.cursor()
        
        # Get all test suite collections with their metadata
        cursor.execute('''
            SELECT ts.id, ts.name, ts.relative_path, ts.delay_between_instances, 
                   ts.execution_mode, ts.max_concurrent_instances, ts.description
            FROM test_suites ts
            WHERE ts.suite_type = 'TestSuiteCollectionEntity'
            ORDER BY ts.name
        ''')
        
        collections = cursor.fetchall()
        
        if not collections:
            if self.output_format == 'markdown':
                self._output("### Complete Test Suite Collection Inventory")
                self._output("")
                self._output("*No Test Suite Collections found in the database.*")
                self._output("")
            else:
                self._output(f"\nComplete Test Suite Collection Inventory:")
                self._output("No Test Suite Collections found in the database.")
                self._output("")
            return
        
        if self.output_format == 'markdown':
            self._output("### Complete Test Suite Collection Inventory")
            self._output("")
            self._output("Test Suite Collections are orchestrated execution configurations that run multiple test suites with specific profiles and settings. This inventory provides comprehensive details about all collections, organized by their content status to help identify configuration issues and optimization opportunities.")
            self._output("")
            
            # Collect summary data for all collections
            collection_summaries = []
            for collection in collections:
                # Get count of referenced suites for this collection
                cursor.execute('''
                    SELECT COUNT(*) as suite_count
                    FROM test_suite_collection_links
                    WHERE collection_suite_id = ?
                ''', (collection['id'],))
                
                suite_count = cursor.fetchone()['suite_count']
                
                collection_summaries.append({
                    'name': collection['name'],
                    'path': collection['relative_path'],
                    'execution_mode': collection['execution_mode'] or 'N/A',
                    'max_concurrent': collection['max_concurrent_instances'] or 'N/A',
                    'delay': collection['delay_between_instances'] or 0,
                    'suite_count': suite_count,
                    'collection_data': collection
                })
            
            # Separate empty and non-empty collections
            empty_collections = [c for c in collection_summaries if c['suite_count'] == 0]
            non_empty_collections = [c for c in collection_summaries if c['suite_count'] > 0]
            
            # Display empty collections if any exist
            if empty_collections:
                self._output("<details>")
                self._output(f"<summary><strong>⚠️ Empty Test Suite Collections</strong> ({len(empty_collections)} collections with 0 test suites)</summary>")
                self._output("")
                self._output("These collections contain no test suite references and may require configuration or cleanup.")
                self._output("")
                self._output("| Collection | Path | Exec Mode | Max Concurrent | Delay (sec) |")
                self._output("|------------|------|-----------|----------------|-------------|")
                for summary in empty_collections:
                    self._output(f"| **{summary['name']}** | `{summary['path']}` | {summary['execution_mode']} | {summary['max_concurrent']} | {summary['delay']} |")
                self._output("")
                self._output("</details>")
                self._output("")
            
            # Display non-empty collections
            if non_empty_collections:
                self._output("<details>")
                self._output(f"<summary><strong>✅ Active Test Suite Collections</strong> ({len(non_empty_collections)} collections with test suites)</summary>")
                self._output("")
                self._output("These collections contain test suite references and are configured for execution.")
                self._output("")
                self._output("| Collection | Path | Total Suites | Exec Mode | Max Concurrent | Delay (sec) |")
                self._output("|------------|------|--------------|-----------|----------------|-------------|")
                for summary in non_empty_collections:
                    self._output(f"| **{summary['name']}** | `{summary['path']}` | {summary['suite_count']} | {summary['execution_mode']} | {summary['max_concurrent']} | {summary['delay']} |")
                self._output("")
                self._output("</details>")
                self._output("")
            
            # Detailed configurations section
            if non_empty_collections:
                self._output("<details>")
                self._output(f"<summary><strong>📋 Detailed Collection Configurations</strong></summary>")
                self._output("")
            
            # Now create detailed sections with collapsible details for non-empty collections only
            for summary in non_empty_collections:
                collection = summary['collection_data']
                self._output(f"<details>")
                self._output(f"<summary><strong>📦 {collection['name']}</strong> ({summary['suite_count']} suites)</summary>")
                self._output("")
                self._output(f"**Path:** `{collection['relative_path']}`")
                if collection['description']:
                    self._output(f"**Description:** {collection['description']}")
                self._output(f"**Execution Mode:** {collection['execution_mode'] or 'N/A'}")
                self._output(f"**Max Concurrent Instances:** {collection['max_concurrent_instances'] or 'N/A'}")
                self._output(f"**Delay Between Instances:** {collection['delay_between_instances'] or 0} seconds")
                self._output("")
                
                # Get collection links for this collection with full test suite paths
                cursor.execute('''
                    SELECT tscl.referenced_suite_path, tscl.run_enabled, tscl.group_name, tscl.profile_name, 
                           tscl.require_configuration_data, tscl.run_configuration_id,
                           ts.relative_path as full_suite_path
                    FROM test_suite_collection_links tscl
                    LEFT JOIN test_suites ts ON ts.name = tscl.referenced_suite_path
                    WHERE tscl.collection_suite_id = ?
                    ORDER BY tscl.referenced_suite_path
                ''', (collection['id'],))
                
                links = cursor.fetchall()
                
                if links:
                    self._output("**Referenced Test Suites:**")
                    self._output("")
                    self._output("| Test Suite | Enabled | Group | Profile | Browser/Config |")
                    self._output("|------------|---------|-------|---------|----------------|")
                    
                    for link in links:
                        # Use full path if available, otherwise fall back to referenced_suite_path
                        suite_path = link['full_suite_path'] if link['full_suite_path'] else link['referenced_suite_path']
                        suite_path = suite_path if suite_path else 'N/A'
                        enabled = "✅ Yes" if link['run_enabled'] else "❌ No"
                        group = link['group_name'] or 'N/A'
                        profile = link['profile_name'] or 'N/A'
                        config = link['run_configuration_id'] or 'N/A'
                        
                        self._output(f"| `{suite_path}` | {enabled} | {group} | {profile} | {config} |")
                    
                    self._output("")
                    self._output(f"**Total Referenced Suites:** {len(links)}")
                else:
                    self._output("**Referenced Test Suites:** *None configured*")
                
                self._output("")
                self._output("</details>")
                self._output("")
            
            # Close the detailed configurations section
            if non_empty_collections:
                self._output("</details>")
                self._output("")
        
        else:
            self._output(f"\nComplete Test Suite Collection Inventory:")
            self._output("=" * 80)
            
            for collection in collections:
                self._output(f"\n📦 {collection['name']}")
                self._output(f"   Path: {collection['relative_path']}")
                if collection['description']:
                    self._output(f"   Description: {collection['description']}")
                self._output(f"   Execution Mode: {collection['execution_mode'] or 'N/A'}")
                self._output(f"   Max Concurrent Instances: {collection['max_concurrent_instances'] or 'N/A'}")
                self._output(f"   Delay Between Instances: {collection['delay_between_instances'] or 0} seconds")
                
                # Get collection links for this collection
                cursor.execute('''
                    SELECT referenced_suite_path, run_enabled, group_name, profile_name, 
                           require_configuration_data, run_configuration_id
                    FROM test_suite_collection_links
                    WHERE collection_suite_id = ?
                    ORDER BY referenced_suite_path
                ''', (collection['id'],))
                
                links = cursor.fetchall()
                
                if links:
                    self._output(f"\n   Referenced Test Suites ({len(links)}):")
                    self._output(f"   {'Suite Name':<40} {'Enabled':<8} {'Group':<15} {'Profile':<10} {'Config':<15}")
                    self._output(f"   {'-' * 40} {'-' * 8} {'-' * 15} {'-' * 10} {'-' * 15}")
                    
                    for link in links:
                        suite_name = link['referenced_suite_path'].split('/')[-1] if link['referenced_suite_path'] else 'N/A'
                        enabled = "Yes" if link['run_enabled'] else "No"
                        group = link['group_name'] or 'N/A'
                        profile = link['profile_name'] or 'N/A'
                        config = link['run_configuration_id'] or 'N/A'
                        
                        # Truncate long names for console display
                        if len(suite_name) > 37:
                            suite_name = suite_name[:34] + "..."
                        if len(group) > 12:
                            group = group[:9] + "..."
                        if len(config) > 12:
                            config = config[:9] + "..."
                        
                        self._output(f"   {suite_name:<40} {enabled:<8} {group:<15} {profile:<10} {config:<15}")
                else:
                    self._output(f"\n   Referenced Test Suites: None configured")
                
                self._output("")
            
            self._output("-" * 80)
            self._output("")
        
    def generate_recommendations(self):
        """Generate strategic recommendations for automation improvement."""
        if self.output_format == 'markdown':
            self._output("## 💡 Strategic Recommendations")
            self._output("")
        else:
            self._output("💡 STRATEGIC RECOMMENDATIONS")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        recommendations = []
        
        # Check coverage gaps - use same logic as executive summary
        total_test_cases = cursor.execute("SELECT COUNT(*) FROM test_cases").fetchone()[0]
        covered_cases = self._get_total_unique_covered_cases()
        uncovered = total_test_cases - covered_cases
        
        if uncovered > 0:
            # Get the specific uncovered test cases for detailed reporting
            covered_test_case_ids = set()
            
            # Get explicitly covered test case IDs (Test Suite)
            cursor.execute("""
                SELECT DISTINCT tscl.test_case_db_id as test_case_id
                FROM test_suite_case_links tscl 
                WHERE tscl.test_case_db_id IS NOT NULL
            """)
            explicit_ids = {row['test_case_id'] for row in cursor.fetchall()}
            covered_test_case_ids.update(explicit_ids)
            
            # Get dynamically covered test case IDs (Dynamic Test Suite)
            cursor.execute("""
                SELECT filtering_text 
                FROM test_suites 
                WHERE suite_type = 'FilteringTestSuiteEntity' 
                AND filtering_text IS NOT NULL AND filtering_text != ''
            """)
            filtering_suites = cursor.fetchall()
            
            for suite in filtering_suites:
                criteria = self._parse_filtering_criteria(suite['filtering_text'])
                if criteria:
                    test_case_ids = self._get_matching_test_case_ids(criteria)
                    covered_test_case_ids.update(test_case_ids)
            
            # Get uncovered test cases details
            if covered_test_case_ids:
                placeholders = ','.join(['?' for _ in covered_test_case_ids])
                cursor.execute(f"""
                    SELECT name, tags, relative_path
                    FROM test_cases 
                    WHERE id NOT IN ({placeholders})
                    ORDER BY name
                """, list(covered_test_case_ids))
            else:
                cursor.execute("""
                    SELECT name, tags, relative_path
                    FROM test_cases 
                    ORDER BY name
                """)
            
            uncovered_test_cases = cursor.fetchall()
            recommendations.append(f"🎯 {uncovered} test cases are not included in any test suite - consider creating focused suites")

            # Add detailed table of uncovered test cases
            if self.output_format == 'markdown' and uncovered_test_cases:
                self._output("")
                self._output("#### Uncovered Test Cases")
                self._output("")
                self._output("Test cases that are not included in any test suite represent potential gaps in automation coverage. These cases may need to be organized into appropriate test suites or evaluated for relevance to current testing strategies.")
                self._output("")
                self._output("<details>")
                self._output(f"<summary><strong>📋 Detailed Uncovered Test Cases</strong> ({len(uncovered_test_cases)} test cases)</summary>")
                self._output("")
                self._output("The following test cases are not referenced by any test suite and may require attention:")
                self._output("")
                self._output("| Test Case Name | Tags | Path |")
                self._output("|----------------|------|------|")
                for test_case in uncovered_test_cases:
                    tags = test_case['tags'] or 'No tags'
                    path = test_case['relative_path']
                    self._output(f"| **{test_case['name']}** | {tags} | `{path}` |")
                self._output("")
                self._output("</details>")
                self._output("")
        
        # Check P1 test coverage - use corrected logic for both explicit and filtering coverage
        cursor.execute("SELECT COUNT(*) as total_p1 FROM test_cases WHERE tags LIKE '%p1%'")
        total_p1 = cursor.fetchone()['total_p1']
        
        if total_p1 > 0:
            # Get P1 test case IDs
            cursor.execute("SELECT id FROM test_cases WHERE tags LIKE '%p1%'")
            p1_test_ids = {row['id'] for row in cursor.fetchall()}
            
            # Get covered test case IDs (both explicit and filtering)
            cursor.execute("""
                SELECT DISTINCT tscl.test_case_db_id as test_case_id
                FROM test_suite_case_links tscl 
                WHERE tscl.test_case_db_id IS NOT NULL
            """)
            explicit_covered_ids = {row['test_case_id'] for row in cursor.fetchall()}
            
            # Get filtering covered IDs
            cursor.execute("""
                SELECT filtering_text 
                FROM test_suites 
                WHERE suite_type = 'FilteringTestSuiteEntity' 
                AND filtering_text IS NOT NULL AND filtering_text != ''
            """)
            filtering_suites = cursor.fetchall()
            filtering_covered_ids = set()
            
            for suite in filtering_suites:
                criteria = self._parse_filtering_criteria(suite['filtering_text'])
                if criteria:
                    test_case_ids = self._get_matching_test_case_ids(criteria)
                    filtering_covered_ids.update(test_case_ids)
            
            # Combine coverage and check P1 overlap
            all_covered_ids = explicit_covered_ids.union(filtering_covered_ids)
            covered_p1_ids = p1_test_ids.intersection(all_covered_ids)
            p1_coverage = (len(covered_p1_ids) / total_p1 * 100)
        else:
            p1_coverage = 0
        
        if p1_coverage < 95:
            recommendations.append(f"⚠️  P1 test coverage is {p1_coverage:.1f}% - ensure critical tests are in smoke/regression suites")
        
        # Check for data-driven opportunities
        cursor.execute("""
            SELECT COUNT(*) as non_data_driven
            FROM test_cases
            WHERE has_test_data_links = FALSE AND tags LIKE '%ui%'
        """)
        non_data_driven = cursor.fetchone()['non_data_driven']

        # Check filtering suite utilization
        cursor.execute("""
            SELECT 
                COUNT(*) as total_suites,
                SUM(CASE WHEN suite_type = 'FilteringTestSuiteEntity' THEN 1 ELSE 0 END) as collection_suites
            FROM test_suites
        """)
        suite_data = cursor.fetchone()
        filtering_percentage = (suite_data['collection_suites'] / suite_data['total_suites'] * 100) if suite_data['total_suites'] > 0 else 0
        
        if filtering_percentage > 70:
            recommendations.append(f"✅ Excellent use of Dynamic Test Suites ({filtering_percentage:.1f}%) - good automation maturity")
        elif filtering_percentage < 30:
            recommendations.append(f"🔄 Consider more Dynamic Test Suites for better maintenance ({filtering_percentage:.1f}% currently)")
        
        if not recommendations:
            recommendations.append("✅ Automation setup looks well-structured - continue monitoring and optimizing")
        
        if self.output_format == 'markdown':
            for i, rec in enumerate(recommendations, 1):
                self._output(f"{i}. {rec}")
            self._output("")
        else:
            for i, rec in enumerate(recommendations, 1):
                self._output(f"{i}. {rec}")
            self._output("")
        
    def generate_trend_analysis(self):
        """Generate trend analysis based on timestamps."""
        if self.output_format == 'markdown':
            self._output("## 📈 Trend Analysis")
            self._output("")
        else:
            self._output("📈 TREND ANALYSIS")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        # Recent activity analysis
        cursor.execute("""
            SELECT 
                DATE(updated_at) as update_date,
                COUNT(*) as updates
            FROM test_cases
            WHERE updated_at IS NOT NULL
            GROUP BY DATE(updated_at)
            ORDER BY update_date DESC
            LIMIT 10
        """)
        
        recent_updates = cursor.fetchall()
        
        if self.output_format == 'markdown':
            if recent_updates:
                self._output("### Recent Test Case Activity")
                self._output("")
                for update in recent_updates:
                    self._output(f"- **{update['update_date']}:** {update['updates']} test cases updated")
            else:
                self._output("No recent timestamp data available")
            self._output("")
        else:
            if recent_updates:
                self._output("Recent Test Case Activity:")
                for update in recent_updates:
                    self._output(f"  • {update['update_date']}: {update['updates']} test cases updated")
            else:
                self._output("No recent timestamp data available")
            self._output("")
        
    def export_detailed_metrics(self):
        """Export detailed metrics for further analysis."""
        if self.output_format == 'markdown':
            self._output("## 📋 Detailed Metrics Export")
            self._output("")
        else:
            self._output("📋 DETAILED METRICS EXPORT")
            self._output("-" * 40)
        
        cursor = self.conn.cursor()
        
        # Export summary metrics to JSON
        metrics = {}
        
        # Basic counts
        cursor.execute("SELECT COUNT(*) as count FROM test_cases")
        metrics['total_test_cases'] = cursor.fetchone()['count']
        
        cursor.execute("SELECT COUNT(*) as count FROM test_suites")
        metrics['total_test_suites'] = cursor.fetchone()['count']
        
        # Coverage metrics - use same calculation as markdown report (includes FilteringTestSuiteEntity)
        metrics['covered_test_cases'] = self._get_total_unique_covered_cases()
        metrics['coverage_percentage'] = (metrics['covered_test_cases'] / metrics['total_test_cases'] * 100) if metrics['total_test_cases'] > 0 else 0
        
        # Tag distribution
        cursor.execute("""
            SELECT tag_name, usage_count
            FROM tags
            ORDER BY usage_count DESC
            LIMIT 20
        """)
        metrics['top_tags'] = [dict(row) for row in cursor.fetchall()]
        
        # Export to file - store in archive folder if using scan-all, otherwise root
        if hasattr(self, 'archive_folder') and self.archive_folder:
            # For scan-all mode: save JSON in archive folder
            archive_path = Path(self.archive_folder)
            archive_path.mkdir(exist_ok=True)
            db_name = self.db_path.stem
            timestamp = datetime.now().strftime('%Y%m%d')
            export_file = archive_path / f"{db_name}_metrics_{timestamp}.json"
        else:
            # For single database mode: save in root (legacy behavior for backward compatibility)
            export_file = self.db_path.parent / f"automation_metrics_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        
        with open(export_file, 'w') as f:
            json.dump(metrics, f, indent=2)
        
        if self.output_format == 'markdown':
            self._output(f"**Detailed metrics exported to:** `{export_file}`")
            self._output("")
        else:
            self._output(f"Detailed metrics exported to: {export_file}")
            self._output("")
        
    def generate_full_report(self):
        """Generate the complete automation progress report."""
        try:
            self.generate_executive_summary()
            self.analyze_test_automation_maturity()
            self.analyze_test_coverage_distribution()
            self.analyze_priority_and_risk()
            self.analyze_automation_efficiency()
            self.generate_test_suite_collection_inventory()
            self.generate_trend_analysis()
            self.generate_recommendations()
            self.export_detailed_metrics()
            
            if self.output_format == 'markdown':
                self._output("---")
                self._output("")
                self._output("✅ **Automation Progress Report Completed**")
                self._save_markdown_report()
            else:
                self._output("=" * 80)
                self._output("✅ AUTOMATION PROGRESS REPORT COMPLETED")
                self._output("=" * 80)
            
        except Exception as e:
            print(f"❌ Error generating report: {e}")
            sys.exit(1)
        finally:
            self.conn.close()


def scan_all_databases(module_depth=2):
    """Scan all .db files and generate organized reports for each."""
    current_dir = Path('.')
    db_files = list(current_dir.glob('*.db'))
    
    if not db_files:
        print("❌ No .db files found in current directory")
        return
    
    print(f"🔍 Found {len(db_files)} database file(s) to process:")
    for db_file in db_files:
        print(f"  • {db_file}")
    print()
    
    for db_file in db_files:
        print(f"📊 Processing database: {db_file}")
        
        # Extract database name without extension
        db_name = db_file.stem
        
        # Create archive folder
        archive_folder = f"{db_name}_archived"
        
        # Primary report filename (same as db name but .md)
        primary_report = f"{db_name}.md"
        
        try:
            # Generate report for this database
            reporter = AutomationProgressReporter(
                db_path=db_file, 
                output_format='markdown',
                module_depth=module_depth
            )
            
            # Set archive folder for organized JSON export
            reporter.archive_folder = archive_folder
            
            # Generate the report content
            reporter.generate_full_report()
            
            # Save primary report and archived copy
            reporter._save_markdown_report(
                custom_filename=primary_report,
                archive_folder=archive_folder
            )
            
            print(f"✅ Completed processing: {db_file}")
            print(f"   📄 Primary report: {primary_report}")
            print(f"   📁 Archive folder: {archive_folder}/")
            print()
            
        except Exception as e:
            print(f"❌ Error processing {db_file}: {str(e)}")
            print()
    
    print("🎉 Multi-database scanning completed!")


def main():
    """Main execution function."""
    parser = argparse.ArgumentParser(
        description="Generate comprehensive Katalon automation progress report",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python3 automation_progress_report.py                    # Auto-detect database
  python3 automation_progress_report.py my_tests.db       # Specific database
  python3 automation_progress_report.py --markdown        # Generate markdown report
  python3 automation_progress_report.py --module-depth 4  # Deeper module categorization
  python3 automation_progress_report.py --scan-all        # Process all .db files with organized reports
        """
    )
    
    parser.add_argument(
        'database_path',
        nargs='?',
        help='Path to SQLite database file (default: auto-detect .db file)'
    )
    
    parser.add_argument(
        '--markdown', '-md',
        action='store_true',
        help='Output report as markdown file instead of console'
    )
    
    parser.add_argument(
        '--module-depth', '-d',
        type=int,
        default=2,
        help='Directory depth level for module extraction from Test Cases/ (default: 2 for Test Cases/Platform/)'
    )
    
    parser.add_argument(
        '--scan-all',
        action='store_true',
        help='Scan all .db files and generate organized reports for each database'
    )
    
    args = parser.parse_args()
    
    # Handle scan-all option
    if args.scan_all:
        scan_all_databases(args.module_depth)
        return
    
    # Auto-detect database if not provided
    if not args.database_path:
        db_files = list(Path('.').glob('*.db'))
        if not db_files:
            print("❌ No .db files found in current directory")
            print("Please specify database path or run extractors first")
            sys.exit(1)
        elif len(db_files) == 1:
            args.database_path = db_files[0]
        else:
            print("Multiple .db files found:")
            for i, db_file in enumerate(db_files, 1):
                print(f"  {i}. {db_file}")
            print("Please specify which database to use")
            sys.exit(1)
    
    # Generate report with organized output (same as --scan-all but for single file)
    db_file = Path(args.database_path)
    db_name = db_file.stem
    
    # Create archive folder
    archive_folder = f"{db_name}_archived"
    
    # Primary report filename (same as db name but .md)
    primary_report = f"{db_name}.md"
    
    print(f"📊 Processing database: {db_file}")
    
    try:
        # Generate report for this database
        output_format = 'markdown' if args.markdown else 'markdown'  # Always use markdown for organized output
        reporter = AutomationProgressReporter(
            db_path=db_file, 
            output_format=output_format,
            module_depth=args.module_depth
        )
        
        # Set archive folder for organized JSON export
        reporter.archive_folder = archive_folder
        
        # Generate the report content
        reporter.generate_full_report()
        
        # Save primary report and archived copy
        reporter._save_markdown_report(
            custom_filename=primary_report,
            archive_folder=archive_folder
        )
        
        print(f"✅ Completed processing: {db_file}")
        print(f"   📄 Primary report: {primary_report}")
        print(f"   📁 Archive folder: {archive_folder}/")
        
    except Exception as e:
        print(f"❌ Error processing {db_file}: {str(e)}")
        sys.exit(1)


if __name__ == "__main__":
    main()
