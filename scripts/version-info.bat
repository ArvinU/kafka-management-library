@echo off
REM Script to display version information
REM Usage: scripts\version-info.bat

echo Kafka Management Library - Version Information
echo ==============================================

REM Get current version from Maven
for /f "tokens=*" %%i in ('mvn help:evaluate -Dexpression=project.version -q -DforceStdout') do set CURRENT_VERSION=%%i
echo Current Version: %CURRENT_VERSION%

REM Get Git information
echo.
echo Git Information:
for /f "tokens=*" %%i in ('git branch --show-current') do echo   Branch: %%i
for /f "tokens=*" %%i in ('git rev-parse --short HEAD') do echo   Commit: %%i
for /f "tokens=*" %%i in ('git log -1 --format=%%ci') do echo   Last Commit: %%i

REM Get tags
echo.
echo Git Tags:
git tag -l | findstr /v "^$" | tail -5

REM Check for uncommitted changes
echo.
echo Working Directory Status:
git status --porcelain > temp_status.txt
for /f %%i in (temp_status.txt) do (
    echo   ⚠️  Has uncommitted changes:
    git status --porcelain
    goto :status_done
)
echo   ✅ Clean working directory
:status_done
del temp_status.txt 2>nul

REM Check for remote changes
echo.
echo Remote Status:
git rev-parse @ > temp_local.txt
git rev-parse @{u} > temp_remote.txt 2>nul
if errorlevel 1 (
    echo   ⚠️  No remote tracking branch
) else (
    fc temp_local.txt temp_remote.txt >nul
    if errorlevel 1 (
        echo   ⚠️  Local branch differs from remote
    ) else (
        echo   ✅ Up to date with remote
    )
)
del temp_local.txt temp_remote.txt 2>nul

REM Maven project info
echo.
echo Maven Project Information:
for /f "tokens=*" %%i in ('mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout') do echo   Group ID: %%i
for /f "tokens=*" %%i in ('mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout') do echo   Artifact ID: %%i
for /f "tokens=*" %%i in ('mvn help:evaluate -Dexpression=project.packaging -q -DforceStdout') do echo   Packaging: %%i

REM Check for available updates
echo.
echo Dependency Updates:
echo   Checking for available updates...
mvn versions:display-dependency-updates -q 2>nul | findstr /i "update upgrade" | head -5
if errorlevel 1 echo   No updates available

echo.
echo Quick Commands:
echo   Update version: scripts\update-version.bat ^<version^>
echo   Create release: scripts\release.bat ^<version^>
echo   Publish to Maven Central: scripts\publish-to-maven-central.bat ^<version^>
