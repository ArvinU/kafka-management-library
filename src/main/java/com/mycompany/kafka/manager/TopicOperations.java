package com.mycompany.kafka.manager;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Handles basic topic operations like create and delete.
 */
public class TopicOperations {
    
    private static final Logger log = LoggerFactory.getLogger(TopicOperations.class);
    
    private final AdminClient adminClient;
    
    public TopicOperations(AdminClient adminClient) {
        this.adminClient = adminClient;
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
    public void createTopic(String topicName, int numPartitions, short replicationFactor, java.util.Map<String, String> configs) {
        log.info("Creating topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        try {
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            
            // Add custom configurations if provided
            if (configs != null && !configs.isEmpty()) {
                newTopic.configs(configs);
            }
            
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            log.info("Successfully created topic: {}", topicName);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                log.warn("Topic {} already exists", topicName);
            } else {
                log.error("Failed to create topic {}: {}", topicName, e.getMessage(), e);
                throw new RuntimeException("Failed to create topic: " + topicName, e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while creating topic {}", topicName, e);
            throw new RuntimeException("Thread interrupted while creating topic: " + topicName, e);
        }
    }
    
    /**
     * Creates a new Kafka topic with default configurations.
     * 
     * @param topicName The name of the topic to create
     * @param numPartitions The number of partitions for the topic
     * @param replicationFactor The replication factor for the topic
     */
    public void createTopic(String topicName, int numPartitions, short replicationFactor) {
        createTopic(topicName, numPartitions, replicationFactor, null);
    }
    
    /**
     * Deletes a Kafka topic.
     * 
     * @param topicName The name of the topic to delete
     * @throws RuntimeException if topic deletion fails
     */
    public void deleteTopic(String topicName) {
        log.info("Deleting topic: {}", topicName);
        
        try {
            adminClient.deleteTopics(Collections.singletonList(topicName)).all().get();
            log.info("Successfully deleted topic: {}", topicName);
        } catch (ExecutionException e) {
            log.error("Failed to delete topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete topic: " + topicName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while deleting topic {}", topicName, e);
            throw new RuntimeException("Thread interrupted while deleting topic: " + topicName, e);
        }
    }
}
