@echo off
REM Katalon Test Case Splitter - Pure Java Version (Windows)
REM Usage: split-test-java.bat "Test Cases/path/to/TestCase" [steps-per-split]

setlocal EnableDelayedExpansion

REM Check if java is available
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    echo Please install Java 11 or higher.
    echo.
    echo Check: java -version
    exit /b 1
)

REM Check Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_STRING=%%g
)
set JAVA_VERSION_STRING=%JAVA_VERSION_STRING:"=%
for /f "delims=. tokens=1" %%v in ("%JAVA_VERSION_STRING%") do set JAVA_MAJOR=%%v

if %JAVA_MAJOR% LSS 11 (
    echo WARNING: Java 11+ recommended detected: Java %JAVA_MAJOR%
    echo The script may not work with older Java versions.
    echo.
)

REM Check if script file exists
set SCRIPT_PATH=%~dp0SplitTestCase.java
if not exist "%SCRIPT_PATH%" (
    echo ERROR: SplitTestCase.java not found!
    echo Expected location: %SCRIPT_PATH%
    exit /b 1
)

REM Show usage if no arguments
if "%~1"=="" (
    echo Usage: %~nx0 ^<test-case-path^> [steps-per-split]
    echo.
    echo Example:
    echo   %~nx0 "Test Cases/AI-Generated/UAT/TC4-Complete Application Process"
    echo   %~nx0 "Test Cases/AI-Generated/UAT/TC4-Complete Application Process" 500
    exit /b 1
)

REM Run the Java script
echo.
echo Running Pure Java splitter...
java "%SCRIPT_PATH%" %*
set EXIT_CODE=%ERRORLEVEL%

echo.
if %EXIT_CODE% EQU 0 (
    echo [32m✓ Test case split completed successfully![0m
) else (
    echo [31m✗ Test case split failed with exit code: %EXIT_CODE%[0m
)

exit /b %EXIT_CODE%
