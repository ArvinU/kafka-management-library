package com.mycompany.kafka.multi.example;

import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Example demonstrating the usage of MultiKafkaManagementLibrary with multiple brokers and schema registries.
 */
public class MultiBrokerExample {
    
    private static final Logger log = LoggerFactory.getLogger(MultiBrokerExample.class);
    
    public static void main(String[] args) {
        MultiKafkaManagementLibrary library = null;
        
        try {
            // Example 1: Basic setup with multiple brokers and schema registries
            demonstrateBasicSetup();
            
            // Example 2: Adding brokers and schema registries dynamically
            demonstrateDynamicConfiguration();
            
            // Example 3: Connection status monitoring
            demonstrateConnectionMonitoring();
            
            // Example 4: Graceful handling of connection failures
            demonstrateFailureHandling();
            
            // Example 5: Topic operations with specific brokers
            demonstrateTopicOperations();
            
            // Example 6: Message operations with specific brokers
            demonstrateMessageOperations();
            
            log.info("=== All multi-broker examples completed successfully! ===");
            
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
     * Demonstrates basic setup with multiple brokers and schema registries.
     */
    private static void demonstrateBasicSetup() {
        log.info("=== Example 1: Basic Setup with Multiple Brokers and Schema Registries ===");
        
        try {
            // Create multiple broker configurations with SSL/JKS
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
            
            NamedKafkaConfig broker3 = new NamedKafkaConfig("broker-3", "localhost:9094");
            broker3.setSecurityProtocol("SSL");
            broker3.setSslTruststoreLocation("/path/to/truststore.jks");
            broker3.setSslTruststorePassword("truststore-password");
            broker3.setSslKeystoreLocation("/path/to/keystore.jks");
            broker3.setSslKeystorePassword("keystore-password");
            broker3.setSslKeyPassword("key-password");
            
            // Create multiple schema registry configurations with SSL/JKS
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
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2, broker3);
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1, registry2);
            
            // Initialize the multi-broker library
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Check connection status
            Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
            Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();
            
            log.info("Broker connection status: {}", brokerStatus);
            log.info("Schema registry connection status: {}", registryStatus);
            
            // Get connected brokers and registries
            List<String> connectedBrokers = library.getConnectedBrokers();
            List<String> connectedRegistries = library.getConnectedSchemaRegistries();
            
            log.info("Connected brokers: {}", connectedBrokers);
            log.info("Connected schema registries: {}", connectedRegistries);
            
            library.close();
            
        } catch (Exception e) {
            log.error("Basic setup example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates adding brokers and schema registries dynamically.
     */
    private static void demonstrateDynamicConfiguration() {
        log.info("=== Example 2: Dynamic Configuration ===");
        
        try {
            // Start with a single broker and registry using SSL
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
                "localhost:9092", "https://localhost:8081");
            
            log.info("Initial setup - Broker count: {}, Schema registry count: {}", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            // Add more brokers dynamically with SSL
            NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
            broker2.setSecurityProtocol("SSL");
            broker2.setSslTruststoreLocation("/path/to/truststore.jks");
            broker2.setSslTruststorePassword("truststore-password");
            broker2.setSslKeystoreLocation("/path/to/keystore.jks");
            broker2.setSslKeystorePassword("keystore-password");
            broker2.setSslKeyPassword("key-password");
            
            NamedKafkaConfig broker3 = new NamedKafkaConfig("broker-3", "localhost:9094");
            broker3.setSecurityProtocol("SSL");
            broker3.setSslTruststoreLocation("/path/to/truststore.jks");
            broker3.setSslTruststorePassword("truststore-password");
            broker3.setSslKeystoreLocation("/path/to/keystore.jks");
            broker3.setSslKeystorePassword("keystore-password");
            broker3.setSslKeyPassword("key-password");
            
            library.addBroker(broker2);
            library.addBroker(broker3);
            
            // Add more schema registries dynamically with SSL
            NamedSchemaRegistryConfig registry2 = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8082");
            registry2.setSecurityProtocol("SSL");
            registry2.setSslTruststoreLocation("/path/to/truststore.jks");
            registry2.setSslTruststorePassword("truststore-password");
            registry2.setSslKeystoreLocation("/path/to/keystore.jks");
            registry2.setSslKeystorePassword("keystore-password");
            registry2.setSslKeyPassword("key-password");
            library.addSchemaRegistry(registry2);
            
            log.info("After dynamic addition - Broker count: {}, Schema registry count: {}", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            // Remove a broker
            library.removeBroker("broker-3");
            
            log.info("After removal - Broker count: {}, Schema registry count: {}", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            library.close();
            
        } catch (Exception e) {
            log.error("Dynamic configuration example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates connection status monitoring.
     */
    private static void demonstrateConnectionMonitoring() {
        log.info("=== Example 3: Connection Status Monitoring ===");
        
        try {
            // Create broker configurations with SSL (some may not be available)
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
            
            NamedKafkaConfig broker3 = new NamedKafkaConfig("broker-3", "nonexistent:9094"); // This will fail
            broker3.setSecurityProtocol("SSL");
            broker3.setSslTruststoreLocation("/path/to/truststore.jks");
            broker3.setSslTruststorePassword("truststore-password");
            broker3.setSslKeystoreLocation("/path/to/keystore.jks");
            broker3.setSslKeystorePassword("keystore-password");
            broker3.setSslKeyPassword("key-password");
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2, broker3);
            
            NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
            registry1.setSecurityProtocol("SSL");
            registry1.setSslTruststoreLocation("/path/to/truststore.jks");
            registry1.setSslTruststorePassword("truststore-password");
            registry1.setSslKeystoreLocation("/path/to/keystore.jks");
            registry1.setSslKeystorePassword("keystore-password");
            registry1.setSslKeyPassword("key-password");
            
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Monitor connection status
            Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
            Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();
            
            log.info("Broker status: {}", brokerStatus);
            log.info("Schema registry status: {}", registryStatus);
            
            // Get counts
            log.info("Total brokers: {}, Connected: {}", 
                    library.getBrokerCount(), library.getConnectedBrokerCount());
            log.info("Total schema registries: {}, Connected: {}", 
                    library.getSchemaRegistryCount(), library.getConnectedSchemaRegistryCount());
            
            // Check individual broker status
            log.info("Broker-1 connected: {}", library.isBrokerConnected("broker-1"));
            log.info("Broker-3 connected: {}", library.isBrokerConnected("broker-3"));
            
            library.close();
            
        } catch (Exception e) {
            log.error("Connection monitoring example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates graceful handling of connection failures and reconnection.
     */
    private static void demonstrateFailureHandling() {
        log.info("=== Example 4: Failure Handling and Reconnection ===");
        
        try {
            // Create broker configurations with SSL
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
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2);
            
            NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
            registry1.setSecurityProtocol("SSL");
            registry1.setSslTruststoreLocation("/path/to/truststore.jks");
            registry1.setSslTruststorePassword("truststore-password");
            registry1.setSslKeystoreLocation("/path/to/keystore.jks");
            registry1.setSslKeystorePassword("keystore-password");
            registry1.setSslKeyPassword("key-password");
            
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Check initial status
            log.info("Initial broker status: {}", library.getBrokerConnectionStatus());
            
            // Attempt to reconnect to a specific broker
            boolean reconnected = library.reconnectBroker("broker-1");
            log.info("Reconnection to broker-1 successful: {}", reconnected);
            
            // Attempt to reconnect to a non-existent broker
            try {
                library.reconnectBroker("non-existent-broker");
            } catch (Exception e) {
                log.info("Expected error when reconnecting to non-existent broker: {}", e.getMessage());
            }
            
            // Reconnect to all brokers and registries
            library.reconnectAll();
            log.info("After reconnecting all - Broker status: {}", library.getBrokerConnectionStatus());
            
            library.close();
            
        } catch (Exception e) {
            log.error("Failure handling example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates topic operations with specific brokers.
     */
    private static void demonstrateTopicOperations() {
        log.info("=== Example 5: Topic Operations with Specific Brokers ===");
        
        try {
            // Create broker configurations with SSL
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
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2);
            
            NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
            registry1.setSecurityProtocol("SSL");
            registry1.setSslTruststoreLocation("/path/to/truststore.jks");
            registry1.setSslTruststorePassword("truststore-password");
            registry1.setSslKeystoreLocation("/path/to/keystore.jks");
            registry1.setSslKeystorePassword("keystore-password");
            registry1.setSslKeyPassword("key-password");
            
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Get the first connected broker
            String connectedBroker = library.getFirstConnectedBroker();
            if (connectedBroker != null) {
                log.info("Using broker: {}", connectedBroker);
                
                // Create a topic on the specific broker
                String topicName = "multi-broker-example-topic";
                boolean created = library.getMultiTopicManager().createTopic(connectedBroker, topicName, 3, (short) 1);
                log.info("Topic '{}' created on broker '{}': {}", topicName, connectedBroker, created);
                
                // Check if topic exists
                boolean exists = library.getMultiTopicManager().topicExists(connectedBroker, topicName);
                log.info("Topic '{}' exists on broker '{}': {}", topicName, connectedBroker, exists);
                
                // List topics on the specific broker
                Set<String> topics = library.getMultiTopicManager().listTopics(connectedBroker);
                log.info("Topics on broker '{}': {}", connectedBroker, topics);
                
                // Delete the topic
                boolean deleted = library.getMultiTopicManager().deleteTopic(connectedBroker, topicName);
                log.info("Topic '{}' deleted from broker '{}': {}", topicName, connectedBroker, deleted);
                
            } else {
                log.warn("No connected brokers available for topic operations");
            }
            
            library.close();
            
        } catch (Exception e) {
            log.error("Topic operations example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates message operations with specific brokers.
     */
    private static void demonstrateMessageOperations() {
        log.info("=== Example 6: Message Operations with Specific Brokers ===");
        
        try {
            // Create broker configurations with SSL
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
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2);
            
            NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
            registry1.setSecurityProtocol("SSL");
            registry1.setSslTruststoreLocation("/path/to/truststore.jks");
            registry1.setSslTruststorePassword("truststore-password");
            registry1.setSslKeystoreLocation("/path/to/keystore.jks");
            registry1.setSslKeystorePassword("keystore-password");
            registry1.setSslKeyPassword("key-password");
            
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Get the first connected broker
            String connectedBroker = library.getFirstConnectedBroker();
            if (connectedBroker != null) {
                log.info("Using broker: {}", connectedBroker);
                
                // Create a topic first
                String topicName = "multi-broker-message-example";
                library.getMultiTopicManager().createTopic(connectedBroker, topicName, 3, (short) 1);
                
                // Send messages to the specific broker
                String messageKey = "test-key";
                String messageValue = "Hello from multi-broker library!";
                
                Future<?> future = library.getMultiMessageManager().sendMessage(connectedBroker, topicName, messageKey, messageValue);
                log.info("Message sent to topic '{}' on broker '{}'", topicName, connectedBroker);
                
                // Send message without key
                String messageValue2 = "Hello without key!";
                Future<?> future2 = library.getMultiMessageManager().sendMessage(connectedBroker, topicName, messageValue2);
                log.info("Message without key sent to topic '{}' on broker '{}'", topicName, connectedBroker);
                
                // Clean up
                library.getMultiTopicManager().deleteTopic(connectedBroker, topicName);
                
            } else {
                log.warn("No connected brokers available for message operations");
            }
            
            library.close();
            
        } catch (Exception e) {
            log.error("Message operations example failed: {}", e.getMessage());
        }
    }
}
