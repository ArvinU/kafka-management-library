# 🚀 Publishing Workflow Guide

This guide explains how to publish new versions of the Kafka Management Library to GitHub Packages.

## 📋 Prerequisites

1. **GitHub Token**: You need a GitHub Personal Access Token with these permissions:
   - `write:packages` (to publish packages)
   - `read:packages` (to read packages)
   - `repo` (if the repository is private)

2. **Maven Settings**: Your `~/.m2/settings.xml` should be configured with GitHub credentials:
   ```xml
   <settings>
       <servers>
           <server>
               <id>github</id>
               <username>arvinu</username>
               <password>YOUR_GITHUB_TOKEN</password>
           </server>
       </servers>
   </settings>
   ```

3. **GitHub CLI (for automatic releases)**: Install GitHub CLI to create releases automatically:
   - **macOS**: `brew install gh`
   - **Windows**: `winget install GitHub.cli`
   - **Linux**: Follow [GitHub CLI installation guide](https://cli.github.com/manual/installation)
   - **Authentication**: Run `gh auth login` after installation

4. **GPG Setup**: For signing artifacts (optional but recommended)

## 🎯 Quick Start

### Option 1: Automated Script (Recommended)

**Basic publishing (creates tag only):**
```bash
# Linux/macOS
./scripts/publish-release.sh 1.0.3

# Windows
scripts\publish-release.bat 1.0.3
```

**Publishing with GitHub release:**
```bash
# Linux/macOS
./scripts/publish-release.sh 1.0.3 release

# Windows
scripts\publish-release.bat 1.0.3 release
```

### Option 2: Manual Steps

1. **Update Version:**
   ```bash
   ./scripts/update-version.sh 1.0.3
   ```

2. **Test Build:**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Publish:**
   ```bash
   mvn clean deploy -DskipTests
   ```

4. **Commit and Tag:**
   ```bash
   git add .
   git commit -m "Release version 1.0.3"
   git tag v1.0.3
   git push origin master
   git push origin v1.0.3
   ```

## 📦 What Gets Published

The publishing process creates and uploads:

- **Main JAR**: The shaded JAR with all dependencies
- **Sources JAR**: Source code for debugging
- **Javadoc JAR**: API documentation
- **POM file**: Maven project metadata
- **GPG signatures**: For all artifacts (if GPG is configured)
- **Maven metadata**: Version information

## 🔗 Published Location

Your library is published to:
- **GitHub Packages**: `https://maven.pkg.github.com/arvinu/kafka-management-library`
- **Maven Coordinates**: `io.github.arvinu:kafka-management-library:VERSION`

## 👥 How Users Can Use Your Library

### 1. Add Repository to pom.xml

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/arvinu/kafka-management-library</url>
    </repository>
</repositories>
```

### 2. Add Dependency

```xml
<dependency>
    <groupId>io.github.arvinu</groupId>
    <artifactId>kafka-management-library</artifactId>
    <version>1.0.3</version>
</dependency>
```

### 3. Configure Authentication

Users need to add GitHub authentication to their `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>USERNAME</username>
            <password>GITHUB_TOKEN</password>
        </server>
    </servers>
</repositories>
</settings>
```

## 🛠️ Script Details

### publish-release.sh (Linux/macOS)

**Features:**
- ✅ Colored output for better readability
- ✅ Error handling with exit codes
- ✅ Step-by-step progress tracking
- ✅ Automatic git operations
- ✅ Summary with next steps

**Usage:**
```bash
./scripts/publish-release.sh <version> [release]
```

**Examples:**
```bash
# Basic publishing (tag only)
./scripts/publish-release.sh 1.0.3

# Publishing with GitHub release
./scripts/publish-release.sh 1.0.3 release
```

### publish-release.bat (Windows)

**Features:**
- ✅ Windows-compatible batch script
- ✅ Error handling
- ✅ Step-by-step progress tracking
- ✅ Automatic git operations
- ✅ Summary with next steps

**Usage:**
```cmd
scripts\publish-release.bat <version> [release]
```

**Examples:**
```cmd
REM Basic publishing (tag only)
scripts\publish-release.bat 1.0.3

REM Publishing with GitHub release
scripts\publish-release.bat 1.0.3 release
```

## 🔧 Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Check your GitHub token is valid
   - Ensure token has correct permissions
   - Verify `~/.m2/settings.xml` configuration

2. **409 Conflict**
   - Version already exists
   - Use a new version number

3. **Build Failures**
   - Check Java version compatibility
   - Ensure all dependencies are available
   - Run `mvn clean compile` to test compilation

4. **Git Issues**
   - Ensure you're in a git repository
   - Check git credentials are configured
   - Verify you have push permissions

### Debug Mode

For detailed output, run Maven with debug flags:
```bash
mvn clean deploy -DskipTests -X
```

## 📈 Version Management

### Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Pre-release Versions

For pre-release versions, use:
- `1.0.3-alpha.1`
- `1.0.3-beta.1`
- `1.0.3-rc.1`

## 🎉 Post-Release Steps

After publishing:

1. **Create GitHub Release**
   ```bash
   gh release create v1.0.3 --title "Release 1.0.3" --notes "Release notes here"
   ```

2. **Update Documentation**
   - Update README.md with new version
   - Update CHANGELOG.md
   - Update any version references

3. **Notify Users**
   - Update release notes
   - Announce on relevant channels
   - Update project website if applicable

## 🔒 Security Notes

- **Never commit tokens** to version control
- **Use environment variables** for CI/CD
- **Rotate tokens** regularly
- **Use minimal permissions** for tokens

## 📞 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Review GitHub Packages documentation
3. Check Maven logs for detailed error messages
4. Ensure all prerequisites are met

---

**Happy Publishing! 🚀**
