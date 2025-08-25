@echo off
setlocal enabledelayedexpansion

REM Automation Test Project Analysis Bootstrap Script
REM This script runs the complete analysis pipeline for a Katalon Studio project
REM Usage: bootstrap.bat <project_path>

REM Function to print colored output (Windows doesn't support colors easily, so we'll use plain text)
goto :main

:print_status
echo [INFO] %~1
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

:usage
echo Usage: %~nx0 ^<project_path^>
echo.
echo Arguments:
echo   project_path    Path to the Katalon Studio project directory
echo.
echo Example:
echo   %~nx0 ..\project_path
echo   %~nx0 C:\path\to\katalon\project
goto :eof

:main
REM Check if argument is provided
if "%~1"=="" (
    call :print_error "No project path provided"
    call :usage
    exit /b 1
)

set "PROJECT_PATH=%~1"

REM Check if project path exists
if not exist "%PROJECT_PATH%" (
    call :print_error "Project path does not exist: %PROJECT_PATH%"
    exit /b 1
)

REM Get absolute path and project name
for %%i in ("%PROJECT_PATH%") do (
    set "PROJECT_PATH=%%~fi"
    set "PROJECT_NAME=%%~ni"
)

call :print_status "Starting Automation Test Project Analysis Pipeline"
call :print_status "Project: !PROJECT_NAME!"
call :print_status "Path: !PROJECT_PATH!"
echo ==================================================

REM Get script directory (where this bootstrap script is located)
set "SCRIPT_DIR=%~dp0"
set "SCRIPTS_PATH=%SCRIPT_DIR%scripts"

REM Check if scripts directory exists
if not exist "%SCRIPTS_PATH%" (
    call :print_error "Scripts directory not found: %SCRIPTS_PATH%"
    exit /b 1
)

REM Step 1: Extract Test Cases
call :print_status "Step 1/3: Extracting Test Cases (.tc files)"
python "%SCRIPTS_PATH%\tc_extractor.py" "%PROJECT_PATH%"
if !errorlevel! neq 0 (
    call :print_error "Test Cases extraction failed"
    exit /b 1
)
call :print_success "Test Cases extraction completed"
echo.

REM Step 2: Extract Test Suites
call :print_status "Step 2/3: Extracting Test Suites (.ts files)"
python "%SCRIPTS_PATH%\ts_extractor.py" "%PROJECT_PATH%"
if !errorlevel! neq 0 (
    call :print_error "Test Suites extraction failed"
    exit /b 1
)
call :print_success "Test Suites extraction completed"
echo.

REM Step 3: Generate Automation Progress Report
call :print_status "Step 3/3: Generating Automation Progress Report"
python "%SCRIPTS_PATH%\automation_progress_report.py" "!PROJECT_NAME!.db"
if !errorlevel! neq 0 (
    call :print_error "Automation Progress Report generation failed"
    exit /b 1
)
call :print_success "Automation Progress Report generated"
echo.

echo ==================================================
call :print_success "Katalon Studio Project Analysis Pipeline completed successfully!"
call :print_status "Database created: !PROJECT_NAME!.db"
call :print_status "Check the generated reports and database for analysis results"

endlocal
