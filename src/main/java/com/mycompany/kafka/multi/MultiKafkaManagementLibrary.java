package com.mycompany.kafka.multi;

import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
import com.mycompany.kafka.multi.manager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Enhanced KafkaManagementLibrary class that supports multiple brokers and schema registries.
 * Provides a unified interface for managing multiple Kafka brokers and Schema Registry instances.
 */
public class MultiKafkaManagementLibrary {
    
    private static final Logger log = LoggerFactory.getLogger(MultiKafkaManagementLibrary.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    private final MultiBrokerManager multiBrokerManager;
    private final MultiSchemaRegistryManager multiSchemaRegistryManager;
    
    // Enhanced managers that work with named brokers and schema registries
    private final MultiTopicManager multiTopicManager;
    private final MultiMessageManager multiMessageManager;
    private final MultiEnhancedMessageManager multiEnhancedMessageManager;
    private final MultiConsumerManager multiConsumerManager;
    private final MultiSessionManager multiSessionManager;
    private final MultiSimpleSchemaManager multiSimpleSchemaManager;
    private final MultiMessageViewer multiMessageViewer;
    private final MultiSessionViewer multiSessionViewer;
    private final MultiTopicTypeManager multiTopicTypeManager;
    
    /**
     * Constructor to initialize MultiKafkaManagementLibrary with multiple brokers and schema registries.
     * 
     * @param brokers List of named Kafka broker configurations
     * @param schemaRegistries List of named Schema Registry configurations
     * @throws KafkaManagementException if initialization fails
     */
    public MultiKafkaManagementLibrary(List<NamedKafkaConfig> brokers, 
                                       List<NamedSchemaRegistryConfig> schemaRegistries) throws KafkaManagementException {
        log.info("Initializing MultiKafkaManagementLibrary with {} brokers and {} schema registries", 
                brokers.size(), schemaRegistries.size());
        
        try {
            // Initialize managers
            this.multiBrokerManager = new MultiBrokerManager();
            this.multiSchemaRegistryManager = new MultiSchemaRegistryManager();
            
            // Add brokers
            for (NamedKafkaConfig broker : brokers) {
                multiBrokerManager.addBroker(broker);
            }
            
            // Add schema registries
            for (NamedSchemaRegistryConfig schemaRegistry : schemaRegistries) {
                multiSchemaRegistryManager.addSchemaRegistry(schemaRegistry);
            }
            
            // Initialize connection factory
            this.multiConnectionFactory = new MultiConnectionFactory(multiBrokerManager, multiSchemaRegistryManager);
            
            // Initialize enhanced managers
            this.multiTopicManager = new MultiTopicManager(multiConnectionFactory);
            this.multiMessageManager = new MultiMessageManager(multiConnectionFactory);
            this.multiEnhancedMessageManager = new MultiEnhancedMessageManager(multiConnectionFactory);
            this.multiConsumerManager = new MultiConsumerManager(multiConnectionFactory);
            this.multiSessionManager = new MultiSessionManager(multiConnectionFactory);
            this.multiSimpleSchemaManager = new MultiSimpleSchemaManager(multiConnectionFactory);
            this.multiMessageViewer = new MultiMessageViewer(multiConnectionFactory);
            this.multiSessionViewer = new MultiSessionViewer(multiConnectionFactory);
            this.multiTopicTypeManager = new MultiTopicTypeManager(multiConnectionFactory);
            
            log.info("MultiKafkaManagementLibrary initialized successfully");
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to initialize MultiKafkaManagementLibrary",
                e);
        }
    }
    
    /**
     * Constructor with simple configuration for basic usage.
     * Creates a single broker and schema registry with default names.
     * 
     * @param bootstrapServers Kafka bootstrap servers (e.g., "localhost:9092")
     * @param schemaRegistryUrl Schema Registry URL (e.g., "http://localhost:8081")
     * @throws KafkaManagementException if connection validation fails
     */
    public MultiKafkaManagementLibrary(String bootstrapServers, String schemaRegistryUrl) throws KafkaManagementException {
        this(bootstrapServers, schemaRegistryUrl, "default-broker", "default-schema-registry");
    }
    
    /**
     * Constructor with simple configuration and custom names.
     * 
     * @param bootstrapServers Kafka bootstrap servers (e.g., "localhost:9092")
     * @param schemaRegistryUrl Schema Registry URL (e.g., "http://localhost:8081")
     * @param brokerName Name for the broker configuration
     * @param schemaRegistryName Name for the schema registry configuration
     * @throws KafkaManagementException if connection validation fails
     */
    public MultiKafkaManagementLibrary(String bootstrapServers, String schemaRegistryUrl, 
                                       String brokerName, String schemaRegistryName) throws KafkaManagementException {
        NamedKafkaConfig broker = new NamedKafkaConfig(brokerName, bootstrapServers);
        NamedSchemaRegistryConfig schemaRegistry = new NamedSchemaRegistryConfig(schemaRegistryName, schemaRegistryUrl);
        
        this.multiBrokerManager = new MultiBrokerManager();
        this.multiSchemaRegistryManager = new MultiSchemaRegistryManager();
        
        multiBrokerManager.addBroker(broker);
        multiSchemaRegistryManager.addSchemaRegistry(schemaRegistry);
        
        this.multiConnectionFactory = new MultiConnectionFactory(multiBrokerManager, multiSchemaRegistryManager);
        
        // Initialize enhanced managers
        this.multiTopicManager = new MultiTopicManager(multiConnectionFactory);
        this.multiMessageManager = new MultiMessageManager(multiConnectionFactory);
        this.multiEnhancedMessageManager = new MultiEnhancedMessageManager(multiConnectionFactory);
        this.multiConsumerManager = new MultiConsumerManager(multiConnectionFactory);
        this.multiSessionManager = new MultiSessionManager(multiConnectionFactory);
        this.multiSimpleSchemaManager = new MultiSimpleSchemaManager(multiConnectionFactory);
        this.multiMessageViewer = new MultiMessageViewer(multiConnectionFactory);
        this.multiSessionViewer = new MultiSessionViewer(multiConnectionFactory);
        this.multiTopicTypeManager = new MultiTopicTypeManager(multiConnectionFactory);
        
        log.info("MultiKafkaManagementLibrary initialized with single broker and schema registry");
    }
    
    /**
     * Adds a new broker configuration and automatically attempts to connect.
     * 
     * @param broker The broker configuration to add
     * @return true if the broker was added and connection was successful, false if added but connection failed
     * @throws KafkaManagementException if a broker with the same name already exists
     */
    public boolean addBroker(NamedKafkaConfig broker) throws KafkaManagementException {
        boolean connected = multiBrokerManager.addBroker(broker);
        log.info("Added broker: {} - Connection status: {}", broker.getName(), connected ? "SUCCESS" : "FAILED");
        return connected;
    }
    
    /**
     * Removes a broker configuration and safely shuts down its connection.
     * 
     * @param brokerName The name of the broker to remove
     * @return true if the broker was successfully removed, false if broker was not found
     */
    public boolean removeBroker(String brokerName) {
        boolean removed = multiBrokerManager.removeBroker(brokerName);
        log.info("Removed broker: {} - Status: {}", brokerName, removed ? "SUCCESS" : "NOT_FOUND");
        return removed;
    }
    
    /**
     * Adds a new schema registry configuration and automatically attempts to connect.
     * 
     * @param schemaRegistry The schema registry configuration to add
     * @return true if the schema registry was added and connection was successful, false if added but connection failed
     * @throws KafkaManagementException if a schema registry with the same name already exists
     */
    public boolean addSchemaRegistry(NamedSchemaRegistryConfig schemaRegistry) throws KafkaManagementException {
        boolean connected = multiSchemaRegistryManager.addSchemaRegistry(schemaRegistry);
        log.info("Added schema registry: {} - Connection status: {}", schemaRegistry.getName(), connected ? "SUCCESS" : "FAILED");
        return connected;
    }
    
    /**
     * Removes a schema registry configuration and safely shuts down its connection.
     * 
     * @param registryName The name of the schema registry to remove
     * @return true if the schema registry was successfully removed, false if registry was not found
     */
    public boolean removeSchemaRegistry(String registryName) {
        boolean removed = multiSchemaRegistryManager.removeSchemaRegistry(registryName);
        log.info("Removed schema registry: {} - Status: {}", registryName, removed ? "SUCCESS" : "NOT_FOUND");
        return removed;
    }
    
    /**
     * Reconnects to a specific broker
     */
    public boolean reconnectBroker(String brokerName) {
        return multiBrokerManager.reconnect(brokerName);
    }
    
    /**
     * Reconnects to a specific schema registry
     */
    public boolean reconnectSchemaRegistry(String registryName) {
        return multiSchemaRegistryManager.reconnect(registryName);
    }
    
    /**
     * Reconnects to all brokers and schema registries
     */
    public void reconnectAll() {
        multiConnectionFactory.reconnectAll();
    }
    
    /**
     * Gets the connection status of all brokers
     */
    public Map<String, Boolean> getBrokerConnectionStatus() {
        return multiBrokerManager.getConnectionStatus();
    }
    
    /**
     * Gets the connection status of all schema registries
     */
    public Map<String, Boolean> getSchemaRegistryConnectionStatus() {
        return multiSchemaRegistryManager.getConnectionStatus();
    }
    
    /**
     * Gets all connected broker names
     */
    public List<String> getConnectedBrokers() {
        return multiBrokerManager.getConnectedBrokerNames();
    }
    
    /**
     * Gets all connected schema registry names
     */
    public List<String> getConnectedSchemaRegistries() {
        return multiSchemaRegistryManager.getConnectedSchemaRegistryNames();
    }
    
    /**
     * Gets the first connected broker name
     */
    public String getFirstConnectedBroker() {
        return multiBrokerManager.getFirstConnectedBroker();
    }
    
    /**
     * Gets the first connected schema registry name
     */
    public String getFirstConnectedSchemaRegistry() {
        return multiSchemaRegistryManager.getFirstConnectedSchemaRegistry();
    }
    
    // Enhanced manager getters
    public MultiTopicManager getMultiTopicManager() { return multiTopicManager; }
    public MultiMessageManager getMultiMessageManager() { return multiMessageManager; }
    public MultiEnhancedMessageManager getMultiEnhancedMessageManager() { return multiEnhancedMessageManager; }
    public MultiConsumerManager getMultiConsumerManager() { return multiConsumerManager; }
    public MultiSessionManager getMultiSessionManager() { return multiSessionManager; }
    public MultiSimpleSchemaManager getMultiSimpleSchemaManager() { return multiSimpleSchemaManager; }
    public MultiMessageViewer getMultiMessageViewer() { return multiMessageViewer; }
    public MultiSessionViewer getMultiSessionViewer() { return multiSessionViewer; }
    public MultiTopicTypeManager getMultiTopicTypeManager() { return multiTopicTypeManager; }
    
    // Manager getters for backward compatibility
    public MultiBrokerManager getMultiBrokerManager() { return multiBrokerManager; }
    public MultiSchemaRegistryManager getMultiSchemaRegistryManager() { return multiSchemaRegistryManager; }
    public MultiConnectionFactory getMultiConnectionFactory() { return multiConnectionFactory; }
    
    /**
     * Closes all connections and cleans up resources
     */
    public void close() {
        log.info("Closing MultiKafkaManagementLibrary");
        multiConnectionFactory.close();
    }
    
    /**
     * Gets the number of brokers
     */
    public int getBrokerCount() {
        return multiBrokerManager.getBrokerCount();
    }
    
    /**
     * Gets the number of connected brokers
     */
    public int getConnectedBrokerCount() {
        return multiBrokerManager.getConnectedBrokerCount();
    }
    
    /**
     * Gets the number of schema registries
     */
    public int getSchemaRegistryCount() {
        return multiSchemaRegistryManager.getSchemaRegistryCount();
    }
    
    /**
     * Gets the number of connected schema registries
     */
    public int getConnectedSchemaRegistryCount() {
        return multiSchemaRegistryManager.getConnectedSchemaRegistryCount();
    }
    
    /**
     * Checks if a broker is connected
     */
    public boolean isBrokerConnected(String brokerName) {
        return multiBrokerManager.isConnected(brokerName);
    }
    
    /**
     * Checks if a schema registry is connected
     */
    public boolean isSchemaRegistryConnected(String registryName) {
        return multiSchemaRegistryManager.isConnected(registryName);
    }
    
    /**
     * Gets the health status of all schema registries
     */
    public Map<String, Boolean> getSchemaRegistryHealthStatus() {
        return multiSchemaRegistryManager.getHealthStatus();
    }
}
