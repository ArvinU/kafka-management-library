# Kafka Management Library

A comprehensive Java library for managing Confluent Kafka brokers and Schema Registry with JKS support. This library provides a unified interface for all Kafka and Schema Registry operations including topic management, message production/consumption, consumer group management, schema management, and transaction support.

## Features

### 🚀 Core Features
- **Topic Management**: Create, delete, list, and describe Kafka topics
- **Message Operations**: Produce and consume messages with various serialization formats
- **Consumer Group Management**: Manage consumer groups, offsets, and lag monitoring
- **Schema Registry Integration**: Full support for Avro, JSON Schema, and Protobuf schemas
- **Transaction Support**: Complete transactional producer and consumer support
- **JKS/SSL Support**: Secure connections with Java KeyStore (JKS) configuration
- **Multiple Serialization Formats**: String, Avro, JSON Schema support

### 🆕 New Features
- **Message Viewing**: Peek at messages without consuming them (read-only operations)
- **Topic Types**: Create different types of topics (compacted, high-throughput, low-latency, etc.)
- **Session Monitoring**: View active sessions and current consumers
- **CLI Interface**: Command-line interface for interactive and non-interactive usage
- **Multi-Broker CLI**: Comprehensive CLI for multi-broker and multi-schema registry operations
- **Advanced Message Filtering**: Filter messages by key/value patterns
- **Partition Information**: Get detailed partition and offset information
- **Consumer Health Monitoring**: Monitor consumer group health and performance
- **Auto-Register Schema**: Automatically register schemas when sending messages with schema support
- **Schema ID Management**: Send messages with specific schema IDs for better performance
- **Multi-Broker Support**: Manage multiple Kafka brokers and schema registries simultaneously
- **Consistent API**: Multi* classes provide the same API as original classes with broker/registry parameters

### 🔧 Supported Operations

#### Topic Operations
- Create topics with custom configurations
- **Create different types of topics (compacted, high-throughput, low-latency, etc.)**
- **Create topics from templates**
- Delete topics
- List all topics
- Describe topic details
- Check topic existence
- Get topic configurations
- Get partition counts

#### Message Operations
- Send messages (String, Avro, JSON Schema)
- Consume messages with various deserializers
- **Peek at messages without consuming them**
- **Filter messages by key/value patterns**
- **View messages from specific partitions**
- **Get partition and offset information**
- Partition-specific consumption
- Offset management
- Message metadata access

#### Consumer Group Operations
- List consumer groups
- Describe consumer group details
- Delete consumer groups
- Get consumer group offsets
- Reset offsets (earliest/latest)
- Calculate consumer group lag
- **Monitor consumer group health**
- **View active consumers and sessions**
- **Get session summary information**

#### Schema Registry Operations
- Register schemas (Avro, JSON, Protobuf)
- **Auto-register schemas** when sending messages
- **Send messages with specific schema IDs** for better performance
- Get schemas by ID or subject
- List subjects and schemas
- Delete subjects and schema versions
- Test schema compatibility
- Manage compatibility levels
- Global and subject-specific configurations

#### Transaction Operations
- Create transactional producers/consumers
- Begin, commit, and abort transactions
- Send messages within transactions
- Commit offsets within transactions
- Transaction state management

## Installation

### Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.mycompany</groupId>
    <artifactId>kafka-management-library</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Dependencies

The library includes the following dependencies:
- Apache Kafka Client 3.5.1
- Confluent Schema Registry Client 7.5.1
- Jackson for JSON processing
- SLF4J for logging

## Quick Start

### CLI Usage

The library now includes a command-line interface for easy interaction:

```bash
# Interactive mode
java -jar kafka-management-library.jar localhost:9092 http://localhost:8081

# Non-interactive mode
java -jar kafka-management-library.jar localhost:9092 http://localhost:8081 "topics list"
```

#### Available CLI Commands

```bash
# Topic operations
topics list                                    # List all topics
topics info <topic-name>                       # Get topic information
create-topic <name> <partitions> <replication> [type]  # Create a topic

# Message operations
messages peek <topic> [count]                  # Peek at messages
send-message <topic> <key> <value>             # Send a message
send-message-with-schema <topic> <key> <value> <subject> <schema>  # Send message with auto schema registration
send-message-with-schema-id <topic> <key> <value> <schema-id>       # Send message with specific schema ID
peek-messages <topic> [count]                  # Peek at messages (alias)

# Consumer operations
consumers list                                 # List all consumer groups
consumers info <group-id>                      # Get consumer group information
list-consumers                                 # List active consumers
consumer-health <group-id>                     # Get consumer group health

# Session operations
sessions summary                               # Get session summary
```

### Basic Usage

```java
import com.mycompany.kafka.KafkaManagementLibrary;

// Initialize the library
KafkaManagementLibrary library = new KafkaManagementLibrary(
    "localhost:9092", 
    "http://localhost:8081"
);

try {
    // Create a topic
    library.createTopic("my-topic", 3, (short) 1);
    
    // Send a message
    library.sendMessage("my-topic", "key", "Hello Kafka!");
    
    // Consume messages
    List<ConsumerRecord<String, String>> records = 
        library.consumeMessages("my-topic", "my-group", 10);
    
    // List topics
    List<String> topics = library.listTopics();
    
} finally {
    library.close();
}
```

### SSL/JKS Configuration

```java
import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;

// Configure Kafka with SSL
KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
kafkaConfig.setSecurityProtocol("SSL");
kafkaConfig.setSslTruststoreLocation("/path/to/truststore.jks");
kafkaConfig.setSslTruststorePassword("truststore-password");
kafkaConfig.setSslKeystoreLocation("/path/to/keystore.jks");
kafkaConfig.setSslKeystorePassword("keystore-password");
kafkaConfig.setSslKeyPassword("key-password");

// Configure Schema Registry with SSL
SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig("https://localhost:8081");
schemaRegistryConfig.setSecurityProtocol("SSL");
schemaRegistryConfig.setSslTruststoreLocation("/path/to/truststore.jks");
schemaRegistryConfig.setSslTruststorePassword("truststore-password");

// Create library with SSL configuration
KafkaManagementLibrary library = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
```

## API Reference

### Main Library Class

The `KafkaManagementLibrary` class provides a unified interface for all operations:

```java
public class KafkaManagementLibrary {
    // Constructors
    public KafkaManagementLibrary(String bootstrapServers, String schemaRegistryUrl)
    public KafkaManagementLibrary(KafkaConfig kafkaConfig, SchemaRegistryConfig schemaRegistryConfig)
    
    // Topic Management
    public void createTopic(String topicName, int numPartitions, short replicationFactor)
    public void createTopic(String topicName, int numPartitions, short replicationFactor, Map<String, String> configs)
    public void deleteTopic(String topicName)
    public List<String> listTopics()
    public List<TopicInfo> listTopicsWithInfo()
    public TopicInfo describeTopic(String topicName)
    public boolean topicExists(String topicName)
    
    // Message Operations
    public Future<RecordMetadata> sendMessage(String topicName, String key, String value)
    public Future<RecordMetadata> sendMessage(String topicName, String value)
    public Future<RecordMetadata> sendAvroMessage(String topicName, String key, Object value)
    public Future<RecordMetadata> sendJsonSchemaMessage(String topicName, String key, Object value)
    public List<ConsumerRecord<String, String>> consumeMessages(String topicName, String groupId, int maxRecords)
    public List<ConsumerRecord<String, Object>> consumeAvroMessages(String topicName, String groupId, int maxRecords)
    public List<ConsumerRecord<String, Object>> consumeJsonSchemaMessages(String topicName, String groupId, int maxRecords)
    
    // Consumer Group Management
    public List<String> listConsumerGroups()
    public List<ConsumerGroupInfo> listConsumerGroupsWithInfo()
    public ConsumerGroupInfo describeConsumerGroup(String groupId)
    public void deleteConsumerGroup(String groupId)
    
    // Schema Registry Operations
    public int registerSchema(String subject, String schema)
    public int registerSchema(String subject, String schema, String schemaType)
    public SchemaInfo getSchemaById(int schemaId)
    public SchemaInfo getLatestSchema(String subject)
    public List<String> listSubjects()
    public List<SchemaInfo> listSubjectsWithInfo()
    public List<Integer> deleteSubject(String subject)
    
    // Transaction Management
    public Producer<String, String> createTransactionalProducer(String transactionId)
    public Consumer<String, String> createTransactionalConsumer(String transactionId, String groupId)
    public void beginTransaction(String transactionId)
    public void commitTransaction(String transactionId)
    public void abortTransaction(String transactionId)
    
    // Manager Access
    public TopicManager getTopicManager()
    public MessageManager getMessageManager()
    public EnhancedMessageManager getEnhancedMessageManager()
    public ConsumerManager getConsumerManager()
    public SessionManager getSessionManager()
    public SchemaManager getSchemaManager()
    public ConnectionFactory getConnectionFactory()
    
    // Resource Management
    public void close()
}
```

### Manager Classes

The library is organized into specialized manager classes:

#### TopicManager
```java
public class TopicManager {
    public void createTopic(String topicName, int numPartitions, short replicationFactor, Map<String, String> configs)
    public void deleteTopic(String topicName)
    public List<String> listTopics()
    public List<TopicInfo> listTopicsWithInfo()
    public TopicInfo describeTopic(String topicName)
    public Map<String, String> getTopicConfig(String topicName)
    public boolean topicExists(String topicName)
    public int getPartitionCount(String topicName)
    public void close()
}
```

#### MessageManager
```java
public class MessageManager {
    public Future<RecordMetadata> sendMessage(String topicName, String key, String value)
    public Future<RecordMetadata> sendAvroMessage(String topicName, String key, Object value)
    public Future<RecordMetadata> sendJsonSchemaMessage(String topicName, String key, Object value)
    public List<ConsumerRecord<String, String>> consumeMessages(String topicName, String groupId, int maxRecords)
    public List<ConsumerRecord<String, Object>> consumeAvroMessages(String topicName, String groupId, int maxRecords)
    public List<ConsumerRecord<String, Object>> consumeJsonSchemaMessages(String topicName, String groupId, int maxRecords)
    public List<ConsumerRecord<String, String>> consumeMessagesFromPartition(String topicName, int partition, long offset, int maxRecords)
    public void commitOffset(String groupId, String topicName, int partition, long offset)
    public long getLatestOffset(String topicName, int partition)
}
```

#### ConsumerManager
```java
public class ConsumerManager {
    public List<String> listConsumerGroups()
    public List<ConsumerGroupInfo> listConsumerGroupsWithInfo()
    public ConsumerGroupInfo describeConsumerGroup(String groupId)
    public void deleteConsumerGroup(String groupId)
    public Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(String groupId)
    public void resetConsumerGroupOffsetsToEarliest(String groupId, Collection<TopicPartition> topicPartitions)
    public void resetConsumerGroupOffsetsToLatest(String groupId, Collection<TopicPartition> topicPartitions)
    public Map<TopicPartition, Long> getConsumerGroupLag(String groupId)
    public boolean consumerGroupExists(String groupId)
    public void close()
}
```

#### SchemaManager
```java
public class SchemaManager {
    public int registerSchema(String subject, String schema)
    public int registerSchema(String subject, String schema, String schemaType)
    public SchemaInfo getSchemaById(int schemaId)
    public SchemaInfo getLatestSchema(String subject)
    public SchemaInfo getSchemaByVersion(String subject, int version)
    public List<String> listSubjects()
    public List<SchemaInfo> listSubjectsWithInfo()
    public List<Integer> getSchemaVersions(String subject)
    public List<Integer> deleteSubject(String subject)
    public int deleteSchemaVersion(String subject, int version)
    public String getCompatibilityLevel(String subject)
    public void setCompatibilityLevel(String subject, String compatibility)
    public boolean testCompatibility(String subject, String schema)
    public boolean subjectExists(String subject)
    public String getGlobalCompatibilityLevel()
    public void setGlobalCompatibilityLevel(String compatibility)
}
```

#### SessionManager
```java
public class SessionManager {
    public Producer<String, String> createTransactionalProducer(String transactionId)
    public Consumer<String, String> createTransactionalConsumer(String transactionId, String groupId)
    public void beginTransaction(String transactionId)
    public void commitTransaction(String transactionId)
    public void abortTransaction(String transactionId)
    public Future<RecordMetadata> sendTransactionalMessage(String transactionId, String topicName, String key, String value)
    public List<Future<RecordMetadata>> sendTransactionalMessages(String transactionId, List<ProducerRecord<String, String>> messages)
    public List<ConsumerRecord<String, String>> consumeTransactionalMessages(String transactionId, String topicName, int maxRecords)
    public void commitTransactionalOffsets(String transactionId, Map<TopicPartition, OffsetAndMetadata> offsets)
    public boolean isTransactionActive(String transactionId)
    public void closeTransactionalProducer(String transactionId)
    public void closeTransactionalConsumer(String transactionId)
    public void closeAll()
}
```

#### MessageViewer
```java
public class MessageViewer {
    public List<ConsumerRecord<String, String>> peekMessages(String topicName, int maxRecords)
    public List<ConsumerRecord<String, String>> peekMessagesFromPartition(String topicName, int partition, long offset, int maxRecords)
    public List<ConsumerRecord<String, String>> peekMessagesFromEarliest(String topicName, int maxRecords)
    public List<ConsumerRecord<String, String>> peekMessagesFromLatest(String topicName, int maxRecords)
    public long getLatestOffset(String topicName, int partition)
    public long getEarliestOffset(String topicName, int partition)
    public long getMessageCount(String topicName, int partition)
    public Map<Integer, Long> getPartitionInfo(String topicName)
    public List<ConsumerRecord<String, String>> peekMessagesByKey(String topicName, String keyPattern, int maxRecords)
    public List<ConsumerRecord<String, String>> peekMessagesByValue(String topicName, String valuePattern, int maxRecords)
}
```

#### EnhancedMessageManager
```java
public class EnhancedMessageManager {
    public Future<RecordMetadata> sendMessageWithSchemaId(String topicName, String key, String value, int schemaId)
    public Future<RecordMetadata> sendMessageWithAutoSchema(String topicName, String key, String value, String subject, String schema)
    public Future<RecordMetadata> sendAvroMessageWithSchemaId(String topicName, String key, Object value, int schemaId)
    public Future<RecordMetadata> sendJsonSchemaMessageWithSchemaId(String topicName, String key, Object value, int schemaId)
    public int registerSchemaIfNotExists(String subject, String schema)
    public int registerSchema(String subject, String schema)
    public int getLatestSchemaId(String subject)
    public boolean subjectExists(String subject)
    public String getSchemaById(int schemaId)
    public ConnectionFactory getConnectionFactory()
    public SchemaRegistryClient getSchemaRegistryClient()
}
```

#### SessionViewer
```java
public class SessionViewer {
    public List<ConsumerGroupInfo> getActiveConsumerGroups()
    public ConsumerGroupInfo getConsumerGroupDetails(String groupId)
    public List<ConsumerGroupInfo.MemberInfo> getActiveConsumers()
    public Map<TopicPartition, Long> getConsumerGroupLag(String groupId)
    public Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(String groupId)
    public Map<String, Object> getSessionSummary()
    public Map<String, Object> getConsumerGroupHealth(String groupId)
    public List<Map<String, Object>> getAllConsumerGroupHealth()
}
```

#### TopicTypeManager
```java
public class TopicTypeManager {
    public void createCompactedTopic(String topicName, int numPartitions, short replicationFactor)
    public void createTimeRetentionTopic(String topicName, int numPartitions, short replicationFactor, long retentionMs)
    public void createSizeRetentionTopic(String topicName, int numPartitions, short replicationFactor, long retentionBytes)
    public void createHighThroughputTopic(String topicName, int numPartitions, short replicationFactor)
    public void createLowLatencyTopic(String topicName, int numPartitions, short replicationFactor)
    public void createDurableTopic(String topicName, int numPartitions, short replicationFactor)
    public void createEventSourcingTopic(String topicName, int numPartitions, short replicationFactor)
    public void createCDCTopic(String topicName, int numPartitions, short replicationFactor)
    public void createDeadLetterTopic(String topicName, int numPartitions, short replicationFactor)
    public void createMetricsTopic(String topicName, int numPartitions, short replicationFactor)
    public void createTopicFromTemplate(String topicName, int numPartitions, short replicationFactor, String template, Map<String, Object> templateParams)
}
```

## Configuration Classes

### KafkaConfig
```java
public class KafkaConfig {
    public KafkaConfig(String bootstrapServers)
    public Properties toProperties()
    
    // Getters and Setters
    public String getBootstrapServers()
    public void setBootstrapServers(String bootstrapServers)
    public String getSecurityProtocol()
    public void setSecurityProtocol(String securityProtocol)
    public String getSslTruststoreLocation()
    public void setSslTruststoreLocation(String sslTruststoreLocation)
    public String getSslTruststorePassword()
    public void setSslTruststorePassword(String sslTruststorePassword)
    public String getSslKeystoreLocation()
    public void setSslKeystoreLocation(String sslKeystoreLocation)
    public String getSslKeystorePassword()
    public void setSslKeystorePassword(String sslKeystorePassword)
    public String getSslKeyPassword()
    public void setSslKeyPassword(String sslKeyPassword)
    public String getSaslMechanism()
    public void setSaslMechanism(String saslMechanism)
    public String getSaslJaasConfig()
    public void setSaslJaasConfig(String saslJaasConfig)
    public String getClientId()
    public void setClientId(String clientId)
    public int getRequestTimeoutMs()
    public void setRequestTimeoutMs(int requestTimeoutMs)
    public int getRetries()
    public void setRetries(int retries)
    public boolean isEnableAutoCommit()
    public void setEnableAutoCommit(boolean enableAutoCommit)
    public String getAutoOffsetReset()
    public void setAutoOffsetReset(String autoOffsetReset)
}
```

### SchemaRegistryConfig
```java
public class SchemaRegistryConfig {
    public SchemaRegistryConfig(String schemaRegistryUrl)
    public Map<String, Object> toProperties()
    
    // Getters and Setters
    public String getSchemaRegistryUrl()
    public void setSchemaRegistryUrl(String schemaRegistryUrl)
    public String getSecurityProtocol()
    public void setSecurityProtocol(String securityProtocol)
    public String getSslTruststoreLocation()
    public void setSslTruststoreLocation(String sslTruststoreLocation)
    public String getSslTruststorePassword()
    public void setSslTruststorePassword(String sslTruststorePassword)
    public String getSslKeystoreLocation()
    public void setSslKeystoreLocation(String sslKeystoreLocation)
    public String getSslKeystorePassword()
    public void setSslKeystorePassword(String sslKeystorePassword)
    public String getSslKeyPassword()
    public void setSslKeyPassword(String sslKeyPassword)
    public String getSaslMechanism()
    public void setSaslMechanism(String saslMechanism)
    public String getSaslJaasConfig()
    public void setSaslJaasConfig(String saslJaasConfig)
    public int getCacheCapacity()
    public void setCacheCapacity(int cacheCapacity)
    public int getRequestTimeoutMs()
    public void setRequestTimeoutMs(int requestTimeoutMs)
    public int getRetries()
    public void setRetries(int retries)
}
```

## Data Transfer Objects (DTOs)

### TopicInfo
```java
public class TopicInfo {
    private String name;
    private int partitions;
    private short replicationFactor;
    private Map<String, String> configs;
    private boolean internal;
    
    // Getters and Setters
}
```

### ConsumerGroupInfo
```java
public class ConsumerGroupInfo {
    private String groupId;
    private String state;
    private String coordinator;
    private String partitionAssignor;
    private List<MemberInfo> members;
    private Map<String, String> metadata;
    
    public static class MemberInfo {
        private String memberId;
        private String clientId;
        private String host;
        private List<TopicPartitionInfo> assignments;
    }
    
    public static class TopicPartitionInfo {
        private String topic;
        private int partition;
    }
    
    // Getters and Setters
}
```

### SchemaInfo
```java
public class SchemaInfo {
    private int id;
    private int version;
    private String subject;
    private String schema;
    private String type;
    private String compatibility;
    
    // Getters and Setters
}
```

## Examples

### Complete Example

See `KafkaLibraryExample.java` for a comprehensive example demonstrating all features:

```java
import com.mycompany.kafka.example.KafkaLibraryExample;

public class MyApplication {
    public static void main(String[] args) {
        KafkaLibraryExample.main(args);
    }
}
```

### Enhanced Example with New Features

See `EnhancedKafkaLibraryExample.java` for examples of the new features:

```java
import com.mycompany.kafka.example.EnhancedKafkaLibraryExample;

public class MyEnhancedApplication {
    public static void main(String[] args) {
        EnhancedKafkaLibraryExample.main(args);
    }
}
```

### Auto-Register Schema Examples

See `AUTO_REGISTER_SCHEMA_EXAMPLES.md` for comprehensive examples of the auto-register schema functionality:

- Basic auto-register schema usage
- Performance optimization with schema IDs
- Avro schema examples
- Error handling patterns
- CLI usage examples
- Best practices and troubleshooting

### Topic Management Example

```java
// Create topic with custom configurations
Map<String, String> configs = new HashMap<>();
configs.put("retention.ms", "604800000"); // 7 days
configs.put("compression.type", "snappy");

library.createTopic("my-topic", 3, (short) 1, configs);

// Create different types of topics
library.createCompactedTopic("compacted-topic", 3, (short) 1);
library.createHighThroughputTopic("high-throughput-topic", 6, (short) 1);
library.createLowLatencyTopic("low-latency-topic", 3, (short) 1);

// Create topic from template
Map<String, Object> params = new HashMap<>();
params.put("retentionMs", 86400000L); // 1 day
library.getTopicTypeManager().createTopicFromTemplate(
    "template-topic", 3, (short) 1, "TIME_RETENTION", params);

// List topics
List<String> topics = library.listTopics();
System.out.println("Available topics: " + topics);

// Get topic information
TopicInfo topicInfo = library.describeTopic("my-topic");
System.out.println("Topic info: " + topicInfo);
```

### Message Production and Consumption Example

```java
// Send messages
library.sendMessage("my-topic", "key1", "Hello Kafka!");
library.sendMessage("my-topic", "key2", "This is a test message");

// Peek at messages without consuming them
List<ConsumerRecord<String, String>> peekedMessages = 
    library.peekMessages("my-topic", 10);

for (ConsumerRecord<String, String> record : peekedMessages) {
    System.out.println("Peeked - Key: " + record.key() + ", Value: " + record.value());
}

// Filter messages by key pattern
List<ConsumerRecord<String, String>> userMessages = 
    library.getMessageViewer().peekMessagesByKey("my-topic", "user.*", 10);

// Get partition information
Map<Integer, Long> partitionInfo = library.getMessageViewer().getPartitionInfo("my-topic");
System.out.println("Partition info: " + partitionInfo);

// Consume messages
List<ConsumerRecord<String, String>> records = 
    library.consumeMessages("my-topic", "my-group", 10);

for (ConsumerRecord<String, String> record : records) {
    System.out.println("Key: " + record.key() + ", Value: " + record.value());
}
```

### Schema Registry Example

```java
// Register Avro schema
String avroSchema = "{\n" +
    "  \"type\": \"record\",\n" +
    "  \"name\": \"User\",\n" +
    "  \"namespace\": \"com.example\",\n" +
    "  \"fields\": [\n" +
    "    {\"name\": \"id\", \"type\": \"int\"},\n" +
    "    {\"name\": \"name\", \"type\": \"string\"}\n" +
    "  ]\n" +
    "}";

int schemaId = library.registerSchema("user-value", avroSchema, "AVRO");
System.out.println("Registered schema with ID: " + schemaId);

// Get schema information
SchemaInfo schemaInfo = library.getSchemaById(schemaId);
System.out.println("Schema info: " + schemaInfo);
```

### Auto-Register Schema Example

```java
// Send message with auto schema registration
String userSchema = "{\n" +
    "  \"type\": \"object\",\n" +
    "  \"properties\": {\n" +
    "    \"id\": {\"type\": \"integer\"},\n" +
    "    \"name\": {\"type\": \"string\"},\n" +
    "    \"email\": {\"type\": \"string\"}\n" +
    "  },\n" +
    "  \"required\": [\"id\", \"name\"]\n" +
    "}";

// Auto-register schema and send message
library.getEnhancedMessageManager().sendMessageWithAutoSchema(
    "user-events", 
    "user-123", 
    "{\"id\": 123, \"name\": \"John Doe\", \"email\": \"john@example.com\"}", 
    "user-event-value", 
    userSchema
);

// Send message with specific schema ID (better performance)
int schemaId = 1; // Previously registered schema ID
library.getEnhancedMessageManager().sendMessageWithSchemaId(
    "user-events", 
    "user-456", 
    "{\"id\": 456, \"name\": \"Jane Smith\"}", 
    schemaId
);
```

### Transaction Example

```java
String transactionId = "my-transaction-1";

// Create transactional producer
Producer<String, String> producer = library.createTransactionalProducer(transactionId);

// Begin transaction
library.beginTransaction(transactionId);

// Send messages within transaction
library.sendTransactionalMessage(transactionId, "my-topic", "key1", "Transaction message 1");
library.sendTransactionalMessage(transactionId, "my-topic", "key2", "Transaction message 2");

// Commit transaction
library.commitTransaction(transactionId);

// Close transactional producer
library.getSessionManager().closeTransactionalProducer(transactionId);
```

### Session Monitoring Example

```java
// Get active consumer groups
List<ConsumerGroupInfo> activeGroups = library.getActiveConsumerGroups();
System.out.println("Active consumer groups: " + activeGroups.size());

// Get session summary
Map<String, Object> sessionSummary = library.getSessionSummary();
System.out.println("Session summary: " + sessionSummary);

// Get consumer group health
for (ConsumerGroupInfo group : activeGroups) {
    Map<String, Object> health = library.getSessionViewer().getConsumerGroupHealth(group.getGroupId());
    System.out.println("Group " + group.getGroupId() + " health: " + health);
}

// Get active consumers
List<ConsumerGroupInfo.MemberInfo> activeConsumers = library.getSessionViewer().getActiveConsumers();
System.out.println("Active consumers: " + activeConsumers.size());
```

### JSON Configuration Example

```java
// Load configurations from JSON files
KafkaConfig kafkaConfig = JsonConfigLoader.loadKafkaConfig("kafka-config.json");
SchemaRegistryConfig schemaRegistryConfig = JsonConfigLoader.loadSchemaRegistryConfig("schema-registry-config.json");

// Initialize library with JSON configurations
KafkaManagementLibrary library = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);

// Use the library normally
List<String> topics = library.listTopics();
System.out.println("Topics: " + topics);

// Create sample configuration files
JsonConfigLoader.createSampleKafkaConfig("sample-kafka-config.json");
JsonConfigLoader.createSampleSchemaRegistryConfig("sample-schema-registry-config.json");
```

## Error Handling

The library provides comprehensive error handling with detailed logging:

```java
try {
    library.createTopic("my-topic", 3, (short) 1);
} catch (RuntimeException e) {
    log.error("Failed to create topic: {}", e.getMessage(), e);
    // Handle error appropriately
}
```

## Logging

The library uses SLF4J for logging. Configure your logging framework (Logback, Log4j2, etc.) to control log levels:

```xml
<!-- Logback configuration example -->
<configuration>
    <logger name="com.mycompany.kafka" level="INFO"/>
    <logger name="org.apache.kafka" level="WARN"/>
    <logger name="io.confluent" level="WARN"/>
</configuration>
```

## CLI Interface

The library includes a comprehensive command-line interface for managing Kafka operations:

### Usage

#### Using the JAR file directly:
```bash
# Interactive mode
java -jar kafka-management-library.jar <bootstrap-servers> <schema-registry-url>

# Non-interactive mode
java -jar kafka-management-library.jar <bootstrap-servers> <schema-registry-url> "<command>"
```

#### Using the provided scripts:

**Linux/Mac:**
```bash
# Interactive mode
./scripts/run-cli.sh

# Non-interactive mode
./scripts/run-cli.sh localhost:9092 http://localhost:8081 "topics list"

# With custom servers
./scripts/run-cli.sh kafka-cluster:9092 https://schema-registry:8081

# Using JSON configuration files
./scripts/run-cli.sh config/kafka-config.json config/schema-registry-config.json

# Generate sample configuration files
./scripts/run-cli.sh --generate-configs
```

**Windows:**
```cmd
REM Interactive mode
scripts\run-cli.bat

REM Non-interactive mode
scripts\run-cli.bat localhost:9092 http://localhost:8081 "topics list"

REM With custom servers
scripts\run-cli.bat kafka-cluster:9092 https://schema-registry:8081

REM Using JSON configuration files
scripts\run-cli.bat config\kafka-config.json config\schema-registry-config.json

REM Generate sample configuration files
scripts\run-cli.bat --generate-configs
```

### Available Commands

| Command | Description | Example |
|---------|-------------|---------|
| `topics list` | List all topics | `topics list` |
| `topics info <topic>` | Get topic information | `topics info my-topic` |
| `create-topic <name> <partitions> <replication> [type]` | Create a topic | `create-topic my-topic 3 1 compacted` |
| `messages peek <topic> [count]` | Peek at messages | `messages peek my-topic 10` |
| `send-message <topic> <key> <value>` | Send a message | `send-message my-topic key1 value1` |
| `send-message-with-schema <topic> <key> <value> <subject> <schema>` | Send message with auto schema registration | `send-message-with-schema my-topic key1 '{"id":123}' user-value '{"type":"object"}'` |
| `send-message-with-schema-id <topic> <key> <value> <schema-id>` | Send message with specific schema ID | `send-message-with-schema-id my-topic key1 '{"id":123}' 1` |
| `consumers list` | List consumer groups | `consumers list` |
| `consumers info <group-id>` | Get consumer group info | `consumers info my-group` |
| `sessions summary` | Get session summary | `sessions summary` |
| `list-consumers` | List active consumers | `list-consumers` |
| `consumer-health <group-id>` | Get consumer group health | `consumer-health my-group` |

### JSON Configuration Support

The CLI supports loading configurations from JSON files, making it easy to manage different environments and complex configurations:

#### Generate Sample Configuration Files

```bash
# Generate sample configuration files
java -jar kafka-management-library.jar --generate-configs
# or
./scripts/run-cli.sh --generate-configs
```

This creates:
- `kafka-config.json` - Kafka client configuration
- `schema-registry-config.json` - Schema Registry configuration

#### Sample Kafka Configuration (JKS SSL)

```json
{
  "bootstrap.servers": "localhost:9092",
  "security.protocol": "SSL",
  "ssl.truststore.location": "/path/to/truststore.jks",
  "ssl.truststore.password": "truststore-password",
  "ssl.keystore.location": "/path/to/keystore.jks",
  "ssl.keystore.password": "keystore-password",
  "ssl.key.password": "key-password"
}
```

#### Sample Schema Registry Configuration (JKS SSL)

```json
{
  "schema.registry.url": "https://localhost:8081",
  "ssl.truststore.location": "/path/to/truststore.jks",
  "ssl.truststore.password": "truststore-password",
  "ssl.keystore.location": "/path/to/keystore.jks",
  "ssl.keystore.password": "keystore-password",
  "ssl.key.password": "key-password"
}
```

#### Minimal Configuration (No SSL)

```json
{
  "bootstrap.servers": "localhost:9092"
}
```

```json
{
  "schema.registry.url": "http://localhost:8081"
}
```

#### Using JSON Configuration Files

```bash
# Using JSON configuration files
java -jar kafka-management-library.jar kafka-config.json schema-registry-config.json

# With commands
java -jar kafka-management-library.jar kafka-config.json schema-registry-config.json "topics list"
```

### Topic Types

The CLI supports creating different types of topics:

- `compacted` - Log compacted topic
- `high-throughput` - Optimized for high message volume
- `low-latency` - Optimized for real-time processing
- `durable` - High durability guarantees
- `event-sourcing` - Long retention for event sourcing
- `cdc` - Change data capture topic
- `dead-letter` - Dead letter queue topic
- `metrics` - Metrics and monitoring data

## Best Practices

1. **Resource Management**: Always call `close()` on the library when done
2. **Error Handling**: Wrap operations in try-catch blocks
3. **Configuration**: Use appropriate configurations for your environment
4. **Logging**: Configure logging levels appropriately
5. **Transactions**: Always commit or abort transactions
6. **SSL**: Use SSL configurations in production environments
7. **Message Viewing**: Use `peekMessages()` for read-only operations
8. **Topic Types**: Choose appropriate topic types for your use case
9. **Session Monitoring**: Monitor consumer group health regularly
10. **CLI Usage**: Use the CLI for quick operations and debugging

## Multi-Broker CLI

The library includes a comprehensive Multi-Broker CLI for managing multiple Kafka brokers and schema registries:

### Multi-Broker CLI Usage

```bash
# Generate sample multi-broker configuration files
java -jar kafka-management-library.jar --generate-configs

# Interactive mode
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json

# Non-interactive mode
java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json "brokers list"
```

### Multi-Broker Commands

| Command | Description | Example |
|---------|-------------|---------|
| `brokers list` | List all brokers and their status | `brokers list` |
| `brokers status` | Show broker connection status | `brokers status` |
| `registries list` | List all schema registries and their status | `registries list` |
| `registries status` | Show schema registry connection status | `registries status` |
| `topics list <broker-id>` | List topics on specific broker | `topics list broker1` |
| `topics info <broker-id> <topic-name>` | Get topic information from specific broker | `topics info broker1 my-topic` |
| `topics create <broker-id> <name> <partitions> <replication> [type]` | Create topic on specific broker | `topics create broker1 my-topic 3 1 compacted` |
| `topics create-across <broker-ids> <name> <partitions> <replication> <template>` | Create topic across multiple brokers | `topics create-across broker1,broker2 my-topic 3 1 high-throughput` |
| `messages send <broker-id> <topic> <key> <value>` | Send message to specific broker | `messages send broker1 my-topic key1 "Hello World"` |
| `messages send-avro <broker-id> <topic> <key> <value>` | Send Avro message to specific broker | `messages send-avro broker1 my-topic key1 "{\"name\":\"John\"}"` |
| `messages send-json <broker-id> <topic> <key> <value>` | Send JSON Schema message to specific broker | `messages send-json broker1 my-topic key1 "{\"name\":\"John\"}"` |
| `messages send-with-schema <broker-id> <topic> <key> <value> <subject> <schema>` | Send message with auto schema registration | `messages send-with-schema broker1 my-topic key1 "{\"name\":\"John\"}" user-value '{"type":"object"}'` |
| `messages send-with-schema-id <broker-id> <topic> <key> <value> <schema-id>` | Send message with specific schema ID | `messages send-with-schema-id broker1 my-topic key1 "{\"name\":\"John\"}" 1` |
| `messages peek <broker-id> <topic> [count]` | Peek at messages from specific broker | `messages peek broker1 my-topic 10` |
| `consumers list <broker-id>` | List consumer groups on specific broker | `consumers list broker1` |
| `consumers info <broker-id> <group-id>` | Get consumer group information from specific broker | `consumers info broker1 my-group` |
| `consumers delete <broker-id> <group-id>` | Delete consumer group on specific broker | `consumers delete broker1 my-group` |
| `consumers reset-earliest <broker-id> <group-id>` | Reset consumer group offsets to earliest | `consumers reset-earliest broker1 my-group` |
| `consumers reset-latest <broker-id> <group-id>` | Reset consumer group offsets to latest | `consumers reset-latest broker1 my-group` |
| `consumers lag <broker-id> <group-id>` | Get consumer group lag | `consumers lag broker1 my-group` |
| `sessions create-producer <broker-id> <transaction-id>` | Create transactional producer | `sessions create-producer broker1 tx-001` |
| `sessions create-consumer <broker-id> <transaction-id> <group-id>` | Create transactional consumer | `sessions create-consumer broker1 tx-001 my-group` |
| `sessions begin <broker-id> <transaction-id>` | Begin transaction | `sessions begin broker1 tx-001` |
| `sessions commit <broker-id> <transaction-id>` | Commit transaction | `sessions commit broker1 tx-001` |
| `sessions abort <broker-id> <transaction-id>` | Abort transaction | `sessions abort broker1 tx-001` |
| `sessions execute <broker-id> <transaction-id> <group-id>` | Execute transaction with automatic cleanup | `sessions execute broker1 tx-001 my-group` |
| `schemas register <registry-id> <subject> <schema>` | Register schema on specific registry | `schemas register registry1 user-value '{"type":"object"}'` |
| `schemas get <registry-id> <schema-id>` | Get schema by ID from specific registry | `schemas get registry1 1` |
| `schemas list <registry-id>` | List subjects on specific registry | `schemas list registry1` |
| `test create-topic <broker-id> <topic-name>` | Create test topic | `test create-topic broker1 test-topic` |
| `test send-messages <broker-id> <topic-name> <count>` | Send test messages | `test send-messages broker1 test-topic 100` |
| `test execute-transaction <broker-id> <transaction-id> <group-id> <topic-name> <count>` | Execute test transaction | `test execute-transaction broker1 tx-test my-group test-topic 50` |
| `test cleanup <broker-id> <topic-names> <group-ids>` | Clean up test resources | `test cleanup broker1 test-topic my-group` |

### Multi-Broker Configuration

The Multi-Broker CLI uses JSON configuration files for both Kafka brokers and Schema Registries:

#### Multi-Broker Configuration (`multi-kafka-config.json`)
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

#### Multi-Schema Registry Configuration (`multi-schema-registry-config.json`)
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

For detailed documentation, see [docs/MULTI_CLI_GUIDE.md](docs/MULTI_CLI_GUIDE.md) and [docs/SCRIPTS_GUIDE.md](docs/SCRIPTS_GUIDE.md).

## Maven Central

The library is published to Maven Central and can be used in your projects by adding the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.arvinu</groupId>
    <artifactId>kafka-management-library</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle

For Gradle projects, add to your `build.gradle`:

```gradle
implementation 'io.github.arvinu:kafka-management-library:1.1.0'
```

### Maven Central Publishing

For information about publishing to Maven Central, see [docs/MAVEN_CENTRAL_PUBLISHING_GUIDE.md](docs/MAVEN_CENTRAL_PUBLISHING_GUIDE.md).

## Requirements

- Java 1.8 or higher
- Apache Kafka 3.5.1 or compatible
- Confluent Schema Registry 7.5.1 or compatible
- Maven 3.6 or higher

## License

This library is provided as-is for educational and development purposes.

## Support

For issues and questions, please refer to the Apache Kafka and Confluent documentation:

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Schema Registry Documentation](https://docs.confluent.io/platform/current/schema-registry/index.html)
- [Confluent Platform Documentation](https://docs.confluent.io/platform/current/index.html)