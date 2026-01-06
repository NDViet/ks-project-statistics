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
native-image --no-fallback -jar "%JAR%" -H:Name=%APP_NAME%
if errorlevel 1 exit /b 1

echo.
echo Done. Native exe:
if not exist "%BINARY%" mkdir "%BINARY%"
move "%ROOT%\%APP_NAME%.exe" "%BINARY%\"
if errorlevel 1 (
    echo ERROR: Failed to move exe to binary folder
    exit /b 1
)
echo   %BINARY%\%APP_NAME%.exe
exit /b 0
