@echo off
REM Kafka Management Library - Release Publishing Script
REM Usage: publish-release.bat <version>
REM Example: publish-release.bat 1.0.3

setlocal enabledelayedexpansion

REM Check if version is provided
if "%~1"=="" (
    echo [ERROR] Version number is required!
    echo Usage: %0 ^<version^> [release]
    echo Example: %0 1.0.3
    echo Example: %0 1.0.3 release
    exit /b 1
)

set VERSION=%~1
set CREATE_RELEASE=%~2
set CURRENT_DIR=%cd%
set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..

echo [INFO] Starting release process for version: %VERSION%
echo [INFO] Project directory: %PROJECT_DIR%

REM Change to project directory
cd /d "%PROJECT_DIR%"

REM Step 1: Update version
echo [INFO] Step 1: Updating version to %VERSION%...
if exist "scripts\update-version.bat" (
    call scripts\update-version.bat %VERSION%
    if !errorlevel! equ 0 (
        echo [SUCCESS] Version updated to %VERSION%
    ) else (
        echo [ERROR] Failed to update version
        exit /b 1
    )
) else (
    echo [ERROR] update-version.bat script not found!
    exit /b 1
)

REM Step 2: Test build
echo [INFO] Step 2: Testing build...
call mvn clean package -DskipTests
if !errorlevel! equ 0 (
    echo [SUCCESS] Build test passed
) else (
    echo [ERROR] Build test failed
    exit /b 1
)

REM Step 3: Publish to GitHub Packages
echo [INFO] Step 3: Publishing to GitHub Packages...
call mvn clean deploy -DskipTests
if !errorlevel! equ 0 (
    echo [SUCCESS] Successfully published to GitHub Packages
) else (
    echo [ERROR] Failed to publish to GitHub Packages
    exit /b 1
)

REM Step 4: Git operations
echo [INFO] Step 4: Committing and tagging release...

REM Check if we're in a git repository
if not exist ".git" (
    echo [WARNING] Not in a git repository, skipping git operations
    goto :summary
)

REM Check if there are uncommitted changes
git status --porcelain >nul 2>&1
if !errorlevel! equ 0 (
    for /f %%i in ('git status --porcelain ^| find /c /v ""') do set CHANGES=%%i
    if !CHANGES! gtr 0 (
        echo [INFO] Committing changes...
        git add .
        git commit -m "Release version %VERSION%"
        echo [SUCCESS] Changes committed
    ) else (
        echo [WARNING] No changes to commit
    )
)

REM Create and push tag
echo [INFO] Creating and pushing tag v%VERSION%...
git tag v%VERSION%
git push origin master
git push origin v%VERSION%
echo [SUCCESS] Tag v%VERSION% created and pushed

REM Create GitHub release if requested
if "%CREATE_RELEASE%"=="release" (
    echo [INFO] Creating GitHub release...
    where gh >nul 2>&1
    if !errorlevel! equ 0 (
        gh release create "v%VERSION%" --title "Release %VERSION%" --notes "Release %VERSION% of Kafka Management Library"
        if !errorlevel! equ 0 (
            echo [SUCCESS] GitHub release created successfully
        ) else (
            echo [WARNING] Failed to create GitHub release (check gh CLI installation and authentication)
        )
    ) else (
        echo [WARNING] GitHub CLI (gh) not found. Install it to create releases automatically.
        echo [INFO] Manual release creation:
        echo 1. Go to: https://github.com/arvinu/kafka-management-library/releases
        echo 2. Click 'Create a new release'
        echo 3. Select tag 'v%VERSION%'
        echo 4. Add release title and notes
    )
)

:summary
REM Step 5: Summary
echo.
echo [SUCCESS] 🎉 Release %VERSION% completed successfully!
echo [INFO] 📦 Published to: https://maven.pkg.github.com/arvinu/kafka-management-library
echo [INFO] 🏷️  Tag: v%VERSION%
echo [INFO] 📋 Maven coordinates: io.github.arvinu:kafka-management-library:%VERSION%

REM Step 6: Next steps
echo.
if "%CREATE_RELEASE%"=="release" (
    echo [INFO] Next steps:
    echo 1. Update your README with the new version
    echo 2. Notify users about the new release
    echo 3. Review the GitHub release: https://github.com/arvinu/kafka-management-library/releases
) else (
    echo [INFO] Next steps:
    echo 1. Create a GitHub release with release notes
    echo 2. Update your README with the new version
    echo 3. Notify users about the new release
    echo.
    echo [INFO] To create a GitHub release, run:
    echo gh release create v%VERSION% --title "Release %VERSION%" --notes "Release notes here"
    echo.
    echo [INFO] Or run this script with 'release' parameter:
    echo %0 %VERSION% release
)

echo [SUCCESS] Release process completed! 🚀

endlocal
