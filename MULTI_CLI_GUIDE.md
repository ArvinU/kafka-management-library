# Multi-Broker CLI Guide

The Multi-Broker CLI provides comprehensive command-line access to all multi-broker and multi-schema registry functionality in the Kafka Management Library.

## Quick Start

### 1. Generate Sample Configuration Files

```bash
java -jar kafka-management-library.jar --generate-configs
```

This creates:
- `config/multi/multi-kafka-config.json` - Multi-broker configuration
- `config/multi/multi-schema-registry-config.json` - Multi-schema registry configuration

### 2. Run the Multi-Broker CLI

```bash
# Interactive mode
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json

# Non-interactive mode
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers list"
```

## Configuration

### Multi-Broker Configuration (`multi-kafka-config.json`)

```json
{
  "brokers": [
    {
      "name": "broker1",
      "bootstrapServers": "localhost:9092",
      "securityProtocol": "PLAINTEXT",
      "saslMechanism": "PLAIN",
      "saslJaasConfig": "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";"
    },
    {
      "name": "broker2", 
      "bootstrapServers": "localhost:9093",
      "securityProtocol": "PLAINTEXT"
    }
  ]
}
```

### Multi-Schema Registry Configuration (`multi-schema-registry-config.json`)

```json
{
  "registries": [
    {
      "name": "registry1",
      "url": "http://localhost:8081",
      "basicAuthUserInfo": "admin:admin-secret"
    },
    {
      "name": "registry2",
      "url": "http://localhost:8082"
    }
  ]
}
```

## Commands Reference

### Broker Management

#### List All Brokers
```bash
brokers list
```
Shows all configured brokers and their connection status.

#### Show Broker Status
```bash
brokers status
```
Shows detailed broker connection information.

### Schema Registry Management

#### List All Schema Registries
```bash
registries list
```
Shows all configured schema registries and their connection status.

#### Show Registry Status
```bash
registries status
```
Shows detailed schema registry connection information.

### Topic Operations

#### List Topics on Specific Broker
```bash
topics list <broker-id>
```
Example: `topics list broker1`

#### Get Topic Information
```bash
topics info <broker-id> <topic-name>
```
Example: `topics info broker1 my-topic`

#### Create Topic on Specific Broker
```bash
topics create <broker-id> <name> <partitions> <replication> [type]
```
Examples:
- `topics create broker1 my-topic 3 1`
- `topics create broker1 compacted-topic 3 1 compacted`

#### Create Topic Across Multiple Brokers
```bash
topics create-across <broker-ids> <name> <partitions> <replication> <template>
```
Example: `topics create-across broker1,broker2 my-topic 3 1 high-throughput`

### Message Operations

#### Send Simple Message
```bash
messages send <broker-id> <topic> <key> <value>
```
Example: `messages send broker1 my-topic key1 "Hello World"`

#### Send Avro Message
```bash
messages send-avro <broker-id> <topic> <key> <value>
```
Example: `messages send-avro broker1 my-topic key1 "{\"name\":\"John\",\"age\":30}"`

#### Send JSON Schema Message
```bash
messages send-json <broker-id> <topic> <key> <value>
```
Example: `messages send-json broker1 my-topic key1 "{\"name\":\"John\",\"age\":30}"`

#### Send Message with Auto Schema Registration
```bash
messages send-with-schema <broker-id> <topic> <key> <value> <subject> <schema>
```
Example: `messages send-with-schema broker1 my-topic key1 "{\"name\":\"John\"}" user-value '{"type":"object","properties":{"name":{"type":"string"}}}'`

#### Send Message with Schema ID
```bash
messages send-with-schema-id <broker-id> <topic> <key> <value> <schema-id>
```
Example: `messages send-with-schema-id broker1 my-topic key1 "{\"name\":\"John\"}" 1`

#### Peek at Messages
```bash
messages peek <broker-id> <topic> [count]
```
Example: `messages peek broker1 my-topic 10`

### Consumer Group Operations

#### List Consumer Groups
```bash
consumers list <broker-id>
```
Example: `consumers list broker1`

#### Get Consumer Group Information
```bash
consumers info <broker-id> <group-id>
```
Example: `consumers info broker1 my-group`

#### Delete Consumer Group
```bash
consumers delete <broker-id> <group-id>
```
Example: `consumers delete broker1 my-group`

#### Reset Consumer Group Offsets to Earliest
```bash
consumers reset-earliest <broker-id> <group-id>
```
Example: `consumers reset-earliest broker1 my-group`

#### Reset Consumer Group Offsets to Latest
```bash
consumers reset-latest <broker-id> <group-id>
```
Example: `consumers reset-latest broker1 my-group`

#### Get Consumer Group Lag
```bash
consumers lag <broker-id> <group-id>
```
Example: `consumers lag broker1 my-group`

### Transaction Management

#### Create Transactional Producer
```bash
sessions create-producer <broker-id> <transaction-id>
```
Example: `sessions create-producer broker1 tx-001`

#### Create Transactional Consumer
```bash
sessions create-consumer <broker-id> <transaction-id> <group-id>
```
Example: `sessions create-consumer broker1 tx-001 my-group`

#### Begin Transaction
```bash
sessions begin <broker-id> <transaction-id>
```
Example: `sessions begin broker1 tx-001`

#### Commit Transaction
```bash
sessions commit <broker-id> <transaction-id>
```
Example: `sessions commit broker1 tx-001`

#### Abort Transaction
```bash
sessions abort <broker-id> <transaction-id>
```
Example: `sessions abort broker1 tx-001`

#### Execute Transaction with Automatic Cleanup
```bash
sessions execute <broker-id> <transaction-id> <group-id>
```
Example: `sessions execute broker1 tx-001 my-group`

### Schema Registry Operations

#### Register Schema
```bash
schemas register <registry-id> <subject> <schema>
```
Example: `schemas register registry1 user-value '{"type":"object","properties":{"name":{"type":"string"}}}'`

#### Get Schema by ID
```bash
schemas get <registry-id> <schema-id>
```
Example: `schemas get registry1 1`

#### List Subjects
```bash
schemas list <registry-id>
```
Example: `schemas list registry1`

### Testing Operations

#### Create Test Topic
```bash
test create-topic <broker-id> <topic-name>
```
Example: `test create-topic broker1 test-topic`

#### Send Test Messages
```bash
test send-messages <broker-id> <topic-name> <count>
```
Example: `test send-messages broker1 test-topic 100`

#### Execute Test Transaction
```bash
test execute-transaction <broker-id> <transaction-id> <group-id> <topic-name> <count>
```
Example: `test execute-transaction broker1 tx-test my-group test-topic 50`

#### Clean Up Test Resources
```bash
test cleanup <broker-id> <topic-names> <group-ids>
```
Example: `test cleanup broker1 test-topic,test-topic-2 my-group,my-group-2`

## Interactive Mode

When running without a command, the CLI enters interactive mode:

```
Multi-Broker Kafka Management Library CLI
Type 'help' for available commands or 'exit' to quit
Connected brokers: [broker1, broker2]
Connected registries: [registry1, registry2]
multi-kafka-cli> 
```

### Interactive Commands

- `help` - Show available commands
- `exit` or `quit` - Exit the CLI
- Any of the commands listed above

## Error Handling

The CLI provides consistent error handling:

- **Connection Errors**: Clear messages when brokers or registries are unavailable
- **Validation Errors**: Helpful messages for invalid parameters
- **Operation Errors**: Detailed error messages with context

## Examples

### Complete Workflow Example

```bash
# Start interactive mode
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json

# Check broker status
multi-kafka-cli> brokers status

# Create a topic on broker1
multi-kafka-cli> topics create broker1 my-topic 3 1

# Send messages to the topic
multi-kafka-cli> messages send broker1 my-topic key1 "Hello from broker1"
multi-kafka-cli> messages send broker1 my-topic key2 "Another message"

# Peek at messages
multi-kafka-cli> messages peek broker1 my-topic 5

# Create a consumer group
multi-kafka-cli> consumers list broker1

# Register a schema
multi-kafka-cli> schemas register registry1 user-value '{"type":"object","properties":{"name":{"type":"string"}}}'

# Send message with schema
multi-kafka-cli> messages send-with-schema broker1 my-topic key3 "{\"name\":\"John\"}" user-value '{"type":"object","properties":{"name":{"type":"string"}}}'

# Execute a transaction
multi-kafka-cli> sessions execute broker1 tx-001 my-group

# Clean up
multi-kafka-cli> test cleanup broker1 my-topic my-group
```

### Non-Interactive Examples

```bash
# Check broker status
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers status"

# Create topic
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "topics create broker1 my-topic 3 1"

# Send message
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "messages send broker1 my-topic key1 'Hello World'"

# List topics
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "topics list broker1"
```

## Advanced Usage

### Multi-Broker Topic Creation

Create the same topic across multiple brokers with different configurations:

```bash
# Create high-throughput topic across broker1 and broker2
topics create-across broker1,broker2 high-throughput-topic 6 2 high-throughput

# Create compacted topic across all brokers
topics create-across broker1,broker2,broker3 compacted-topic 3 1 compacted
```

### Transaction Management

Execute complex transactions across multiple brokers:

```bash
# Create transactional producer on broker1
sessions create-producer broker1 tx-001

# Create transactional consumer on broker1
sessions create-consumer broker1 tx-001 my-group

# Begin transaction
sessions begin broker1 tx-001

# Send transactional messages (would be done programmatically)
# ...

# Commit transaction
sessions commit broker1 tx-001
```

### Schema Management

Manage schemas across multiple registries:

```bash
# Register schema on registry1
schemas register registry1 user-value '{"type":"object","properties":{"name":{"type":"string"}}}'

# Register same schema on registry2
schemas register registry2 user-value '{"type":"object","properties":{"name":{"type":"string"}}}'

# List subjects on both registries
schemas list registry1
schemas list registry2
```

## Troubleshooting

### Common Issues

1. **Broker Connection Failed**
   - Check broker configuration in `multi-kafka-config.json`
   - Verify broker is running and accessible
   - Check network connectivity

2. **Schema Registry Connection Failed**
   - Check registry configuration in `multi-schema-registry-config.json`
   - Verify registry is running and accessible
   - Check authentication credentials

3. **Topic Creation Failed**
   - Verify broker is connected
   - Check topic name validity
   - Ensure sufficient permissions

4. **Message Send Failed**
   - Verify topic exists
   - Check broker connection
   - Validate message format

### Debug Mode

Enable debug logging by setting the log level:

```bash
export KAFKA_LOG_LEVEL=DEBUG
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json
```

## Integration with CI/CD

The CLI can be integrated into CI/CD pipelines for automated testing and deployment:

```bash
#!/bin/bash
# CI/CD Pipeline Example

# Check broker connectivity
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers status"

# Create test topics
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "topics create broker1 test-topic 3 1"

# Send test messages
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "test send-messages broker1 test-topic 100"

# Execute test transaction
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "test execute-transaction broker1 tx-test my-group test-topic 50"

# Clean up
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "test cleanup broker1 test-topic my-group"
```

This comprehensive CLI provides full access to all multi-broker functionality with consistent error handling and extensive documentation.
