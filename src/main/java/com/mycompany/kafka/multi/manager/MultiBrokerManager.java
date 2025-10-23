package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages multiple Kafka brokers with connection status tracking.
 * Provides methods to add, remove, and reconnect to brokers.
 */
public class MultiBrokerManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiBrokerManager.class);
    
    private final Map<String, NamedKafkaConfig> brokers = new ConcurrentHashMap<>();
    private final Map<String, AdminClient> adminClients = new ConcurrentHashMap<>();
    
    /**
     * Adds a broker configuration and automatically attempts to connect.
     * 
     * @param brokerConfig The broker configuration to add
     * @return true if the broker was added and connection was successful, false if added but connection failed
     * @throws KafkaManagementException if a broker with the same name already exists
     */
    public boolean addBroker(NamedKafkaConfig brokerConfig) throws KafkaManagementException {
        if (brokerConfig == null) {
            throw new IllegalArgumentException("Broker configuration cannot be null");
        }
        
        String name = brokerConfig.getName();
        log.info("Adding broker configuration: {}", name);
        
        // Check if broker with the same name already exists
        if (brokers.containsKey(name)) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker with name '" + name + "' already exists. Cannot add duplicate broker names.");
        }
        
        brokers.put(name, brokerConfig);
        
        // Test connection and create admin client if successful
        boolean connected = testConnection(name);
        log.info("Broker '{}' added - Connection status: {}", name, connected ? "SUCCESS" : "FAILED");
        return connected;
    }
    
    /**
     * Removes a broker configuration and safely shuts down its connection.
     * 
     * @param brokerName The name of the broker to remove
     * @return true if the broker was successfully removed, false if broker was not found
     */
    public boolean removeBroker(String brokerName) {
        if (brokerName == null || brokerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Broker name cannot be null or empty");
        }
        
        log.info("Removing broker configuration: {}", brokerName);
        
        // Close admin client if exists
        AdminClient adminClient = adminClients.remove(brokerName);
        if (adminClient != null) {
            try {
                adminClient.close();
                log.info("Successfully closed admin client for broker: {}", brokerName);
            } catch (Exception e) {
                log.warn("Error closing admin client for broker {}: {}", brokerName, e.getMessage());
            }
        }
        
        // Remove configuration
        NamedKafkaConfig removed = brokers.remove(brokerName);
        boolean removedSuccessfully = removed != null;
        
        if (removedSuccessfully) {
            log.info("Successfully removed broker configuration: {}", brokerName);
        } else {
            log.warn("Broker configuration not found: {}", brokerName);
        }
        
        return removedSuccessfully;
    }
    
    /**
     * Tests connection to a specific broker
     */
    public boolean testConnection(String brokerName) {
        NamedKafkaConfig config = brokers.get(brokerName);
        if (config == null) {
            log.error("Broker configuration not found: {}", brokerName);
            return false;
        }
        
        try {
            log.info("Testing connection to broker: {}", brokerName);
            
            Properties props = config.toProperties();
            props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            
            AdminClient adminClient = AdminClient.create(props);
            
            // Test connection by describing cluster
            DescribeClusterResult result = adminClient.describeCluster();
            result.clusterId().get(5, TimeUnit.SECONDS); // Wait for cluster ID
            
            // Connection successful
            config.setConnected(true);
            adminClients.put(brokerName, adminClient);
            
            log.info("Successfully connected to broker: {}", brokerName);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to connect to broker {}: {}", brokerName, e.getMessage());
            config.setConnectionError(e.getMessage());
            
            // Close any existing admin client
            AdminClient existingClient = adminClients.remove(brokerName);
            if (existingClient != null) {
                try {
                    existingClient.close();
                } catch (Exception closeException) {
                    log.warn("Error closing admin client for broker {}: {}", brokerName, closeException.getMessage());
                }
            }
            
            return false;
        }
    }
    
    /**
     * Reconnects to a specific broker
     */
    public boolean reconnect(String brokerName) {
        log.info("Attempting to reconnect to broker: {}", brokerName);
        
        NamedKafkaConfig config = brokers.get(brokerName);
        if (config == null) {
            throw new IllegalArgumentException("Broker configuration not found: " + brokerName);
        }
        
        // Reset connection status
        config.resetConnectionStatus();
        
        // Close existing admin client if exists
        AdminClient existingClient = adminClients.remove(brokerName);
        if (existingClient != null) {
            try {
                existingClient.close();
            } catch (Exception e) {
                log.warn("Error closing existing admin client for broker {}: {}", brokerName, e.getMessage());
            }
        }
        
        // Test connection
        return testConnection(brokerName);
    }
    
    /**
     * Reconnects to all brokers
     */
    public void reconnectAll() {
        log.info("Attempting to reconnect to all brokers");
        
        for (String brokerName : brokers.keySet()) {
            try {
                reconnect(brokerName);
            } catch (Exception e) {
                log.error("Error reconnecting to broker {}: {}", brokerName, e.getMessage());
            }
        }
    }
    
    /**
     * Gets the admin client for a specific broker
     */
    public AdminClient getAdminClient(String brokerName) throws KafkaManagementException {
        NamedKafkaConfig config = brokers.get(brokerName);
        if (config == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (!config.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        AdminClient adminClient = adminClients.get(brokerName);
        if (adminClient == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Admin client not available for broker: " + brokerName);
        }
        
        return adminClient;
    }
    
    /**
     * Gets the configuration for a specific broker
     */
    public NamedKafkaConfig getBrokerConfig(String brokerName) {
        return brokers.get(brokerName);
    }
    
    /**
     * Gets all broker configurations
     */
    public Map<String, NamedKafkaConfig> getAllBrokers() {
        return new HashMap<>(brokers);
    }
    
    /**
     * Gets all connected brokers
     */
    public Map<String, NamedKafkaConfig> getConnectedBrokers() {
        Map<String, NamedKafkaConfig> connectedBrokers = new HashMap<>();
        for (Map.Entry<String, NamedKafkaConfig> entry : brokers.entrySet()) {
            if (entry.getValue().isConnected()) {
                connectedBrokers.put(entry.getKey(), entry.getValue());
            }
        }
        return connectedBrokers;
    }
    
    /**
     * Gets all disconnected brokers
     */
    public Map<String, NamedKafkaConfig> getDisconnectedBrokers() {
        Map<String, NamedKafkaConfig> disconnectedBrokers = new HashMap<>();
        for (Map.Entry<String, NamedKafkaConfig> entry : brokers.entrySet()) {
            if (!entry.getValue().isConnected()) {
                disconnectedBrokers.put(entry.getKey(), entry.getValue());
            }
        }
        return disconnectedBrokers;
    }
    
    /**
     * Checks if a broker is connected
     */
    public boolean isConnected(String brokerName) {
        NamedKafkaConfig config = brokers.get(brokerName);
        return config != null && config.isConnected();
    }
    
    /**
     * Gets the connection status of all brokers
     */
    public Map<String, Boolean> getConnectionStatus() {
        Map<String, Boolean> status = new HashMap<>();
        for (Map.Entry<String, NamedKafkaConfig> entry : brokers.entrySet()) {
            status.put(entry.getKey(), entry.getValue().isConnected());
        }
        return status;
    }
    
    /**
     * Gets the first connected broker name
     */
    public String getFirstConnectedBroker() {
        for (Map.Entry<String, NamedKafkaConfig> entry : brokers.entrySet()) {
            if (entry.getValue().isConnected()) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Gets a list of all broker names
     */
    public List<String> getAllBrokerNames() {
        return new ArrayList<>(brokers.keySet());
    }
    
    /**
     * Gets a list of connected broker names
     */
    public List<String> getConnectedBrokerNames() {
        List<String> connectedNames = new ArrayList<>();
        for (Map.Entry<String, NamedKafkaConfig> entry : brokers.entrySet()) {
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
        log.info("Closing all broker connections");
        
        for (Map.Entry<String, AdminClient> entry : adminClients.entrySet()) {
            try {
                entry.getValue().close();
                log.info("Closed admin client for broker: {}", entry.getKey());
            } catch (Exception e) {
                log.warn("Error closing admin client for broker {}: {}", entry.getKey(), e.getMessage());
            }
        }
        
        adminClients.clear();
        
        // Reset connection status for all brokers
        for (NamedKafkaConfig config : brokers.values()) {
            config.resetConnectionStatus();
        }
    }
    
    /**
     * Gets the number of brokers
     */
    public int getBrokerCount() {
        return brokers.size();
    }
    
    /**
     * Gets the number of connected brokers
     */
    public int getConnectedBrokerCount() {
        int count = 0;
        for (NamedKafkaConfig config : brokers.values()) {
            if (config.isConnected()) {
                count++;
            }
        }
        return count;
    }
}
