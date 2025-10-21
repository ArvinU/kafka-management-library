package com.mycompany.kafka.manager;

import com.mycompany.kafka.dto.TopicInfo;
import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Manager class for Kafka topic operations.
 * Provides functionality to create, delete, list, and describe topics.
 */
public class TopicManager {
    
    private static final Logger log = LoggerFactory.getLogger(TopicManager.class);
    
    private final AdminClient adminClient;
    private final TopicOperations topicOperations;
    private final TopicQueries topicQueries;
    
    public TopicManager(ConnectionFactory connectionFactory) {
        this.adminClient = connectionFactory.createAdminClient();
        this.topicOperations = new TopicOperations(adminClient);
        this.topicQueries = new TopicQueries(adminClient);
    }
    
    /**
     * Creates a new Kafka topic with the specified parameters.
     * 
     * @param topicName The name of the topic to create
     * @param numPartitions The number of partitions for the topic
     * @param replicationFactor The replication factor for the topic
     * @param configs Additional topic configurations
     * @throws RuntimeException if topic creation fails
     */
    public void createTopic(String topicName, int numPartitions, short replicationFactor, Map<String, String> configs) {
        topicOperations.createTopic(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a new Kafka topic with default configurations.
     * 
     * @param topicName The name of the topic to create
     * @param numPartitions The number of partitions for the topic
     * @param replicationFactor The replication factor for the topic
     */
    public void createTopic(String topicName, int numPartitions, short replicationFactor) {
        topicOperations.createTopic(topicName, numPartitions, replicationFactor);
    }
    
    /**
     * Deletes a Kafka topic.
     * 
     * @param topicName The name of the topic to delete
     * @throws RuntimeException if topic deletion fails
     */
    public void deleteTopic(String topicName) {
        topicOperations.deleteTopic(topicName);
    }
    
    /**
     * Lists all available topics.
     * 
     * @return List of topic names
     * @throws RuntimeException if listing topics fails
     */
    public List<String> listTopics() {
        return topicQueries.listTopics();
    }
    
    /**
     * Lists all available topics with their information.
     * 
     * @return List of TopicInfo objects
     * @throws RuntimeException if listing topics fails
     */
    public List<TopicInfo> listTopicsWithInfo() {
        return topicQueries.listTopicsWithInfo();
    }
    
    /**
     * Describes a specific topic.
     * 
     * @param topicName The name of the topic to describe
     * @return TopicInfo object with topic details
     * @throws RuntimeException if describing topic fails
     */
    public TopicInfo describeTopic(String topicName) {
        return topicQueries.describeTopic(topicName);
    }
    
    /**
     * Gets detailed topic configuration.
     * 
     * @param topicName The name of the topic
     * @return Map of configuration key-value pairs
     * @throws RuntimeException if getting topic configuration fails
     */
    public Map<String, String> getTopicConfig(String topicName) {
        return topicQueries.getTopicConfig(topicName);
    }
    
    /**
     * Checks if a topic exists.
     * 
     * @param topicName The name of the topic to check
     * @return true if topic exists, false otherwise
     */
    public boolean topicExists(String topicName) {
        return topicQueries.topicExists(topicName);
    }
    
    /**
     * Gets the number of partitions for a topic.
     * 
     * @param topicName The name of the topic
     * @return Number of partitions
     * @throws RuntimeException if getting partition count fails
     */
    public int getPartitionCount(String topicName) {
        return topicQueries.getPartitionCount(topicName);
    }
    
    /**
     * Closes the AdminClient to release resources.
     */
    public void close() {
        if (adminClient != null) {
            adminClient.close();
            log.info("TopicManager AdminClient closed successfully");
        }
    }
    
    // Getters for access to sub-components
    public TopicOperations getTopicOperations() {
        return topicOperations;
    }
    
    public TopicQueries getTopicQueries() {
        return topicQueries;
    }
}