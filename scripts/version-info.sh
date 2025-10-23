#!/bin/bash

# Script to display version information
# Usage: ./scripts/version-info.sh

echo "Kafka Management Library - Version Information"
echo "=============================================="

# Get current version from Maven
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Current Version: $CURRENT_VERSION"

# Get Git information
echo ""
echo "Git Information:"
echo "  Branch: $(git branch --show-current)"
echo "  Commit: $(git rev-parse --short HEAD)"
echo "  Last Commit: $(git log -1 --format='%ci')"

# Get tags
echo ""
echo "Git Tags:"
git tag -l | tail -5 | while read tag; do
    echo "  $tag"
done

# Check for uncommitted changes
echo ""
echo "Working Directory Status:"
if [ -n "$(git status --porcelain)" ]; then
    echo "  ⚠️  Has uncommitted changes:"
    git status --porcelain | sed 's/^/    /'
else
    echo "  ✅ Clean working directory"
fi

# Check for remote changes
echo ""
echo "Remote Status:"
LOCAL=$(git rev-parse @)
REMOTE=$(git rev-parse @{u} 2>/dev/null || echo "no-remote")
if [ "$REMOTE" = "no-remote" ]; then
    echo "  ⚠️  No remote tracking branch"
elif [ "$LOCAL" = "$REMOTE" ]; then
    echo "  ✅ Up to date with remote"
else
    echo "  ⚠️  Local branch differs from remote"
fi

# Maven project info
echo ""
echo "Maven Project Information:"
echo "  Group ID: $(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout)"
echo "  Artifact ID: $(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)"
echo "  Packaging: $(mvn help:evaluate -Dexpression=project.packaging -q -DforceStdout)"

# Check for available updates
echo ""
echo "Dependency Updates:"
echo "  Checking for available updates..."
mvn versions:display-dependency-updates -q 2>/dev/null | grep -E "(UPDATE|UPGRADE)" | head -5 || echo "  No updates available"

echo ""
echo "Quick Commands:"
echo "  Update version: ./scripts/update-version.sh <version>"
echo "  Create release: ./scripts/release.sh <version>"
echo "  Publish to Maven Central: ./scripts/publish-to-maven-central.sh <version>"
