#!/bin/bash

# Script to publish Kafka Management Library to Maven Central
# Usage: ./scripts/publish-to-maven-central.sh [version]

set -e

VERSION=${1:-1.0.1}
echo "Publishing version: $VERSION"

# Check if required environment variables are set
if [ -z "$OSSRH_USERNAME" ] || [ -z "$OSSRH_TOKEN" ]; then
    echo "Error: OSSRH_USERNAME and OSSRH_TOKEN environment variables must be set"
    echo "Set them in your ~/.m2/settings.xml or export them:"
    echo "export OSSRH_USERNAME=your-username"
    echo "export OSSRH_TOKEN=your-token"
    exit 1
fi

# Check if GPG is configured
if ! command -v gpg &> /dev/null; then
    echo "Error: GPG is not installed. Please install GPG first."
    exit 1
fi

# Verify GPG key is available
if ! gpg --list-secret-keys --keyid-format LONG | grep -q "sec"; then
    echo "Error: No GPG secret keys found. Please import your GPG key first."
    echo "gpg --import your-private-key.asc"
    exit 1
fi

echo "Starting Maven Central publishing process..."

# Clean and compile
echo "1. Cleaning and compiling..."
mvn clean compile

# Run tests
echo "2. Running tests..."
mvn test

# Generate sources and javadoc
echo "3. Generating sources and javadoc..."
mvn source:jar javadoc:jar

# Deploy to staging repository
echo "4. Deploying to Sonatype staging repository..."
mvn clean deploy -P release

echo "✅ Deployment completed successfully!"
echo ""
echo "Next steps:"
echo "1. Go to https://s01.oss.sonatype.org/"
echo "2. Login with your Sonatype credentials"
echo "3. Navigate to 'Staging Repositories'"
echo "4. Find your repository (iogithubarvinu-XXXX)"
echo "5. Select it and click 'Close'"
echo "6. Once closed successfully, click 'Release'"
echo "7. Confirm the release"
echo ""
echo "After release, your artifacts will be available at:"
echo "https://repo1.maven.org/maven2/io/github/arvinu/kafka-management-library/"
echo ""
echo "Users can then add your library to their pom.xml:"
echo "<dependency>"
echo "    <groupId>io.github.arvinu</groupId>"
echo "    <artifactId>kafka-management-library</artifactId>"
echo "    <version>$VERSION</version>"
echo "</dependency>"
