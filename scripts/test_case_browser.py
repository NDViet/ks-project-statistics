#!/usr/bin/env python3
"""
Test Case Browser
=================
Query database and list all test cases grouped by folder with collapsible/expandable sections.
Uses the same folder depth logic as automation_progress_report.py.

Usage: python3 test_case_browser.py [database_path] [--module-depth DEPTH] [--output FORMAT]
"""

import sqlite3
import argparse
import json
from datetime import datetime
from pathlib import Path
import sys


class TestCaseBrowser:
    """Browse and display test cases grouped by folder structure."""
    
    def __init__(self, db_path, output_format='markdown', module_depth=2):
        """Initialize browser with database connection.
        
        Args:
            db_path: Path to the SQLite database
            output_format: 'console' or 'markdown'
            module_depth: Number of directory levels deep from 'Test Cases/' to extract module names
                         Default 2: Test Cases/Platform/ -> 'Platform'
                         Depth 3: Test Cases/Platform/Admin/ -> 'Admin'
        """
        self.db_path = Path(db_path)
        if not self.db_path.exists():
            raise FileNotFoundError(f"Database not found: {db_path}")
        
        self.conn = sqlite3.connect(db_path)
        self.conn.row_factory = sqlite3.Row  # Enable column access by name
        self.output_format = output_format
        self.module_depth = module_depth
        self.report_content = []  # Store report content for markdown output
    
    def _output(self, text):
        """Output text to console or store for markdown."""
        if self.output_format == 'console':
            print(text)
        else:
            self.report_content.append(text)
    
    def _save_markdown_report(self, filename=None):
        """Save accumulated report content to markdown file."""
        if self.output_format == 'markdown':
            if filename:
                markdown_file = Path(filename)
                with open(markdown_file, 'w', encoding='utf-8') as f:
                    f.write('\n'.join(self.report_content))
                print(f"📄 Test case browser report saved to: {markdown_file}")
                return markdown_file
            else:
                # Auto-generate filename based on database name
                db_stem = self.db_path.stem
                auto_filename = f"{db_stem}_list_test_cases.md"
                markdown_file = Path(auto_filename)
                with open(markdown_file, 'w', encoding='utf-8') as f:
                    f.write('\n'.join(self.report_content))
                print(f"📄 Test case browser report saved to: {markdown_file}")
                return markdown_file
    
    def _extract_module_path_from_path(self, relative_path):
        """Extract full directory path from relative path based on configured depth.
        
        Args:
            relative_path: Test case path like 'Test Cases/Platform/Admin/API/test.tc'
            
        Returns:
            Full directory path based on depth configuration, intelligently handling shorter paths
            
        Examples:
            depth=2: 'Test Cases/Platform/Admin/API/test.tc' -> 'Test Cases/Platform'
            depth=3: 'Test Cases/Platform/Admin/API/test.tc' -> 'Test Cases/Platform/Admin'
            depth=2: 'Test Cases/Website/test.tc' -> 'Test Cases/Website' (uses available depth)
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
    
    def _get_test_cases_by_folder(self):
        """Get all test cases grouped by folder path."""
        cursor = self.conn.cursor()
        
        # Get all test cases with their details
        cursor.execute("""
            SELECT 
                id,
                name,
                description,
                tags,
                relative_path,
                has_variables,
                has_test_data_links,
                updated_at,
                created_at
            FROM test_cases
            ORDER BY relative_path, name
        """)
        
        test_cases = cursor.fetchall()
        
        # Group by folder path
        folder_groups = {}
        
        for test_case in test_cases:
            # Extract the directory path up to the configured depth
            folder_path = self._extract_module_path_from_path(test_case['relative_path'])
            
            if folder_path not in folder_groups:
                folder_groups[folder_path] = []
            
            # Add test case details to the folder group
            folder_groups[folder_path].append({
                'id': test_case['id'],
                'name': test_case['name'],
                'description': test_case['description'] or '',
                'tags': test_case['tags'] or '',
                'relative_path': test_case['relative_path'],
                'has_variables': test_case['has_variables'],
                'has_test_data_links': test_case['has_test_data_links'],
                'updated_at': test_case['updated_at'],
                'created_at': test_case['created_at']
            })
        
        return folder_groups
    
    def _get_test_suite_coverage_for_case(self, test_case_id):
        """Get test suite coverage information for a specific test case."""
        cursor = self.conn.cursor()
        
        # Get explicit test suite links
        cursor.execute("""
            SELECT DISTINCT
                ts.name as suite_name,
                ts.suite_type_alias,
                ts.relative_path as suite_path
            FROM test_suite_case_links tscl
            JOIN test_suites ts ON tscl.test_suite_id = ts.id
            WHERE tscl.test_case_db_id = ?
            ORDER BY ts.name
        """, (test_case_id,))
        
        explicit_suites = cursor.fetchall()
        
        # Check for dynamic test suite coverage (filtering suites)
        cursor.execute("""
            SELECT name, tags FROM test_cases WHERE id = ?
        """, (test_case_id,))
        
        test_case_info = cursor.fetchone()
        if not test_case_info:
            return explicit_suites
        
        # Get all filtering test suites to check if this test case matches
        cursor.execute("""
            SELECT 
                ts.name as suite_name,
                ts.suite_type_alias,
                ts.relative_path as suite_path,
                ts.filtering_text
            FROM test_suites ts
            WHERE ts.suite_type = 'FilteringTestSuiteEntity' 
            AND ts.filtering_text IS NOT NULL AND ts.filtering_text != ''
        """)
        
        filtering_suites = cursor.fetchall()
        dynamic_matches = []
        
        for suite in filtering_suites:
            if self._test_case_matches_filter(test_case_info, suite['filtering_text']):
                dynamic_matches.append({
                    'suite_name': suite['suite_name'],
                    'suite_type_alias': suite['suite_type_alias'],
                    'suite_path': suite['suite_path']
                })
        
        # Combine explicit and dynamic matches
        all_suites = list(explicit_suites) + dynamic_matches
        return all_suites
    
    def _test_case_matches_filter(self, test_case_info, filtering_text):
        """Check if a test case matches the filtering criteria."""
        if not filtering_text:
            return False
        
        criteria = self._parse_filtering_criteria(filtering_text)
        if not criteria:
            return False
        
        # Check name criteria
        if 'name' in criteria:
            name_match = False
            for name_pattern in criteria['name']:
                if name_pattern and test_case_info['name'].startswith(name_pattern):
                    name_match = True
                    break
            if not name_match:
                return False
        
        # Check tag criteria
        if 'tag' in criteria:
            test_tags = test_case_info['tags'] or ''
            test_tag_list = [tag.strip() for tag in test_tags.split(',') if tag.strip()]
            
            for required_tag in criteria['tag']:
                if required_tag and required_tag not in test_tag_list:
                    return False
        
        return True
    
    def _parse_filtering_criteria(self, filtering_text):
        """Parse filtering text from Dynamic Test Suite to extract criteria."""
        if not filtering_text:
            return {}
        
        import re
        criteria = {}
        
        # Pattern to match name=(value) or tag=(value1,value2)
        pattern = r'(\w+)=\(([^)]*)\)'
        matches = re.findall(pattern, filtering_text)
        
        for key, value in matches:
            values = [v.strip() for v in value.split(',') if v.strip()]
            if values:
                criteria[key] = values
        
        return criteria
    
    def generate_test_case_browser_report(self):
        """Generate the main test case browser report."""
        if self.output_format == 'markdown':
            self._output("# 📁 Test Case Browser")
            self._output("")
            self._output(f"- **📊 Report Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            self._output(f"- **📁 Database:** {self.db_path.name}")
            self._output(f"- **📊 Module Depth:** {self.module_depth}")
            self._output("")
            self._output("Browse all test cases organized by folder structure. Each folder section can be expanded to view detailed test case information including names, descriptions, tags, and test suite coverage.")
            self._output("")
        else:
            self._output("=" * 80)
            self._output("📁 TEST CASE BROWSER")
            self._output("=" * 80)
            self._output(f"📊 Report Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            self._output(f"📁 Database: {self.db_path.name}")
            self._output(f"📊 Module Depth: {self.module_depth}")
            self._output("")
        
        # Get test cases grouped by folder
        folder_groups = self._get_test_cases_by_folder()
        
        # Sort folders by name for consistent output
        sorted_folders = sorted(folder_groups.items())
        
        if self.output_format == 'markdown':
            self._output("## 📊 Test Cases by Folder")
            self._output("")
            
            total_test_cases = sum(len(cases) for cases in folder_groups.values())
            self._output(f"**Total Test Cases:** {total_test_cases:,} across {len(folder_groups)} folders")
            self._output("")
            
            for folder_path, test_cases in sorted_folders:
                self._output("<details>")
                self._output(f"<summary><strong>📁 {folder_path}</strong> ({len(test_cases)} test cases)</summary>")
                self._output("")
                
                # Create table for test cases in this folder
                self._output("| Test Case | Description | Tags |")
                self._output("|-----------|-------------|------|")
                
                for test_case in sorted(test_cases, key=lambda x: x['name']):
                    # Prepare table data
                    name = test_case['name']
                    
                    # Clean description - remove steps and keep only main description
                    description = test_case['description']
                    if description:
                        # Remove steps section if present
                        if 'Steps:' in description:
                            description = description.split('Steps:')[0].strip()
                        if 'Step:' in description:
                            description = description.split('Step:')[0].strip()
                        # Truncate if too long
                        if len(description) > 150:
                            description = description[:150] + "..."
                    else:
                        description = ""
                    
                    tags = test_case['tags'] if test_case['tags'] else ""
                    
                    # Escape pipe characters for markdown table
                    description = description.replace('|', '\\|').replace('\n', ' ')
                    tags = tags.replace('|', '\\|')
                    
                    self._output(f"| **{name}** | {description} | `{tags}` |")
                
                self._output("")
                self._output("</details>")
                self._output("")
        
        else:
            # Console output
            total_test_cases = sum(len(cases) for cases in folder_groups.values())
            self._output(f"📊 Total Test Cases: {total_test_cases:,} across {len(folder_groups)} folders")
            self._output("")
            
            for folder_path, test_cases in sorted_folders:
                self._output(f"📁 {folder_path} ({len(test_cases)} test cases)")
                self._output("-" * (len(folder_path) + 20))
                
                for i, test_case in enumerate(sorted(test_cases, key=lambda x: x['name']), 1):
                    self._output(f"  {i:>2}. {test_case['name']}")
                    
                    # Clean description - remove steps and keep only main description
                    description = test_case['description']
                    if description:
                        # Remove steps section if present
                        if 'Steps:' in description:
                            description = description.split('Steps:')[0].strip()
                        if 'Step:' in description:
                            description = description.split('Step:')[0].strip()
                        # Truncate if too long
                        if len(description) > 100:
                            description = description[:100] + "..."
                        self._output(f"      Description: {description}")
                    
                    if test_case['tags']:
                        self._output(f"      Tags: {test_case['tags']}")
                    
                    self._output("")
                
                self._output("")


def main():
    """Main function to run the test case browser."""
    parser = argparse.ArgumentParser(description='Browse test cases grouped by folder structure')
    parser.add_argument('database_path', nargs='?', default='healthcare-hyperexecute.db',
                       help='Path to the SQLite database (default: healthcare-hyperexecute.db)')
    parser.add_argument('--module-depth', type=int, default=2,
                       help='Directory depth level for folder grouping (default: 2)')
    parser.add_argument('--output', choices=['console', 'markdown'], default='markdown',
                       help='Output format (default: markdown)')
    parser.add_argument('--save-to', type=str,
                       help='Save markdown output to specified file')
    
    args = parser.parse_args()
    
    try:
        # Initialize browser
        browser = TestCaseBrowser(
            db_path=args.database_path,
            output_format=args.output,
            module_depth=args.module_depth
        )
        
        # Generate report
        browser.generate_test_case_browser_report()
        
        # Save to file if requested or auto-generate filename for markdown
        if args.output == 'markdown':
            browser._save_markdown_report(args.save_to)  # Will auto-generate filename if save_to is None
        
    except FileNotFoundError as e:
        print(f"❌ Error: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        sys.exit(1)


if __name__ == '__main__':
    main()
