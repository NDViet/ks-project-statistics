#!/usr/bin/env python3
"""
Test Suite Extractor - Extract all .ts files from Katalon Test Suites directory
and store the data in SQLite3 database for analysis.

This script parses XML TestSuiteEntity, FilteringTestSuiteEntity, and TestSuiteCollectionEntity files and extracts:
- Test suite name, description, tags
- Test suite GUID and configuration
- Test case links and relationships
- Filtering criteria for dynamic test suites
- Variables and their configurations
- Test suite collection links and execution configurations
"""

import os
import sqlite3
import xml.etree.ElementTree as ET
from pathlib import Path
import json
from datetime import datetime
import sys
import argparse
import re


class TestSuiteExtractor:
    def __init__(self, root_dir, db_path="katalon_project.db"):
        self.root_dir = Path(root_dir)
        self.db_path = db_path
        self.conn = None
        
    def create_database(self):
        """Create SQLite database and tables for test suite data.
        If test case tables exist, this will add test suite tables to the same database."""
        self.conn = sqlite3.connect(self.db_path)
        cursor = self.conn.cursor()
        
        # Check if test_cases table exists (from tc_extractor.py)
        cursor.execute('''
            SELECT name FROM sqlite_master 
            WHERE type='table' AND name='test_cases'
        ''')
        has_test_cases = cursor.fetchone() is not None
        
        if has_test_cases:
            print(f"Found existing test_cases table - adding test suite tables to shared database")
        else:
            print(f"Creating new database with test suite tables")
        
        # Main test suites table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_suites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                tags TEXT,  -- Comma-separated tags
                tags_list TEXT,  -- JSON array of tags for easier querying
                suite_type TEXT NOT NULL,  -- 'TestSuiteEntity', 'FilteringTestSuiteEntity', or 'TestSuiteCollectionEntity'
                suite_type_alias TEXT NOT NULL,  -- UI-friendly names: 'Test Suite', 'Dynamic Test Suite', 'Test Suite Collection'
                test_suite_guid TEXT UNIQUE,
                relative_path TEXT NOT NULL,
                is_rerun BOOLEAN DEFAULT FALSE,
                mail_recipient TEXT,
                number_of_rerun INTEGER DEFAULT 0,
                page_load_timeout INTEGER DEFAULT 30,
                page_load_timeout_default BOOLEAN DEFAULT TRUE,
                rerun_failed_test_cases_only BOOLEAN DEFAULT FALSE,
                rerun_immediately BOOLEAN DEFAULT FALSE,
                filtering_text TEXT,  -- For FilteringTestSuiteEntity
                filtering_built_in TEXT,
                filtering_extension TEXT,
                filtering_plugin TEXT,
                -- TestSuiteCollectionEntity specific fields
                delay_between_instances INTEGER,
                execution_mode TEXT,  -- e.g., 'PARALLEL'
                max_concurrent_instances INTEGER,
                updated_at TIMESTAMP,  -- File last modified timestamp
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Test case links table (for TestSuiteEntity)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_suite_case_links (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                test_suite_id INTEGER,
                link_guid TEXT,
                test_case_id TEXT,  -- Path to test case (e.g., "Test Cases/Platform/...")
                test_case_db_id INTEGER,  -- Foreign key to test_cases table if available
                is_reuse_driver BOOLEAN DEFAULT FALSE,
                is_run BOOLEAN DEFAULT TRUE,
                using_data_binding_at_test_suite_level BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (test_suite_id) REFERENCES test_suites (id)
            )
        ''')
        
        # Test suite variables table (for variable links in test case links)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_suite_variables (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                test_case_link_id INTEGER,
                variable_id TEXT,
                test_data_link_id TEXT,
                type TEXT,  -- e.g., 'DEFAULT'
                value TEXT,
                FOREIGN KEY (test_case_link_id) REFERENCES test_suite_case_links (id)
            )
        ''')
        
        # Tags table for normalized tag storage (reuse from test cases if exists)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS suite_tags (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tag_name TEXT UNIQUE NOT NULL,
                usage_count INTEGER DEFAULT 1
            )
        ''')
        
        # Test suite tags relationship table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_suite_tags (
                test_suite_id INTEGER,
                tag_id INTEGER,
                FOREIGN KEY (test_suite_id) REFERENCES test_suites (id),
                FOREIGN KEY (tag_id) REFERENCES suite_tags (id),
                PRIMARY KEY (test_suite_id, tag_id)
            )
        ''')
        
        # Test suite collection links table (for TestSuiteCollectionEntity)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_suite_collection_links (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                collection_suite_id INTEGER,
                referenced_suite_path TEXT,  -- Path to referenced test suite
                referenced_suite_guid TEXT,  -- GUID of referenced test suite (if available)
                run_enabled BOOLEAN DEFAULT TRUE,
                group_name TEXT,
                profile_name TEXT,
                require_configuration_data BOOLEAN DEFAULT FALSE,
                run_configuration_id TEXT,
                FOREIGN KEY (collection_suite_id) REFERENCES test_suites (id)
            )
        ''')
        
        # Create indexes for better performance
        cursor.execute('CREATE INDEX IF NOT EXISTS idx_test_suites_guid ON test_suites(test_suite_guid)')
        cursor.execute('CREATE INDEX IF NOT EXISTS idx_test_suites_type ON test_suites(suite_type)')
        cursor.execute('CREATE INDEX IF NOT EXISTS idx_test_suite_case_links_suite_id ON test_suite_case_links(test_suite_id)')
        cursor.execute('CREATE INDEX IF NOT EXISTS idx_test_suite_case_links_test_case_id ON test_suite_case_links(test_case_id)')
        
        self.conn.commit()
        print(f"Database created: {self.db_path}")
    
    def find_ts_files(self):
        """Find all .ts files in the Test Suites directory."""
        test_suites_dir = self.root_dir / "Test Suites"
        if not test_suites_dir.exists():
            raise FileNotFoundError(f"Test Suites directory not found: {test_suites_dir}")
        
        ts_files = list(test_suites_dir.rglob("*.ts"))
        print(f"Found {len(ts_files)} .ts files")
        return ts_files
    
    def parse_ts_file(self, file_path):
        """Parse a single .ts file and extract TestSuiteEntity, FilteringTestSuiteEntity, or TestSuiteCollectionEntity data."""
        try:
            tree = ET.parse(file_path)
            root = tree.getroot()
            
            if root.tag not in ["TestSuiteEntity", "FilteringTestSuiteEntity", "TestSuiteCollectionEntity"]:
                print(f"Warning: {file_path} is not a TestSuiteEntity, FilteringTestSuiteEntity, or TestSuiteCollectionEntity file")
                return None
            
            # Extract file metadata
            file_stat = file_path.stat()
            updated_at = datetime.fromtimestamp(file_stat.st_mtime).strftime('%Y-%m-%d %H:%M:%S')
            
            # Map suite types to UI-friendly aliases
            suite_type_mapping = {
                'TestSuiteEntity': 'Test Suite',
                'FilteringTestSuiteEntity': 'Dynamic Test Suite',
                'TestSuiteCollectionEntity': 'Test Suite Collection'
            }
            
            # Extract basic fields (common to all types)
            data = {
                'name': self.get_text(root, 'name'),
                'description': self.get_text(root, 'description'),
                'tags': self.get_text(root, 'tag'),
                'suite_type': root.tag,
                'suite_type_alias': suite_type_mapping.get(root.tag, root.tag),
                'relative_path': str(file_path.relative_to(self.root_dir)),
                'updated_at': updated_at
            }
            
            # Handle testSuiteGuid - TestSuiteCollectionEntity files don't have this field
            if root.tag == "TestSuiteCollectionEntity":
                # Generate a unique identifier for TestSuiteCollectionEntity based on file path
                import hashlib
                file_hash = hashlib.md5(str(file_path).encode()).hexdigest()[:8]
                data['test_suite_guid'] = f"collection_{file_hash}"
            else:
                data['test_suite_guid'] = self.get_text(root, 'testSuiteGuid')
            
            # Extract type-specific fields
            if root.tag in ["TestSuiteEntity", "FilteringTestSuiteEntity"]:
                # Fields specific to TestSuiteEntity and FilteringTestSuiteEntity
                data.update({
                    'is_rerun': self.get_text(root, 'isRerun').lower() == 'true',
                    'mail_recipient': self.get_text(root, 'mailRecipient'),
                    'number_of_rerun': int(self.get_text(root, 'numberOfRerun') or 0),
                    'page_load_timeout': int(self.get_text(root, 'pageLoadTimeout') or 30),
                    'page_load_timeout_default': self.get_text(root, 'pageLoadTimeoutDefault').lower() == 'true',
                    'rerun_failed_test_cases_only': self.get_text(root, 'rerunFailedTestCasesOnly').lower() == 'true',
                    'rerun_immediately': self.get_text(root, 'rerunImmediately').lower() == 'true'
                })
            elif root.tag == "TestSuiteCollectionEntity":
                # Fields specific to TestSuiteCollectionEntity
                data.update({
                    'delay_between_instances': int(self.get_text(root, 'delayBetweenInstances') or 0),
                    'execution_mode': self.get_text(root, 'executionMode'),
                    'max_concurrent_instances': int(self.get_text(root, 'maxConcurrentInstances') or 1)
                })
            
            # Process tags
            tags_list = []
            if data['tags']:
                tags_list = [tag.strip() for tag in data['tags'].split(',') if tag.strip()]
            data['tags_list'] = json.dumps(tags_list)
            
            # Extract filtering-specific fields for FilteringTestSuiteEntity
            if root.tag == "FilteringTestSuiteEntity":
                data['filtering_text'] = self.get_text(root, 'filteringText')
                data['filtering_built_in'] = self.get_text(root, 'filteringBuiltIn')
                data['filtering_extension'] = self.get_text(root, 'filteringExtension')
                data['filtering_plugin'] = self.get_text(root, 'filteringPlugin')
            else:
                data['filtering_text'] = None
                data['filtering_built_in'] = None
                data['filtering_extension'] = None
                data['filtering_plugin'] = None
            
            # Extract test case links for TestSuiteEntity
            test_case_links = []
            if root.tag == "TestSuiteEntity":
                for link in root.findall('testCaseLink'):
                    link_data = {
                        'link_guid': self.get_text(link, 'guid'),
                        'test_case_id': self.get_text(link, 'testCaseId'),
                        'is_reuse_driver': self.get_text(link, 'isReuseDriver').lower() == 'true',
                        'is_run': self.get_text(link, 'isRun').lower() == 'true',
                        'using_data_binding_at_test_suite_level': self.get_text(link, 'usingDataBindingAtTestSuiteLevel').lower() == 'true',
                        'variables': []
                    }
                    
                    # Extract variable links
                    for var_link in link.findall('variableLink'):
                        var_data = {
                            'variable_id': self.get_text(var_link, 'variableId'),
                            'test_data_link_id': self.get_text(var_link, 'testDataLinkId'),
                            'type': self.get_text(var_link, 'type'),
                            'value': self.get_text(var_link, 'value')
                        }
                        link_data['variables'].append(var_data)
                    
                    test_case_links.append(link_data)
            
            data['test_case_links'] = test_case_links
            
            # Extract test suite collection links for TestSuiteCollectionEntity
            test_suite_collection_links = []
            if root.tag == "TestSuiteCollectionEntity":
                for run_config in root.findall('testSuiteRunConfigurations/TestSuiteRunConfiguration'):
                    config_element = run_config.find('configuration')
                    collection_link_data = {
                        'referenced_suite_path': self.get_text(run_config, 'testSuiteEntity'),
                        'run_enabled': self.get_text(run_config, 'runEnabled').lower() == 'true',
                        'group_name': self.get_text(config_element, 'groupName') if config_element is not None else '',
                        'profile_name': self.get_text(config_element, 'profileName') if config_element is not None else '',
                        'require_configuration_data': self.get_text(config_element, 'requireConfigurationData').lower() == 'true' if config_element is not None else False,
                        'run_configuration_id': self.get_text(config_element, 'runConfigurationId') if config_element is not None else ''
                    }
                    test_suite_collection_links.append(collection_link_data)
            
            data['test_suite_collection_links'] = test_suite_collection_links
            
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
    
    def insert_test_suite(self, data):
        """Insert or update test suite data in database."""
        cursor = self.conn.cursor()
        
        # Check if test suite already exists by GUID
        cursor.execute('SELECT id FROM test_suites WHERE test_suite_guid = ?', (data['test_suite_guid'],))
        existing_record = cursor.fetchone()
        
        if existing_record:
            # Update existing test suite
            test_suite_id = existing_record[0]
            cursor.execute('''
                UPDATE test_suites SET 
                name = ?, description = ?, tags = ?, tags_list = ?, suite_type = ?, suite_type_alias = ?,
                relative_path = ?, is_rerun = ?, mail_recipient = ?, number_of_rerun = ?,
                page_load_timeout = ?, page_load_timeout_default = ?, rerun_failed_test_cases_only = ?,
                rerun_immediately = ?, filtering_text = ?, filtering_built_in = ?,
                filtering_extension = ?, filtering_plugin = ?, delay_between_instances = ?,
                execution_mode = ?, max_concurrent_instances = ?, updated_at = ?
                WHERE test_suite_guid = ?
            ''', (
                data['name'], data['description'], data['tags'], data['tags_list'], data['suite_type'], data['suite_type_alias'],
                data['relative_path'], data.get('is_rerun'), data.get('mail_recipient'), data.get('number_of_rerun'),
                data.get('page_load_timeout'), data.get('page_load_timeout_default'), data.get('rerun_failed_test_cases_only'),
                data.get('rerun_immediately'), data.get('filtering_text'), data.get('filtering_built_in'),
                data.get('filtering_extension'), data.get('filtering_plugin'), data.get('delay_between_instances'),
                data.get('execution_mode'), data.get('max_concurrent_instances'), data['updated_at'],
                data['test_suite_guid']
            ))
            
            # Clear existing related data to avoid duplicates
            cursor.execute('DELETE FROM test_suite_tags WHERE test_suite_id = ?', (test_suite_id,))
            cursor.execute('DELETE FROM test_suite_case_links WHERE test_suite_id = ?', (test_suite_id,))
            cursor.execute('DELETE FROM test_suite_collection_links WHERE collection_suite_id = ?', (test_suite_id,))
            
        else:
            # Insert new test suite record
            cursor.execute('''
                INSERT INTO test_suites 
                (name, description, tags, tags_list, suite_type, suite_type_alias, test_suite_guid, relative_path,
                 is_rerun, mail_recipient, number_of_rerun, page_load_timeout, page_load_timeout_default,
                 rerun_failed_test_cases_only, rerun_immediately, filtering_text, filtering_built_in,
                 filtering_extension, filtering_plugin, delay_between_instances, execution_mode,
                 max_concurrent_instances, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''', (
                data['name'], data['description'], data['tags'], data['tags_list'], data['suite_type'],
                data['suite_type_alias'], data['test_suite_guid'], data['relative_path'], data.get('is_rerun'), data.get('mail_recipient'),
                data.get('number_of_rerun'), data.get('page_load_timeout'), data.get('page_load_timeout_default'),
                data.get('rerun_failed_test_cases_only'), data.get('rerun_immediately'), data.get('filtering_text'),
                data.get('filtering_built_in'), data.get('filtering_extension'), data.get('filtering_plugin'),
                data.get('delay_between_instances'), data.get('execution_mode'), data.get('max_concurrent_instances'),
                data['updated_at']
            ))
            
            test_suite_id = cursor.lastrowid
        
        # Process tags
        if data['tags']:
            tags_list = [tag.strip() for tag in data['tags'].split(',') if tag.strip()]
            for tag in tags_list:
                # Insert or update tag (only increment count for new test suites)
                cursor.execute('''
                    INSERT OR IGNORE INTO suite_tags (tag_name, usage_count) VALUES (?, 1)
                ''', (tag,))
                
                # Only increment usage count if this is a new test suite
                if not existing_record:
                    cursor.execute('''
                        UPDATE suite_tags SET usage_count = usage_count + 1 WHERE tag_name = ?
                    ''', (tag,))
                
                # Get tag ID and create relationship
                cursor.execute('SELECT id FROM suite_tags WHERE tag_name = ?', (tag,))
                tag_id = cursor.fetchone()[0]
                
                cursor.execute('''
                    INSERT INTO test_suite_tags (test_suite_id, tag_id) 
                    VALUES (?, ?)
                ''', (test_suite_id, tag_id))
        
        # Process test case links (for TestSuiteEntity)
        if data['test_case_links']:
            for link in data['test_case_links']:
                # Try to find the corresponding test case in the test_cases table
                test_case_db_id = None
                if link['test_case_id']:
                    # Convert testCaseId path to relative_path format
                    # e.g., "Test Cases/Platform/Admin/..." -> "Test Cases/Platform/Admin/...tc"
                    relative_path = link['test_case_id'] + '.tc'
                    cursor.execute('SELECT id FROM test_cases WHERE relative_path = ?', (relative_path,))
                    result = cursor.fetchone()
                    if result:
                        test_case_db_id = result[0]
                
                cursor.execute('''
                    INSERT INTO test_suite_case_links 
                    (test_suite_id, link_guid, test_case_id, test_case_db_id, is_reuse_driver, is_run, 
                     using_data_binding_at_test_suite_level)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                ''', (
                    test_suite_id, link['link_guid'], link['test_case_id'], test_case_db_id,
                    link['is_reuse_driver'], link['is_run'], link['using_data_binding_at_test_suite_level']
                ))
                
                test_case_link_id = cursor.lastrowid
                
                # Process variables for this test case link
                for var in link['variables']:
                    cursor.execute('''
                        INSERT INTO test_suite_variables 
                        (test_case_link_id, variable_id, test_data_link_id, type, value)
                        VALUES (?, ?, ?, ?, ?)
                    ''', (
                        test_case_link_id, var['variable_id'], var['test_data_link_id'],
                        var['type'], var['value']
                    ))
        
        # Process filtering test suites (FilteringTestSuiteEntity) - create links for matching test cases
        elif data['suite_type'] == 'FilteringTestSuiteEntity' and data['filtering_text']:
            self._create_filtering_test_suite_links(test_suite_id, data['filtering_text'])
        
        # Process test suite collection links for TestSuiteCollectionEntity
        if data['suite_type'] == 'TestSuiteCollectionEntity':
            for collection_link in data['test_suite_collection_links']:
                cursor.execute('''
                    INSERT INTO test_suite_collection_links 
                    (collection_suite_id, referenced_suite_path, run_enabled, group_name, 
                     profile_name, require_configuration_data, run_configuration_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                ''', (
                    test_suite_id, collection_link['referenced_suite_path'], collection_link['run_enabled'],
                    collection_link['group_name'], collection_link['profile_name'], 
                    collection_link['require_configuration_data'], collection_link['run_configuration_id']
                ))
        
        return existing_record is not None
    
    def _create_filtering_test_suite_links(self, test_suite_id, filtering_text):
        """
        Create test_suite_case_links entries for FilteringTestSuiteEntity based on filtering criteria.
        
        Args:
            test_suite_id (int): ID of the test suite
            filtering_text (str): Filtering criteria text
        """
        cursor = self.conn.cursor()
        
        # Check if test_cases table exists
        cursor.execute('''
            SELECT name FROM sqlite_master 
            WHERE type='table' AND name='test_cases'
        ''')
        if not cursor.fetchone():
            return  # No test cases to link to
        
        # Parse filtering criteria
        filtering_criteria = self.parse_filtering_criteria(filtering_text)
        if not filtering_criteria:
            return
        
        # Build WHERE clause to find matching test cases
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
            return
        
        # Find matching test cases
        query = f"SELECT id, name, relative_path FROM test_cases WHERE {' AND '.join(where_conditions)}"
        cursor.execute(query, params)
        matching_test_cases = cursor.fetchall()
        
        # Create test_suite_case_links entries for each matching test case
        for test_case in matching_test_cases:
            test_case_db_id = test_case[0]
            test_case_name = test_case[1]
            relative_path = test_case[2]
            
            # Convert relative_path back to test_case_id format (remove .tc extension)
            test_case_id = relative_path[:-3] if relative_path.endswith('.tc') else relative_path
            
            # Create a synthetic link_guid for filtering-based links
            link_guid = f"filtering_{test_suite_id}_{test_case_db_id}"
            
            cursor.execute('''
                INSERT INTO test_suite_case_links 
                (test_suite_id, link_guid, test_case_id, test_case_db_id, is_reuse_driver, is_run, 
                 using_data_binding_at_test_suite_level)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            ''', (
                test_suite_id, link_guid, test_case_id, test_case_db_id,
                False, True, False  # Default values for filtering-based links
            ))
    
    def parse_filtering_criteria(self, filtering_text):
        """
        Parse filtering text from FilteringTestSuiteEntity to extract criteria.
        
        Example: "name=(AC-) tag=(api,) " 
        Returns: {'name': ['AC-'], 'tag': ['api']}
        """
        if not filtering_text:
            return {}
        
        criteria = {}
        
        # Pattern to match name=(value) or tag=(value1,value2)
        pattern = r'(\w+)=\(([^)]*)\)'
        matches = re.findall(pattern, filtering_text)
        
        for key, value in matches:
            if value:
                # Split by comma and clean up values
                values = [v.strip() for v in value.split(',') if v.strip()]
                criteria[key] = values
        
        return criteria
    
    def count_matching_test_cases(self, filtering_criteria):
        """
        Count how many test cases match the filtering criteria.
        
        Args:
            filtering_criteria (dict): Parsed criteria from filteringText
            
        Returns:
            int: Number of matching test cases
        """
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
    
    def analyze_filtering_test_suites(self):
        """
        Analyze all FilteringTestSuiteEntity test suites and show how many test cases match their criteria.
        """
        cursor = self.conn.cursor()
        
        # Check if test_cases table exists
        cursor.execute('''
            SELECT name FROM sqlite_master 
            WHERE type='table' AND name='test_cases'
        ''')
        if not cursor.fetchone():
            print("No test_cases table found - cannot analyze filtering criteria")
            return
        
        print("\n" + "="*60)
        print("FILTERING TEST SUITE ANALYSIS")
        print("="*60)
        
        # Get all FilteringTestSuiteEntity test suites
        cursor.execute('''
            SELECT name, filtering_text, relative_path 
            FROM test_suites 
            WHERE suite_type = 'FilteringTestSuiteEntity' 
            AND filtering_text IS NOT NULL AND filtering_text != ''
            ORDER BY name
        ''')
        
        filtering_suites = cursor.fetchall()
        
        if not filtering_suites:
            print("No FilteringTestSuiteEntity with filtering criteria found")
            return
        
        total_matches = 0
        
        for suite_name, filtering_text, relative_path in filtering_suites:
            print(f"\n📋 **{suite_name}**")
            print(f"   Path: {relative_path}")
            print(f"   Filtering Text: {filtering_text}")
            
            # Parse criteria
            criteria = self.parse_filtering_criteria(filtering_text)
            print(f"   Parsed Criteria: {criteria}")
            
            # Count matching test cases
            if criteria:
                match_count = self.count_matching_test_cases(criteria)
                total_matches += match_count
                print(f"   ✅ **Matching Test Cases: {match_count}**")
                
                # Show some example matches if there are any
                if match_count > 0 and match_count <= 5:
                    examples = self.get_matching_test_cases_examples(criteria, limit=5)
                    if examples:
                        print(f"   Examples:")
                        for example in examples:
                            print(f"     - {example}")
            else:
                print(f"   ⚠️  No valid criteria found")
        
        print(f"\n📊 **Summary**: {len(filtering_suites)} filtering test suites analyzed")
        print(f"🎯 **Total potential test case executions**: {total_matches}")
    
    def get_matching_test_cases_examples(self, filtering_criteria, limit=5):
        """
        Get example test cases that match the filtering criteria.
        """
        if not filtering_criteria:
            return []
        
        cursor = self.conn.cursor()
        
        # Build WHERE clause (same logic as count_matching_test_cases)
        where_conditions = []
        params = []
        
        if 'name' in filtering_criteria:
            name_conditions = []
            for name_pattern in filtering_criteria['name']:
                if name_pattern:
                    name_conditions.append("name LIKE ?")
                    params.append(f"{name_pattern}%")
            
            if name_conditions:
                where_conditions.append(f"({' OR '.join(name_conditions)})")
        
        if 'tag' in filtering_criteria:
            tag_conditions = []
            for tag in filtering_criteria['tag']:
                if tag:
                    tag_conditions.append("(tags LIKE ? OR tags LIKE ? OR tags LIKE ? OR tags = ?)")
                    params.extend([f"{tag},%", f"%,{tag},%", f"%,{tag}", tag])
            
            if tag_conditions:
                where_conditions.append(f"({' AND '.join(tag_conditions)})")
        
        if not where_conditions:
            return []
        
        # Execute query
        query = f"SELECT name FROM test_cases WHERE {' AND '.join(where_conditions)} LIMIT ?"
        params.append(limit)
        cursor.execute(query, params)
        return [row[0] for row in cursor.fetchall()]
    
    def get_directory_distribution(self, depth=2, base_path="Test Suites"):
        """
        Get directory distribution with configurable depth and dynamic partitioning.
        
        Args:
            depth (int): Number of directory levels to include after base_path
            base_path (str): Base path to analyze from (default: "Test Suites")
            
        Returns:
            list: Sorted list of (directory_path, count) tuples
        """
        cursor = self.conn.cursor()
        cursor.execute('SELECT relative_path FROM test_suites')
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
        """Extract all test suites and store in database."""
        print("Starting test suite extraction...")
        
        # Create database
        self.create_database()
        
        # Find all .ts files
        ts_files = self.find_ts_files()
        
        # Process each file
        processed = 0
        updated = 0
        errors = 0
        
        for file_path in ts_files:
            print(f"Processing: {file_path.relative_to(self.root_dir)}")
            
            data = self.parse_ts_file(file_path)
            if data:
                try:
                    is_update = self.insert_test_suite(data)
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
        print("TEST SUITE EXTRACTION SUMMARY")
        print("="*50)
        
        # Total test suites
        cursor.execute("SELECT COUNT(*) FROM test_suites")
        total_suites = cursor.fetchone()[0]
        print(f"Total test suites: {total_suites}")
        
        # Test suites by type
        cursor.execute('''
            SELECT suite_type, COUNT(*) 
            FROM test_suites 
            GROUP BY suite_type
        ''')
        print(f"\nTest suites by type:")
        type_mapping = {
            'TestSuiteEntity': 'Test Suite',
            'FilteringTestSuiteEntity': 'Dynamic Test Suite', 
            'TestSuiteCollectionEntity': 'Test Suite Collection'
        }
        for suite_type, count in cursor.fetchall():
            friendly_name = type_mapping.get(suite_type, suite_type)
            print(f"  {friendly_name}: {count}")
        
        # Total unique tags
        cursor.execute("SELECT COUNT(*) FROM suite_tags")
        total_tags = cursor.fetchone()[0]
        print(f"\nTotal unique tags: {total_tags}")
        
        # Top 10 most used tags
        if total_tags > 0:
            print(f"\nTop 10 most used tags:")
            cursor.execute('''
                SELECT tag_name, usage_count 
                FROM suite_tags 
                ORDER BY usage_count DESC 
                LIMIT 10
            ''')
            for tag, count in cursor.fetchall():
                print(f"  {tag}: {count}")
        
        # Test case links statistics
        cursor.execute("SELECT COUNT(*) FROM test_suite_case_links")
        total_links = cursor.fetchone()[0]
        print(f"\nTotal test case links: {total_links}")
        
        # Relationship statistics (if test_cases table exists)
        cursor.execute('''
            SELECT name FROM sqlite_master 
            WHERE type='table' AND name='test_cases'
        ''')
        if cursor.fetchone():
            cursor.execute('''
                SELECT COUNT(*) FROM test_suite_case_links 
                WHERE test_case_db_id IS NOT NULL
            ''')
            linked_cases = cursor.fetchone()[0]
            cursor.execute('''
                SELECT COUNT(*) FROM test_suite_case_links 
                WHERE test_case_db_id IS NULL
            ''')
            unlinked_cases = cursor.fetchone()[0]
            print(f"Test case relationships:")
            print(f"  Linked to test_cases table: {linked_cases}")
            print(f"  Unlinked (missing test cases): {unlinked_cases}")
        
        # Test suites by directory (configurable depth)
        print(f"\nTest suites by directory (2 levels):")
        directory_stats = self.get_directory_distribution(depth=2)
        for directory, count in directory_stats:
            print(f"  {directory}: {count}")
        
        # Analyze filtering test suites
        self.analyze_filtering_test_suites()
    
    def close(self):
        """Close database connection."""
        if self.conn:
            self.conn.close()


def main():
    """Main execution function."""
    parser = argparse.ArgumentParser(
        description="Extract Katalon test suites from .ts files into SQLite database"
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
        help="Path for the output SQLite database (default: <project_folder_name>.db - same as tc_extractor.py)"
    )
    
    args = parser.parse_args()
    
    # Convert to absolute path and resolve any relative paths
    project_path = Path(args.katalon_project_path).resolve()
    
    # Generate database name from project folder name if not specified
    # Use same naming convention as tc_extractor.py for shared database
    if args.db_path is None:
        db_name = f"{project_path.name}.db"
    else:
        db_name = args.db_path
    
    print(f"Katalon Test Suite Extractor")
    print(f"Project directory: {project_path}")
    print(f"Database will be created as: {db_name}")
    print("-" * 50)
    
    # Validate that the project path exists
    if not project_path.exists():
        print(f"Error: Project path does not exist: {project_path}")
        sys.exit(1)
    
    # Create extractor and run
    extractor = TestSuiteExtractor(project_path, db_name)
    
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
