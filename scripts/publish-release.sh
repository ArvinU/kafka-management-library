#!/bin/bash

# Kafka Management Library - Release Publishing Script
# Usage: ./scripts/publish-release.sh <version>
# Example: ./scripts/publish-release.sh 1.0.3

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if version is provided
if [ $# -eq 0 ]; then
    print_error "Version number is required!"
    echo "Usage: $0 <version> [release]"
    echo "Example: $0 1.0.3"
    echo "Example: $0 1.0.3 release"
    exit 1
fi

VERSION=$1
CREATE_RELEASE=${2:-""}
CURRENT_DIR=$(pwd)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

print_status "Starting release process for version: $VERSION"
print_status "Project directory: $PROJECT_DIR"

# Change to project directory
cd "$PROJECT_DIR"

# Step 1: Update version
print_status "Step 1: Updating version to $VERSION..."
if [ -f "./scripts/update-version.sh" ]; then
    ./scripts/update-version.sh "$VERSION"
    if [ $? -eq 0 ]; then
        print_success "Version updated to $VERSION"
    else
        print_error "Failed to update version"
        exit 1
    fi
else
    print_error "update-version.sh script not found!"
    exit 1
fi

# Step 2: Test build
print_status "Step 2: Testing build..."
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    print_success "Build test passed"
else
    print_error "Build test failed"
    exit 1
fi

# Step 3: Publish to GitHub Packages
print_status "Step 3: Publishing to GitHub Packages..."
mvn clean deploy -DskipTests
if [ $? -eq 0 ]; then
    print_success "Successfully published to GitHub Packages"
else
    print_error "Failed to publish to GitHub Packages"
    exit 1
fi

# Step 4: Git operations
print_status "Step 4: Committing and tagging release..."

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    print_warning "Not in a git repository, skipping git operations"
else
    # Check if there are uncommitted changes
    if [ -n "$(git status --porcelain)" ]; then
        print_status "Committing changes..."
        git add .
        git commit -m "Release version $VERSION"
        print_success "Changes committed"
    else
        print_warning "No changes to commit"
    fi
    
    # Create and push tag
    print_status "Creating and pushing tag v$VERSION..."
    git tag "v$VERSION"
    git push origin master
    git push origin "v$VERSION"
    print_success "Tag v$VERSION created and pushed"
    
    # Create GitHub release if requested
    if [ "$CREATE_RELEASE" = "release" ]; then
        print_status "Creating GitHub release..."
        if command -v gh >/dev/null 2>&1; then
            gh release create "v$VERSION" --title "Release $VERSION" --notes "Release $VERSION of Kafka Management Library"
            if [ $? -eq 0 ]; then
                print_success "GitHub release created successfully"
            else
                print_warning "Failed to create GitHub release (check gh CLI installation and authentication)"
            fi
        else
            print_warning "GitHub CLI (gh) not found. Install it to create releases automatically."
            print_status "Manual release creation:"
            print_status "1. Go to: https://github.com/arvinu/kafka-management-library/releases"
            print_status "2. Click 'Create a new release'"
            print_status "3. Select tag 'v$VERSION'"
            print_status "4. Add release title and notes"
        fi
    fi
fi

# Step 5: Summary
print_success "🎉 Release $VERSION completed successfully!"
print_status "📦 Published to: https://maven.pkg.github.com/arvinu/kafka-management-library"
print_status "🏷️  Tag: v$VERSION"
print_status "📋 Maven coordinates: io.github.arvinu:kafka-management-library:$VERSION"

# Step 6: Next steps
echo ""
if [ "$CREATE_RELEASE" = "release" ]; then
    print_status "Next steps:"
    echo "1. Update your README with the new version"
    echo "2. Notify users about the new release"
    echo "3. Review the GitHub release: https://github.com/arvinu/kafka-management-library/releases"
else
    print_status "Next steps:"
    echo "1. Create a GitHub release with release notes"
    echo "2. Update your README with the new version"
    echo "3. Notify users about the new release"
    echo ""
    print_status "To create a GitHub release, run:"
    echo "gh release create v$VERSION --title \"Release $VERSION\" --notes \"Release notes here\""
    echo ""
    print_status "Or run this script with 'release' parameter:"
    echo "$0 $VERSION release"
fi

print_success "Release process completed! 🚀"
