# Maven Central Publishing Guide

This guide explains how to publish the Kafka Management Library to Maven Central.

## Prerequisites

### 1. Sonatype OSSRH Account
1. Create an account at [Sonatype OSSRH](https://s01.oss.sonatype.org/)
2. Create a JIRA ticket to request namespace approval for `io.github.arvinubhi`
3. Wait for approval (usually within 24-48 hours)

### 2. GPG Key Setup
1. **Install GPG** (if not already installed):
   ```bash
   # macOS
   brew install gnupg
   
   # Ubuntu/Debian
   sudo apt-get install gnupg
   
   # Windows
   # Download from https://www.gnupg.org/download/
   ```

2. **Generate GPG Key**:
   ```bash
   gpg --gen-key
   # Choose RSA and RSA (default)
   # Key size: 4096
   # Expiration: 2y (2 years)
   # Enter your name and email
   # Set a passphrase
   ```

3. **Export Public Key**:
   ```bash
   # Get your key ID
   gpg --list-keys
   
   # Export public key (replace YOUR_KEY_ID with actual key ID)
   gpg --armor --export YOUR_KEY_ID > public-key.asc
   ```

4. **Upload to Keyservers**:
   ```bash
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
   gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID
   ```

### 3. Maven Settings Configuration

Create or update `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <servers>
        <server>
            <id>ossrh</id>
            <username>YOUR_SONATYPE_USERNAME</username>
            <password>YOUR_SONATYPE_PASSWORD</password>
        </server>
    </servers>
    
    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

## Publishing Process

### 1. Update Version and Prepare Release

```bash
# Update version in pom.xml (if needed)
# Current version: 1.0.0

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Generate sources and javadoc
mvn source:jar javadoc:jar
```

### 2. Deploy to Staging Repository

```bash
# Deploy to Sonatype staging repository
mvn clean deploy -P release

# Or if you want to skip tests (not recommended)
mvn clean deploy -P release -DskipTests
```

### 3. Release from Staging

1. **Login to Sonatype Nexus**:
   - Go to https://s01.oss.sonatype.org/
   - Login with your Sonatype credentials

2. **Navigate to Staging Repositories**:
   - Click "Staging Repositories" in the left menu
   - Find your repository (usually named `iogithubarvinubhi-XXXX`)

3. **Close and Release**:
   - Select your repository
   - Click "Close" (this validates the artifacts)
   - Once closed successfully, click "Release"
   - Confirm the release

### 4. Verify Release

After release, your artifacts will be available at:
- **Maven Central**: https://repo1.maven.org/maven2/io/github/arvinubhi/kafka-management-library/
- **Search**: https://search.maven.org/artifact/io.github.arvinubhi/kafka-management-library

## Usage in Other Projects

Once published, other projects can use your library by adding it to their `pom.xml`:

```xml
<dependency>
    <groupId>io.github.arvinubhi</groupId>
    <artifactId>kafka-management-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Automated Publishing with GitHub Actions

Create `.github/workflows/maven-publish.yml`:

```yaml
name: Publish to Maven Central

on:
  release:
    types: [published]

jobs:
  publish:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Configure GPG
      run: |
        echo "${{ secrets.GPG_PRIVATE_KEY }}" | gpg --import
        gpg --list-secret-keys --keyid-format LONG
    
    - name: Publish to Maven Central
      run: mvn clean deploy -P release
      env:
        OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
        OSSRH_TOKEN: ${{ secrets.OSSRH_TOKEN }}
        GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
```

### Required GitHub Secrets

Add these secrets to your GitHub repository:

- `OSSRH_USERNAME`: Your Sonatype username
- `OSSRH_TOKEN`: Your Sonatype password/token
- `GPG_PRIVATE_KEY`: Your GPG private key (export with `gpg --armor --export-secret-keys YOUR_KEY_ID`)
- `GPG_PASSPHRASE`: Your GPG passphrase

## Troubleshooting

### Common Issues

1. **GPG Key Not Found**:
   ```bash
   # List available keys
   gpg --list-keys
   
   # Import key if needed
   gpg --import your-private-key.asc
   ```

2. **Authentication Failed**:
   - Verify credentials in `~/.m2/settings.xml`
   - Check Sonatype account status
   - Ensure namespace is approved

3. **Staging Repository Issues**:
   - Check Sonatype Nexus for error messages
   - Verify all required metadata is present
   - Ensure GPG signature is valid

4. **Javadoc Generation Errors**:
   ```bash
   # Skip javadoc generation temporarily
   mvn clean deploy -P release -Dmaven.javadoc.skip=true
   ```

### Validation Commands

```bash
# Validate POM
mvn validate

# Check for issues
mvn clean compile
mvn clean test
mvn clean package

# Generate all artifacts
mvn clean source:jar javadoc:jar package

# Verify GPG signature
gpg --verify target/kafka-management-library-1.0.0.jar.asc
```

## Version Management

### Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Version Update Process

1. **Update version in `pom.xml`**:
   ```xml
   <version>1.0.1</version>
   ```

2. **Update version in scripts** (if needed):
   ```bash
   # Update version references in scripts
   sed -i 's/1.0.0/1.0.1/g' scripts/run-cli.sh
   sed -i 's/1.0.0/1.0.1/g' scripts/run-cli.bat
   ```

3. **Commit and tag**:
   ```bash
   git add .
   git commit -m "Release version 1.0.1"
   git tag v1.0.1
   git push origin v1.0.1
   ```

## Best Practices

1. **Always test locally** before publishing
2. **Use staging repository** for validation
3. **Keep GPG keys secure** and backed up
4. **Document breaking changes** in release notes
5. **Maintain backward compatibility** when possible
6. **Use semantic versioning** consistently
7. **Automate the process** with CI/CD when possible

## Support

For issues with Maven Central publishing:
- [Sonatype OSSRH Documentation](https://central.sonatype.org/publish/publish-guide/)
- [Maven Central Publishing Guide](https://maven.apache.org/repository/guide-central-repository-upload.html)
- [Sonatype Support](https://central.sonatype.org/help/)
