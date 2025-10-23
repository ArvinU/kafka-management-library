#!/bin/bash

# Script to update version across the project
# Usage: ./scripts/update-version.sh <version>
# Example: ./scripts/update-version.sh 1.0.1

set -e

VERSION=$1
if [ -z "$VERSION" ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.1"
    exit 1
fi

echo "Updating version to $VERSION..."

# Get current version for reference
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Current version: $CURRENT_VERSION"

# Update pom.xml
echo "1. Updating pom.xml..."
mvn versions:set -DnewVersion=$VERSION

# Update scripts that reference version
echo "2. Updating scripts..."
if [ -f "scripts/run-cli.sh" ]; then
    sed -i.bak "s/kafka-management-library-${CURRENT_VERSION}/kafka-management-library-${VERSION}/g" scripts/run-cli.sh
    echo "  - Updated scripts/run-cli.sh"
fi

if [ -f "scripts/run-cli.bat" ]; then
    sed -i.bak "s/kafka-management-library-${CURRENT_VERSION}/kafka-management-library-${VERSION}/g" scripts/run-cli.bat
    echo "  - Updated scripts/run-cli.bat"
fi

# Update documentation
echo "3. Updating documentation..."
if [ -f "docs/README.md" ]; then
    sed -i.bak "s/version>$CURRENT_VERSION</version>${VERSION}</g" docs/README.md
    sed -i.bak "s/${CURRENT_VERSION}/${VERSION}/g" docs/README.md
    echo "  - Updated docs/README.md"
fi

if [ -f "docs/MAVEN_CENTRAL_PUBLISHING_GUIDE.md" ]; then
    sed -i.bak "s/${CURRENT_VERSION}/${VERSION}/g" docs/MAVEN_CENTRAL_PUBLISHING_GUIDE.md
    echo "  - Updated docs/MAVEN_CENTRAL_PUBLISHING_GUIDE.md"
fi

if [ -f "docs/SCRIPTS_GUIDE.md" ]; then
    sed -i.bak "s/${CURRENT_VERSION}/${VERSION}/g" docs/SCRIPTS_GUIDE.md
    echo "  - Updated docs/SCRIPTS_GUIDE.md"
fi

if [ -f "docs/MULTI_CLI_GUIDE.md" ]; then
    sed -i.bak "s/${CURRENT_VERSION}/${VERSION}/g" docs/MULTI_CLI_GUIDE.md
    echo "  - Updated docs/MULTI_CLI_GUIDE.md"
fi

# Update publish scripts
if [ -f "scripts/publish-to-maven-central.sh" ]; then
    sed -i.bak "s/VERSION=\${1:-.*}/VERSION=\${1:-${VERSION}}/g" scripts/publish-to-maven-central.sh
    echo "  - Updated scripts/publish-to-maven-central.sh"
fi

if [ -f "scripts/publish-to-maven-central.bat" ]; then
    sed -i.bak "s/set VERSION=%1/set VERSION=%1/g" scripts/publish-to-maven-central.bat
    sed -i.bak "s/if \"%VERSION%\"==\"\" set VERSION=.*/if \"%VERSION%\"==\"\" set VERSION=${VERSION}/g" scripts/publish-to-maven-central.bat
    echo "  - Updated scripts/publish-to-maven-central.bat"
fi

echo ""
echo "✅ Version updated to $VERSION"
echo ""
echo "Next steps:"
echo "1. Review changes: git diff"
echo "2. Test the build: mvn clean compile test"
echo "3. Commit changes: git add . && git commit -m \"Update version to $VERSION\""
echo "4. Create release: ./scripts/release.sh $VERSION"
