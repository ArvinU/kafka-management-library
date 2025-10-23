#!/bin/bash

# Script to create a release
# Usage: ./scripts/release.sh <version>
# Example: ./scripts/release.sh 1.0.1

set -e

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
    echo "Uncommitted changes:"
    git status --porcelain
    exit 1
fi

# Check if tag already exists
if git tag -l | grep -q "^v$VERSION$"; then
    echo "Error: Tag v$VERSION already exists"
    exit 1
fi

# Update version
echo "1. Updating version to $VERSION..."
./scripts/update-version.sh $VERSION

# Build and test
echo "2. Building and testing..."
mvn clean compile test
if [ $? -ne 0 ]; then
    echo "Error: Build or tests failed"
    exit 1
fi

# Package
echo "3. Packaging..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Error: Packaging failed"
    exit 1
fi

# Commit changes
echo "4. Committing changes..."
git add .
git commit -m "Release version $VERSION"

# Create tag
echo "5. Creating tag v$VERSION..."
git tag -a v$VERSION -m "Release version $VERSION"

# Push changes
echo "6. Pushing changes..."
git push origin master
git push origin v$VERSION

echo ""
echo "✅ Release $VERSION completed successfully!"
echo ""
echo "Next steps:"
echo "1. Go to GitHub and create a release from tag v$VERSION"
echo "2. Add release notes to the GitHub release"
echo "3. Publish to Maven Central: ./scripts/publish-to-maven-central.sh $VERSION"
echo "4. Update docs/CHANGELOG.md with release notes (if you have one)"
echo ""
echo "GitHub release URL: https://github.com/ArvinU/kafka-management-library/releases/new?tag=v$VERSION"
