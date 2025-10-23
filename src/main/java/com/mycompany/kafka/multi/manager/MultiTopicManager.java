package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced Topic Manager that works with named brokers.
 * Provides topic management operations for specific brokers.
 */
public class MultiTopicManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiTopicManager.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    
    public MultiTopicManager(MultiConnectionFactory multiConnectionFactory) {
        this.multiConnectionFactory = multiConnectionFactory;
    }
    
    /**
     * Creates a topic on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic to create
     * @param numPartitions Number of partitions
     * @param replicationFactor Replication factor
     * @return true if topic was created successfully
     * @throws KafkaManagementException if operation fails
     */
    public boolean createTopic(String brokerName, String topicName, int numPartitions, short replicationFactor) throws KafkaManagementException {
        log.info("Creating topic '{}' on broker '{}' with {} partitions and replication factor {}", 
                topicName, brokerName, numPartitions, replicationFactor);
        
        try {
            AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerName);
            
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            
            // Wait for the topic creation to complete
            result.all().get(30, TimeUnit.SECONDS);
            
            log.info("Topic '{}' created successfully on broker '{}'", topicName, brokerName);
            return true;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to create topic '" + topicName + "' on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Creates a topic with custom configuration on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic to create
     * @param numPartitions Number of partitions
     * @param replicationFactor Replication factor
     * @param configs Additional topic configurations
     * @return true if topic was created successfully
     * @throws KafkaManagementException if operation fails
     */
    public boolean createTopic(String brokerName, String topicName, int numPartitions, short replicationFactor, Map<String, String> configs) throws KafkaManagementException {
        log.info("Creating topic '{}' on broker '{}' with custom configuration", topicName, brokerName);
        
        try {
            AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerName);
            
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            if (configs != null && !configs.isEmpty()) {
                newTopic.configs(configs);
            }
            
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            
            // Wait for the topic creation to complete
            result.all().get(30, TimeUnit.SECONDS);
            
            log.info("Topic '{}' created successfully on broker '{}' with custom configuration", topicName, brokerName);
            return true;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to create topic '" + topicName + "' on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Deletes a topic on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic to delete
     * @return true if topic was deleted successfully
     * @throws KafkaManagementException if operation fails
     */
    public boolean deleteTopic(String brokerName, String topicName) throws KafkaManagementException {
        log.info("Deleting topic '{}' on broker '{}'", topicName, brokerName);
        
        try {
            AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerName);
            
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singletonList(topicName));
            
            // Wait for the topic deletion to complete
            result.all().get(30, TimeUnit.SECONDS);
            
            log.info("Topic '{}' deleted successfully on broker '{}'", topicName, brokerName);
            return true;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to delete topic '" + topicName + "' on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Lists all topics on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @return Set of topic names
     * @throws KafkaManagementException if operation fails
     */
    public Set<String> listTopics(String brokerName) throws KafkaManagementException {
        log.info("Listing topics on broker '{}'", brokerName);
        
        try {
            AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerName);
            
            ListTopicsResult result = adminClient.listTopics();
            Collection<TopicListing> topics = result.listings().get(30, TimeUnit.SECONDS);
            
            Set<String> topicNames = new java.util.HashSet<>();
            for (TopicListing topic : topics) {
                topicNames.add(topic.name());
            }
            
            log.info("Found {} topics on broker '{}'", topicNames.size(), brokerName);
            return topicNames;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to list topics on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Checks if a topic exists on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic to check
     * @return true if topic exists
     * @throws KafkaManagementException if operation fails
     */
    public boolean topicExists(String brokerName, String topicName) throws KafkaManagementException {
        log.info("Checking if topic '{}' exists on broker '{}'", topicName, brokerName);
        
        try {
            Set<String> topics = listTopics(brokerName);
            boolean exists = topics.contains(topicName);
            
            log.info("Topic '{}' {} on broker '{}'", topicName, exists ? "exists" : "does not exist", brokerName);
            return exists;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to check if topic '" + topicName + "' exists on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Gets detailed information about a topic on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic
     * @return TopicDescription containing topic details
     * @throws KafkaManagementException if operation fails
     */
    public TopicDescription getTopicDescription(String brokerName, String topicName) throws KafkaManagementException {
        log.info("Getting description for topic '{}' on broker '{}'", topicName, brokerName);
        
        try {
            AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerName);
            
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singletonList(topicName));
            Map<String, TopicDescription> descriptions = result.all().get(30, TimeUnit.SECONDS);
            
            TopicDescription description = descriptions.get(topicName);
            if (description == null) {
                throw new KafkaManagementException(
                    ErrorConstants.KAFKA_CONNECTION_FAILED,
                    "Topic '" + topicName + "' not found on broker '" + brokerName + "'");
            }
            
            log.info("Retrieved description for topic '{}' on broker '{}'", topicName, brokerName);
            return description;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to get description for topic '" + topicName + "' on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Creates a topic on the first connected broker.
     * 
     * @param topicName The name of the topic to create
     * @param numPartitions Number of partitions
     * @param replicationFactor Replication factor
     * @return true if topic was created successfully
     * @throws KafkaManagementException if operation fails
     */
    public boolean createTopic(String topicName, int numPartitions, short replicationFactor) throws KafkaManagementException {
        String brokerName = multiConnectionFactory.getMultiBrokerManager().getFirstConnectedBroker();
        if (brokerName == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "No connected brokers available");
        }
        
        return createTopic(brokerName, topicName, numPartitions, replicationFactor);
    }
    
    /**
     * Deletes a topic on the first connected broker.
     * 
     * @param topicName The name of the topic to delete
     * @return true if topic was deleted successfully
     * @throws KafkaManagementException if operation fails
     */
    public boolean deleteTopic(String topicName) throws KafkaManagementException {
        String brokerName = multiConnectionFactory.getMultiBrokerManager().getFirstConnectedBroker();
        if (brokerName == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "No connected brokers available");
        }
        
        return deleteTopic(brokerName, topicName);
    }
    
    /**
     * Lists all topics on the first connected broker.
     * 
     * @return Set of topic names
     * @throws KafkaManagementException if operation fails
     */
    public Set<String> listTopics() throws KafkaManagementException {
        String brokerName = multiConnectionFactory.getMultiBrokerManager().getFirstConnectedBroker();
        if (brokerName == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "No connected brokers available");
        }
        
        return listTopics(brokerName);
    }
}
