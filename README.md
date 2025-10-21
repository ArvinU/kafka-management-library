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

### 🔧 Supported Operations

#### Topic Operations
- Create topics with custom configurations
- Delete topics
- List all topics
- Describe topic details
- Check topic existence
- Get topic configurations
- Get partition counts

#### Message Operations
- Send messages (String, Avro, JSON Schema)
- Consume messages with various deserializers
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
- Monitor consumer group health

#### Schema Registry Operations
- Register schemas (Avro, JSON, Protobuf)
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
    <version>1.0.0</version>
</dependency>
```

### Dependencies

The library includes the following dependencies:
- Apache Kafka Client 3.5.1
- Confluent Schema Registry Client 7.5.1
- Jackson for JSON processing
- SLF4J for logging

## Quick Start

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

### Topic Management Example

```java
// Create topic with custom configurations
Map<String, String> configs = new HashMap<>();
configs.put("retention.ms", "604800000"); // 7 days
configs.put("compression.type", "snappy");

library.createTopic("my-topic", 3, (short) 1, configs);

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

## Best Practices

1. **Resource Management**: Always call `close()` on the library when done
2. **Error Handling**: Wrap operations in try-catch blocks
3. **Configuration**: Use appropriate configurations for your environment
4. **Logging**: Configure logging levels appropriately
5. **Transactions**: Always commit or abort transactions
6. **SSL**: Use SSL configurations in production environments

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