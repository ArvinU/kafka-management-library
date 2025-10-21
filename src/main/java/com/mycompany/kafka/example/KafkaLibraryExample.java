package com.mycompany.kafka.example;

import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.dto.ConsumerGroupInfo;
import com.mycompany.kafka.dto.SchemaInfo;
import com.mycompany.kafka.dto.TopicInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Comprehensive example demonstrating all features of the Kafka Management Library.
 * This example shows how to use the library for various Kafka and Schema Registry operations.
 */
public class KafkaLibraryExample {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaLibraryExample.class);
    
    public static void main(String[] args) {
        // Configuration
        final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
        final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
        final String TEST_TOPIC_NAME = "example-topic";
        final String CONSUMER_GROUP_ID = "example-consumer-group";
        final String TRANSACTION_ID = "example-transaction-1";
        
        KafkaManagementLibrary library = null;
        
        try {
            // Initialize the library with basic configuration
            library = new KafkaManagementLibrary(KAFKA_BOOTSTRAP_SERVERS, SCHEMA_REGISTRY_URL);
            
            log.info("=== Kafka Management Library Example ===");
            
            // Example 1: Topic Management
            demonstrateTopicManagement(library, TEST_TOPIC_NAME);
            
            // Example 2: Message Production and Consumption
            demonstrateMessageOperations(library, TEST_TOPIC_NAME, CONSUMER_GROUP_ID);
            
            // Example 3: Consumer Group Management
            demonstrateConsumerGroupManagement(library, CONSUMER_GROUP_ID);
            
            // Example 4: Schema Registry Operations
            demonstrateSchemaRegistryOperations(library);
            
            // Example 5: Transaction Management
            demonstrateTransactionManagement(library, TEST_TOPIC_NAME, TRANSACTION_ID);
            
            // Example 6: SSL/JKS Configuration
            demonstrateSSLConfiguration();
            
            log.info("=== All examples completed successfully! ===");
            
        } catch (Exception e) {
            log.error("Example failed: {}", e.getMessage(), e);
        } finally {
            // Clean up resources
            if (library != null) {
                library.close();
            }
        }
    }
    
    /**
     * Demonstrates topic management operations.
     */
    private static void demonstrateTopicManagement(KafkaManagementLibrary library, String topicName) {
        log.info("\n=== Topic Management Example ===");
        
        try {
            // Check if topic exists
            boolean exists = library.topicExists(topicName);
            log.info("Topic '{}' exists: {}", topicName, exists);
            
            if (!exists) {
                // Create topic with custom configurations
                Map<String, String> configs = new HashMap<>();
                configs.put("retention.ms", "604800000"); // 7 days
                configs.put("compression.type", "snappy");
                
                library.createTopic(topicName, 3, (short) 1, configs);
                log.info("Created topic: {}", topicName);
            }
            
            // List all topics
            List<String> topics = library.listTopics();
            log.info("Available topics: {}", topics);
            
            // Get topic information
            TopicInfo topicInfo = library.describeTopic(topicName);
            log.info("Topic info: {}", topicInfo);
            
            // Get topic configuration
            Map<String, String> topicConfigs = library.getTopicManager().getTopicConfig(topicName);
            log.info("Topic configurations: {}", topicConfigs);
            
        } catch (Exception e) {
            log.error("Topic management example failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates message production and consumption operations.
     */
    private static void demonstrateMessageOperations(KafkaManagementLibrary library, String topicName, String groupId) {
        log.info("\n=== Message Operations Example ===");
        
        try {
            // Send messages
            log.info("Sending messages to topic: {}", topicName);
            
            Future<RecordMetadata> future1 = library.sendMessage(topicName, "key1", "Hello Kafka!");
            Future<RecordMetadata> future2 = library.sendMessage(topicName, "key2", "This is a test message");
            Future<RecordMetadata> future3 = library.sendMessage(topicName, "key3", "Kafka Management Library");
            
            // Wait for messages to be sent
            RecordMetadata metadata1 = future1.get();
            RecordMetadata metadata2 = future2.get();
            RecordMetadata metadata3 = future3.get();
            
            log.info("Message 1 sent to partition: {} offset: {}", metadata1.partition(), metadata1.offset());
            log.info("Message 2 sent to partition: {} offset: {}", metadata2.partition(), metadata2.offset());
            log.info("Message 3 sent to partition: {} offset: {}", metadata3.partition(), metadata3.offset());
            
            // Consume messages
            log.info("Consuming messages from topic: {}", topicName);
            List<ConsumerRecord<String, String>> records = library.consumeMessages(topicName, groupId, 10);
            
            log.info("Consumed {} messages:", records.size());
            for (ConsumerRecord<String, String> record : records) {
                log.info("  Key: {}, Value: {}, Partition: {}, Offset: {}", 
                    record.key(), record.value(), record.partition(), record.offset());
            }
            
        } catch (Exception e) {
            log.error("Message operations example failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates consumer group management operations.
     */
    private static void demonstrateConsumerGroupManagement(KafkaManagementLibrary library, String groupId) {
        log.info("\n=== Consumer Group Management Example ===");
        
        try {
            // List consumer groups
            List<String> consumerGroups = library.listConsumerGroups();
            log.info("Available consumer groups: {}", consumerGroups);
            
            // Get consumer group information
            if (consumerGroups.contains(groupId)) {
                ConsumerGroupInfo groupInfo = library.describeConsumerGroup(groupId);
                log.info("Consumer group info: {}", groupInfo);
                
                // Get consumer group offsets
                Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets = 
                    library.getConsumerManager().getConsumerGroupOffsets(groupId);
                log.info("Consumer group offsets: {}", offsets);
                
                // Get consumer group lag
                Map<org.apache.kafka.common.TopicPartition, Long> lag = 
                    library.getConsumerManager().getConsumerGroupLag(groupId);
                log.info("Consumer group lag: {}", lag);
            }
            
        } catch (Exception e) {
            log.error("Consumer group management example failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates Schema Registry operations.
     */
    private static void demonstrateSchemaRegistryOperations(KafkaManagementLibrary library) {
        log.info("\n=== Schema Registry Operations Example ===");
        
        try {
            // Define an Avro schema
            String avroSchema = "{\n" +
                "  \"type\": \"record\",\n" +
                "  \"name\": \"User\",\n" +
                "  \"namespace\": \"com.example\",\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"name\": \"id\",\n" +
                "      \"type\": \"int\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"name\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"email\",\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
            
            // Register schema
            String subject = "user-value";
            int schemaId = library.registerSchema(subject, avroSchema, "AVRO");
            log.info("Registered schema with ID: {} for subject: {}", schemaId, subject);
            
            // Get schema by ID
            SchemaInfo schemaInfo = library.getSchemaById(schemaId);
            log.info("Schema info: {}", schemaInfo);
            
            // Get schema by ID (mock)
            SchemaInfo latestSchema = library.getSchemaById(schemaId);
            log.info("Schema by ID: {}", latestSchema);
            
            // List all subjects
            List<String> subjects = library.listSubjects();
            log.info("Available subjects: {}", subjects);
            
            // Check if subject exists
            boolean subjectExists = library.subjectExists(subject);
            log.info("Subject '{}' exists: {}", subject, subjectExists);
            
        } catch (Exception e) {
            log.error("Schema Registry operations example failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates transaction management operations.
     */
    private static void demonstrateTransactionManagement(KafkaManagementLibrary library, String topicName, String transactionId) {
        log.info("\n=== Transaction Management Example ===");
        
        try {
            // Create transactional producer
            library.createTransactionalProducer(transactionId);
            
            // Begin transaction
            library.beginTransaction(transactionId);
            log.info("Started transaction: {}", transactionId);
            
            // Send messages within transaction
            library.getSessionManager().sendTransactionalMessage(transactionId, topicName, "tx-key1", "Transaction message 1");
            library.getSessionManager().sendTransactionalMessage(transactionId, topicName, "tx-key2", "Transaction message 2");
            log.info("Sent messages within transaction: {}", transactionId);
            
            // Commit transaction
            library.commitTransaction(transactionId);
            log.info("Committed transaction: {}", transactionId);
            
            // Close transactional producer
            library.getSessionManager().closeTransactionalProducer(transactionId);
            log.info("Closed transactional producer for transaction: {}", transactionId);
            
        } catch (Exception e) {
            log.error("Transaction management example failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates SSL/JKS configuration.
     */
    private static void demonstrateSSLConfiguration() {
        log.info("\n=== SSL/JKS Configuration Example ===");
        
        try {
            // Create Kafka configuration with SSL
            KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
            kafkaConfig.setSecurityProtocol("SSL");
            kafkaConfig.setSslTruststoreLocation("/path/to/truststore.jks");
            kafkaConfig.setSslTruststorePassword("truststore-password");
            kafkaConfig.setSslKeystoreLocation("/path/to/keystore.jks");
            kafkaConfig.setSslKeystorePassword("keystore-password");
            kafkaConfig.setSslKeyPassword("key-password");
            
            // Create Schema Registry configuration with SSL
            SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig("https://localhost:8081");
            schemaRegistryConfig.setSecurityProtocol("SSL");
            schemaRegistryConfig.setSslTruststoreLocation("/path/to/truststore.jks");
            schemaRegistryConfig.setSslTruststorePassword("truststore-password");
            schemaRegistryConfig.setSslKeystoreLocation("/path/to/keystore.jks");
            schemaRegistryConfig.setSslKeystorePassword("keystore-password");
            schemaRegistryConfig.setSslKeyPassword("key-password");
            
            // Create library with SSL configuration
            KafkaManagementLibrary sslLibrary = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
            
            log.info("SSL configuration example completed");
            log.info("Kafka SSL properties: {}", kafkaConfig.toProperties());
            log.info("Schema Registry SSL properties: {}", schemaRegistryConfig.toProperties());
            
            // Close SSL library
            sslLibrary.close();
            
        } catch (Exception e) {
            log.error("SSL configuration example failed: {}", e.getMessage(), e);
        }
    }
}
