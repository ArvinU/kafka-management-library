# Multi-Broker and Multi-Schema Registry Guide

This guide explains how to use the enhanced Kafka Management Library with multiple brokers and schema registries.

## Overview

The enhanced library now supports multiple brokers and schema registries with a clean package structure. All multi-broker functionality is organized under the `com.mycompany.kafka.multi` package to separate it from the single-broker implementation.

## Package Structure

```
com.mycompany.kafka.multi/
├── MultiKafkaManagementLibrary.java          # Main library class
├── config/                                   # Configuration classes
│   ├── NamedKafkaConfig.java                # Enhanced Kafka config
│   ├── NamedSchemaRegistryConfig.java       # Enhanced Schema Registry config
│   └── MultiJsonConfigLoader.java           # Configuration loader
├── factory/                                  # Connection factory
│   └── MultiConnectionFactory.java          # Multi-broker connection factory
├── manager/                                  # Manager classes
│   ├── MultiBrokerManager.java              # Broker management
│   ├── MultiSchemaRegistryManager.java      # Schema Registry management
│   └── [Other Multi*Manager classes]        # Various operation managers
└── example/                                  # Example classes
    ├── MultiBrokerExample.java              # Comprehensive examples
    └── MultiConfigExample.java              # Configuration examples
```

The enhanced library now supports:
- **Multiple Kafka Brokers**: Connect to and manage multiple Kafka broker clusters
- **Multiple Schema Registries**: Connect to and manage multiple Schema Registry instances
- **Unique Naming**: Each broker and schema registry has a unique name for identification
- **Connection Status Tracking**: Monitor connection status for each broker and schema registry
- **Graceful Failure Handling**: Handle connection failures gracefully without affecting other connections
- **Reconnection Support**: Reconnect to failed brokers and schema registries

## Key Components

### 1. NamedKafkaConfig
Enhanced Kafka configuration with unique name and connection status tracking.

```java
NamedKafkaConfig broker = new NamedKafkaConfig("broker-1", "localhost:9092");
broker.setSecurityProtocol("SSL");
broker.setSslTruststoreLocation("/path/to/truststore.jks");
broker.setSslTruststorePassword("truststore-password");
broker.setSslKeystoreLocation("/path/to/keystore.jks");
broker.setSslKeystorePassword("keystore-password");
broker.setSslKeyPassword("key-password");
broker.setClientId("my-client");
```

### 2. NamedSchemaRegistryConfig
Enhanced Schema Registry configuration with unique name and connection status tracking.

```java
NamedSchemaRegistryConfig registry = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
registry.setSecurityProtocol("SSL");
registry.setSslTruststoreLocation("/path/to/truststore.jks");
registry.setSslTruststorePassword("truststore-password");
registry.setSslKeystoreLocation("/path/to/keystore.jks");
registry.setSslKeystorePassword("keystore-password");
registry.setSslKeyPassword("key-password");
registry.setCacheCapacity(1000);
registry.setRequestTimeoutMs(30000);
```

### 3. MultiKafkaManagementLibrary
Main library class that manages multiple brokers and schema registries.

```java
List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2, broker3);
List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1, registry2);

MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
```

## Connection Behavior

### **Adding Brokers and Schema Registries**
When you add a broker or schema registry, the library **automatically attempts to connect** and **validates for duplicate names**:

```java
// Adding a broker - automatically attempts to connect
NamedKafkaConfig broker = new NamedKafkaConfig("broker-1", "localhost:9092");
boolean connected = library.addBroker(broker);
// Returns true if connection successful, false if connection failed
// Throws KafkaManagementException if broker with same name already exists

// Adding a schema registry - automatically attempts to connect
NamedSchemaRegistryConfig registry = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
boolean connected = library.addSchemaRegistry(registry);
// Returns true if connection successful, false if connection failed
// Throws KafkaManagementException if schema registry with same name already exists
```

**Duplicate Name Validation:**
- Each broker must have a unique name within the broker collection
- Each schema registry must have a unique name within the schema registry collection
- Brokers and schema registries can have the same name (they are separate collections)
- Attempting to add a duplicate name throws `KafkaManagementException`

### **Removing Brokers and Schema Registries**
When you remove a broker or schema registry, the library **safely shuts down connections**:

```java
// Removing a broker - safely shuts down AdminClient connection
boolean removed = library.removeBroker("broker-1");
// Returns true if broker was found and removed, false if not found

// Removing a schema registry - safely removes client from cache
boolean removed = library.removeSchemaRegistry("registry-1");
// Returns true if registry was found and removed, false if not found
```

### **Connection Status Monitoring**
You can monitor the connection status of all brokers and schema registries:

```java
// Check connection status of all brokers
Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();

// Check connection status of all schema registries
Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();

// Check individual connection status
boolean isBrokerConnected = library.isBrokerConnected("broker-1");
boolean isRegistryConnected = library.isSchemaRegistryConnected("registry-1");

// Get lists of connected brokers and registries
List<String> connectedBrokers = library.getConnectedBrokers();
List<String> connectedRegistries = library.getConnectedSchemaRegistries();
```

## Usage Examples

### Basic Setup

```java
// Create broker configurations with SSL/JKS
NamedKafkaConfig broker1 = new NamedKafkaConfig("broker-1", "localhost:9092");
broker1.setSecurityProtocol("SSL");
broker1.setSslTruststoreLocation("/path/to/truststore.jks");
broker1.setSslTruststorePassword("truststore-password");
broker1.setSslKeystoreLocation("/path/to/keystore.jks");
broker1.setSslKeystorePassword("keystore-password");
broker1.setSslKeyPassword("key-password");

NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
broker2.setSecurityProtocol("SSL");
broker2.setSslTruststoreLocation("/path/to/truststore.jks");
broker2.setSslTruststorePassword("truststore-password");
broker2.setSslKeystoreLocation("/path/to/keystore.jks");
broker2.setSslKeystorePassword("keystore-password");
broker2.setSslKeyPassword("key-password");

// Create schema registry configurations with SSL/JKS
NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
registry1.setSecurityProtocol("SSL");
registry1.setSslTruststoreLocation("/path/to/truststore.jks");
registry1.setSslTruststorePassword("truststore-password");
registry1.setSslKeystoreLocation("/path/to/keystore.jks");
registry1.setSslKeystorePassword("keystore-password");
registry1.setSslKeyPassword("key-password");

NamedSchemaRegistryConfig registry2 = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8082");
registry2.setSecurityProtocol("SSL");
registry2.setSslTruststoreLocation("/path/to/truststore.jks");
registry2.setSslTruststorePassword("truststore-password");
registry2.setSslKeystoreLocation("/path/to/keystore.jks");
registry2.setSslKeystorePassword("keystore-password");
registry2.setSslKeyPassword("key-password");

// Initialize library
List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2);
List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1, registry2);

MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
```

### Dynamic Configuration

```java
// Start with basic configuration
MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
    "localhost:9092", "http://localhost:8081");

// Add more brokers dynamically
NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
library.addBroker(broker2);

// Add more schema registries dynamically
NamedSchemaRegistryConfig registry2 = new NamedSchemaRegistryConfig("registry-2", "http://localhost:8082");
library.addSchemaRegistry(registry2);

// Remove brokers/registries
library.removeBroker("broker-2");
library.removeSchemaRegistry("registry-2");
```

### Connection Status Monitoring

```java
// Check connection status
Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();

// Get connected brokers and registries
List<String> connectedBrokers = library.getConnectedBrokers();
List<String> connectedRegistries = library.getConnectedSchemaRegistries();

// Check individual status
boolean isBrokerConnected = library.isBrokerConnected("broker-1");
boolean isRegistryConnected = library.isSchemaRegistryConnected("registry-1");
```

### Topic Operations with Specific Brokers

```java
// Create topic on specific broker
String brokerName = "broker-1";
String topicName = "my-topic";
boolean created = library.getMultiTopicManager().createTopic(brokerName, topicName, 3, (short) 1);

// Check if topic exists on specific broker
boolean exists = library.getMultiTopicManager().topicExists(brokerName, topicName);

// List topics on specific broker
Set<String> topics = library.getMultiTopicManager().listTopics(brokerName);

// Delete topic from specific broker
boolean deleted = library.getMultiTopicManager().deleteTopic(brokerName, topicName);
```

### Message Operations with Specific Brokers

```java
// Send message to specific broker
String brokerName = "broker-1";
String topicName = "my-topic";
String key = "message-key";
String value = "message-value";

Future<RecordMetadata> future = library.getMultiMessageManager()
    .sendMessage(brokerName, topicName, key, value);

// Send message without key
Future<RecordMetadata> future2 = library.getMultiMessageManager()
    .sendMessage(brokerName, topicName, value);
```

### Reconnection Support

```java
// Reconnect to specific broker
boolean reconnected = library.reconnectBroker("broker-1");

// Reconnect to specific schema registry
boolean reconnected = library.reconnectSchemaRegistry("registry-1");

// Reconnect to all brokers and registries
library.reconnectAll();
```

## Configuration Files

### Multi-Broker Configuration (multi-kafka-config.json)

```json
{
  "brokers": [
    {
      "name": "broker-1",
      "bootstrapServers": "localhost:9092",
      "securityProtocol": "SSL",
      "clientId": "kafka-management-library-broker-1",
      "requestTimeoutMs": 30000,
      "retries": 3,
      "enableAutoCommit": true,
      "autoOffsetReset": "latest",
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    },
    {
      "name": "broker-2",
      "bootstrapServers": "localhost:9093",
      "securityProtocol": "SSL",
      "clientId": "kafka-management-library-broker-2",
      "requestTimeoutMs": 30000,
      "retries": 3,
      "enableAutoCommit": true,
      "autoOffsetReset": "latest",
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    }
  ]
}
```

### Multi-Schema Registry Configuration (multi-schema-registry-config.json)

```json
{
  "schemaRegistries": [
    {
      "name": "registry-1",
      "schemaRegistryUrl": "https://localhost:8081",
      "securityProtocol": "SSL",
      "cacheCapacity": 100,
      "requestTimeoutMs": 30000,
      "retries": 3,
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    },
    {
      "name": "registry-2",
      "schemaRegistryUrl": "https://localhost:8082",
      "securityProtocol": "SSL",
      "cacheCapacity": 100,
      "requestTimeoutMs": 30000,
      "retries": 3,
      "sslTruststoreLocation": "/path/to/truststore.jks",
      "sslTruststorePassword": "truststore-password",
      "sslKeystoreLocation": "/path/to/keystore.jks",
      "sslKeystorePassword": "keystore-password",
      "sslKeyPassword": "key-password"
    }
  ]
}
```

### Loading Configurations from Files

```java
import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.MultiJsonConfigLoader;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;

// Load broker configurations
List<NamedKafkaConfig> brokers = MultiJsonConfigLoader.loadKafkaConfigs("config/multi/multi-kafka-config.json");

// Load schema registry configurations
List<NamedSchemaRegistryConfig> registries = MultiJsonConfigLoader.loadSchemaRegistryConfigs("config/multi/multi-schema-registry-config.json");

// Create library from loaded configurations
MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
```

## Use Cases

### 1. Multi-Environment Setup
- **Development**: Connect to local Kafka and Schema Registry
- **Staging**: Connect to staging Kafka and Schema Registry
- **Production**: Connect to production Kafka and Schema Registry

### 2. High Availability
- Connect to multiple Kafka clusters for redundancy
- Connect to multiple Schema Registry instances for failover
- Automatic failover when primary connections fail

### 3. Multi-Tenant Architecture
- Different brokers for different tenants
- Different schema registries for different data types
- Isolated configurations per tenant

### 4. Disaster Recovery
- Primary and secondary Kafka clusters
- Primary and secondary Schema Registry instances
- Automatic switching during failures

## Error Handling

The library provides graceful error handling:

1. **Connection Failures**: Failed connections are marked as disconnected but don't affect other connections
2. **Reconnection**: Automatic reconnection support for failed brokers and registries
3. **Status Monitoring**: Real-time connection status monitoring
4. **Fallback**: Use first connected broker/registry when specific ones are unavailable

## Best Practices

1. **Naming Convention**: Use descriptive names for brokers and registries
2. **Configuration Management**: Use configuration files for complex setups
3. **Connection Monitoring**: Regularly monitor connection status
4. **Error Handling**: Implement proper error handling and retry logic
5. **Resource Cleanup**: Always close the library when done

## Migration from Single Broker

To migrate from the single broker library:

1. **Update imports**: Change from `com.mycompany.kafka` to `com.mycompany.kafka.multi`
2. **Replace KafkaConfig** with `NamedKafkaConfig` (from `com.mycompany.kafka.multi.config`)
3. **Replace SchemaRegistryConfig** with `NamedSchemaRegistryConfig` (from `com.mycompany.kafka.multi.config`)
4. **Replace KafkaManagementLibrary** with `MultiKafkaManagementLibrary` (from `com.mycompany.kafka.multi`)
5. **Update method calls** to include broker and registry names
6. **Update configuration files** to use the new format and place them in `config/multi/` directory

## Error Handling

### **Duplicate Name Exceptions**
When attempting to add brokers or schema registries with duplicate names:

```java
try {
    NamedKafkaConfig broker = new NamedKafkaConfig("existing-broker", "localhost:9092");
    library.addBroker(broker);
} catch (KafkaManagementException e) {
    log.error("Failed to add broker: {}", e.getMessage());
    // Handle duplicate name error
}

try {
    NamedSchemaRegistryConfig registry = new NamedSchemaRegistryConfig("existing-registry", "https://localhost:8081");
    library.addSchemaRegistry(registry);
} catch (KafkaManagementException e) {
    log.error("Failed to add schema registry: {}", e.getMessage());
    // Handle duplicate name error
}
```

### **Connection Failure Handling**
When adding brokers or registries that fail to connect:

```java
NamedKafkaConfig broker = new NamedKafkaConfig("broker-1", "unreachable:9092");
boolean connected = library.addBroker(broker);
if (!connected) {
    log.warn("Broker added but connection failed. Will be marked as disconnected.");
    // Broker is stored but marked as disconnected
    // Can be reconnected later using reconnectBroker()
}
```

## Examples

See the following example classes in `com.mycompany.kafka.multi.example`:
- `MultiBrokerExample.java` - Comprehensive examples of multi-broker usage
- `MultiConfigExample.java` - Configuration loading examples
- `ConnectionBehaviorExample.java` - Connection behavior demonstrations
- `DuplicateNameValidationExample.java` - Duplicate name validation examples

## Support

For issues or questions about the multi-broker functionality, please refer to the main documentation or create an issue in the project repository.
