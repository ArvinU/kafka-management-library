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
library.getMultiTopicManager().createTopic(brokerName, topicName, 3, (short) 1);

// Check if topic exists on specific broker
boolean exists = library.getMultiTopicManager().topicExists(brokerName, topicName);

// List topics on specific broker
Set<String> topics = library.getMultiTopicManager().listTopics(brokerName);

// Delete topic from specific broker
library.getMultiTopicManager().deleteTopic(brokerName, topicName);

// Create different types of topics
library.getMultiTopicTypeManager().createCompactedTopic(brokerName, "compacted-topic", 3, (short) 1);
library.getMultiTopicTypeManager().createHighThroughputTopic(brokerName, "high-throughput-topic", 6, (short) 1);
library.getMultiTopicTypeManager().createLowLatencyTopic(brokerName, "low-latency-topic", 3, (short) 1);
```

### Message Operations with Specific Brokers

```java
// Send message to specific broker
String brokerName = "broker-1";
String topicName = "my-topic";
String key = "message-key";
String value = "message-value";

// Basic message sending (consistent with original MessageManager API)
Future<RecordMetadata> future = library.getMultiEnhancedMessageManager()
    .sendMessage(brokerName, topicName, key, value);

// Send message without key (overloaded method)
Future<RecordMetadata> future2 = library.getMultiEnhancedMessageManager()
    .sendMessage(brokerName, topicName, value);

// Send Avro message (consistent with original MessageManager API)
Object avroObject = new User(123, "John Doe");
Future<RecordMetadata> avroFuture = library.getMultiEnhancedMessageManager()
    .sendAvroMessage(brokerName, topicName, key, avroObject);

// Send JSON Schema message (consistent with original MessageManager API)
Object jsonObject = new JsonObject();
Future<RecordMetadata> jsonFuture = library.getMultiEnhancedMessageManager()
    .sendJsonSchemaMessage(brokerName, topicName, key, jsonObject);

// Send message with specific schema ID (enhanced functionality)
int schemaId = 1;
Future<RecordMetadata> schemaIdFuture = library.getMultiEnhancedMessageManager()
    .sendMessageWithSchemaId(brokerName, topicName, key, value, schemaId);

// Send message with auto schema registration (enhanced functionality)
String subject = "user-value";
String schema = "{\"type\":\"object\"}";
Future<RecordMetadata> autoSchemaFuture = library.getMultiEnhancedMessageManager()
    .sendMessageWithAutoSchema(brokerName, topicName, key, value, subject, schema);
```

### Consumer Group Operations with Specific Brokers

```java
// List consumer groups on specific broker
String brokerName = "broker-1";
List<String> consumerGroups = library.getMultiConsumerManager()
    .listConsumerGroups(brokerName);

// Describe consumer group on specific broker
String groupId = "my-consumer-group";
ConsumerGroupInfo groupInfo = library.getMultiConsumerManager()
    .describeConsumerGroup(brokerName, groupId);

// Delete consumer group on specific broker
library.getMultiConsumerManager().deleteConsumerGroup(brokerName, groupId);

// Get consumer group offsets on specific broker
Map<TopicPartition, OffsetAndMetadata> offsets = library.getMultiConsumerManager()
    .getConsumerGroupOffsets(brokerName, groupId);

// Reset offsets to earliest on specific broker
library.getMultiConsumerManager().resetConsumerGroupOffsetsToEarliest(brokerName, groupId, topicPartitions);

// Reset offsets to latest on specific broker
library.getMultiConsumerManager().resetConsumerGroupOffsetsToLatest(brokerName, groupId, topicPartitions);

// Get consumer group lag on specific broker
Map<TopicPartition, Long> lag = library.getMultiConsumerManager()
    .getConsumerGroupLag(brokerName, groupId);

// Check if consumer group exists on specific broker
boolean exists = library.getMultiConsumerManager()
    .consumerGroupExists(brokerName, groupId);
```

### Session Management with Specific Brokers

```java
// Create transactional producer on specific broker
String brokerName = "broker-1";
String transactionId = "tx-1";
Producer<String, String> producer = library.getMultiSessionManager()
    .createTransactionalProducer(brokerName, transactionId);

// Create transactional consumer on specific broker
String groupId = "transactional-group";
Consumer<String, String> consumer = library.getMultiSessionManager()
    .createTransactionalConsumer(brokerName, transactionId, groupId);

// Begin transaction on specific broker
library.getMultiSessionManager().beginTransaction(brokerName, transactionId);

// Send transactional message on specific broker
Future<RecordMetadata> future = library.getMultiSessionManager()
    .sendTransactionalMessage(brokerName, transactionId, topicName, key, value);

// Commit transaction on specific broker
library.getMultiSessionManager().commitTransaction(brokerName, transactionId);

// Abort transaction on specific broker
library.getMultiSessionManager().abortTransaction(brokerName, transactionId);

// Execute transaction with automatic cleanup (enhanced functionality)
library.getMultiSessionManager().executeTransaction(brokerName, transactionId, groupId, (producer, consumer) -> {
    // Transaction logic here
    producer.send(new ProducerRecord<>(topicName, key, value));
});

// Check if transaction is active on specific broker
boolean isActive = library.getMultiSessionManager()
    .isTransactionActive(brokerName, transactionId);

// Close transactional components on specific broker
library.getMultiSessionManager().closeTransactionalProducer(brokerName, transactionId);
library.getMultiSessionManager().closeTransactionalConsumer(brokerName, transactionId);

// Close all sessions for specific broker
library.getMultiSessionManager().closeAllForBroker(brokerName);
```

### Schema Registry Operations with Specific Registries

```java
// Register schema on specific schema registry
String registryName = "registry-1";
String subject = "user-value";
String schema = "{\"type\":\"object\"}";
int schemaId = library.getMultiSimpleSchemaManager()
    .registerSchema(registryName, subject, schema);

// Get schema by ID from specific schema registry
String schemaById = library.getMultiSimpleSchemaManager()
    .getSchemaById(registryName, schemaId);

// List subjects on specific schema registry
List<String> subjects = library.getMultiSimpleSchemaManager()
    .listSubjects(registryName);

// Check if subject exists on specific schema registry
boolean subjectExists = library.getMultiSimpleSchemaManager()
    .subjectExists(registryName, subject);
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

## Consistent API Patterns

The Multi* classes provide the same API as the original manager classes, with the addition of broker/registry parameters:

### **API Consistency Benefits**

1. **Same Method Names**: Multi* classes use identical method names as original classes
2. **Same Parameter Order**: Broker/registry ID is always the first parameter
3. **Same Return Types**: Identical return types and behavior
4. **Same Exception Handling**: All classes throw `KafkaManagementException` with proper error constants
5. **Method Overloading**: Same overloaded methods for convenience

### **Example: MessageManager vs MultiEnhancedMessageManager**

```java
// Original MessageManager
messageManager.sendMessage(topicName, key, value);
messageManager.sendMessage(topicName, value);  // Overloaded
messageManager.sendAvroMessage(topicName, key, value);
messageManager.sendJsonSchemaMessage(topicName, key, value);

// MultiEnhancedMessageManager (same API + broker parameter)
multiMessageManager.sendMessage(brokerId, topicName, key, value);
multiMessageManager.sendMessage(brokerId, topicName, value);  // Overloaded
multiMessageManager.sendAvroMessage(brokerId, topicName, key, value);
multiMessageManager.sendJsonSchemaMessage(brokerId, topicName, key, value);
```

### **Enhanced Functionality**

Multi* classes also provide enhanced functionality not available in original classes:

```java
// Enhanced message operations with schema support
multiMessageManager.sendMessageWithSchemaId(brokerId, topicName, key, value, schemaId);
multiMessageManager.sendMessageWithAutoSchema(brokerId, topicName, key, value, subject, schema);

// Enhanced session management with automatic cleanup
multiSessionManager.executeTransaction(brokerId, transactionId, groupId, (producer, consumer) -> {
    // Transaction logic
});

// Enhanced topic management across multiple brokers
multiTopicTypeManager.createTopicAcrossBrokers(brokerIds, topicName, partitions, replication, template, params);
```

## Error Handling

The library provides comprehensive error handling with consistent patterns:

### **Consistent Exception Handling**

All Multi* classes throw `KafkaManagementException` with standardized error constants:

```java
try {
    multiMessageManager.sendMessage(brokerId, topicName, key, value);
} catch (KafkaManagementException e) {
    // Consistent error handling across all Multi* classes
    log.error("Failed to send message: {}", e.getMessage());
    // Error code and message are standardized
}
```

### **Error Constants**

All classes use the same error constants from `ErrorConstants`:

- **Message Operations**: `MESSAGE_SEND_FAILED`, `MESSAGE_CONSUME_FAILED`
- **Consumer Operations**: `CONSUMER_GROUP_LIST_FAILED`, `CONSUMER_GROUP_DELETE_FAILED`
- **Topic Operations**: `TOPIC_CREATION_FAILED`, `TOPIC_DELETION_FAILED`
- **Transaction Operations**: `TRANSACTION_BEGIN_FAILED`, `TRANSACTION_COMMIT_FAILED`
- **Schema Operations**: `SCHEMA_REGISTRATION_FAILED`, `SCHEMA_RETRIEVAL_FAILED`

### **Connection Error Handling**

1. **Connection Failures**: Failed connections are marked as disconnected but don't affect other connections
2. **Reconnection**: Automatic reconnection support for failed brokers and registries
3. **Status Monitoring**: Real-time connection status monitoring
4. **Fallback**: Use first connected broker/registry when specific ones are unavailable
5. **Graceful Degradation**: Operations continue with available connections

## Testing with Multi* Classes

The consistent API makes testing much easier and clearer:

### **Test Helper Class**

Use the `MultiManagerTestHelper` for convenient testing:

```java
import com.mycompany.kafka.multi.manager.MultiManagerTestHelper;

// Create test helper
MultiManagerTestHelper testHelper = new MultiManagerTestHelper(multiConnectionFactory);

// Create test topics
boolean success = testHelper.createTestTopic("broker-1", "test-topic");

// Send test messages
List<Future<RecordMetadata>> futures = testHelper.sendTestMessages("broker-1", "test-topic", 5);

// Execute test transactions
boolean txSuccess = testHelper.executeTestTransaction("broker-1", "tx-1", "group-1", "test-topic", 3);

// Clean up test resources
testHelper.cleanupTestResources("broker-1", Arrays.asList("test-topic"), Arrays.asList("group-1"));
```

### **Consistent Test Patterns**

```java
@Test
void testMessageSendingConsistency() {
    // Same API as original MessageManager, just with brokerId parameter
    Future<RecordMetadata> future = multiMessageManager.sendMessage("broker-1", "topic", "key", "value");
    assertNotNull(future);
    
    // Overloaded method works the same way
    Future<RecordMetadata> future2 = multiMessageManager.sendMessage("broker-1", "topic", "value");
    assertNotNull(future2);
}

@Test
void testErrorHandlingConsistency() {
    // Should throw KafkaManagementException (same as original managers)
    assertThrows(KafkaManagementException.class, () -> {
        multiMessageManager.sendMessage("invalid-broker", "topic", "key", "value");
    });
}
```

### **Multi-Broker Testing**

```java
@Test
void testMultiBrokerOperations() {
    List<String> brokerIds = Arrays.asList("broker-1", "broker-2", "broker-3");
    
    // Test creating topics across multiple brokers
    multiTopicTypeManager.createTopicAcrossBrokers(brokerIds, "test-topic", 3, (short) 1, "HIGH_THROUGHPUT", new HashMap<>());
    
    // Test sending messages to multiple brokers
    Map<String, String> brokerTopicMap = new HashMap<>();
    brokerTopicMap.put("broker-1", "topic-1");
    brokerTopicMap.put("broker-2", "topic-2");
    
    Map<String, List<Future<RecordMetadata>>> results = 
        testHelper.sendTestMessagesToMultipleBrokers(brokerTopicMap, 5);
    
    assertNotNull(results);
    assertEquals(2, results.size());
}
```

## Best Practices

1. **Naming Convention**: Use descriptive names for brokers and registries
2. **Configuration Management**: Use configuration files for complex setups
3. **Connection Monitoring**: Regularly monitor connection status
4. **Error Handling**: Implement proper error handling and retry logic
5. **Resource Cleanup**: Always close the library when done
6. **API Consistency**: Use the same patterns as original classes for easier migration
7. **Testing**: Use the test helper classes for consistent testing patterns
8. **Exception Handling**: Always catch `KafkaManagementException` for proper error handling

## Migration from Single Broker

To migrate from the single broker library:

### **1. Update Imports**
```java
// Before
import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.manager.MessageManager;

// After
import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.manager.MultiEnhancedMessageManager;
```

### **2. Update Configuration Classes**
```java
// Before
KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
SchemaRegistryConfig schemaConfig = new SchemaRegistryConfig("http://localhost:8081");

// After
NamedKafkaConfig kafkaConfig = new NamedKafkaConfig("broker-1", "localhost:9092");
NamedSchemaRegistryConfig schemaConfig = new NamedSchemaRegistryConfig("registry-1", "http://localhost:8081");
```

### **3. Update Main Library Class**
```java
// Before
KafkaManagementLibrary library = new KafkaManagementLibrary(kafkaConfig, schemaConfig);

// After
List<NamedKafkaConfig> brokers = Arrays.asList(kafkaConfig);
List<NamedSchemaRegistryConfig> registries = Arrays.asList(schemaConfig);
MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
```

### **4. Update Method Calls (Minimal Changes)**
The API is consistent, so you only need to add broker/registry parameters:

```java
// Before
messageManager.sendMessage(topicName, key, value);
topicManager.createTopic(topicName, partitions, replication);
consumerManager.listConsumerGroups();

// After (just add broker parameter)
multiMessageManager.sendMessage("broker-1", topicName, key, value);
multiTopicManager.createTopic("broker-1", topicName, partitions, replication);
multiConsumerManager.listConsumerGroups("broker-1");
```

### **5. Exception Handling (No Changes Needed)**
Exception handling remains the same:

```java
try {
    multiMessageManager.sendMessage("broker-1", topicName, key, value);
} catch (KafkaManagementException e) {
    // Same exception handling as before
    log.error("Failed to send message: {}", e.getMessage());
}
```

### **6. Configuration Files**
Update configuration files to use the new format and place them in `config/multi/` directory:

```json
// config/multi/multi-kafka-config.json
{
  "brokers": [
    {
      "name": "broker-1",
      "bootstrapServers": "localhost:9092"
    }
  ]
}
```

### **Migration Benefits**

1. **Minimal Code Changes**: Only need to add broker/registry parameters
2. **Same Method Names**: All method names remain identical
3. **Same Return Types**: All return types remain the same
4. **Same Exception Handling**: Exception handling patterns remain identical
5. **Enhanced Functionality**: Gain access to multi-broker features
6. **Backward Compatibility**: Original classes still work for single-broker scenarios

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
