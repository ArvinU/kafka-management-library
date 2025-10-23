# Scripts Guide

This guide covers the updated CLI scripts that support both single-broker and multi-broker operations.

## Overview

The library includes two CLI scripts:
- `run-cli.sh` - Linux/Mac script
- `run-cli.bat` - Windows script

Both scripts support:
- Single-broker mode (default)
- Multi-broker mode
- Configuration file generation
- Interactive and non-interactive modes

## Single-Broker Mode (Default)

### Basic Usage

**Linux/Mac:**
```bash
# Interactive mode with default servers
./scripts/run-cli.sh

# Interactive mode with custom servers
./scripts/run-cli.sh kafka-cluster:9092 https://schema-registry:8081

# Non-interactive mode
./scripts/run-cli.sh localhost:9092 http://localhost:8081 "topics list"

# Using JSON configuration files
./scripts/run-cli.sh kafka-config.json schema-registry-config.json "topics list"
```

**Windows:**
```cmd
REM Interactive mode with default servers
scripts\run-cli.bat

REM Interactive mode with custom servers
scripts\run-cli.bat kafka-cluster:9092 https://schema-registry:8081

REM Non-interactive mode
scripts\run-cli.bat localhost:9092 http://localhost:8081 "topics list

REM Using JSON configuration files
scripts\run-cli.bat kafka-config.json schema-registry-config.json "topics list"
```

### Generate Single-Broker Configuration Files

**Linux/Mac:**
```bash
./scripts/run-cli.sh --generate-configs
```

**Windows:**
```cmd
scripts\run-cli.bat --generate-configs
```

This creates:
- `kafka-config.json` - Single broker configuration
- `schema-registry-config.json` - Single schema registry configuration

## Multi-Broker Mode

### Basic Usage

**Linux/Mac:**
```bash
# Interactive mode with default multi-broker configs
./scripts/run-cli.sh --multi

# Interactive mode with custom multi-broker configs
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json

# Non-interactive mode
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers list"
```

**Windows:**
```cmd
REM Interactive mode with default multi-broker configs
scripts\run-cli.bat --multi

REM Interactive mode with custom multi-broker configs
scripts\run-cli.bat --multi config\multi\multi-kafka-config.json config\multi\multi-schema-registry-config.json

REM Non-interactive mode
scripts\run-cli.bat --multi config\multi\multi-kafka-config.json config\multi\multi-schema-registry-config.json "brokers list"
```

### Generate Multi-Broker Configuration Files

**Linux/Mac:**
```bash
./scripts/run-cli.sh --generate-multi-configs
```

**Windows:**
```cmd
scripts\run-cli.bat --generate-multi-configs
```

This creates:
- `config/multi/multi-kafka-config.json` - Multi-broker configuration
- `config/multi/multi-schema-registry-config.json` - Multi-schema registry configuration

## Configuration Files

### Single-Broker Configuration

**kafka-config.json (SSL JKS):**
```json
{
  "bootstrap.servers": "localhost:9092",
  "security.protocol": "SSL",
  "ssl.truststore.location": "/path/to/truststore.jks",
  "ssl.truststore.password": "truststore-password",
  "ssl.keystore.location": "/path/to/keystore.jks",
  "ssl.keystore.password": "keystore-password",
  "ssl.key.password": "key-password",
  "ssl.endpoint.identification.algorithm": "https"
}
```

**schema-registry-config.json (SSL JKS):**
```json
{
  "schema.registry.url": "https://localhost:8081",
  "basic.auth.user.info": "admin:admin-secret",
  "ssl.truststore.location": "/path/to/truststore.jks",
  "ssl.truststore.password": "truststore-password",
  "ssl.keystore.location": "/path/to/keystore.jks",
  "ssl.keystore.password": "keystore-password",
  "ssl.key.password": "key-password"
}
```

**kafka-config.json (PLAINTEXT - for development):**
```json
{
  "bootstrap.servers": "localhost:9092",
  "security.protocol": "PLAINTEXT"
}
```

**schema-registry-config.json (HTTP - for development):**
```json
{
  "schema.registry.url": "http://localhost:8081"
}
```

### Multi-Broker Configuration

**multi-kafka-config.json (SSL JKS):**
```json
{
  "brokers": [
    {
      "name": "broker1",
      "bootstrapServers": "localhost:9092",
      "securityProtocol": "SSL",
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    },
    {
      "name": "broker2",
      "bootstrapServers": "localhost:9093",
      "securityProtocol": "SSL",
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    }
  ]
}
```

**multi-schema-registry-config.json (SSL JKS):**
```json
{
  "registries": [
    {
      "name": "registry1",
      "url": "https://localhost:8081",
      "basicAuthUserInfo": "admin:admin-secret",
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    },
    {
      "name": "registry2",
      "url": "https://localhost:8082",
      "basicAuthUserInfo": "admin:admin-secret",
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    }
  ]
}
```

**multi-kafka-config.json (PLAINTEXT - for development):**
```json
{
  "brokers": [
    {
      "name": "broker1",
      "bootstrapServers": "localhost:9092",
      "securityProtocol": "PLAINTEXT"
    },
    {
      "name": "broker2",
      "bootstrapServers": "localhost:9093",
      "securityProtocol": "PLAINTEXT"
    }
  ]
}
```

**multi-schema-registry-config.json (HTTP - for development):**
```json
{
  "registries": [
    {
      "name": "registry1",
      "url": "http://localhost:8081"
    },
    {
      "name": "registry2",
      "url": "http://localhost:8082"
    }
  ]
}
```

## Commands Comparison

### Single-Broker Commands

| Command | Description | Example |
|---------|-------------|---------|
| `topics list` | List all topics | `topics list` |
| `topics info <topic>` | Get topic information | `topics info my-topic` |
| `create-topic <name> <partitions> <replication> [type]` | Create a topic | `create-topic my-topic 3 1 compacted` |
| `messages peek <topic> [count]` | Peek at messages | `messages peek my-topic 10` |
| `send-message <topic> <key> <value>` | Send a message | `send-message my-topic key1 value1` |
| `consumers list` | List consumer groups | `consumers list` |
| `consumers info <group-id>` | Get consumer group info | `consumers info my-group` |

### Multi-Broker Commands

| Command | Description | Example |
|---------|-------------|---------|
| `brokers list` | List all brokers and their status | `brokers list` |
| `brokers status` | Show broker connection status | `brokers status` |
| `topics list <broker-id>` | List topics on specific broker | `topics list broker1` |
| `topics info <broker-id> <topic-name>` | Get topic information from specific broker | `topics info broker1 my-topic` |
| `topics create <broker-id> <name> <partitions> <replication> [type]` | Create topic on specific broker | `topics create broker1 my-topic 3 1 compacted` |
| `messages send <broker-id> <topic> <key> <value>` | Send message to specific broker | `messages send broker1 my-topic key1 "Hello World"` |
| `consumers list <broker-id>` | List consumer groups on specific broker | `consumers list broker1` |
| `consumers info <broker-id> <group-id>` | Get consumer group information from specific broker | `consumers info broker1 my-group` |

## SSL JKS Setup

### Prerequisites for SSL JKS

1. **Generate JKS files** (if not already available):
   ```bash
   # Generate truststore
   keytool -genkey -alias kafka-client -keyalg RSA -keystore kafka-client.jks -validity 365 -storepass password
   
   # Generate keystore
   keytool -genkey -alias kafka-server -keyalg RSA -keystore kafka-server.jks -validity 365 -storepass password
   ```

2. **Configure Kafka brokers** with SSL:
   ```properties
   # server.properties
   listeners=PLAINTEXT://localhost:9092,SSL://localhost:9093
   security.inter.broker.protocol=SSL
   ssl.keystore.location=/path/to/kafka-server.jks
   ssl.keystore.password=password
   ssl.key.password=password
   ssl.truststore.location=/path/to/kafka-client.jks
   ssl.truststore.password=password
   ```

3. **Configure Schema Registry** with SSL:
   ```properties
   # schema-registry.properties
   listeners=https://localhost:8081
   ssl.keystore.location=/path/to/schema-registry.jks
   ssl.keystore.password=password
   ssl.key.password=password
   ```

### SSL JKS Usage Examples

**Single-Broker with SSL:**
```bash
# Using SSL JKS configuration files
./scripts/run-cli.sh kafka-ssl-config.json schema-registry-ssl-config.json "topics list"

# Interactive mode with SSL
./scripts/run-cli.sh kafka-ssl-config.json schema-registry-ssl-config.json
```

**Multi-Broker with SSL:**
```bash
# Using SSL JKS multi-broker configuration files
./scripts/run-cli.sh --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json "brokers list"

# Interactive mode with SSL
./scripts/run-cli.sh --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json
```

## Examples

### Single-Broker Examples

**Interactive Mode:**
```bash
# Start interactive mode
./scripts/run-cli.sh

# Available commands in interactive mode
kafka-cli> topics list
kafka-cli> create-topic my-topic 3 1
kafka-cli> send-message my-topic key1 "Hello World"
kafka-cli> messages peek my-topic 5
kafka-cli> consumers list
kafka-cli> exit
```

**Non-Interactive Mode:**
```bash
# List topics
./scripts/run-cli.sh localhost:9092 http://localhost:8081 "topics list"

# Create topic
./scripts/run-cli.sh localhost:9092 http://localhost:8081 "create-topic my-topic 3 1"

# Send message
./scripts/run-cli.sh localhost:9092 http://localhost:8081 "send-message my-topic key1 'Hello World'"

# Peek messages
./scripts/run-cli.sh localhost:9092 http://localhost:8081 "messages peek my-topic 5"
```

### Multi-Broker Examples

**Interactive Mode:**
```bash
# Start multi-broker interactive mode
./scripts/run-cli.sh --multi

# Available commands in multi-broker interactive mode
multi-kafka-cli> brokers list
multi-kafka-cli> topics list broker1
multi-kafka-cli> topics create broker1 my-topic 3 1
multi-kafka-cli> messages send broker1 my-topic key1 "Hello from broker1"
multi-kafka-cli> consumers list broker1
multi-kafka-cli> exit
```

**Non-Interactive Mode:**
```bash
# List brokers
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers list"

# List topics on broker1
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "topics list broker1"

# Create topic on broker1
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "topics create broker1 my-topic 3 1"

# Send message to broker1
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "messages send broker1 my-topic key1 'Hello from broker1'"
```

## Advanced Usage

### Custom Configuration Files

**Single-Broker with Custom Config:**
```bash
# Using custom single-broker configuration
./scripts/run-cli.sh my-kafka-config.json my-schema-registry-config.json "topics list"
```

**Multi-Broker with Custom Config:**
```bash
# Using custom multi-broker configuration
./scripts/run-cli.sh --multi my-multi-kafka-config.json my-multi-schema-registry-config.json "brokers list"
```

### CI/CD Integration

**Single-Broker CI/CD (SSL JKS):**
```bash
#!/bin/bash
# CI/CD Pipeline for Single-Broker with SSL JKS

# Check connectivity with SSL
./scripts/run-cli.sh kafka-ssl-config.json schema-registry-ssl-config.json "topics list"

# Create test topic with SSL
./scripts/run-cli.sh kafka-ssl-config.json schema-registry-ssl-config.json "create-topic test-topic 3 1"

# Send test messages with SSL
./scripts/run-cli.sh kafka-ssl-config.json schema-registry-ssl-config.json "send-message test-topic key1 'Test message'"
```

**Single-Broker CI/CD (PLAINTEXT - for development):**
```bash
#!/bin/bash
# CI/CD Pipeline for Single-Broker (Development)

# Check connectivity
./scripts/run-cli.sh kafka-cluster:9092 http://schema-registry:8081 "topics list"

# Create test topic
./scripts/run-cli.sh kafka-cluster:9092 http://schema-registry:8081 "create-topic test-topic 3 1"

# Send test messages
./scripts/run-cli.sh kafka-cluster:9092 http://schema-registry:8081 "send-message test-topic key1 'Test message'"
```

**Multi-Broker CI/CD (SSL JKS):**
```bash
#!/bin/bash
# CI/CD Pipeline for Multi-Broker with SSL JKS

# Check broker connectivity with SSL
./scripts/run-cli.sh --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json "brokers status"

# Create test topic on broker1 with SSL
./scripts/run-cli.sh --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json "topics create broker1 test-topic 3 1"

# Send test messages to broker1 with SSL
./scripts/run-cli.sh --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json "messages send broker1 test-topic key1 'Test message'"
```

**Multi-Broker CI/CD (PLAINTEXT - for development):**
```bash
#!/bin/bash
# CI/CD Pipeline for Multi-Broker (Development)

# Check broker connectivity
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers status"

# Create test topic on broker1
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "topics create broker1 test-topic 3 1"

# Send test messages to broker1
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "messages send broker1 test-topic key1 'Test message'"
```

## Troubleshooting

### Common Issues

1. **JAR File Not Found**
   - The script automatically builds the project if the JAR file is missing
   - Ensure Maven is installed and accessible

2. **Configuration File Not Found**
   - Generate sample configuration files using `--generate-configs` or `--generate-multi-configs`
   - Check file paths are correct

3. **Connection Issues**
   - Verify broker and schema registry URLs are correct
   - Check network connectivity
   - Verify authentication credentials

4. **SSL JKS Issues**
   - **JKS file not found**: Verify JKS file paths in configuration
   - **Password incorrect**: Check JKS passwords match configuration
   - **Certificate issues**: Ensure certificates are valid and not expired
   - **Truststore issues**: Verify truststore contains correct CA certificates
   - **Keystore issues**: Verify keystore contains valid client certificates

5. **Permission Issues (Linux/Mac)**
   - Make scripts executable: `chmod +x scripts/run-cli.sh`
   - Ensure Java is in PATH
   - Check JKS file permissions: `chmod 600 *.jks`

### Debug Mode

Enable debug logging by setting environment variables:

**Linux/Mac:**
```bash
# Debug single-broker with SSL
export KAFKA_LOG_LEVEL=DEBUG
./scripts/run-cli.sh kafka-ssl-config.json schema-registry-ssl-config.json "topics list"

# Debug multi-broker with SSL
export KAFKA_LOG_LEVEL=DEBUG
./scripts/run-cli.sh --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json "brokers list"

# Debug multi-broker (development)
export KAFKA_LOG_LEVEL=DEBUG
./scripts/run-cli.sh --multi config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers list"
```

**Windows:**
```cmd
REM Debug single-broker with SSL
set KAFKA_LOG_LEVEL=DEBUG
scripts\run-cli.bat kafka-ssl-config.json schema-registry-ssl-config.json "topics list"

REM Debug multi-broker with SSL
set KAFKA_LOG_LEVEL=DEBUG
scripts\run-cli.bat --multi multi-kafka-ssl-config.json multi-schema-registry-ssl-config.json "brokers list"

REM Debug multi-broker (development)
set KAFKA_LOG_LEVEL=DEBUG
scripts\run-cli.bat --multi config\multi\multi-kafka-config.json config\multi\multi-schema-registry-config.json "brokers list"
```

### SSL JKS Debug Commands

**Test JKS files:**
```bash
# Test truststore
keytool -list -keystore /path/to/truststore.jks -storepass password

# Test keystore
keytool -list -keystore /path/to/keystore.jks -storepass password

# Test certificate validity
keytool -list -v -keystore /path/to/keystore.jks -storepass password
```

**Test SSL connectivity:**
```bash
# Test Kafka SSL port
openssl s_client -connect localhost:9093 -servername localhost

# Test Schema Registry SSL port
curl -k https://localhost:8081/subjects
```

## Migration Guide

### From Single-Broker to Multi-Broker

1. **Generate Multi-Broker Configuration:**
   ```bash
   ./scripts/run-cli.sh --generate-multi-configs
   ```

2. **Update Configuration Files:**
   - Edit `config/multi/multi-kafka-config.json` with your broker details
   - Edit `config/multi/multi-schema-registry-config.json` with your registry details

3. **Update Scripts:**
   - Use `--multi` flag for multi-broker operations
   - Update command syntax to include broker IDs

4. **Test Migration:**
   ```bash
   # Test multi-broker connectivity
   ./scripts/run-cli.sh --multi "brokers list"
   
   # Test operations on specific brokers
   ./scripts/run-cli.sh --multi "topics list broker1"
   ```

This comprehensive guide covers all aspects of using the updated CLI scripts for both single-broker and multi-broker operations.
