package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiple Schema Registry instances with connection status tracking.
 * Provides methods to add, remove, and reconnect to schema registries.
 */
public class MultiSchemaRegistryManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiSchemaRegistryManager.class);
    
    private final Map<String, NamedSchemaRegistryConfig> schemaRegistries = new ConcurrentHashMap<>();
    private final Map<String, SchemaRegistryClient> clients = new ConcurrentHashMap<>();
    
    /**
     * Adds a schema registry configuration and automatically attempts to connect.
     * 
     * @param schemaRegistryConfig The schema registry configuration to add
     * @return true if the schema registry was added and connection was successful, false if added but connection failed
     * @throws KafkaManagementException if a schema registry with the same name already exists
     */
    public boolean addSchemaRegistry(NamedSchemaRegistryConfig schemaRegistryConfig) throws KafkaManagementException {
        if (schemaRegistryConfig == null) {
            throw new IllegalArgumentException("Schema registry configuration cannot be null");
        }
        
        String name = schemaRegistryConfig.getName();
        log.info("Adding schema registry configuration: {}", name);
        
        // Check if schema registry with the same name already exists
        if (schemaRegistries.containsKey(name)) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry with name '" + name + "' already exists. Cannot add duplicate schema registry names.");
        }
        
        schemaRegistries.put(name, schemaRegistryConfig);
        
        // Test connection and create client if successful
        boolean connected = testConnection(name);
        log.info("Schema registry '{}' added - Connection status: {}", name, connected ? "SUCCESS" : "FAILED");
        return connected;
    }
    
    /**
     * Removes a schema registry configuration and safely shuts down its connection.
     * 
     * @param registryName The name of the schema registry to remove
     * @return true if the schema registry was successfully removed, false if registry was not found
     */
    public boolean removeSchemaRegistry(String registryName) {
        if (registryName == null || registryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Schema registry name cannot be null or empty");
        }
        
        log.info("Removing schema registry configuration: {}", registryName);
        
        // Remove client if exists
        SchemaRegistryClient client = clients.remove(registryName);
        if (client != null) {
            try {
                // SchemaRegistryClient doesn't have a close method, just remove from cache
                log.info("Successfully removed schema registry client: {}", registryName);
            } catch (Exception e) {
                log.warn("Error removing schema registry client for {}: {}", registryName, e.getMessage());
            }
        }
        
        // Remove configuration
        NamedSchemaRegistryConfig removed = schemaRegistries.remove(registryName);
        boolean removedSuccessfully = removed != null;
        
        if (removedSuccessfully) {
            log.info("Successfully removed schema registry configuration: {}", registryName);
        } else {
            log.warn("Schema registry configuration not found: {}", registryName);
        }
        
        return removedSuccessfully;
    }
    
    /**
     * Tests connection to a specific schema registry
     */
    public boolean testConnection(String registryName) {
        NamedSchemaRegistryConfig config = schemaRegistries.get(registryName);
        if (config == null) {
            log.error("Schema registry configuration not found: {}", registryName);
            return false;
        }
        
        try {
            log.info("Testing connection to schema registry: {}", registryName);
            
            SchemaRegistryClient client = new CachedSchemaRegistryClient(
                config.getSchemaRegistryUrl(), 
                config.getCacheCapacity()
            );
            
            // Test connection by getting all subjects
            client.getAllSubjects();
            
            // Connection successful
            config.setConnected(true);
            clients.put(registryName, client);
            
            log.info("Successfully connected to schema registry: {}", registryName);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to connect to schema registry {}: {}", registryName, e.getMessage());
            config.setConnectionError(e.getMessage());
            
            // Remove any existing client
            clients.remove(registryName);
            
            return false;
        }
    }
    
    /**
     * Reconnects to a specific schema registry
     */
    public boolean reconnect(String registryName) {
        log.info("Attempting to reconnect to schema registry: {}", registryName);
        
        NamedSchemaRegistryConfig config = schemaRegistries.get(registryName);
        if (config == null) {
            throw new IllegalArgumentException("Schema registry configuration not found: " + registryName);
        }
        
        // Reset connection status
        config.resetConnectionStatus();
        
        // Remove existing client if exists
        clients.remove(registryName);
        
        // Test connection
        return testConnection(registryName);
    }
    
    /**
     * Reconnects to all schema registries
     */
    public void reconnectAll() {
        log.info("Attempting to reconnect to all schema registries");
        
        for (String registryName : schemaRegistries.keySet()) {
            try {
                reconnect(registryName);
            } catch (Exception e) {
                log.error("Error reconnecting to schema registry {}: {}", registryName, e.getMessage());
            }
        }
    }
    
    /**
     * Gets the schema registry client for a specific registry
     */
    public SchemaRegistryClient getSchemaRegistryClient(String registryName) throws KafkaManagementException {
        NamedSchemaRegistryConfig config = schemaRegistries.get(registryName);
        if (config == null) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry configuration not found: " + registryName);
        }
        
        if (!config.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry is not connected: " + registryName);
        }
        
        SchemaRegistryClient client = clients.get(registryName);
        if (client == null) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry client not available for: " + registryName);
        }
        
        return client;
    }
    
    /**
     * Gets the configuration for a specific schema registry
     */
    public NamedSchemaRegistryConfig getSchemaRegistryConfig(String registryName) {
        return schemaRegistries.get(registryName);
    }
    
    /**
     * Gets all schema registry configurations
     */
    public Map<String, NamedSchemaRegistryConfig> getAllSchemaRegistries() {
        return new HashMap<>(schemaRegistries);
    }
    
    /**
     * Gets all connected schema registries
     */
    public Map<String, NamedSchemaRegistryConfig> getConnectedSchemaRegistries() {
        Map<String, NamedSchemaRegistryConfig> connectedRegistries = new HashMap<>();
        for (Map.Entry<String, NamedSchemaRegistryConfig> entry : schemaRegistries.entrySet()) {
            if (entry.getValue().isConnected()) {
                connectedRegistries.put(entry.getKey(), entry.getValue());
            }
        }
        return connectedRegistries;
    }
    
    /**
     * Gets all disconnected schema registries
     */
    public Map<String, NamedSchemaRegistryConfig> getDisconnectedSchemaRegistries() {
        Map<String, NamedSchemaRegistryConfig> disconnectedRegistries = new HashMap<>();
        for (Map.Entry<String, NamedSchemaRegistryConfig> entry : schemaRegistries.entrySet()) {
            if (!entry.getValue().isConnected()) {
                disconnectedRegistries.put(entry.getKey(), entry.getValue());
            }
        }
        return disconnectedRegistries;
    }
    
    /**
     * Checks if a schema registry is connected
     */
    public boolean isConnected(String registryName) {
        NamedSchemaRegistryConfig config = schemaRegistries.get(registryName);
        return config != null && config.isConnected();
    }
    
    /**
     * Gets the connection status of all schema registries
     */
    public Map<String, Boolean> getConnectionStatus() {
        Map<String, Boolean> status = new HashMap<>();
        for (Map.Entry<String, NamedSchemaRegistryConfig> entry : schemaRegistries.entrySet()) {
            status.put(entry.getKey(), entry.getValue().isConnected());
        }
        return status;
    }
    
    /**
     * Gets the first connected schema registry name
     */
    public String getFirstConnectedSchemaRegistry() {
        for (Map.Entry<String, NamedSchemaRegistryConfig> entry : schemaRegistries.entrySet()) {
            if (entry.getValue().isConnected()) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Gets a list of all schema registry names
     */
    public List<String> getAllSchemaRegistryNames() {
        return new ArrayList<>(schemaRegistries.keySet());
    }
    
    /**
     * Gets a list of connected schema registry names
     */
    public List<String> getConnectedSchemaRegistryNames() {
        List<String> connectedNames = new ArrayList<>();
        for (Map.Entry<String, NamedSchemaRegistryConfig> entry : schemaRegistries.entrySet()) {
            if (entry.getValue().isConnected()) {
                connectedNames.add(entry.getKey());
            }
        }
        return connectedNames;
    }
    
    /**
     * Closes all connections and cleans up resources
     */
    public void close() {
        log.info("Closing all schema registry connections");
        
        // SchemaRegistryClient doesn't have a close method, just clear the cache
        clients.clear();
        
        // Reset connection status for all schema registries
        for (NamedSchemaRegistryConfig config : schemaRegistries.values()) {
            config.resetConnectionStatus();
        }
    }
    
    /**
     * Gets the number of schema registries
     */
    public int getSchemaRegistryCount() {
        return schemaRegistries.size();
    }
    
    /**
     * Gets the number of connected schema registries
     */
    public int getConnectedSchemaRegistryCount() {
        int count = 0;
        for (NamedSchemaRegistryConfig config : schemaRegistries.values()) {
            if (config.isConnected()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Tests if a schema registry is healthy by checking if it can list subjects
     */
    public boolean isHealthy(String registryName) {
        try {
            SchemaRegistryClient client = getSchemaRegistryClient(registryName);
            client.getAllSubjects();
            return true;
        } catch (Exception e) {
            log.warn("Schema registry {} is not healthy: {}", registryName, e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets health status of all schema registries
     */
    public Map<String, Boolean> getHealthStatus() {
        Map<String, Boolean> healthStatus = new HashMap<>();
        for (String registryName : schemaRegistries.keySet()) {
            healthStatus.put(registryName, isHealthy(registryName));
        }
        return healthStatus;
    }
}
