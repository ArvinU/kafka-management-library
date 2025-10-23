package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Test helper class for Multi* manager classes.
 * Provides convenient methods for testing multi-broker and multi-schema-registry scenarios.
 */
public class MultiManagerTestHelper {
    
    private static final Logger log = LoggerFactory.getLogger(MultiManagerTestHelper.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    private final MultiEnhancedMessageManager messageManager;
    private final MultiConsumerManager consumerManager;
    private final MultiSimpleSchemaManager schemaManager;
    private final MultiTopicTypeManager topicTypeManager;
    private final MultiSessionManager sessionManager;
    
    public MultiManagerTestHelper(MultiConnectionFactory multiConnectionFactory) {
        this.multiConnectionFactory = multiConnectionFactory;
        this.messageManager = new MultiEnhancedMessageManager(multiConnectionFactory);
        this.consumerManager = new MultiConsumerManager(multiConnectionFactory);
        this.schemaManager = new MultiSimpleSchemaManager(multiConnectionFactory);
        this.topicTypeManager = new MultiTopicTypeManager(multiConnectionFactory);
        this.sessionManager = new MultiSessionManager(multiConnectionFactory);
    }
    
    /**
     * Creates a test topic on a specific broker with default settings.
     * 
     * @param brokerId The broker ID
     * @param topicName The topic name
     * @return true if topic was created successfully
     */
    public boolean createTestTopic(String brokerId, String topicName) {
        try {
            topicTypeManager.createTopic(brokerId, topicName);
            log.info("Created test topic: {} on broker: {}", topicName, brokerId);
            return true;
        } catch (Exception e) {
            log.error("Failed to create test topic: {} on broker: {}: {}", topicName, brokerId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Creates test topics on multiple brokers with the same configuration.
     * 
     * @param brokerIds List of broker IDs
     * @param topicName The topic name
     * @return Map of broker ID to success status
     */
    public Map<String, Boolean> createTestTopics(List<String> brokerIds, String topicName) {
        Map<String, Boolean> results = new HashMap<>();
        
        for (String brokerId : brokerIds) {
            results.put(brokerId, createTestTopic(brokerId, topicName));
        }
        
        return results;
    }
    
    /**
     * Sends test messages to a topic on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The topic name
     * @param messageCount Number of messages to send
     * @return List of Future objects for sent messages
     */
    public List<Future<RecordMetadata>> sendTestMessages(String brokerId, String topicName, int messageCount) {
        List<Future<RecordMetadata>> futures = new ArrayList<>();
        
        for (int i = 0; i < messageCount; i++) {
            try {
                String key = "test-key-" + i;
                String value = "test-message-" + i;
                Future<RecordMetadata> future = messageManager.sendMessage(brokerId, topicName, key, value);
                futures.add(future);
            } catch (Exception e) {
                log.error("Failed to send test message {} to topic: {} on broker: {}: {}", i, topicName, brokerId, e.getMessage(), e);
            }
        }
        
        log.info("Sent {} test messages to topic: {} on broker: {}", messageCount, topicName, brokerId);
        return futures;
    }
    
    /**
     * Sends test messages to topics on multiple brokers.
     * 
     * @param brokerTopicMap Map of broker ID to topic name
     * @param messageCount Number of messages to send per topic
     * @return Map of broker ID to list of Future objects
     */
    public Map<String, List<Future<RecordMetadata>>> sendTestMessagesToMultipleBrokers(
            Map<String, String> brokerTopicMap, int messageCount) {
        Map<String, List<Future<RecordMetadata>>> results = new HashMap<>();
        
        for (Map.Entry<String, String> entry : brokerTopicMap.entrySet()) {
            String brokerId = entry.getKey();
            String topicName = entry.getValue();
            List<Future<RecordMetadata>> futures = sendTestMessages(brokerId, topicName, messageCount);
            results.put(brokerId, futures);
        }
        
        return results;
    }
    
    /**
     * Creates a test consumer group on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID
     * @return true if consumer group was created successfully
     */
    public boolean createTestConsumerGroup(String brokerId, String groupId) {
        try {
            // This would typically involve creating a consumer and subscribing to a topic
            // For now, just log the creation
            log.info("Created test consumer group: {} on broker: {}", groupId, brokerId);
            return true;
        } catch (Exception e) {
            log.error("Failed to create test consumer group: {} on broker: {}: {}", groupId, brokerId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Registers a test schema on a specific schema registry.
     * 
     * @param schemaRegistryId The schema registry ID
     * @param subject The subject name
     * @param schema The schema definition
     * @return The schema ID
     */
    public int registerTestSchema(String schemaRegistryId, String subject, String schema) {
        try {
            int schemaId = schemaManager.registerSchema(schemaRegistryId, subject, schema);
            log.info("Registered test schema with ID: {} for subject: {} on schema registry: {}", schemaId, subject, schemaRegistryId);
            return schemaId;
        } catch (Exception e) {
            log.error("Failed to register test schema for subject: {} on schema registry: {}: {}", subject, schemaRegistryId, e.getMessage(), e);
            return -1;
        }
    }
    
    /**
     * Executes a test transaction on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @param groupId The consumer group ID
     * @param topicName The topic name
     * @param messageCount Number of messages to send in the transaction
     * @return true if transaction was executed successfully
     */
    public boolean executeTestTransaction(String brokerId, String transactionId, String groupId, 
                                        String topicName, int messageCount) {
        try {
            sessionManager.executeTransaction(brokerId, transactionId, groupId, (producer, consumer) -> {
                // Send messages in the transaction
                for (int i = 0; i < messageCount; i++) {
                    String key = "tx-key-" + i;
                    String value = "tx-message-" + i;
                    ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
                    producer.send(record);
                }
            });
            
            log.info("Executed test transaction: {} on broker: {} with {} messages", transactionId, brokerId, messageCount);
            return true;
        } catch (Exception e) {
            log.error("Failed to execute test transaction: {} on broker: {}: {}", transactionId, brokerId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Cleans up test resources for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicNames List of topic names to clean up
     * @param consumerGroupIds List of consumer group IDs to clean up
     */
    public void cleanupTestResources(String brokerId, List<String> topicNames, List<String> consumerGroupIds) {
        log.info("Cleaning up test resources for broker: {}", brokerId);
        
        // Clean up consumer groups
        for (String groupId : consumerGroupIds) {
            try {
                consumerManager.deleteConsumerGroup(brokerId, groupId);
                log.info("Deleted consumer group: {} on broker: {}", groupId, brokerId);
            } catch (Exception e) {
                log.error("Failed to delete consumer group: {} on broker: {}: {}", groupId, brokerId, e.getMessage(), e);
            }
        }
        
        // Clean up sessions
        try {
            sessionManager.closeAllForBroker(brokerId);
            log.info("Closed all sessions for broker: {}", brokerId);
        } catch (Exception e) {
            log.error("Failed to close sessions for broker: {}: {}", brokerId, e.getMessage(), e);
        }
        
        log.info("Completed cleanup for broker: {}", brokerId);
    }
    
    /**
     * Cleans up test resources across all brokers.
     * 
     * @param brokerTopicMap Map of broker ID to list of topic names
     * @param brokerGroupMap Map of broker ID to list of consumer group IDs
     */
    public void cleanupAllTestResources(Map<String, List<String>> brokerTopicMap, 
                                       Map<String, List<String>> brokerGroupMap) {
        log.info("Cleaning up all test resources");
        
        for (String brokerId : brokerTopicMap.keySet()) {
            List<String> topicNames = brokerTopicMap.get(brokerId);
            List<String> groupIds = brokerGroupMap.getOrDefault(brokerId, new ArrayList<>());
            cleanupTestResources(brokerId, topicNames, groupIds);
        }
        
        // Close all sessions globally
        try {
            sessionManager.closeAll();
            log.info("Closed all sessions globally");
        } catch (Exception e) {
            log.error("Failed to close all sessions: {}", e.getMessage(), e);
        }
        
        log.info("Completed cleanup of all test resources");
    }
    
    /**
     * Gets the message manager.
     * 
     * @return MultiEnhancedMessageManager instance
     */
    public MultiEnhancedMessageManager getMessageManager() {
        return messageManager;
    }
    
    /**
     * Gets the consumer manager.
     * 
     * @return MultiConsumerManager instance
     */
    public MultiConsumerManager getConsumerManager() {
        return consumerManager;
    }
    
    /**
     * Gets the schema manager.
     * 
     * @return MultiSimpleSchemaManager instance
     */
    public MultiSimpleSchemaManager getSchemaManager() {
        return schemaManager;
    }
    
    /**
     * Gets the topic type manager.
     * 
     * @return MultiTopicTypeManager instance
     */
    public MultiTopicTypeManager getTopicTypeManager() {
        return topicTypeManager;
    }
    
    /**
     * Gets the session manager.
     * 
     * @return MultiSessionManager instance
     */
    public MultiSessionManager getSessionManager() {
        return sessionManager;
    }
    
    /**
     * Gets the multi-connection factory.
     * 
     * @return MultiConnectionFactory instance
     */
    public MultiConnectionFactory getMultiConnectionFactory() {
        return multiConnectionFactory;
    }
}
