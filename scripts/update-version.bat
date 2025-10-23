@echo off
REM Script to update version across the project
REM Usage: scripts\update-version.bat <version>
REM Example: scripts\update-version.bat 1.0.1

setlocal enabledelayedexpansion

set VERSION=%1
if "%VERSION%"=="" (
    echo Usage: %0 ^<version^>
    echo Example: %0 1.0.1
    exit /b 1
)

echo Updating version to %VERSION%...

REM Get current version for reference
for /f "tokens=*" %%i in ('mvn help:evaluate -Dexpression=project.version -q -DforceStdout') do set CURRENT_VERSION=%%i
echo Current version: !CURRENT_VERSION!

REM Update pom.xml
echo 1. Updating pom.xml...
call mvn versions:set -DnewVersion=%VERSION%
if errorlevel 1 exit /b 1

REM Update scripts that reference version
echo 2. Updating scripts...
if exist "scripts\run-cli.sh" (
    powershell -Command "(Get-Content 'scripts\run-cli.sh') -replace 'kafka-management-library-!CURRENT_VERSION!', 'kafka-management-library-%VERSION%' | Set-Content 'scripts\run-cli.sh'"
    echo   - Updated scripts\run-cli.sh
)

if exist "scripts\run-cli.bat" (
    powershell -Command "(Get-Content 'scripts\run-cli.bat') -replace 'kafka-management-library-!CURRENT_VERSION!', 'kafka-management-library-%VERSION%' | Set-Content 'scripts\run-cli.bat'"
    echo   - Updated scripts\run-cli.bat
)

REM Update documentation
echo 3. Updating documentation...
if exist "README.md" (
    powershell -Command "(Get-Content 'README.md') -replace 'version^>!CURRENT_VERSION!^<', 'version^>%VERSION%^<' | Set-Content 'README.md'"
    powershell -Command "(Get-Content 'README.md') -replace '!CURRENT_VERSION!', '%VERSION%' | Set-Content 'README.md'"
    echo   - Updated README.md
)

if exist "MAVEN_CENTRAL_PUBLISHING_GUIDE.md" (
    powershell -Command "(Get-Content 'MAVEN_CENTRAL_PUBLISHING_GUIDE.md') -replace '!CURRENT_VERSION!', '%VERSION%' | Set-Content 'MAVEN_CENTRAL_PUBLISHING_GUIDE.md'"
    echo   - Updated MAVEN_CENTRAL_PUBLISHING_GUIDE.md
)

if exist "SCRIPTS_GUIDE.md" (
    powershell -Command "(Get-Content 'SCRIPTS_GUIDE.md') -replace '!CURRENT_VERSION!', '%VERSION%' | Set-Content 'SCRIPTS_GUIDE.md'"
    echo   - Updated SCRIPTS_GUIDE.md
)

if exist "MULTI_CLI_GUIDE.md" (
    powershell -Command "(Get-Content 'MULTI_CLI_GUIDE.md') -replace '!CURRENT_VERSION!', '%VERSION%' | Set-Content 'MULTI_CLI_GUIDE.md'"
    echo   - Updated MULTI_CLI_GUIDE.md
)

REM Update publish scripts
if exist "scripts\publish-to-maven-central.sh" (
    powershell -Command "(Get-Content 'scripts\publish-to-maven-central.sh') -replace 'VERSION=\${1:-.*}', 'VERSION=\${1:-%VERSION%}' | Set-Content 'scripts\publish-to-maven-central.sh'"
    echo   - Updated scripts\publish-to-maven-central.sh
)

if exist "scripts\publish-to-maven-central.bat" (
    powershell -Command "(Get-Content 'scripts\publish-to-maven-central.bat') -replace 'if \"%%VERSION%%\"==\"\" set VERSION=.*', 'if \"%%VERSION%%\"==\"\" set VERSION=%VERSION%' | Set-Content 'scripts\publish-to-maven-central.bat'"
    echo   - Updated scripts\publish-to-maven-central.bat
)

echo.
echo ✅ Version updated to %VERSION%
echo.
echo Next steps:
echo 1. Review changes: git diff
echo 2. Test the build: mvn clean compile test
echo 3. Commit changes: git add . ^&^& git commit -m "Update version to %VERSION%"
echo 4. Create release: scripts\release.bat %VERSION%
