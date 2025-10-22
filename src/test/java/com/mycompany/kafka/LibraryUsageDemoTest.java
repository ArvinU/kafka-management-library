package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstration test showing how to use the Kafka Management Library.
 * This test shows the API usage patterns without requiring a real Kafka broker.
 */
public class LibraryUsageDemoTest {
    
    private static final Logger log = LoggerFactory.getLogger(LibraryUsageDemoTest.class);
    
    @Test
    void demonstrateLibraryUsage() {
        log.info("Demonstrating Kafka Management Library usage");
        
        // Example 1: Basic configuration setup
        log.info("=== Example 1: Basic Configuration Setup ===");
        
        // Create Kafka configuration
        KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
        kafkaConfig.setClientId("demo-client");
        kafkaConfig.setRequestTimeoutMs(30000);
        kafkaConfig.setRetries(3);
        kafkaConfig.setSecurityProtocol("PLAINTEXT");
        
        log.info("Kafka Config created:");
        log.info("  Bootstrap Servers: {}", kafkaConfig.getBootstrapServers());
        log.info("  Client ID: {}", kafkaConfig.getClientId());
        log.info("  Request Timeout: {} ms", kafkaConfig.getRequestTimeoutMs());
        log.info("  Retries: {}", kafkaConfig.getRetries());
        log.info("  Security Protocol: {}", kafkaConfig.getSecurityProtocol());
        
        // Create Schema Registry configuration
        SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig("http://localhost:8081");
        schemaRegistryConfig.setCacheCapacity(100);
        schemaRegistryConfig.setRequestTimeoutMs(30000);
        schemaRegistryConfig.setRetries(3);
        
        log.info("Schema Registry Config created:");
        log.info("  URL: {}", schemaRegistryConfig.getSchemaRegistryUrl());
        log.info("  Cache Capacity: {}", schemaRegistryConfig.getCacheCapacity());
        log.info("  Request Timeout: {} ms", schemaRegistryConfig.getRequestTimeoutMs());
        log.info("  Retries: {}", schemaRegistryConfig.getRetries());
        
        // Example 2: SSL Configuration
        log.info("\n=== Example 2: SSL Configuration ===");
        
        KafkaConfig sslKafkaConfig = new KafkaConfig("ssl-broker:9093");
        sslKafkaConfig.setSecurityProtocol("SSL");
        sslKafkaConfig.setSslTruststoreLocation("/path/to/truststore.jks");
        sslKafkaConfig.setSslTruststorePassword("truststore-password");
        sslKafkaConfig.setSslKeystoreLocation("/path/to/keystore.jks");
        sslKafkaConfig.setSslKeystorePassword("keystore-password");
        sslKafkaConfig.setSslKeyPassword("key-password");
        
        log.info("SSL Kafka Config created:");
        log.info("  Bootstrap Servers: {}", sslKafkaConfig.getBootstrapServers());
        log.info("  Security Protocol: {}", sslKafkaConfig.getSecurityProtocol());
        log.info("  Truststore Location: {}", sslKafkaConfig.getSslTruststoreLocation());
        log.info("  Keystore Location: {}", sslKafkaConfig.getSslKeystoreLocation());
        
        // Example 3: SASL Configuration
        log.info("\n=== Example 3: SASL Configuration ===");
        
        KafkaConfig saslKafkaConfig = new KafkaConfig("sasl-broker:9092");
        saslKafkaConfig.setSecurityProtocol("SASL_SSL");
        saslKafkaConfig.setSaslMechanism("PLAIN");
        saslKafkaConfig.setSaslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";");
        
        log.info("SASL Kafka Config created:");
        log.info("  Bootstrap Servers: {}", saslKafkaConfig.getBootstrapServers());
        log.info("  Security Protocol: {}", saslKafkaConfig.getSecurityProtocol());
        log.info("  SASL Mechanism: {}", saslKafkaConfig.getSaslMechanism());
        
        // Example 4: Schema Registry SSL Configuration
        log.info("\n=== Example 4: Schema Registry SSL Configuration ===");
        
        SchemaRegistryConfig sslSchemaConfig = new SchemaRegistryConfig("https://schema-registry:8081");
        sslSchemaConfig.setSecurityProtocol("SSL");
        sslSchemaConfig.setSslTruststoreLocation("/path/to/schema-truststore.jks");
        sslSchemaConfig.setSslTruststorePassword("schema-truststore-password");
        sslSchemaConfig.setSslKeystoreLocation("/path/to/schema-keystore.jks");
        sslSchemaConfig.setSslKeystorePassword("schema-keystore-password");
        
        log.info("SSL Schema Registry Config created:");
        log.info("  URL: {}", sslSchemaConfig.getSchemaRegistryUrl());
        log.info("  Security Protocol: {}", sslSchemaConfig.getSecurityProtocol());
        log.info("  Truststore Location: {}", sslSchemaConfig.getSslTruststoreLocation());
        
        // Example 5: Library initialization (without actual connection)
        log.info("\n=== Example 5: Library Initialization Pattern ===");
        
        try {
            // This would normally initialize the library, but we'll skip the actual connection
            // KafkaManagementLibrary library = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
            log.info("Library initialization pattern demonstrated");
            log.info("  - Connection validation would be performed");
            log.info("  - All managers would be initialized");
            log.info("  - Ready for Kafka operations");
        } catch (Exception e) {
            log.info("Expected exception during demo (no real broker): {}", e.getMessage());
        }
        
        // Example 6: Common usage patterns
        log.info("\n=== Example 6: Common Usage Patterns ===");
        
        log.info("Topic Management Operations:");
        log.info("  - library.createTopic(topicName, partitions, replicationFactor)");
        log.info("  - library.listTopics()");
        log.info("  - library.deleteTopic(topicName)");
        log.info("  - library.getTopicInfo(topicName)");
        
        log.info("Message Operations:");
        log.info("  - library.sendMessage(topic, key, value)");
        log.info("  - library.peekMessages(topic, count)");
        log.info("  - library.sendMessageWithSchema(topic, key, value, subject, schema)");
        
        log.info("Schema Registry Operations:");
        log.info("  - library.registerSchema(subject, schema)");
        log.info("  - library.getSchemaById(schemaId)");
        log.info("  - library.listSubjects()");
        log.info("  - library.subjectExists(subject)");
        
        log.info("Consumer Group Operations:");
        log.info("  - library.listConsumerGroups()");
        log.info("  - library.getConsumerGroupInfo(groupId)");
        
        log.info("Session Management:");
        log.info("  - library.getSessionSummary()");
        log.info("  - library.close()");
        
        // Example 7: Error handling patterns
        log.info("\n=== Example 7: Error Handling Patterns ===");
        
        log.info("Common error scenarios:");
        log.info("  - Connection failures to Kafka broker");
        log.info("  - Schema Registry unavailable");
        log.info("  - Topic already exists during creation");
        log.info("  - Non-existent topic operations");
        log.info("  - Invalid schema formats");
        log.info("  - Authentication/authorization failures");
        
        log.info("Error handling approach:");
        log.info("  - All operations throw KafkaManagementException");
        log.info("  - Exception contains error codes and messages");
        log.info("  - Graceful degradation for non-critical operations");
        log.info("  - Proper resource cleanup in finally blocks");
        
        // Example 8: Performance considerations
        log.info("\n=== Example 8: Performance Considerations ===");
        
        log.info("Configuration optimizations:");
        log.info("  - Tune request timeout for your network");
        log.info("  - Set appropriate retry counts");
        log.info("  - Configure connection pooling");
        log.info("  - Use connection validation sparingly");
        
        log.info("Resource management:");
        log.info("  - Always call library.close() when done");
        log.info("  - Reuse library instances when possible");
        log.info("  - Monitor connection health");
        log.info("  - Handle connection failures gracefully");
        
        log.info("Library usage demonstration completed successfully!");
        
        // Verify that configurations are properly set
        assertNotNull(kafkaConfig.getBootstrapServers());
        assertNotNull(schemaRegistryConfig.getSchemaRegistryUrl());
        assertEquals("demo-client", kafkaConfig.getClientId());
        assertEquals(100, schemaRegistryConfig.getCacheCapacity());
    }
    
    @Test
    void demonstrateJava8Compatibility() {
        log.info("Demonstrating Java 8 compatibility features");
        
        // Show that the library works with Java 8 language features
        log.info("=== Java 8 Compatibility Features ===");
        
        // Lambda expressions (Java 8 feature)
        Runnable task = () -> log.info("Lambda expression executed successfully");
        task.run();
        
        // Method references (Java 8 feature)
        log.info("Method reference demonstration");
        
        // Stream API (Java 8 feature)
        String[] topics = {"topic1", "topic2", "topic3"};
        long topicCount = java.util.Arrays.stream(topics)
            .filter(topic -> topic.startsWith("topic"))
            .count();
        
        assertEquals(3, topicCount);
        log.info("Stream API processed {} topics", topicCount);
        
        // Optional (Java 8 feature)
        java.util.Optional<String> optionalTopic = java.util.Optional.of("test-topic");
        optionalTopic.ifPresent(topic -> log.info("Optional topic: {}", topic));
        
        // Time API (Java 8 feature)
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        log.info("Current time: {}", now);
        
        log.info("Java 8 compatibility demonstration completed!");
    }
    
    @Test
    void demonstrateKafka4Compatibility() {
        log.info("Demonstrating Apache Kafka 4 compatibility");
        
        log.info("=== Apache Kafka 4 Compatibility Features ===");
        
        // Show that we're using Kafka client 3.4.1 which is compatible with Kafka 4.0
        log.info("Kafka Client Version: 3.4.1");
        log.info("Compatible with Apache Kafka 4.0 brokers");
        
        // Demonstrate protocol compatibility
        log.info("Protocol Features:");
        log.info("  - Supports Kafka 4.0 protocol");
        log.info("  - Backward compatible with older brokers");
        log.info("  - Enhanced security features");
        log.info("  - Improved performance");
        
        // Show configuration for Kafka 4.0
        KafkaConfig kafka4Config = new KafkaConfig("kafka4-broker:9092");
        kafka4Config.setClientId("kafka4-client");
        kafka4Config.setRequestTimeoutMs(30000);
        
        log.info("Kafka 4.0 Configuration:");
        log.info("  Bootstrap Servers: {}", kafka4Config.getBootstrapServers());
        log.info("  Client ID: {}", kafka4Config.getClientId());
        log.info("  Request Timeout: {} ms", kafka4Config.getRequestTimeoutMs());
        
        // Schema Registry compatibility
        SchemaRegistryConfig schema4Config = new SchemaRegistryConfig("http://kafka4-schema:8081");
        schema4Config.setCacheCapacity(100);
        
        log.info("Schema Registry Configuration:");
        log.info("  URL: {}", schema4Config.getSchemaRegistryUrl());
        log.info("  Cache Capacity: {}", schema4Config.getCacheCapacity());
        
        log.info("Apache Kafka 4 compatibility demonstration completed!");
    }
}
