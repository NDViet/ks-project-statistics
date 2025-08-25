-- Katalon Test Case Extractor Database Schema
-- This SQL script creates the database structure used by tc_extractor.py
-- Execute with: sqlite3 your_database.db < init_katalon_db.sql

-- Main test cases table
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
);

-- Tags table for normalized tag storage
CREATE TABLE IF NOT EXISTS tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tag_name TEXT UNIQUE NOT NULL,
    usage_count INTEGER DEFAULT 1
);

-- Test case tags relationship table (many-to-many)
CREATE TABLE IF NOT EXISTS test_case_tags (
    test_case_id INTEGER,
    tag_id INTEGER,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id),
    FOREIGN KEY (tag_id) REFERENCES tags (id),
    PRIMARY KEY (test_case_id, tag_id)
);

-- Test data links table
CREATE TABLE IF NOT EXISTS test_data_links (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    test_case_id INTEGER,
    combination_type TEXT,
    link_id TEXT,
    iteration_type TEXT,
    iteration_value TEXT,
    test_data_id TEXT,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id)
);

-- Variables table
CREATE TABLE IF NOT EXISTS test_variables (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    test_case_id INTEGER,
    variable_id TEXT,
    name TEXT,
    default_value TEXT,
    description TEXT,
    masked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_test_cases_guid ON test_cases(test_case_guid);
CREATE INDEX IF NOT EXISTS idx_test_cases_path ON test_cases(relative_path);
CREATE INDEX IF NOT EXISTS idx_test_cases_tags ON test_cases(has_test_data_links, has_variables);
CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(tag_name);
CREATE INDEX IF NOT EXISTS idx_test_data_links_case_id ON test_data_links(test_case_id);
CREATE INDEX IF NOT EXISTS idx_test_variables_case_id ON test_variables(test_case_id);

-- Display schema information
.schema
