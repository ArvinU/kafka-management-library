package com.mycompany.kafka.example;

import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.config.JsonConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Example demonstrating how to use JSON configuration files with the Kafka Management Library.
 * This example shows how to load configurations from JSON files and use them with the library.
 */
public class JsonConfigExample {
    
    private static final Logger log = LoggerFactory.getLogger(JsonConfigExample.class);
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java JsonConfigExample <kafka-config.json> <schema-registry-config.json>");
            System.out.println("Example: java JsonConfigExample config/kafka-config.json config/schema-registry-config.json");
            System.exit(1);
        }
        
        String kafkaConfigFile = args[0];
        String schemaRegistryConfigFile = args[1];
        
        KafkaManagementLibrary library = null;
        
        try {
            // Load configurations from JSON files
            log.info("Loading Kafka configuration from: {}", kafkaConfigFile);
            KafkaConfig kafkaConfig = JsonConfigLoader.loadKafkaConfig(kafkaConfigFile);
            
            log.info("Loading Schema Registry configuration from: {}", schemaRegistryConfigFile);
            SchemaRegistryConfig schemaRegistryConfig = JsonConfigLoader.loadSchemaRegistryConfig(schemaRegistryConfigFile);
            
            // Initialize the library with JSON configurations
            library = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
            
            // Demonstrate library functionality
            demonstrateLibraryUsage(library);
            
            log.info("JSON configuration example completed successfully!");
            
        } catch (Exception e) {
            log.error("JSON configuration example failed: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (library != null) {
                library.close();
            }
        }
    }
    
    /**
     * Demonstrates basic library usage with JSON configurations.
     */
    private static void demonstrateLibraryUsage(KafkaManagementLibrary library) {
        try {
            // List topics
            List<String> topics = library.listTopics();
            log.info("Found {} topics: {}", topics.size(), topics);
            
            // Create a test topic
            String testTopic = "json-config-test-topic";
            library.createTopic(testTopic, 3, (short) 1);
            log.info("Created test topic: {}", testTopic);
            
            // Send a test message
            library.sendMessage(testTopic, "test-key", "Test message from JSON config");
            log.info("Sent test message to topic: {}", testTopic);
            
            // Peek at messages
            List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> messages = 
                library.peekMessages(testTopic, 5);
            log.info("Peeked at {} messages from topic: {}", messages.size(), testTopic);
            
            // Get session summary
            java.util.Map<String, Object> sessionSummary = library.getSessionSummary();
            log.info("Session summary: {}", sessionSummary);
            
        } catch (Exception e) {
            log.error("Error demonstrating library usage: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Creates sample configuration files for demonstration.
     */
    public static void createSampleConfigs() {
        try {
            log.info("Creating sample configuration files...");
            
            JsonConfigLoader.createSampleKafkaConfig("sample-kafka-config.json");
            JsonConfigLoader.createSampleSchemaRegistryConfig("sample-schema-registry-config.json");
            
            log.info("Sample configuration files created:");
            log.info("  - sample-kafka-config.json");
            log.info("  - sample-schema-registry-config.json");
            
        } catch (Exception e) {
            log.error("Error creating sample configs: {}", e.getMessage(), e);
        }
    }
}
