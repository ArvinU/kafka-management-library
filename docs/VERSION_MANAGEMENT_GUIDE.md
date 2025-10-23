# Version Management Guide

This guide explains how to manage versions for the Kafka Management Library using semantic versioning and automated tools.

## Semantic Versioning

We follow [Semantic Versioning (SemVer)](https://semver.org/) format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes that are not backward compatible
- **MINOR**: New features that are backward compatible
- **PATCH**: Bug fixes that are backward compatible

### Examples

- `1.0.0` → `1.0.1` (Bug fix)
- `1.0.1` → `1.1.0` (New feature)
- `1.1.0` → `2.0.0` (Breaking change)

## Version Management Tools

### 1. Maven Versions Plugin

The Maven Versions Plugin helps manage version updates:

```bash
# Check for available updates
mvn versions:display-dependency-updates

# Check for plugin updates
mvn versions:display-plugin-updates

# Update to next version
mvn versions:set -DnewVersion=1.0.1

# Revert version changes
mvn versions:revert
```

### 2. Git Tags for Releases

Use Git tags to mark releases:

```bash
# Create a release tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# List all tags
git tag -l

# Delete a tag (if needed)
git tag -d v1.0.0
git push origin --delete v1.0.0
```

## Automated Version Management

### 1. Version Update Script

Create a script to automate version updates:

```bash
#!/bin/bash
# scripts/update-version.sh

VERSION=$1
if [ -z "$VERSION" ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.1"
    exit 1
fi

echo "Updating version to $VERSION..."

# Update pom.xml
mvn versions:set -DnewVersion=$VERSION

# Update scripts that reference version
sed -i "s/kafka-management-library-1\.0\.0/kafka-management-library-$VERSION/g" scripts/run-cli.sh
sed -i "s/kafka-management-library-1\.0\.0/kafka-management-library-$VERSION/g" scripts/run-cli.bat

# Update documentation
sed -i "s/version>1\.0\.0</version>$VERSION</g" README.md
sed -i "s/1\.0\.0/$VERSION/g" MAVEN_CENTRAL_PUBLISHING_GUIDE.md

echo "Version updated to $VERSION"
echo "Don't forget to:"
echo "1. git add ."
echo "2. git commit -m \"Release version $VERSION\""
echo "3. git tag -a v$VERSION -m \"Release version $VERSION\""
echo "4. git push origin v$VERSION"
```

### 2. Release Process Script

Create a comprehensive release script:

```bash
#!/bin/bash
# scripts/release.sh

VERSION=$1
if [ -z "$VERSION" ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.1"
    exit 1
fi

echo "Starting release process for version $VERSION..."

# Check if we're on master branch
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "master" ]; then
    echo "Error: Must be on master branch. Current branch: $CURRENT_BRANCH"
    exit 1
fi

# Check if working directory is clean
if [ -n "$(git status --porcelain)" ]; then
    echo "Error: Working directory is not clean. Please commit or stash changes."
    exit 1
fi

# Update version
echo "1. Updating version to $VERSION..."
mvn versions:set -DnewVersion=$VERSION

# Update scripts
echo "2. Updating scripts..."
sed -i "s/kafka-management-library-1\.0\.0/kafka-management-library-$VERSION/g" scripts/run-cli.sh
sed -i "s/kafka-management-library-1\.0\.0/kafka-management-library-$VERSION/g" scripts/run-cli.bat

# Update documentation
echo "3. Updating documentation..."
sed -i "s/version>1\.0\.0</version>$VERSION</g" README.md
sed -i "s/1\.0\.0/$VERSION/g" MAVEN_CENTRAL_PUBLISHING_GUIDE.md

# Build and test
echo "4. Building and testing..."
mvn clean compile test
if [ $? -ne 0 ]; then
    echo "Error: Build or tests failed"
    exit 1
fi

# Commit changes
echo "5. Committing changes..."
git add .
git commit -m "Release version $VERSION"

# Create tag
echo "6. Creating tag v$VERSION..."
git tag -a v$VERSION -m "Release version $VERSION"

# Push changes
echo "7. Pushing changes..."
git push origin master
git push origin v$VERSION

echo "✅ Release $VERSION completed successfully!"
echo ""
echo "Next steps:"
echo "1. Go to GitHub and create a release from tag v$VERSION"
echo "2. Publish to Maven Central: ./scripts/publish-to-maven-central.sh $VERSION"
echo "3. Update CHANGELOG.md with release notes"
```

## Version Management Best Practices

### 1. Pre-Release Versions

For development and testing, use pre-release versions:

```bash
# Alpha version (internal testing)
mvn versions:set -DnewVersion=1.1.0-alpha.1

# Beta version (public testing)
mvn versions:set -DnewVersion=1.1.0-beta.1

# Release candidate
mvn versions:set -DnewVersion=1.1.0-rc.1
```

### 2. Version Ranges in Dependencies

Use version ranges for dependencies to get automatic updates:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>[3.4.0,3.5.0)</version>
</dependency>
```

### 3. Property-Based Versioning

Use Maven properties for consistent versioning:

```xml
<properties>
    <kafka.version>3.4.1</kafka.version>
    <confluent.version>7.4.0</confluent.version>
</properties>

<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>${kafka.version}</version>
</dependency>
```

## GitHub Actions for Automated Versioning

### 1. Automated Release on Tag

Create `.github/workflows/release.yml`:

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up JDK 8
      uses: actions/setup-java@v4
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Extract version from tag
      id: version
      run: echo "VERSION=${GITHUB_REF#refs/tags/v}" >> $GITHUB_OUTPUT
    
    - name: Build and test
      run: mvn clean compile test
    
    - name: Create GitHub Release
      uses: actions/create-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: ${{ github.ref }}
        release_name: Release ${{ steps.version.outputs.VERSION }}
        body: |
          ## Changes in this Release
          
          - Bug fixes and improvements
          - See [CHANGELOG.md](CHANGELOG.md) for detailed changes
          
          ## Usage
          
          ```xml
          <dependency>
              <groupId>io.github.arvinu</groupId>
              <artifactId>kafka-management-library</artifactId>
              <version>${{ steps.version.outputs.VERSION }}</version>
          </dependency>
          ```
        draft: false
        prerelease: false
```

### 2. Automated Version Bumping

Create `.github/workflows/version-bump.yml`:

```yaml
name: Version Bump

on:
  workflow_dispatch:
    inputs:
      version_type:
        description: 'Version bump type'
        required: true
        default: 'patch'
        type: choice
        options:
        - patch
        - minor
        - major

jobs:
  version-bump:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        token: ${{ secrets.GITHUB_TOKEN }}
    
    - name: Get current version
      id: current-version
      run: |
        VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
        echo "CURRENT_VERSION=$VERSION" >> $GITHUB_OUTPUT
    
    - name: Calculate new version
      id: new-version
      run: |
        CURRENT=${{ steps.current-version.outputs.CURRENT_VERSION }}
        TYPE=${{ github.event.inputs.version_type }}
        
        # Remove -SNAPSHOT if present
        CLEAN_VERSION=${CURRENT%-SNAPSHOT}
        
        # Split version into parts
        IFS='.' read -ra VERSION_PARTS <<< "$CLEAN_VERSION"
        MAJOR=${VERSION_PARTS[0]}
        MINOR=${VERSION_PARTS[1]}
        PATCH=${VERSION_PARTS[2]}
        
        case $TYPE in
          major)
            NEW_VERSION=$((MAJOR + 1)).0.0
            ;;
          minor)
            NEW_VERSION=$MAJOR.$((MINOR + 1)).0
            ;;
          patch)
            NEW_VERSION=$MAJOR.$MINOR.$((PATCH + 1))
            ;;
        esac
        
        echo "NEW_VERSION=$NEW_VERSION" >> $GITHUB_OUTPUT
    
    - name: Update version
      run: |
        mvn versions:set -DnewVersion=${{ steps.new-version.outputs.NEW_VERSION }}
        git config --local user.email "action@github.com"
        git config --local user.name "GitHub Action"
        git add pom.xml
        git commit -m "Bump version to ${{ steps.new-version.outputs.NEW_VERSION }}"
        git push
```

## Changelog Management

### 1. Keep a CHANGELOG.md

Create and maintain a `CHANGELOG.md` file:

```markdown
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- New features

### Changed
- Changes to existing functionality

### Deprecated
- Soon-to-be removed features

### Removed
- Removed features

### Fixed
- Bug fixes

### Security
- Security improvements

## [1.0.0] - 2024-01-01

### Added
- Initial release
- Multi-broker support
- SSL JKS support
- CLI interface
```

### 2. Automated Changelog Generation

Use tools like `conventional-changelog` or `git-changelog`:

```bash
# Install conventional-changelog
npm install -g conventional-changelog-cli

# Generate changelog
conventional-changelog -p angular -i CHANGELOG.md -s
```

## Version Management Checklist

### Before Release

- [ ] Update version in `pom.xml`
- [ ] Update version in scripts (`run-cli.sh`, `run-cli.bat`)
- [ ] Update version in documentation (`README.md`, guides)
- [ ] Update `CHANGELOG.md` with release notes
- [ ] Run tests: `mvn clean test`
- [ ] Build project: `mvn clean package`
- [ ] Commit changes: `git add . && git commit -m "Release version X.Y.Z"`
- [ ] Create tag: `git tag -a vX.Y.Z -m "Release version X.Y.Z"`
- [ ] Push changes: `git push origin master && git push origin vX.Y.Z`

### After Release

- [ ] Create GitHub release from tag
- [ ] Publish to Maven Central
- [ ] Update documentation
- [ ] Announce release (if significant)

## Quick Commands

```bash
# Check current version
mvn help:evaluate -Dexpression=project.version -q -DforceStdout

# Update to next patch version
mvn versions:set -DnewVersion=1.0.1

# Update to next minor version
mvn versions:set -DnewVersion=1.1.0

# Update to next major version
mvn versions:set -DnewVersion=2.0.0

# Create release
./scripts/release.sh 1.0.1

# Publish to Maven Central
./scripts/publish-to-maven-central.sh 1.0.1
```

This version management strategy ensures consistent, automated versioning with proper release processes and documentation.
