package com.mycompany.kafka.multi.example;

import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example demonstrating duplicate name validation for brokers and schema registries.
 */
public class DuplicateNameValidationExample {
    
    private static final Logger log = LoggerFactory.getLogger(DuplicateNameValidationExample.class);
    
    public static void main(String[] args) {
        MultiKafkaManagementLibrary library = null;
        
        try {
            // Example 1: Adding duplicate broker names
            demonstrateDuplicateBrokerNames();
            
            // Example 2: Adding duplicate schema registry names
            demonstrateDuplicateSchemaRegistryNames();
            
            // Example 3: Mixed duplicate validation
            demonstrateMixedDuplicateValidation();
            
            log.info("=== All duplicate name validation examples completed successfully! ===");
            
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
     * Demonstrates validation when adding duplicate broker names.
     */
    private static void demonstrateDuplicateBrokerNames() {
        log.info("=== Example 1: Duplicate Broker Name Validation ===");
        
        try {
            // Create library with initial broker
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
                "localhost:9092", "https://localhost:8081");
            
            log.info("Initial setup - Broker count: {}", library.getBrokerCount());
            
            // Add a new broker with unique name - should succeed
            NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
            broker2.setSecurityProtocol("SSL");
            broker2.setSslTruststoreLocation("/path/to/truststore.jks");
            broker2.setSslTruststorePassword("truststore-password");
            broker2.setSslKeystoreLocation("/path/to/keystore.jks");
            broker2.setSslKeystorePassword("keystore-password");
            broker2.setSslKeyPassword("key-password");
            
            boolean connected = library.addBroker(broker2);
            log.info("Added broker-2 (unique name) - Connection successful: {}", connected);
            log.info("Broker count after adding broker-2: {}", library.getBrokerCount());
            
            // Try to add a broker with duplicate name - should throw exception
            try {
                NamedKafkaConfig duplicateBroker = new NamedKafkaConfig("broker-2", "localhost:9094");
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
            
            // Try to add a broker with the default name - should throw exception
            try {
                NamedKafkaConfig defaultBroker = new NamedKafkaConfig("default-broker", "localhost:9095");
                defaultBroker.setSecurityProtocol("SSL");
                defaultBroker.setSslTruststoreLocation("/path/to/truststore.jks");
                defaultBroker.setSslTruststorePassword("truststore-password");
                defaultBroker.setSslKeystoreLocation("/path/to/keystore.jks");
                defaultBroker.setSslKeystorePassword("keystore-password");
                defaultBroker.setSslKeyPassword("key-password");
                
                library.addBroker(defaultBroker);
                log.error("ERROR: Should have thrown exception for duplicate default broker name!");
            } catch (KafkaManagementException e) {
                log.info("SUCCESS: Caught expected exception for duplicate default broker name: {}", e.getMessage());
            }
            
            log.info("Final broker count: {}", library.getBrokerCount());
            library.close();
            
        } catch (Exception e) {
            log.error("Duplicate broker names example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates validation when adding duplicate schema registry names.
     */
    private static void demonstrateDuplicateSchemaRegistryNames() {
        log.info("=== Example 2: Duplicate Schema Registry Name Validation ===");
        
        try {
            // Create library with initial schema registry
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
                "localhost:9092", "https://localhost:8081");
            
            log.info("Initial setup - Schema registry count: {}", library.getSchemaRegistryCount());
            
            // Add a new schema registry with unique name - should succeed
            NamedSchemaRegistryConfig registry2 = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8082");
            registry2.setSecurityProtocol("SSL");
            registry2.setSslTruststoreLocation("/path/to/truststore.jks");
            registry2.setSslTruststorePassword("truststore-password");
            registry2.setSslKeystoreLocation("/path/to/keystore.jks");
            registry2.setSslKeystorePassword("keystore-password");
            registry2.setSslKeyPassword("key-password");
            
            boolean connected = library.addSchemaRegistry(registry2);
            log.info("Added registry-2 (unique name) - Connection successful: {}", connected);
            log.info("Schema registry count after adding registry-2: {}", library.getSchemaRegistryCount());
            
            // Try to add a schema registry with duplicate name - should throw exception
            try {
                NamedSchemaRegistryConfig duplicateRegistry = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8083");
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
            
            // Try to add a schema registry with the default name - should throw exception
            try {
                NamedSchemaRegistryConfig defaultRegistry = new NamedSchemaRegistryConfig("default-schema-registry", "https://localhost:8084");
                defaultRegistry.setSecurityProtocol("SSL");
                defaultRegistry.setSslTruststoreLocation("/path/to/truststore.jks");
                defaultRegistry.setSslTruststorePassword("truststore-password");
                defaultRegistry.setSslKeystoreLocation("/path/to/keystore.jks");
                defaultRegistry.setSslKeystorePassword("keystore-password");
                defaultRegistry.setSslKeyPassword("key-password");
                
                library.addSchemaRegistry(defaultRegistry);
                log.error("ERROR: Should have thrown exception for duplicate default schema registry name!");
            } catch (KafkaManagementException e) {
                log.info("SUCCESS: Caught expected exception for duplicate default schema registry name: {}", e.getMessage());
            }
            
            log.info("Final schema registry count: {}", library.getSchemaRegistryCount());
            library.close();
            
        } catch (Exception e) {
            log.error("Duplicate schema registry names example failed: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates mixed duplicate validation scenarios.
     */
    private static void demonstrateMixedDuplicateValidation() {
        log.info("=== Example 3: Mixed Duplicate Validation ===");
        
        try {
            // Create library with initial broker and schema registry
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(
                "localhost:9092", "https://localhost:8081");
            
            log.info("Initial setup - Broker count: {}, Schema registry count: {}", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            // Add brokers and registries with unique names
            NamedKafkaConfig broker2 = new NamedKafkaConfig("broker-2", "localhost:9093");
            broker2.setSecurityProtocol("SSL");
            broker2.setSslTruststoreLocation("/path/to/truststore.jks");
            broker2.setSslTruststorePassword("truststore-password");
            broker2.setSslKeystoreLocation("/path/to/keystore.jks");
            broker2.setSslKeystorePassword("keystore-password");
            broker2.setSslKeyPassword("key-password");
            
            NamedSchemaRegistryConfig registry2 = new NamedSchemaRegistryConfig("registry-2", "https://localhost:8082");
            registry2.setSecurityProtocol("SSL");
            registry2.setSslTruststoreLocation("/path/to/truststore.jks");
            registry2.setSslTruststorePassword("truststore-password");
            registry2.setSslKeystoreLocation("/path/to/keystore.jks");
            registry2.setSslKeystorePassword("keystore-password");
            registry2.setSslKeyPassword("key-password");
            
            library.addBroker(broker2);
            library.addSchemaRegistry(registry2);
            
            log.info("After adding unique names - Broker count: {}, Schema registry count: {}", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            // Try to add broker with same name as existing schema registry - should succeed (different types)
            NamedKafkaConfig brokerWithRegistryName = new NamedKafkaConfig("registry-2", "localhost:9094");
            brokerWithRegistryName.setSecurityProtocol("SSL");
            brokerWithRegistryName.setSslTruststoreLocation("/path/to/truststore.jks");
            brokerWithRegistryName.setSslTruststorePassword("truststore-password");
            brokerWithRegistryName.setSslKeystoreLocation("/path/to/keystore.jks");
            brokerWithRegistryName.setSslKeystorePassword("keystore-password");
            brokerWithRegistryName.setSslKeyPassword("key-password");
            
            boolean connected = library.addBroker(brokerWithRegistryName);
            log.info("Added broker with same name as schema registry - Connection successful: {}", connected);
            log.info("Broker count after adding broker with registry name: {}", library.getBrokerCount());
            
            // Try to add schema registry with same name as existing broker - should succeed (different types)
            NamedSchemaRegistryConfig registryWithBrokerName = new NamedSchemaRegistryConfig("broker-2", "https://localhost:8083");
            registryWithBrokerName.setSecurityProtocol("SSL");
            registryWithBrokerName.setSslTruststoreLocation("/path/to/truststore.jks");
            registryWithBrokerName.setSslTruststorePassword("truststore-password");
            registryWithBrokerName.setSslKeystoreLocation("/path/to/keystore.jks");
            registryWithBrokerName.setSslKeystorePassword("keystore-password");
            registryWithBrokerName.setSslKeyPassword("key-password");
            
            boolean connected2 = library.addSchemaRegistry(registryWithBrokerName);
            log.info("Added schema registry with same name as broker - Connection successful: {}", connected2);
            log.info("Schema registry count after adding registry with broker name: {}", library.getSchemaRegistryCount());
            
            // Now try to add duplicate broker name - should throw exception
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
            
            // Now try to add duplicate schema registry name - should throw exception
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
            
            log.info("Final counts - Broker count: {}, Schema registry count: {}", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            library.close();
            
        } catch (Exception e) {
            log.error("Mixed duplicate validation example failed: {}", e.getMessage());
        }
    }
}
