@echo off
REM Script to create a release
REM Usage: scripts\release.bat <version>
REM Example: scripts\release.bat 1.0.1

setlocal enabledelayedexpansion

set VERSION=%1
if "%VERSION%"=="" (
    echo Usage: %0 ^<version^>
    echo Example: %0 1.0.1
    exit /b 1
)

echo Starting release process for version %VERSION%...

REM Check if we're on master branch
for /f "tokens=*" %%i in ('git branch --show-current') do set CURRENT_BRANCH=%%i
if not "%CURRENT_BRANCH%"=="master" (
    echo Error: Must be on master branch. Current branch: %CURRENT_BRANCH%
    exit /b 1
)

REM Check if working directory is clean
git status --porcelain > temp_status.txt
for /f %%i in (temp_status.txt) do (
    echo Error: Working directory is not clean. Please commit or stash changes.
    echo Uncommitted changes:
    git status --porcelain
    del temp_status.txt
    exit /b 1
)
del temp_status.txt

REM Check if tag already exists
git tag -l | findstr "^v%VERSION%$" >nul
if not errorlevel 1 (
    echo Error: Tag v%VERSION% already exists
    exit /b 1
)

REM Update version
echo 1. Updating version to %VERSION%...
call scripts\update-version.bat %VERSION%
if errorlevel 1 exit /b 1

REM Build and test
echo 2. Building and testing...
call mvn clean compile test
if errorlevel 1 (
    echo Error: Build or tests failed
    exit /b 1
)

REM Package
echo 3. Packaging...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Error: Packaging failed
    exit /b 1
)

REM Commit changes
echo 4. Committing changes...
git add .
git commit -m "Release version %VERSION%"

REM Create tag
echo 5. Creating tag v%VERSION%...
git tag -a v%VERSION% -m "Release version %VERSION%"

REM Push changes
echo 6. Pushing changes...
git push origin master
git push origin v%VERSION%

echo.
echo ✅ Release %VERSION% completed successfully!
echo.
echo Next steps:
echo 1. Go to GitHub and create a release from tag v%VERSION%
echo 2. Add release notes to the GitHub release
echo 3. Publish to Maven Central: scripts\publish-to-maven-central.bat %VERSION%
echo 4. Update docs\CHANGELOG.md with release notes (if you have one)
echo.
echo GitHub release URL: https://github.com/ArvinU/kafka-management-library/releases/new?tag=v%VERSION%
