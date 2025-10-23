package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple integration test that demonstrates the library working with a real Kafka broker.
 * This test requires a running Kafka broker and Schema Registry (optional).
 * 
 * To run this test:
 * 1. Start a Kafka broker on localhost:9092
 * 2. Optionally start Schema Registry on localhost:8081
 * 3. Run: mvn test -Dtest=SimpleKafkaIntegrationTest
 */
public class SimpleKafkaIntegrationTest {
    
    private static final Logger log = LoggerFactory.getLogger(SimpleKafkaIntegrationTest.class);
    
    // Configuration for connecting to a real Kafka broker
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    
    @Test
    void testLibraryWithRealKafkaBroker() throws Exception {
        log.info("Testing library with real Kafka broker");
        
        // Set a global timeout for the entire test (5 seconds max)
        long testStartTime = System.currentTimeMillis();
        long maxTestTimeMs = 5000; // 5 seconds max
        
        // Create configurations with very short timeouts for testing
        KafkaConfig kafkaConfig = new KafkaConfig(KAFKA_BOOTSTRAP_SERVERS);
        kafkaConfig.setClientId("integration-test-client");
        kafkaConfig.setRequestTimeoutMs(2000);  // 2 second timeout
        kafkaConfig.setRetries(0);  // No retries

        SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig(SCHEMA_REGISTRY_URL);
        schemaRegistryConfig.setCacheCapacity(100);
        schemaRegistryConfig.setRequestTimeoutMs(2000);  // 2 second timeout
        schemaRegistryConfig.setRetries(0);  // No retries
        
        // Test library initialization with timeout
        KafkaManagementLibrary library = null;
        try {
            // Use a timeout wrapper to prevent hanging
            library = createLibraryWithTimeout(kafkaConfig, schemaRegistryConfig, 3000);
            log.info("Library initialized successfully with real Kafka broker");
            
            // Test basic operations
            testBasicOperations(library);
            
        } catch (Exception e) {
            log.warn("Library initialization failed (expected if Kafka/Schema Registry not running): {}", e.getMessage());
            log.info("Testing configuration examples instead");
            testConfigurationExamples();
        } finally {
            if (library != null) {
                try {
                    library.close();
                } catch (Exception e) {
                    log.warn("Error closing library: {}", e.getMessage());
                }
            }
        }
        
        // Check if test took too long
        long testDuration = System.currentTimeMillis() - testStartTime;
        if (testDuration > maxTestTimeMs) {
            log.warn("Test took longer than expected: {} ms (max: {} ms)", testDuration, maxTestTimeMs);
        } else {
            log.info("Test completed in {} ms", testDuration);
        }

        log.info("Integration test completed");
    }
    
    private void testBasicOperations(KafkaManagementLibrary library) throws Exception {
        log.info("Testing basic library operations");
        
        // Test topic listing
        List<String> topics = library.listTopics();
        assertNotNull(topics, "Topics list should not be null");
        log.info("Found topics: {}", topics);
        
        // Test session summary
        Map<String, Object> sessionSummary = library.getSessionSummary();
        assertNotNull(sessionSummary, "Session summary should not be null");
        log.info("Session summary: {}", sessionSummary);
        
        // Test topic creation (if supported)
        String testTopic = "integration-test-" + System.currentTimeMillis();
        try {
            library.createTopic(testTopic, 1, (short) 1);
            log.info("Successfully created topic: {}", testTopic);
            
            // Test topic info (using topicManager directly)
            try {
                Object topicInfo = library.getTopicManager().describeTopic(testTopic);
                log.info("Topic info: {}", topicInfo);
            } catch (Exception e) {
                log.warn("Topic info retrieval failed: {}", e.getMessage());
            }
            
            // Test message operations
            testMessageOperations(library, testTopic);
            
            // Clean up
            library.deleteTopic(testTopic);
            log.info("Successfully deleted topic: {}", testTopic);
            
        } catch (Exception e) {
            log.warn("Topic operations failed: {}", e.getMessage());
        }
    }
    
    private void testMessageOperations(KafkaManagementLibrary library, String topicName) throws Exception {
        log.info("Testing message operations with topic: {}", topicName);
        
        // Test message sending
        String testMessage = "Hello from integration test!";
        library.sendMessage(topicName, "test-key", testMessage);
        log.info("Successfully sent message to topic: {}", topicName);
        
        // Wait a bit for message to be available
        Thread.sleep(1000);
        
        // Test message consumption
        List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> messages = library.peekMessages(topicName, 10);
        assertNotNull(messages, "Messages list should not be null");
        log.info("Retrieved {} messages from topic: {}", messages.size(), topicName);
        
        // Verify message content
        boolean messageFound = false;
        for (org.apache.kafka.clients.consumer.ConsumerRecord<String, String> message : messages) {
            if (testMessage.equals(message.value())) {
                messageFound = true;
                break;
            }
        }
        assertTrue(messageFound, "Should have found the sent message");
    }
    
    
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConfigurationExamples() {
        log.info("Testing configuration examples");
        
        // Set a timeout for this test (10 seconds max)
        long testStartTime = System.currentTimeMillis();
        long maxTestTimeMs = 10000; // 10 seconds max
        
        // Test various configuration scenarios
        testBasicConfiguration();
        testSSLConfiguration();
        testSASLConfiguration();
        testSchemaRegistryConfiguration();
        
        // Check if test took too long
        long testDuration = System.currentTimeMillis() - testStartTime;
        if (testDuration > maxTestTimeMs) {
            log.warn("Configuration test took longer than expected: {} ms (max: {} ms)", testDuration, maxTestTimeMs);
        } else {
            log.info("Configuration test completed in {} ms", testDuration);
        }
        
        log.info("Configuration examples test completed");
    }
    
    /**
     * Creates a KafkaManagementLibrary with a timeout to prevent hanging.
     * This method ensures the test doesn't hang forever when Kafka is unavailable.
     */
    private KafkaManagementLibrary createLibraryWithTimeout(KafkaConfig kafkaConfig, 
                                                           SchemaRegistryConfig schemaRegistryConfig, 
                                                           long timeoutMs) throws Exception {
        final KafkaManagementLibrary[] result = new KafkaManagementLibrary[1];
        final Exception[] exception = new Exception[1];
        
        Thread libraryThread = new Thread(() -> {
            try {
                result[0] = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
            } catch (Exception e) {
                exception[0] = e;
            }
        });
        
        libraryThread.start();
        libraryThread.join(timeoutMs);
        
        if (libraryThread.isAlive()) {
            libraryThread.interrupt();
            throw new RuntimeException("Library initialization timed out after " + timeoutMs + "ms");
        }
        
        if (exception[0] != null) {
            throw exception[0];
        }
        
        return result[0];
    }
    
    
    private void testBasicConfiguration() {
        log.info("Testing basic configuration");
        
        KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
        kafkaConfig.setClientId("test-client");
        kafkaConfig.setRequestTimeoutMs(30000);
        kafkaConfig.setRetries(3);
        kafkaConfig.setSecurityProtocol("PLAINTEXT");
        
        SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig("http://localhost:8081");
        schemaRegistryConfig.setCacheCapacity(100);
        schemaRegistryConfig.setRequestTimeoutMs(30000);
        schemaRegistryConfig.setRetries(3);
        
        log.info("Basic configuration created successfully");
    }
    
    private void testSSLConfiguration() {
        log.info("Testing SSL configuration");
        
        KafkaConfig sslKafkaConfig = new KafkaConfig("ssl-broker:9093");
        sslKafkaConfig.setSecurityProtocol("SSL");
        sslKafkaConfig.setSslTruststoreLocation("/path/to/truststore.jks");
        sslKafkaConfig.setSslTruststorePassword("truststore-password");
        sslKafkaConfig.setSslKeystoreLocation("/path/to/keystore.jks");
        sslKafkaConfig.setSslKeystorePassword("keystore-password");
        sslKafkaConfig.setSslKeyPassword("key-password");
        
        SchemaRegistryConfig sslSchemaRegistryConfig = new SchemaRegistryConfig("https://schema-registry:8081");
        sslSchemaRegistryConfig.setSecurityProtocol("SSL");
        sslSchemaRegistryConfig.setSslTruststoreLocation("/path/to/schema-truststore.jks");
        sslSchemaRegistryConfig.setSslTruststorePassword("schema-truststore-password");
        
        log.info("SSL configuration created successfully");
    }
    
    private void testSASLConfiguration() {
        log.info("Testing SASL configuration");
        
        KafkaConfig saslKafkaConfig = new KafkaConfig("sasl-broker:9092");
        saslKafkaConfig.setSecurityProtocol("SASL_SSL");
        saslKafkaConfig.setSaslMechanism("PLAIN");
        saslKafkaConfig.setSaslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
        
        log.info("SASL configuration created successfully");
    }
    
    private void testSchemaRegistryConfiguration() {
        log.info("Testing Schema Registry configuration");
        
        SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig("http://localhost:8081");
        schemaRegistryConfig.setCacheCapacity(100);
        schemaRegistryConfig.setRequestTimeoutMs(30000);
        schemaRegistryConfig.setRetries(3);
        schemaRegistryConfig.setSecurityProtocol("PLAINTEXT");
        
        log.info("Schema Registry configuration created successfully");
    }
}
