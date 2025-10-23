package com.mycompany.kafka.multi.example;

import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating the connection behavior when adding and removing brokers and schema registries.
 */
public class ConnectionBehaviorExample {
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionBehaviorExample.class);
    
    public static void main(String[] args) {
        MultiKafkaManagementLibrary library = null;
        
        try {
            // Example 1: Adding brokers with automatic connection
            demonstrateAddingBrokers();
            
            // Example 2: Adding schema registries with automatic connection
            demonstrateAddingSchemaRegistries();
            
            // Example 3: Removing brokers with safe shutdown
            demonstrateRemovingBrokers();
            
            // Example 4: Removing schema registries with safe shutdown
            demonstrateRemovingSchemaRegistries();
            
            // Example 5: Connection status monitoring
            demonstrateConnectionMonitoring();
            
            log.info("=== All connection behavior examples completed successfully! ===");
            
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
     * Demonstrates adding brokers with automatic connection behavior.
     */
    private static void demonstrateAddingBrokers() {
        log.info("=== Example 1: Adding Brokers with Automatic Connection ===");
        
        try {
            // Create library with initial broker
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
                "localhost:9092", "https://localhost:8081");
            
            log.info("Initial setup - Broker count: {}, Connected: {}", 
                    library.getBrokerCount(), library.getConnectedBrokerCount());
            
            // Add a new broker - it will automatically attempt to connect
            NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
            broker2.setSecurityProtocol("SSL");
            broker2.setSslTruststoreLocation("/path/to/truststore.jks");
            broker2.setSslTruststorePassword("truststore-password");
            broker2.setSslKeystoreLocation("/path/to/keystore.jks");
            broker2.setSslKeystorePassword("keystore-password");
            broker2.setSslKeyPassword("key-password");
            
            boolean connected = library.addBroker(broker2);
            log.info("Added broker-2 - Connection successful: {}", connected);
            
            // Add a broker that will fail to connect (non-existent)
            NamedKafkaConfig broker3 = new NamedKafkaConfig("broker-3", "nonexistent:9094");
            broker3.setSecurityProtocol("SSL");
            broker3.setSslTruststoreLocation("/path/to/truststore.jks");
            broker3.setSslTruststorePassword("truststore-password");
            broker3.setSslKeystoreLocation("/path/to/keystore.jks");
            broker3.setSslKeystorePassword("keystore-password");
            broker3.setSslKeyPassword("key-password");
            
            boolean connected3 = library.addBroker(broker3);
            log.info("Added broker-3 (non-existent) - Connection successful: {}", connected3);
            
            // Try to add a broker with duplicate name - should throw exception
            try {
                NamedKafkaConfig duplicateBroker = new NamedKafkaConfig("broker-2", "localhost:9095");
                duplicateBroker.setSecurityProtocol("SSL");
                duplicateBroker.setSslTruststoreLocation("/path/to/truststore.jks");
                duplicateBroker.setSslTruststorePassword("truststore-password");
                duplicateBroker.setSslKeystoreLocation("/path/to/keystore.jks");
                duplicateBroker.setSslKeystorePassword("keystore-password");
                duplicateBroker.setSslKeyPassword("key-password");
                
                library.addBroker(duplicateBroker);
                log.error("ERROR: Should have thrown exception for duplicate broker name!");
            } catch (KafkaManagementException e) {
                log.info("SUCCESS: Caught expected exception for duplicate broker name: {}", e.getMessage());
            }
            
            // Show final status
            log.info("Final setup - Broker count: {}, Connected: {}", 
                    library.getBrokerCount(), library.getConnectedBrokerCount());
            
            Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
            log.info("Broker connection status: {}", brokerStatus);
            
            library.close();
            
        } catch (Exception e) {
            log.error("Adding brokers example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates adding schema registries with automatic connection behavior.
     */
    private static void demonstrateAddingSchemaRegistries() {
        log.info("=== Example 2: Adding Schema Registries with Automatic Connection ===");
        
        try {
            // Create library with initial schema registry
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
                "localhost:9092", "https://localhost:8081");
            
            log.info("Initial setup - Schema registry count: {}, Connected: {}", 
                    library.getSchemaRegistryCount(), library.getConnectedSchemaRegistryCount());
            
            // Add a new schema registry - it will automatically attempt to connect
            NamedSchemaRegistryConfig registry2 = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8082");
            registry2.setSecurityProtocol("SSL");
            registry2.setSslTruststoreLocation("/path/to/truststore.jks");
            registry2.setSslTruststorePassword("truststore-password");
            registry2.setSslKeystoreLocation("/path/to/keystore.jks");
            registry2.setSslKeystorePassword("keystore-password");
            registry2.setSslKeyPassword("key-password");
            
            boolean connected = library.addSchemaRegistry(registry2);
            log.info("Added registry-2 - Connection successful: {}", connected);
            
            // Add a schema registry that will fail to connect (non-existent)
            NamedSchemaRegistryConfig registry3 = new NamedSchemaRegistryConfig("registry-3", "https://nonexistent:8083");
            registry3.setSecurityProtocol("SSL");
            registry3.setSslTruststoreLocation("/path/to/truststore.jks");
            registry3.setSslTruststorePassword("truststore-password");
            registry3.setSslKeystoreLocation("/path/to/keystore.jks");
            registry3.setSslKeystorePassword("keystore-password");
            registry3.setSslKeyPassword("key-password");
            
            boolean connected3 = library.addSchemaRegistry(registry3);
            log.info("Added registry-3 (non-existent) - Connection successful: {}", connected3);
            
            // Try to add a schema registry with duplicate name - should throw exception
            try {
                NamedSchemaRegistryConfig duplicateRegistry = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8084");
                duplicateRegistry.setSecurityProtocol("SSL");
                duplicateRegistry.setSslTruststoreLocation("/path/to/truststore.jks");
                duplicateRegistry.setSslTruststorePassword("truststore-password");
                duplicateRegistry.setSslKeystoreLocation("/path/to/keystore.jks");
                duplicateRegistry.setSslKeystorePassword("keystore-password");
                duplicateRegistry.setSslKeyPassword("key-password");
                
                library.addSchemaRegistry(duplicateRegistry);
                log.error("ERROR: Should have thrown exception for duplicate schema registry name!");
            } catch (KafkaManagementException e) {
                log.info("SUCCESS: Caught expected exception for duplicate schema registry name: {}", e.getMessage());
            }
            
            // Show final status
            log.info("Final setup - Schema registry count: {}, Connected: {}", 
                    library.getSchemaRegistryCount(), library.getConnectedSchemaRegistryCount());
            
            Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();
            log.info("Schema registry connection status: {}", registryStatus);
            
            library.close();
            
        } catch (Exception e) {
            log.error("Adding schema registries example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates removing brokers with safe shutdown behavior.
     */
    private static void demonstrateRemovingBrokers() {
        log.info("=== Example 3: Removing Brokers with Safe Shutdown ===");
        
        try {
            // Create library with multiple brokers
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
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(
                new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081"));
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            log.info("Initial setup - Broker count: {}, Connected: {}", 
                    library.getBrokerCount(), library.getConnectedBrokerCount());
            
            // Remove a broker - it will safely shut down the connection
            boolean removed = library.removeBroker("broker-1");
            log.info("Removed broker-1 - Removal successful: {}", removed);
            
            // Try to remove a non-existent broker
            boolean removedNonExistent = library.removeBroker("non-existent-broker");
            log.info("Removed non-existent-broker - Removal successful: {}", removedNonExistent);
            
            // Show final status
            log.info("Final setup - Broker count: {}, Connected: {}", 
                    library.getBrokerCount(), library.getConnectedBrokerCount());
            
            Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
            log.info("Broker connection status: {}", brokerStatus);
            
            library.close();
            
        } catch (Exception e) {
            log.error("Removing brokers example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates removing schema registries with safe shutdown behavior.
     */
    private static void demonstrateRemovingSchemaRegistries() {
        log.info("=== Example 4: Removing Schema Registries with Safe Shutdown ===");
        
        try {
            // Create library with multiple schema registries
            NamedKafkaConfig broker = new NamedKafkaConfig("broker-1", "localhost:9092");
            broker.setSecurityProtocol("SSL");
            broker.setSslTruststoreLocation("/path/to/truststore.jks");
            broker.setSslTruststorePassword("truststore-password");
            broker.setSslKeystoreLocation("/path/to/keystore.jks");
            broker.setSslKeystorePassword("keystore-password");
            broker.setSslKeyPassword("key-password");
            
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
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker);
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1, registry2);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            log.info("Initial setup - Schema registry count: {}, Connected: {}", 
                    library.getSchemaRegistryCount(), library.getConnectedSchemaRegistryCount());
            
            // Remove a schema registry - it will safely shut down the connection
            boolean removed = library.removeSchemaRegistry("registry-1");
            log.info("Removed registry-1 - Removal successful: {}", removed);
            
            // Try to remove a non-existent schema registry
            boolean removedNonExistent = library.removeSchemaRegistry("non-existent-registry");
            log.info("Removed non-existent-registry - Removal successful: {}", removedNonExistent);
            
            // Show final status
            log.info("Final setup - Schema registry count: {}, Connected: {}", 
                    library.getSchemaRegistryCount(), library.getConnectedSchemaRegistryCount());
            
            Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();
            log.info("Schema registry connection status: {}", registryStatus);
            
            library.close();
            
        } catch (Exception e) {
            log.error("Removing schema registries example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates connection status monitoring.
     */
    private static void demonstrateConnectionMonitoring() {
        log.info("=== Example 5: Connection Status Monitoring ===");
        
        try {
            // Create library with multiple brokers and registries
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
            
            NamedSchemaRegistryConfig registry1 = new NamedSchemaRegistryConfig("registry-1", "https://localhost:8081");
            registry1.setSecurityProtocol("SSL");
            registry1.setSslTruststoreLocation("/path/to/truststore.jks");
            registry1.setSslTruststorePassword("truststore-password");
            registry1.setSslKeystoreLocation("/path/to/keystore.jks");
            registry1.setSslKeystorePassword("keystore-password");
            registry1.setSslKeyPassword("key-password");
            
            List<NamedKafkaConfig> brokers = Arrays.asList(broker1, broker2);
            List<NamedSchemaRegistryConfig> registries = Arrays.asList(registry1);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Monitor connection status
            Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
            Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();
            
            log.info("Broker connection status: {}", brokerStatus);
            log.info("Schema registry connection status: {}", registryStatus);
            
            // Get connected brokers and registries
            List<String> connectedBrokers = library.getConnectedBrokers();
            List<String> connectedRegistries = library.getConnectedSchemaRegistries();
            
            log.info("Connected brokers: {}", connectedBrokers);
            log.info("Connected schema registries: {}", connectedRegistries);
            
            // Check individual connection status
            for (String brokerName : brokerStatus.keySet()) {
                boolean isConnected = library.isBrokerConnected(brokerName);
                log.info("Broker '{}' is connected: {}", brokerName, isConnected);
            }
            
            for (String registryName : registryStatus.keySet()) {
                boolean isConnected = library.isSchemaRegistryConnected(registryName);
                log.info("Schema registry '{}' is connected: {}", registryName, isConnected);
            }
            
            library.close();
            
        } catch (Exception e) {
            log.error("Connection monitoring example failed: {}", e.getMessage());
        }
    }
}
