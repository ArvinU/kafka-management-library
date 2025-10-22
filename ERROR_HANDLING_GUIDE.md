# Kafka Management Library - Error Handling Guide

This document provides comprehensive information about error handling in the Kafka Management Library, including standardized error codes, success/failure scenarios, and best practices.

## Error Code Structure

All error codes follow the pattern: `KML_[CATEGORY]_[NUMBER]`

### Categories:
- `CONN_` - Connection errors (1000-1999)
- `TOPIC_` - Topic management errors (2000-2999)
- `MSG_` - Message operations errors (3000-3999)
- `CONSUMER_` - Consumer group errors (4000-4999)
- `SCHEMA_` - Schema Registry errors (5000-5999)
- `SESSION_` - Session/Transaction errors (6000-6999)
- `CONFIG_` - Configuration errors (7000-7999)
- `VAL_` - Validation errors (8000-8999)

## Connection Management

### Constructor Success/Failure Scenarios

#### `KafkaManagementLibrary(KafkaConfig, SchemaRegistryConfig)`
- **Success**: Library initializes successfully with validated connections to both Kafka and Schema Registry
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_CONN_1001` - Kafka connection failed
  - `KML_CONN_1002` - Schema Registry connection failed
  - `KML_CONN_1003` - Connection timeout
  - `KML_CONN_1004` - Connection refused
  - `KML_CONN_1005` - SSL handshake failed
  - `KML_CONN_1006` - Authentication failed
  - `KML_CONN_1007` - Authorization failed
  - `KML_CONN_1008` - Network unreachable

#### `KafkaManagementLibrary(String, String)`
- **Success**: Library initializes with simple configuration and validates connections
- **Failure**: Same as above constructor

## Topic Management

### `createTopic(String topicName, int numPartitions, short replicationFactor)`
- **Success**: Topic is created successfully with specified partitions and replication factor
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_TOPIC_2001` - Topic creation failed
  - `KML_TOPIC_2004` - Topic already exists
  - `KML_VAL_8004` - Invalid partition count (must be > 0)
  - `KML_VAL_8005` - Invalid replication factor (must be between 1 and number of brokers)
  - `KML_VAL_8001` - Invalid topic name (null, empty, or contains invalid characters)

### `createTopic(String topicName, int numPartitions, short replicationFactor, Map<String, String> configs)`
- **Success**: Topic is created with custom configurations
- **Failure**: Same as above, plus:
  - `KML_TOPIC_2007` - Invalid topic configuration

### `deleteTopic(String topicName)`
- **Success**: Topic is deleted successfully
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_TOPIC_2002` - Topic deletion failed
  - `KML_TOPIC_2003` - Topic not found
  - `KML_VAL_8001` - Invalid topic name

### `listTopics()`
- **Success**: Returns a list of topic names from the Kafka cluster
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_TOPIC_2005` - Failed to list topics
  - `KML_CONN_1001` - Kafka connection failed

### `listTopicsWithInfo()`
- **Success**: Returns a list of TopicInfo objects with detailed information
- **Failure**: Same as `listTopics()`

### `describeTopic(String topicName)`
- **Success**: Returns TopicInfo object with topic details
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_TOPIC_2006` - Failed to describe topic
  - `KML_TOPIC_2003` - Topic not found
  - `KML_VAL_8001` - Invalid topic name

### `topicExists(String topicName)`
- **Success**: Returns true if topic exists, false otherwise
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_TOPIC_2006` - Failed to check topic existence
  - `KML_VAL_8001` - Invalid topic name

## Message Management

### `sendMessage(String topicName, String key, String value)`
- **Success**: Message is sent successfully and returns a Future containing RecordMetadata
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_MSG_3001` - Message sending failed
  - `KML_MSG_3003` - Message serialization failed
  - `KML_MSG_3008` - Message operation timeout
  - `KML_MSG_3009` - Message size exceeded
  - `KML_MSG_3010` - Producer is closed
  - `KML_TOPIC_2003` - Topic not found
  - `KML_VAL_8001` - Invalid topic name
  - `KML_VAL_8006` - Message value is null

### `sendMessage(String topicName, String value)`
- **Success**: Message is sent without key
- **Failure**: Same as above

### `sendAvroMessage(String topicName, String key, Object value)`
- **Success**: Avro message is sent successfully
- **Failure**: Same as `sendMessage()`, plus:
  - `KML_SCHEMA_5001` - Schema registration failed
  - `KML_SCHEMA_5004` - Invalid schema

### `sendJsonSchemaMessage(String topicName, String key, Object value)`
- **Success**: JSON Schema message is sent successfully
- **Failure**: Same as `sendAvroMessage()`

### `consumeMessages(String topicName, String groupId, int maxRecords)`
- **Success**: Returns a list of ConsumerRecord objects
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_MSG_3002` - Message consumption failed
  - `KML_MSG_3004` - Message deserialization failed
  - `KML_MSG_3008` - Message operation timeout
  - `KML_CONSUMER_4009` - Consumer subscription failed
  - `KML_VAL_8002` - Invalid consumer group ID
  - `KML_VAL_8009` - Invalid max records (must be > 0)

### `consumeAvroMessages(String topicName, String groupId, int maxRecords)`
- **Success**: Returns a list of Avro ConsumerRecord objects
- **Failure**: Same as `consumeMessages()`, plus:
  - `KML_SCHEMA_5002` - Schema retrieval failed
  - `KML_SCHEMA_5003` - Schema not found

### `consumeJsonSchemaMessages(String topicName, String groupId, int maxRecords)`
- **Success**: Returns a list of JSON Schema ConsumerRecord objects
- **Failure**: Same as `consumeAvroMessages()`

## Consumer Group Management

### `listConsumerGroups()`
- **Success**: Returns a list of consumer group IDs
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_CONSUMER_4002` - Failed to list consumer groups
  - `KML_CONN_1001` - Kafka connection failed

### `listConsumerGroupsWithInfo()`
- **Success**: Returns a list of ConsumerGroupInfo objects
- **Failure**: Same as `listConsumerGroups()`

### `describeConsumerGroup(String groupId)`
- **Success**: Returns ConsumerGroupInfo object with group details
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_CONSUMER_4001` - Consumer group not found
  - `KML_CONSUMER_4004` - Failed to describe consumer group
  - `KML_VAL_8002` - Invalid consumer group ID

### `deleteConsumerGroup(String groupId)`
- **Success**: Consumer group is deleted successfully
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_CONSUMER_4003` - Failed to delete consumer group
  - `KML_CONSUMER_4001` - Consumer group not found
  - `KML_VAL_8002` - Invalid consumer group ID

## Schema Registry Management

### `registerSchema(String subject, String schema)`
- **Success**: Schema is registered and returns schema ID
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SCHEMA_5001` - Schema registration failed
  - `KML_SCHEMA_5004` - Invalid schema
  - `KML_VAL_8003` - Invalid subject name
  - `KML_VAL_8007` - Schema is null
  - `KML_CONN_1002` - Schema Registry connection failed

### `registerSchema(String subject, String schema, String schemaType)`
- **Success**: Schema is registered with specific type
- **Failure**: Same as above

### `getSchemaById(int schemaId)`
- **Success**: Returns SchemaInfo object with schema details
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SCHEMA_5002` - Schema retrieval failed
  - `KML_SCHEMA_5003` - Schema not found
  - `KML_CONN_1002` - Schema Registry connection failed

### `listSubjects()`
- **Success**: Returns a list of subject names
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SCHEMA_5007` - Failed to list subjects
  - `KML_CONN_1002` - Schema Registry connection failed

### `subjectExists(String subject)`
- **Success**: Returns true if subject exists, false otherwise
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SCHEMA_5007` - Failed to check subject existence
  - `KML_VAL_8003` - Invalid subject name

## Enhanced Message Management with Auto-Register Schema

### `sendMessageWithAutoSchema(String topicName, String key, String value, String subject, String schema)`
- **Success**: Message is sent with auto-registered schema and returns Future containing RecordMetadata
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_MSG_3001` - Message sending failed
  - `KML_SCHEMA_5001` - Schema registration failed
  - `KML_SCHEMA_5004` - Invalid schema
  - `KML_VAL_8003` - Invalid subject name
  - `KML_VAL_8007` - Schema is null
  - `KML_MSG_3008` - Message operation timeout
  - `KML_TOPIC_2003` - Topic not found
  - `KML_VAL_8001` - Invalid topic name
  - `KML_VAL_8006` - Message value is null

### `sendMessageWithSchemaId(String topicName, String key, String value, int schemaId)`
- **Success**: Message is sent with specific schema ID and returns Future containing RecordMetadata
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_MSG_3001` - Message sending failed
  - `KML_SCHEMA_5002` - Schema retrieval failed
  - `KML_SCHEMA_5003` - Schema not found
  - `KML_MSG_3008` - Message operation timeout
  - `KML_TOPIC_2003` - Topic not found
  - `KML_VAL_8001` - Invalid topic name
  - `KML_VAL_8006` - Message value is null
  - `KML_VAL_8010` - Invalid schema ID (must be > 0)

### `registerSchemaIfNotExists(String subject, String schema)`
- **Success**: Schema is registered if it doesn't exist, returns schema ID
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SCHEMA_5001` - Schema registration failed
  - `KML_SCHEMA_5002` - Schema retrieval failed
  - `KML_SCHEMA_5004` - Invalid schema
  - `KML_VAL_8003` - Invalid subject name
  - `KML_VAL_8007` - Schema is null
  - `KML_CONN_1002` - Schema Registry connection failed

### `getLatestSchemaId(String subject)`
- **Success**: Returns the latest schema ID for the subject
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SCHEMA_5002` - Schema retrieval failed
  - `KML_SCHEMA_5003` - Schema not found
  - `KML_VAL_8003` - Invalid subject name
  - `KML_CONN_1002` - Schema Registry connection failed

## Session/Transaction Management

### `createTransactionalProducer(String transactionId)`
- **Success**: Returns Producer instance configured for transactions
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6006` - Transactional producer creation failed
  - `KML_VAL_8008` - Invalid transaction ID
  - `KML_CONN_1001` - Kafka connection failed

### `createTransactionalConsumer(String transactionId, String groupId)`
- **Success**: Returns Consumer instance configured for transactions
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6007` - Transactional consumer creation failed
  - `KML_VAL_8008` - Invalid transaction ID
  - `KML_VAL_8002` - Invalid consumer group ID

### `beginTransaction(String transactionId)`
- **Success**: Transaction is begun successfully
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6001` - Transaction begin failed
  - `KML_VAL_8008` - Invalid transaction ID

### `commitTransaction(String transactionId)`
- **Success**: Transaction is committed successfully
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6002` - Transaction commit failed
  - `KML_VAL_8008` - Invalid transaction ID

### `abortTransaction(String transactionId)`
- **Success**: Transaction is aborted successfully
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6003` - Transaction abort failed
  - `KML_VAL_8008` - Invalid transaction ID

## Message Viewing

### `peekMessages(String topicName, int maxRecords)`
- **Success**: Returns a list of ConsumerRecord objects without consuming them
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_MSG_3005` - Message viewing failed
  - `KML_TOPIC_2003` - Topic not found
  - `KML_VAL_8001` - Invalid topic name
  - `KML_VAL_8009` - Invalid max records

## Session Monitoring

### `getActiveConsumerGroups()`
- **Success**: Returns a list of ConsumerGroupInfo objects for active groups
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6008` - Session monitoring failed
  - `KML_CONN_1001` - Kafka connection failed

### `getSessionSummary()`
- **Success**: Returns a map containing session summary information
- **Failure**: Throws `KafkaManagementException` with error codes:
  - `KML_SESSION_6009` - Session summary failed
  - `KML_CONN_1001` - Kafka connection failed

## Topic Type Management

### `createCompactedTopic(String topicName, int numPartitions, short replicationFactor)`
- **Success**: Compacted topic is created successfully
- **Failure**: Same as `createTopic()` with additional compaction-specific errors

### `createHighThroughputTopic(String topicName, int numPartitions, short replicationFactor)`
- **Success**: High-throughput topic is created successfully
- **Failure**: Same as `createTopic()` with additional throughput-specific errors

### `createLowLatencyTopic(String topicName, int numPartitions, short replicationFactor)`
- **Success**: Low-latency topic is created successfully
- **Failure**: Same as `createTopic()` with additional latency-specific errors

## Error Handling Best Practices

### 1. Always Catch KafkaManagementException
```java
try {
    library.createTopic("my-topic", 3, (short) 1);
} catch (KafkaManagementException e) {
    // Handle specific error based on error code
    if (e.getErrorCode().equals(ErrorConstants.TOPIC_ALREADY_EXISTS)) {
        // Handle topic already exists
    } else if (e.getErrorCode().equals(ErrorConstants.KAFKA_CONNECTION_FAILED)) {
        // Handle connection failure
    }
}
```

### 2. Check Error Categories
```java
try {
    library.sendMessage("my-topic", "key", "value");
} catch (KafkaManagementException e) {
    if (e.isConnectionError()) {
        // Handle connection issues
    } else if (e.isMessageError()) {
        // Handle message-specific issues
    }
}
```

### 3. Handle Connection Failures
```java
try {
    library = new KafkaManagementLibrary("localhost:9092", "http://localhost:8081");
} catch (KafkaManagementException e) {
    if (e.isConnectionError()) {
        // Check if Kafka or Schema Registry is running
        // Verify network connectivity
        // Check configuration
    }
}
```

### 4. Validate Input Parameters
```java
if (topicName == null || topicName.trim().isEmpty()) {
    throw new KafkaManagementException(
        ErrorConstants.VALIDATION_TOPIC_NAME_INVALID,
        ErrorConstants.formatMessage(ErrorConstants.VALIDATION_TOPIC_NAME_INVALID_MSG, topicName));
}
```

### 5. Resource Cleanup
```java
KafkaManagementLibrary library = null;
try {
    library = new KafkaManagementLibrary(bootstrapServers, schemaRegistryUrl);
    // Use library
} catch (KafkaManagementException e) {
    // Handle error
} finally {
    if (library != null) {
        library.close();
    }
}
```

## Connection Failure Recovery

The library automatically validates connections before performing operations. If a connection fails:

1. **Initial Connection Failure**: The library will not initialize if connections cannot be established
2. **Runtime Connection Loss**: Operations will fail with appropriate error codes
3. **Connection Recovery**: Use `connectionFactory.resetConnectionValidation()` to revalidate connections

## Error Message Formatting

All error messages support parameter substitution:
```java
String message = ErrorConstants.formatMessage(
    ErrorConstants.TOPIC_CREATION_FAILED_MSG, 
    topicName, 
    "Topic already exists"
);
// Result: "Failed to create topic 'my-topic'. Topic already exists"
```

## Logging

The library uses SLF4J for logging. Configure your logging framework to control log levels:
```xml
<logger name="com.mycompany.kafka" level="INFO"/>
<logger name="com.mycompany.kafka.validator" level="DEBUG"/>
```

## Testing Error Scenarios

Use the following patterns to test error scenarios:

```java
// Test connection failure
try {
    new KafkaManagementLibrary("invalid:9092", "http://invalid:8081");
} catch (KafkaManagementException e) {
    assertTrue(e.isConnectionError());
}

// Test topic creation failure
try {
    library.createTopic("", 3, (short) 1); // Invalid topic name
} catch (KafkaManagementException e) {
    assertEquals(ErrorConstants.VALIDATION_TOPIC_NAME_INVALID, e.getErrorCode());
}
```
