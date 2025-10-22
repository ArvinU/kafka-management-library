# Auto-Register Schema Examples

This document provides comprehensive examples of using the auto-register schema functionality in the Kafka Management Library.

## Overview

The auto-register schema functionality allows you to:
- Automatically register schemas when sending messages
- Send messages with specific schema IDs for better performance
- Handle schema registration transparently
- Support both Avro and JSON Schema formats

## Basic Auto-Register Schema Example

```java
import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.manager.EnhancedMessageManager;

public class AutoRegisterSchemaExample {
    public static void main(String[] args) {
        // Initialize the library
        KafkaManagementLibrary library = new KafkaManagementLibrary(
            "localhost:9092", 
            "http://localhost:8081"
        );
        
        try {
            // Define a JSON Schema
            String userSchema = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\"type\": \"integer\"},\n" +
                "    \"name\": {\"type\": \"string\"},\n" +
                "    \"email\": {\"type\": \"string\"},\n" +
                "    \"age\": {\"type\": \"integer\"}\n" +
                "  },\n" +
                "  \"required\": [\"id\", \"name\"]\n" +
                "}";
            
            // Send message with auto schema registration
            library.getEnhancedMessageManager().sendMessageWithAutoSchema(
                "user-events", 
                "user-123", 
                "{\"id\": 123, \"name\": \"John Doe\", \"email\": \"john@example.com\", \"age\": 30}", 
                "user-event-value", 
                userSchema
            );
            
            System.out.println("Message sent with auto-registered schema!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            library.close();
        }
    }
}
```

## Using Schema ID for Better Performance

```java
import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.manager.EnhancedMessageManager;

public class SchemaIdExample {
    public static void main(String[] args) {
        KafkaManagementLibrary library = new KafkaManagementLibrary(
            "localhost:9092", 
            "http://localhost:8081"
        );
        
        try {
            // First, register a schema and get its ID
            String productSchema = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"productId\": {\"type\": \"string\"},\n" +
                "    \"name\": {\"type\": \"string\"},\n" +
                "    \"price\": {\"type\": \"number\"},\n" +
                "    \"category\": {\"type\": \"string\"}\n" +
                "  },\n" +
                "  \"required\": [\"productId\", \"name\", \"price\"]\n" +
                "}";
            
            int schemaId = library.getEnhancedMessageManager().registerSchema(
                "product-value", 
                productSchema
            );
            
            System.out.println("Schema registered with ID: " + schemaId);
            
            // Now send multiple messages using the schema ID (better performance)
            for (int i = 1; i <= 100; i++) {
                String productJson = String.format(
                    "{\"productId\": \"prod-%d\", \"name\": \"Product %d\", \"price\": %.2f, \"category\": \"Electronics\"}", 
                    i, i, 99.99 + i
                );
                
                library.getEnhancedMessageManager().sendMessageWithSchemaId(
                    "products", 
                    "product-" + i, 
                    productJson, 
                    schemaId
                );
            }
            
            System.out.println("100 messages sent with schema ID: " + schemaId);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            library.close();
        }
    }
}
```

## Avro Schema Auto-Registration

```java
import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.manager.EnhancedMessageManager;

public class AvroSchemaExample {
    public static void main(String[] args) {
        KafkaManagementLibrary library = new KafkaManagementLibrary(
            "localhost:9092", 
            "http://localhost:8081"
        );
        
        try {
            // Define an Avro schema
            String avroSchema = "{\n" +
                "  \"type\": \"record\",\n" +
                "  \"name\": \"Order\",\n" +
                "  \"namespace\": \"com.example\",\n" +
                "  \"fields\": [\n" +
                "    {\"name\": \"orderId\", \"type\": \"string\"},\n" +
                "    {\"name\": \"customerId\", \"type\": \"string\"},\n" +
                "    {\"name\": \"totalAmount\", \"type\": \"double\"},\n" +
                "    {\"name\": \"orderDate\", \"type\": \"long\"},\n" +
                "    {\"name\": \"items\", \"type\": {\"type\": \"array\", \"items\": \"string\"}}\n" +
                "  ]\n" +
                "}";
            
            // Create an order object (in real usage, this would be a proper Avro object)
            String orderJson = "{\n" +
                "  \"orderId\": \"order-123\",\n" +
                "  \"customerId\": \"customer-456\",\n" +
                "  \"totalAmount\": 299.99,\n" +
                "  \"orderDate\": 1640995200000,\n" +
                "  \"items\": [\"item1\", \"item2\", \"item3\"]\n" +
                "}";
            
            // Send Avro message with auto schema registration
            library.getEnhancedMessageManager().sendMessageWithAutoSchema(
                "orders", 
                "order-123", 
                orderJson, 
                "order-value", 
                avroSchema
            );
            
            System.out.println("Avro message sent with auto-registered schema!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            library.close();
        }
    }
}
```

## Error Handling for Auto-Register Schema

```java
import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.constants.ErrorConstants;

public class AutoRegisterSchemaErrorHandling {
    public static void main(String[] args) {
        KafkaManagementLibrary library = new KafkaManagementLibrary(
            "localhost:9092", 
            "http://localhost:8081"
        );
        
        try {
            String schema = "{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}}";
            
            // This will fail due to invalid topic name
            library.getEnhancedMessageManager().sendMessageWithAutoSchema(
                "", // Invalid topic name
                "key1", 
                "{\"id\": 123}", 
                "test-value", 
                schema
            );
            
        } catch (KafkaManagementException e) {
            if (e.getErrorCode().equals(ErrorConstants.VALIDATION_TOPIC_NAME_INVALID)) {
                System.err.println("Invalid topic name: " + e.getMessage());
            } else if (e.getErrorCode().equals(ErrorConstants.SCHEMA_REGISTRATION_FAILED)) {
                System.err.println("Schema registration failed: " + e.getMessage());
            } else {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("General error: " + e.getMessage());
        } finally {
            library.close();
        }
    }
}
```

## CLI Usage Examples

### Interactive Mode

```bash
# Start CLI in interactive mode
java -jar kafka-management-library.jar localhost:9092 http://localhost:8081

# In the CLI, use these commands:
kafka-cli> send-message-with-schema user-events user-123 '{"id":123,"name":"John"}' user-value '{"type":"object"}'
kafka-cli> send-message-with-schema-id user-events user-456 '{"id":456,"name":"Jane"}' 1
```

### Non-Interactive Mode

```bash
# Send message with auto schema registration
java -jar kafka-management-library.jar localhost:9092 http://localhost:8081 "send-message-with-schema user-events user-123 '{\"id\":123,\"name\":\"John\"}' user-value '{\"type\":\"object\"}'"

# Send message with specific schema ID
java -jar kafka-management-library.jar localhost:9092 http://localhost:8081 "send-message-with-schema-id user-events user-456 '{\"id\":456,\"name\":\"Jane\"}' 1"
```

## Best Practices

### 1. Schema Design
- Use descriptive subject names (e.g., `user-event-value`, `order-value`)
- Include required fields in your schema
- Use appropriate data types for better validation

### 2. Performance Optimization
- Register schemas once and reuse schema IDs for better performance
- Use `sendMessageWithSchemaId()` for high-throughput scenarios
- Use `sendMessageWithAutoSchema()` for development and one-off messages

### 3. Error Handling
- Always wrap schema operations in try-catch blocks
- Handle specific error codes for better user experience
- Validate schema format before sending

### 4. Schema Evolution
- Plan for schema evolution from the start
- Use compatible changes when updating schemas
- Consider using schema versioning strategies

## Advanced Usage

### Batch Processing with Schema IDs

```java
public class BatchProcessingExample {
    public static void main(String[] args) {
        KafkaManagementLibrary library = new KafkaManagementLibrary(
            "localhost:9092", 
            "http://localhost:8081"
        );
        
        try {
            // Register schema once
            String schema = "{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}}";
            int schemaId = library.getEnhancedMessageManager().registerSchema("batch-value", schema);
            
            // Send batch of messages with the same schema ID
            List<Future<RecordMetadata>> futures = new ArrayList<>();
            
            for (int i = 0; i < 1000; i++) {
                String message = "{\"id\": " + i + "}";
                Future<RecordMetadata> future = library.getEnhancedMessageManager()
                    .sendMessageWithSchemaId("batch-topic", "key-" + i, message, schemaId);
                futures.add(future);
            }
            
            // Wait for all messages to be sent
            for (Future<RecordMetadata> future : futures) {
                future.get(); // Wait for completion
            }
            
            System.out.println("Batch processing completed with schema ID: " + schemaId);
            
        } catch (Exception e) {
            System.err.println("Batch processing failed: " + e.getMessage());
        } finally {
            library.close();
        }
    }
}
```

### Schema Validation

```java
public class SchemaValidationExample {
    public static void main(String[] args) {
        KafkaManagementLibrary library = new KafkaManagementLibrary(
            "localhost:9092", 
            "http://localhost:8081"
        );
        
        try {
            // Validate schema before registration
            String schema = "{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}}";
            
            // Check if subject already exists
            if (library.getEnhancedMessageManager().subjectExists("test-value")) {
                System.out.println("Subject already exists, getting latest schema ID");
                int schemaId = library.getEnhancedMessageManager().getLatestSchemaId("test-value");
                System.out.println("Latest schema ID: " + schemaId);
            } else {
                System.out.println("Subject does not exist, registering new schema");
                int schemaId = library.getEnhancedMessageManager().registerSchema("test-value", schema);
                System.out.println("New schema registered with ID: " + schemaId);
            }
            
        } catch (Exception e) {
            System.err.println("Schema validation failed: " + e.getMessage());
        } finally {
            library.close();
        }
    }
}
```

## Troubleshooting

### Common Issues

1. **Schema Registration Fails**
   - Check if Schema Registry is running
   - Verify schema format is valid JSON
   - Ensure subject name follows naming conventions

2. **Message Sending Fails**
   - Verify topic exists
   - Check Kafka broker connectivity
   - Validate message format matches schema

3. **Schema ID Not Found**
   - Ensure schema was registered successfully
   - Check if subject exists
   - Verify schema ID is correct

### Debug Tips

- Enable debug logging for schema operations
- Use CLI to test schema registration
- Check Schema Registry UI for registered schemas
- Monitor Kafka logs for serialization errors
