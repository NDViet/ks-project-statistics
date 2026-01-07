@echo off
setlocal EnableExtensions

REM Build a native Windows exe using GraalVM native-image.
REM Output: SplitTestCase.exe in the project root.

set "ROOT=%~dp0"
set "APP_NAME=SplitTestCase"
set "MAIN_CLASS=SplitTestCase"
set "SRC=%ROOT%SplitTestCase.java"
set "BUILD=%ROOT%build"
set "BINARY=%ROOT%binary"
set "JAR=%BUILD%\%APP_NAME%.jar"

where native-image >nul 2>nul
if errorlevel 1 (
    echo ERROR: native-image not found. Install GraalVM and run: gu install native-image
    exit /b 1
)

where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: javac not found. Install a JDK - GraalVM recommended.
    exit /b 1
)

if not exist "%SRC%" (
    echo ERROR: %SRC% not found.
    exit /b 1
)

if not exist "%BUILD%" mkdir "%BUILD%"

echo Compiling...
javac -d "%BUILD%" "%SRC%"
if errorlevel 1 exit /b 1

echo Creating JAR...
jar --create --file "%JAR%" --main-class %MAIN_CLASS% -C "%BUILD%" .
if errorlevel 1 exit /b 1

echo Building native exe...
call native-image --no-fallback -jar "%JAR%" -H:Name=%APP_NAME%
if errorlevel 1 (
    echo ERROR: native-image build failed
    exit /b 1
)

echo.
echo Build completed, checking output...
if not exist "%BINARY%" mkdir "%BINARY%"

if exist "%APP_NAME%.exe" (
    echo Found %APP_NAME%.exe in current directory, moving to binary folder...
    move /Y "%APP_NAME%.exe" "%BINARY%\%APP_NAME%.exe"
    if errorlevel 1 (
        echo ERROR: Failed to move exe to binary folder
        exit /b 1
    )
    echo.
    echo Done! Native exe created at:
    echo   %BINARY%\%APP_NAME%.exe
) else (
    echo ERROR: %APP_NAME%.exe not found after build
    dir *.exe
    exit /b 1
)

exit /b 0
