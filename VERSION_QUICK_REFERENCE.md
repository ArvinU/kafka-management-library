# Version Management Quick Reference

## Quick Commands

### Check Current Version
```bash
# Show version info
./scripts/version-info.sh

# Get just the version number
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

### Update Version
```bash
# Update to specific version
./scripts/update-version.sh 1.0.1

# Update using Maven directly
mvn versions:set -DnewVersion=1.0.1
```

### Create Release
```bash
# Create release (updates version, commits, tags, pushes)
./scripts/release.sh 1.0.1

# Manual release process
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1
```

### Publish to Maven Central
```bash
# Publish to Maven Central
./scripts/publish-to-maven-central.sh 1.0.1
```

## Version Types

### Semantic Versioning
- **PATCH** (1.0.0 → 1.0.1): Bug fixes
- **MINOR** (1.0.0 → 1.1.0): New features (backward compatible)
- **MAJOR** (1.0.0 → 2.0.0): Breaking changes

### Pre-Release Versions
- **Alpha**: `1.1.0-alpha.1` (internal testing)
- **Beta**: `1.1.0-beta.1` (public testing)
- **RC**: `1.1.0-rc.1` (release candidate)

## Common Workflows

### 1. Bug Fix Release
```bash
# 1. Update version
./scripts/update-version.sh 1.0.1

# 2. Test changes
mvn clean test

# 3. Create release
./scripts/release.sh 1.0.1

# 4. Publish to Maven Central
./scripts/publish-to-maven-central.sh 1.0.1
```

### 2. Feature Release
```bash
# 1. Update version
./scripts/update-version.sh 1.1.0

# 2. Test changes
mvn clean test

# 3. Create release
./scripts/release.sh 1.1.0

# 4. Publish to Maven Central
./scripts/publish-to-maven-central.sh 1.1.0
```

### 3. Breaking Change Release
```bash
# 1. Update version
./scripts/update-version.sh 2.0.0

# 2. Test changes
mvn clean test

# 3. Create release
./scripts/release.sh 2.0.0

# 4. Publish to Maven Central
./scripts/publish-to-maven-central.sh 2.0.0
```

## Pre-Release Workflow

### Alpha Release
```bash
# 1. Update to alpha version
mvn versions:set -DnewVersion=1.1.0-alpha.1

# 2. Test
mvn clean test

# 3. Create alpha release
git tag -a v1.1.0-alpha.1 -m "Alpha release 1.1.0-alpha.1"
git push origin v1.1.0-alpha.1
```

### Beta Release
```bash
# 1. Update to beta version
mvn versions:set -DnewVersion=1.1.0-beta.1

# 2. Test
mvn clean test

# 3. Create beta release
git tag -a v1.1.0-beta.1 -m "Beta release 1.1.0-beta.1"
git push origin v1.1.0-beta.1
```

## Automated Version Management

### GitHub Actions
- **Manual Trigger**: Go to Actions → Version Management → Run workflow
- **Version Types**: patch, minor, major
- **Custom Version**: Override with specific version

### Automated Release
- **Trigger**: Push tag (e.g., `v1.0.1`)
- **Actions**: Build, test, create GitHub release, publish to Maven Central

## Troubleshooting

### Common Issues

1. **Version already exists**:
   ```bash
   # Check existing tags
   git tag -l
   
   # Delete local tag
   git tag -d v1.0.1
   
   # Delete remote tag
   git push origin --delete v1.0.1
   ```

2. **Working directory not clean**:
   ```bash
   # Check status
   git status
   
   # Stash changes
   git stash
   
   # Or commit changes
   git add .
   git commit -m "WIP: changes"
   ```

3. **Build failures**:
   ```bash
   # Clean and rebuild
   mvn clean compile test
   
   # Check for dependency issues
   mvn versions:display-dependency-updates
   ```

### Validation Commands

```bash
# Validate POM
mvn validate

# Check for issues
mvn clean compile test

# Verify version consistency
./scripts/version-info.sh

# Check Git status
git status
git log --oneline -5
```

## Best Practices

1. **Always test before release**:
   ```bash
   mvn clean compile test
   ```

2. **Use semantic versioning**:
   - Patch for bug fixes
   - Minor for new features
   - Major for breaking changes

3. **Keep changelog updated**:
   - Update `CHANGELOG.md` with each release
   - Document breaking changes clearly

4. **Tag releases**:
   - Always create Git tags for releases
   - Use descriptive tag messages

5. **Automate when possible**:
   - Use GitHub Actions for automated workflows
   - Use scripts for consistent processes

## File Locations

- **Version Scripts**: `scripts/`
- **Documentation**: `VERSION_MANAGEMENT_GUIDE.md`
- **Changelog**: `CHANGELOG.md`
- **Maven Config**: `pom.xml`
- **GitHub Actions**: `.github/workflows/`

## Support

For version management issues:
- Check `VERSION_MANAGEMENT_GUIDE.md` for detailed instructions
- Review GitHub Actions logs for automated workflows
- Use `./scripts/version-info.sh` to diagnose issues
