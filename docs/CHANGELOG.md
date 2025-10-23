# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Version management scripts and automation
- Maven Central publishing configuration
- Comprehensive documentation for versioning

### Changed
- Updated group ID to `io.github.arvinubhi` for Maven Central publishing
- Enhanced documentation with Maven Central usage examples

### Fixed
- Improved error handling in CLI scripts
- Fixed linting errors in Multi* classes

## [1.0.0] - 2024-01-01

### Added
- Initial release of Kafka Management Library
- Multi-broker and multi-schema registry support
- SSL JKS support for secure connections
- Comprehensive CLI interface for both single-broker and multi-broker operations
- Transaction management with automatic cleanup
- Schema registry operations (register, retrieve, list schemas)
- Consumer group management (list, describe, delete, reset offsets)
- Topic management with various types (compacted, high-throughput, etc.)
- Message operations (send, peek, consume with various serialization formats)
- Session management for transactional operations
- Testing utilities and comprehensive test suites
- Extensive documentation and guides

### Features
- **Single-Broker Operations**: Complete Kafka and Schema Registry management
- **Multi-Broker Operations**: Manage multiple Kafka brokers and schema registries
- **SSL JKS Support**: Production-ready secure connections
- **CLI Interface**: Interactive and non-interactive command-line interface
- **Transaction Support**: Complete transactional producer and consumer support
- **Schema Management**: Avro, JSON Schema, and Protobuf support
- **Consumer Group Management**: Full lifecycle management of consumer groups
- **Topic Management**: Create, configure, and manage various topic types
- **Message Operations**: Send, consume, and peek at messages
- **Session Management**: Monitor and manage active sessions
- **Testing Support**: Comprehensive testing utilities and examples

### Documentation
- README.md with comprehensive usage examples
- MULTI_BROKER_GUIDE.md for multi-broker operations
- MULTI_CLI_GUIDE.md for CLI usage
- SCRIPTS_GUIDE.md for script usage
- MAVEN_CENTRAL_PUBLISHING_GUIDE.md for publishing
- VERSION_MANAGEMENT_GUIDE.md for version management
- CHANGELOG.md for tracking changes

### Scripts
- `run-cli.sh` / `run-cli.bat` - CLI runner scripts
- `publish-to-maven-central.sh` / `publish-to-maven-central.bat` - Publishing scripts
- `update-version.sh` / `update-version.bat` - Version update scripts
- `release.sh` / `release.bat` - Release creation scripts
- `version-info.sh` / `version-info.bat` - Version information scripts
