# Multi-Broker Kafka Management Library

This package contains the enhanced multi-broker and multi-schema-registry implementation of the Kafka Management Library.

## Package Structure

```
com.mycompany.kafka.multi/
├── MultiKafkaManagementLibrary.java          # Main library class for multi-broker support
├── config/                                   # Configuration classes
│   ├── NamedKafkaConfig.java                # Enhanced Kafka config with unique names
│   ├── NamedSchemaRegistryConfig.java       # Enhanced Schema Registry config with unique names
│   └── MultiJsonConfigLoader.java           # Configuration loader for multiple brokers/registries
├── factory/                                  # Connection factory classes
│   └── MultiConnectionFactory.java          # Factory for creating multi-broker connections
├── manager/                                  # Manager classes
│   ├── MultiBrokerManager.java              # Manages multiple Kafka brokers
│   ├── MultiSchemaRegistryManager.java      # Manages multiple Schema Registry instances
│   ├── MultiTopicManager.java               # Topic operations with specific brokers
│   ├── MultiMessageManager.java             # Message operations with specific brokers
│   ├── MultiConsumerManager.java            # Consumer operations with specific brokers
│   ├── MultiSessionManager.java             # Session operations with specific brokers
│   ├── MultiSimpleSchemaManager.java        # Schema operations with specific registries
│   ├── MultiMessageViewer.java              # Message viewing with specific brokers
│   ├── MultiSessionViewer.java              # Session viewing with specific brokers
│   └── MultiTopicTypeManager.java           # Topic type operations with specific brokers
└── example/                                  # Example classes
    ├── MultiBrokerExample.java              # Comprehensive multi-broker examples
    └── MultiConfigExample.java              # Configuration loading examples
```

## Key Features

### 1. **Multiple Broker Support**
- Connect to multiple Kafka broker clusters simultaneously
- Each broker has a unique name for identification
- Connection status tracking for each broker
- Graceful failure handling and reconnection support

### 2. **Multiple Schema Registry Support**
- Connect to multiple Schema Registry instances simultaneously
- Each schema registry has a unique name for identification
- Connection status tracking for each schema registry
- Graceful failure handling and reconnection support

### 3. **Named Operations**
- All operations require specifying broker and/or schema registry names
- Clear separation of concerns between different brokers and registries
- Easy identification and management of connections

### 4. **Connection Management**
- Real-time connection status monitoring
- Automatic connection validation
- Reconnection support for failed connections
- Health checks for all connections

## Usage Examples

### Basic Setup
```java
import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;

// Create broker configurations
NamedKafkaConfig broker1 = new NamedKafkaConfig("broker-1", "localhost:9092");
broker1.setSecurityProtocol("SSL");
broker1.setSslTruststoreLocation("/path/to/truststore.jks");
// ... other SSL configurations

NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
broker2.setSecurityProtocol("SSL");
// ... other SSL configurations

// Create schema registry configurations
NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
registry1.setSecurityProtocol("SSL");
// ... other SSL configurations

// Initialize library
List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2);
List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1);

MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
```

### Topic Operations with Specific Brokers
```java
// Create topic on specific broker
library.getMultiTopicManager().createTopic("broker-1", "my-topic", 3, (short) 1);

// Send message to specific broker
library.getMultiMessageManager().sendMessage("broker-1", "my-topic", "key", "value");

// List topics on specific broker
Set<String> topics = library.getMultiTopicManager().listTopics("broker-1");
```

### Connection Monitoring
```java
// Check connection status
Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();

// Get connected brokers and registries
List<String> connectedBrokers = library.getConnectedBrokers();
List<String> connectedRegistries = library.getConnectedSchemaRegistries();

// Reconnect to failed broker
library.reconnectBroker("broker-1");
```

## Configuration Files

Configuration files are located in the `config/multi/` directory:

- `config/multi/multi-kafka-config.json` - Multi-broker configuration
- `config/multi/multi-schema-registry-config.json` - Multi-schema-registry configuration

## Migration from Single Broker

To migrate from the single broker library:

1. **Replace imports**: Change from `com.mycompany.kafka` to `com.mycompany.kafka.multi`
2. **Update configuration classes**: Use `NamedKafkaConfig` and `NamedSchemaRegistryConfig`
3. **Update main library class**: Use `MultiKafkaManagementLibrary`
4. **Update method calls**: Include broker and registry names in operations
5. **Update configuration files**: Use the new multi-broker format

## Examples

See the example classes for comprehensive usage demonstrations:
- `MultiBrokerExample.java` - Shows all multi-broker functionality
- `MultiConfigExample.java` - Shows configuration loading and usage

## Support

For issues or questions about the multi-broker functionality, please refer to the main documentation or create an issue in the project repository.
