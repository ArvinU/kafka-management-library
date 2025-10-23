package com.mycompany.kafka.multi.example;

import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.MultiJsonConfigLoader;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating configuration loading for multiple brokers and schema registries.
 */
public class MultiConfigExample {
    
    private static final Logger log = LoggerFactory.getLogger(MultiConfigExample.class);
    
    public static void main(String[] args) {
        MultiKafkaManagementLibrary library = null;
        
        try {
            // Example 1: Load configurations from JSON files
            demonstrateConfigLoading();
            
            // Example 2: Create library from loaded configurations
            demonstrateLibraryCreation();
            
            // Example 3: Show connection status after loading
            demonstrateConnectionStatus();
            
            log.info("=== All multi-config examples completed successfully! ===");
            
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
     * Demonstrates loading configurations from JSON files.
     */
    private static void demonstrateConfigLoading() {
        log.info("=== Example 1: Loading Configurations from JSON Files ===");
        
        try {
            // Load broker configurations
            List<NamedKafkaConfig> brokers = MultiJsonConfigLoader.loadKafkaConfigs("config/multi/multi-kafka-config.json");
            log.info("Loaded {} broker configurations:", brokers.size());
            
            for (NamedKafkaConfig broker : brokers) {
                log.info("  - Broker: {} -> {}", broker.getName(), broker.getBootstrapServers());
            }
            
            // Load schema registry configurations
            List<NamedSchemaRegistryConfig> registries = MultiJsonConfigLoader.loadSchemaRegistryConfigs("config/multi/multi-schema-registry-config.json");
            log.info("Loaded {} schema registry configurations:", registries.size());
            
            for (NamedSchemaRegistryConfig registry : registries) {
                log.info("  - Registry: {} -> {}", registry.getName(), registry.getSchemaRegistryUrl());
            }
            
        } catch (IOException e) {
            log.error("Failed to load configurations: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates creating library from loaded configurations.
     */
    private static void demonstrateLibraryCreation() {
        log.info("=== Example 2: Creating Library from Loaded Configurations ===");
        
        try {
            // Load configurations
            List<NamedKafkaConfig> brokers = MultiJsonConfigLoader.loadKafkaConfigs("config/multi/multi-kafka-config.json");
            List<NamedSchemaRegistryConfig> registries = MultiJsonConfigLoader.loadSchemaRegistryConfigs("config/multi/multi-schema-registry-config.json");
            
            // Create library
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            log.info("Library created successfully with {} brokers and {} schema registries", 
                    library.getBrokerCount(), library.getSchemaRegistryCount());
            
            // Show connected counts
            log.info("Connected brokers: {}, Connected schema registries: {}", 
                    library.getConnectedBrokerCount(), library.getConnectedSchemaRegistryCount());
            
            library.close();
            
        } catch (IOException | KafkaManagementException e) {
            log.error("Failed to create library: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstrates connection status after loading.
     */
    private static void demonstrateConnectionStatus() {
        log.info("=== Example 3: Connection Status After Loading ===");
        
        try {
            // Load configurations
            List<NamedKafkaConfig> brokers = MultiJsonConfigLoader.loadKafkaConfigs("config/multi/multi-kafka-config.json");
            List<NamedSchemaRegistryConfig> registries = MultiJsonConfigLoader.loadSchemaRegistryConfigs("config/multi/multi-schema-registry-config.json");
            
            // Create library
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            
            // Show broker connection status
            Map<String, Boolean> brokerStatus = library.getBrokerConnectionStatus();
            log.info("Broker connection status:");
            for (Map.Entry<String, Boolean> entry : brokerStatus.entrySet()) {
                log.info("  - {}: {}", entry.getKey(), entry.getValue() ? "CONNECTED" : "DISCONNECTED");
            }
            
            // Show schema registry connection status
            Map<String, Boolean> registryStatus = library.getSchemaRegistryConnectionStatus();
            log.info("Schema registry connection status:");
            for (Map.Entry<String, Boolean> entry : registryStatus.entrySet()) {
                log.info("  - {}: {}", entry.getKey(), entry.getValue() ? "CONNECTED" : "DISCONNECTED");
            }
            
            // Show connected brokers and registries
            List<String> connectedBrokers = library.getConnectedBrokers();
            List<String> connectedRegistries = library.getConnectedSchemaRegistries();
            
            log.info("Connected brokers: {}", connectedBrokers);
            log.info("Connected schema registries: {}", connectedRegistries);
            
            // Show first connected broker and registry
            String firstBroker = library.getFirstConnectedBroker();
            String firstRegistry = library.getFirstConnectedSchemaRegistry();
            
            log.info("First connected broker: {}", firstBroker);
            log.info("First connected schema registry: {}", firstRegistry);
            
            library.close();
            
        } catch (IOException | KafkaManagementException e) {
            log.error("Failed to demonstrate connection status: {}", e.getMessage());
        }
    }
}
