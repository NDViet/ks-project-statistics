#!/usr/bin/env python3
"""
TestCase Extractor - Extract all .tc files from Katalon Test Cases directory
and store the data in SQLite3 database for analysis.

This script parses XML TestCaseEntity files and extracts:
- Test case name, description, comment
- Tags (stored as comma-separated list)
- Record option and GUID
- Relative file path
- Additional metadata (test data links, variables, etc.)
"""

import os
import sqlite3
import xml.etree.ElementTree as ET
from pathlib import Path
import json
from datetime import datetime
import sys
import argparse


class TestCaseExtractor:
    def __init__(self, root_dir, db_path="test_cases.db"):
        self.root_dir = Path(root_dir)
        self.db_path = db_path
        self.conn = None
        
    def create_database(self):
        """Create SQLite database and tables for test case data."""
        self.conn = sqlite3.connect(self.db_path)
        cursor = self.conn.cursor()
        
        # Main test cases table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_cases (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                tags TEXT,  -- Comma-separated tags
                tags_list TEXT,  -- JSON array of tags for easier querying
                comment TEXT,
                record_option TEXT,
                test_case_guid TEXT UNIQUE,
                relative_path TEXT NOT NULL,
                has_test_data_links BOOLEAN DEFAULT FALSE,
                has_variables BOOLEAN DEFAULT FALSE,
                updated_at TIMESTAMP,  -- File last modified timestamp
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Tags table for normalized tag storage
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS tags (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tag_name TEXT UNIQUE NOT NULL,
                usage_count INTEGER DEFAULT 1
            )
        ''')
        
        # Test case tags relationship table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_case_tags (
                test_case_id INTEGER,
                tag_id INTEGER,
                FOREIGN KEY (test_case_id) REFERENCES test_cases (id),
                FOREIGN KEY (tag_id) REFERENCES tags (id),
                PRIMARY KEY (test_case_id, tag_id)
            )
        ''')
        
        # Test data links table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_data_links (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                test_case_id INTEGER,
                combination_type TEXT,
                link_id TEXT,
                iteration_type TEXT,
                iteration_value TEXT,
                test_data_id TEXT,
                FOREIGN KEY (test_case_id) REFERENCES test_cases (id)
            )
        ''')
        
        # Variables table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_variables (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                test_case_id INTEGER,
                variable_id TEXT,
                name TEXT,
                default_value TEXT,
                description TEXT,
                masked BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (test_case_id) REFERENCES test_cases (id)
            )
        ''')
        
        self.conn.commit()
        print(f"Database created: {self.db_path}")
    
    def find_tc_files(self):
        """Find all .tc files in the Test Cases directory."""
        test_cases_dir = self.root_dir / "Test Cases"
        if not test_cases_dir.exists():
            raise FileNotFoundError(f"Test Cases directory not found: {test_cases_dir}")
        
        tc_files = list(test_cases_dir.rglob("*.tc"))
        print(f"Found {len(tc_files)} .tc files")
        return tc_files
    
    def parse_tc_file(self, file_path):
        """Parse a single .tc file and extract TestCaseEntity data."""
        try:
            tree = ET.parse(file_path)
            root = tree.getroot()
            
            if root.tag != "TestCaseEntity":
                print(f"Warning: {file_path} is not a TestCaseEntity file")
                return None
            
            # Extract basic fields
            file_stat = file_path.stat()
            updated_at = datetime.fromtimestamp(file_stat.st_mtime).strftime('%Y-%m-%d %H:%M:%S')
            
            data = {
                'name': self.get_text(root, 'name'),
                'description': self.get_text(root, 'description'),
                'tags': self.get_text(root, 'tag'),
                'comment': self.get_text(root, 'comment'),
                'record_option': self.get_text(root, 'recordOption'),
                'test_case_guid': self.get_text(root, 'testCaseGuid'),
                'relative_path': str(file_path.relative_to(self.root_dir)),
                'updated_at': updated_at
            }
            
            # Process tags
            tags_list = []
            if data['tags']:
                tags_list = [tag.strip() for tag in data['tags'].split(',') if tag.strip()]
            data['tags_list'] = json.dumps(tags_list)
            
            # Check for test data links
            test_data_links = root.findall('testDataLinks')
            data['has_test_data_links'] = len(test_data_links) > 0
            data['test_data_links'] = test_data_links
            
            # Check for variables
            variables = root.findall('variable')
            data['has_variables'] = len(variables) > 0
            data['variables'] = variables
            
            return data
            
        except ET.ParseError as e:
            print(f"Error parsing {file_path}: {e}")
            return None
        except Exception as e:
            print(f"Unexpected error parsing {file_path}: {e}")
            return None
    
    def get_text(self, parent, tag_name):
        """Safely get text content from XML element."""
        element = parent.find(tag_name)
        return element.text if element is not None and element.text else ""
    
    def insert_test_case(self, data):
        """Insert or update test case data in database."""
        cursor = self.conn.cursor()
        
        # Check if test case already exists by GUID
        cursor.execute('SELECT id FROM test_cases WHERE test_case_guid = ?', (data['test_case_guid'],))
        existing_record = cursor.fetchone()
        
        if existing_record:
            # Update existing test case
            test_case_id = existing_record[0]
            cursor.execute('''
                UPDATE test_cases SET 
                name = ?, description = ?, tags = ?, tags_list = ?, comment = ?, 
                record_option = ?, relative_path = ?, 
                has_test_data_links = ?, has_variables = ?, updated_at = ?
                WHERE test_case_guid = ?
            ''', (
                data['name'], data['description'], data['tags'], data['tags_list'],
                data['comment'], data['record_option'], 
                data['relative_path'], data['has_test_data_links'], data['has_variables'],
                data['updated_at'], data['test_case_guid']
            ))
            
            # Clear existing related data to avoid duplicates
            cursor.execute('DELETE FROM test_case_tags WHERE test_case_id = ?', (test_case_id,))
            cursor.execute('DELETE FROM test_data_links WHERE test_case_id = ?', (test_case_id,))
            cursor.execute('DELETE FROM test_variables WHERE test_case_id = ?', (test_case_id,))
            
        else:
            # Insert new test case record
            cursor.execute('''
                INSERT INTO test_cases 
                (name, description, tags, tags_list, comment, record_option, 
                 test_case_guid, relative_path, has_test_data_links, has_variables, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''', (
                data['name'], data['description'], data['tags'], data['tags_list'],
                data['comment'], data['record_option'], data['test_case_guid'],
                data['relative_path'], 
                data['has_test_data_links'], data['has_variables'], data['updated_at']
            ))
            
            test_case_id = cursor.lastrowid
        
        # Process tags
        if data['tags']:
            tags_list = [tag.strip() for tag in data['tags'].split(',') if tag.strip()]
            for tag in tags_list:
                # Insert or update tag (only increment count for new test cases)
                cursor.execute('''
                    INSERT OR IGNORE INTO tags (tag_name, usage_count) VALUES (?, 1)
                ''', (tag,))
                
                # Only increment usage count if this is a new test case
                if not existing_record:
                    cursor.execute('''
                        UPDATE tags SET usage_count = usage_count + 1 WHERE tag_name = ?
                    ''', (tag,))
                
                # Get tag ID and create relationship
                cursor.execute('SELECT id FROM tags WHERE tag_name = ?', (tag,))
                tag_id = cursor.fetchone()[0]
                
                cursor.execute('''
                    INSERT INTO test_case_tags (test_case_id, tag_id) 
                    VALUES (?, ?)
                ''', (test_case_id, tag_id))
        
        # Process test data links
        if data['has_test_data_links']:
            for link in data['test_data_links']:
                combination_type = self.get_text(link, 'combinationType')
                link_id = self.get_text(link, 'id')
                test_data_id = self.get_text(link, 'testDataId')
                
                iteration_elem = link.find('iterationEntity')
                iteration_type = ""
                iteration_value = ""
                if iteration_elem is not None:
                    iteration_type = self.get_text(iteration_elem, 'iterationType')
                    iteration_value = self.get_text(iteration_elem, 'value')
                
                cursor.execute('''
                    INSERT INTO test_data_links 
                    (test_case_id, combination_type, link_id, iteration_type, 
                     iteration_value, test_data_id)
                    VALUES (?, ?, ?, ?, ?, ?)
                ''', (test_case_id, combination_type, link_id, iteration_type, 
                      iteration_value, test_data_id))
        
        # Process variables
        if data['has_variables']:
            for var in data['variables']:
                variable_id = self.get_text(var, 'id')
                name = self.get_text(var, 'name')
                default_value = self.get_text(var, 'defaultValue')
                description = self.get_text(var, 'description')
                masked = self.get_text(var, 'masked').lower() == 'true'
                
                cursor.execute('''
                    INSERT INTO test_variables 
                    (test_case_id, variable_id, name, default_value, description, masked)
                    VALUES (?, ?, ?, ?, ?, ?)
                ''', (test_case_id, variable_id, name, default_value, description, masked))
        
        return existing_record is not None
    
    def get_directory_distribution(self, depth=2, base_path="Test Cases"):
        """
        Get directory distribution with configurable depth and dynamic partitioning.
        
        Args:
            depth (int): Number of directory levels to include after base_path
            base_path (str): Base path to analyze from (default: "Test Cases")
            
        Returns:
            list: Sorted list of (directory_path, count) tuples
        """
        cursor = self.conn.cursor()
        cursor.execute('SELECT relative_path FROM test_cases')
        paths = cursor.fetchall()
        
        directory_counts = {}
        base_path_with_slash = f"{base_path}/"
        base_path_len = len(base_path_with_slash)
        
        for (path,) in paths:
            if path.startswith(base_path_with_slash):
                # Remove base path prefix and split by '/'
                remaining_path = path[base_path_len:]
                parts = remaining_path.split('/')
                
                # Build directory path based on requested depth
                if len(parts) >= depth:
                    # Take exactly 'depth' levels
                    directory_parts = parts[:depth]
                    directory = f"{base_path}/" + "/".join(directory_parts)
                elif len(parts) > 0:
                    # Take all available parts if less than requested depth
                    directory = f"{base_path}/" + "/".join(parts[:-1]) if len(parts) > 1 else f"{base_path}/{parts[0]}"
                else:
                    directory = base_path
                
                directory_counts[directory] = directory_counts.get(directory, 0) + 1
            else:
                # Handle paths that don't start with base_path
                directory_counts[path] = directory_counts.get(path, 0) + 1
        
        # Sort by count descending
        return sorted(directory_counts.items(), key=lambda x: x[1], reverse=True)
    
    def extract_all(self):
        """Extract all test cases and store in database."""
        print("Starting test case extraction...")
        
        # Create database
        self.create_database()
        
        # Find all .tc files
        tc_files = self.find_tc_files()
        
        # Process each file
        processed = 0
        updated = 0
        errors = 0
        
        for file_path in tc_files:
            print(f"Processing: {file_path.relative_to(self.root_dir)}")
            
            data = self.parse_tc_file(file_path)
            if data:
                try:
                    is_update = self.insert_test_case(data)
                    if is_update:
                        updated += 1
                    else:
                        processed += 1
                except Exception as e:
                    print(f"Error inserting {file_path}: {e}")
                    errors += 1
            else:
                errors += 1
        
        self.conn.commit()
        print(f"\nExtraction complete!")
        print(f"New records: {processed} files")
        print(f"Updated records: {updated} files")
        print(f"Errors: {errors} files")
        
        # Generate summary statistics
        self.generate_summary()
    
    def generate_summary(self):
        """Generate summary statistics about the extracted data."""
        cursor = self.conn.cursor()
        
        print("\n" + "="*50)
        print("EXTRACTION SUMMARY")
        print("="*50)
        
        # Total test cases
        cursor.execute("SELECT COUNT(*) FROM test_cases")
        total_cases = cursor.fetchone()[0]
        print(f"Total test cases: {total_cases}")
        
        # Total unique tags
        cursor.execute("SELECT COUNT(*) FROM tags")
        total_tags = cursor.fetchone()[0]
        print(f"Total unique tags: {total_tags}")
        
        # Test cases with test data links
        cursor.execute("SELECT COUNT(*) FROM test_cases WHERE has_test_data_links = 1")
        with_data_links = cursor.fetchone()[0]
        print(f"Test cases with data links: {with_data_links}")
        
        # Test cases with variables
        cursor.execute("SELECT COUNT(*) FROM test_cases WHERE has_variables = 1")
        with_variables = cursor.fetchone()[0]
        print(f"Test cases with variables: {with_variables}")
        
        # Top 10 most used tags
        print(f"\nTop 10 most used tags:")
        cursor.execute('''
            SELECT tag_name, usage_count 
            FROM tags 
            ORDER BY usage_count DESC 
            LIMIT 10
        ''')
        for tag, count in cursor.fetchall():
            print(f"  {tag}: {count}")
        
        # Test cases by directory (configurable depth)
        print(f"\nTest cases by directory (2 levels):")
        directory_stats = self.get_directory_distribution(depth=2)
        for directory, count in directory_stats:
            print(f"  {directory}: {count}")

    def close(self):
        """Close database connection."""
        if self.conn:
            self.conn.close()


def main():
    """Main execution function."""
    parser = argparse.ArgumentParser(
        description="Extract Katalon test cases from .tc files into SQLite database"
    )
    parser.add_argument(
        "katalon_project_path",
        nargs="?",
        default=".",
        help="Path to the Katalon project directory (default: current directory)"
    )
    parser.add_argument(
        "--db-path",
        default=None,
        help="Path for the output SQLite database (default: <project_folder_name>.db)"
    )
    
    args = parser.parse_args()
    
    # Convert to absolute path and resolve any relative paths
    project_path = Path(args.katalon_project_path).resolve()
    
    # Generate database name from project folder name if not specified
    if args.db_path is None:
        db_name = f"{project_path.name}.db"
    else:
        db_name = args.db_path
    
    print(f"Katalon Test Case Extractor")
    print(f"Project directory: {project_path}")
    print(f"Database will be created as: {db_name}")
    print("-" * 50)
    
    # Validate that the project path exists
    if not project_path.exists():
        print(f"Error: Project path does not exist: {project_path}")
        sys.exit(1)
    
    # Create extractor and run
    extractor = TestCaseExtractor(project_path, db_name)
    
    try:
        extractor.extract_all()
    except Exception as e:
        print(f"Error during extraction: {e}")
        sys.exit(1)
    finally:
        extractor.close()
    
    print(f"\nDatabase saved as: {Path(db_name).resolve()}")
    print("You can now analyze the data using SQL queries or Python scripts.")


if __name__ == "__main__":
    main()
