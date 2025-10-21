package com.mycompany.kafka.manager;

import com.mycompany.kafka.dto.TopicInfo;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Handles topic query operations like list, describe, and get configurations.
 */
public class TopicQueries {
    
    private static final Logger log = LoggerFactory.getLogger(TopicQueries.class);
    
    private final AdminClient adminClient;
    
    public TopicQueries(AdminClient adminClient) {
        this.adminClient = adminClient;
    }
    
    /**
     * Lists all available topics.
     * 
     * @return List of topic names
     * @throws RuntimeException if listing topics fails
     */
    public List<String> listTopics() {
        log.info("Listing all topics");
        
        try {
            Set<String> topicNames = adminClient.listTopics().names().get();
            List<String> topics = new ArrayList<>(topicNames);
            log.info("Found {} topics", topics.size());
            return topics;
        } catch (ExecutionException e) {
            log.error("Failed to list topics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list topics", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while listing topics", e);
            throw new RuntimeException("Thread interrupted while listing topics", e);
        }
    }
    
    /**
     * Lists all available topics with their information.
     * 
     * @return List of TopicInfo objects
     * @throws RuntimeException if listing topics fails
     */
    public List<TopicInfo> listTopicsWithInfo() {
        log.info("Listing all topics with information");
        
        try {
            Map<String, TopicDescription> topicDescriptions = adminClient.describeTopics(
                adminClient.listTopics().names().get()
            ).allTopicNames().get();
            
            List<TopicInfo> topics = topicDescriptions.entrySet().stream()
                .map(entry -> {
                    TopicDescription desc = entry.getValue();
                    TopicInfo info = new TopicInfo();
                    info.setName(desc.name());
                    info.setPartitions(desc.partitions().size());
                    info.setReplicationFactor((short) desc.partitions().get(0).replicas().size());
                    info.setInternal(desc.isInternal());
                    return info;
                })
                .collect(Collectors.toList());
            
            log.info("Found {} topics with information", topics.size());
            return topics;
        } catch (ExecutionException e) {
            log.error("Failed to list topics with information: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list topics with information", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while listing topics with information", e);
            throw new RuntimeException("Thread interrupted while listing topics with information", e);
        }
    }
    
    /**
     * Describes a specific topic.
     * 
     * @param topicName The name of the topic to describe
     * @return TopicInfo object with topic details
     * @throws RuntimeException if describing topic fails
     */
    public TopicInfo describeTopic(String topicName) {
        log.info("Describing topic: {}", topicName);
        
        try {
            TopicDescription description = adminClient.describeTopics(Collections.singletonList(topicName))
                .allTopicNames().get().get(topicName);
            
            TopicInfo info = new TopicInfo();
            info.setName(description.name());
            info.setPartitions(description.partitions().size());
            info.setInternal(description.isInternal());
            
            if (!description.partitions().isEmpty()) {
                info.setReplicationFactor((short) description.partitions().get(0).replicas().size());
            }
            
            log.info("Successfully described topic: {}", topicName);
            return info;
        } catch (ExecutionException e) {
            log.error("Failed to describe topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to describe topic: " + topicName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while describing topic {}", topicName, e);
            throw new RuntimeException("Thread interrupted while describing topic: " + topicName, e);
        }
    }
    
    /**
     * Gets detailed topic configuration.
     * 
     * @param topicName The name of the topic
     * @return Map of configuration key-value pairs
     * @throws RuntimeException if getting topic configuration fails
     */
    public Map<String, String> getTopicConfig(String topicName) {
        log.info("Getting configuration for topic: {}", topicName);
        
        try {
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
            Config config = adminClient.describeConfigs(
                Collections.singletonList(configResource)
            ).all().get().get(configResource);
            
            Map<String, String> configs = new HashMap<>();
            for (ConfigEntry entry : config.entries()) {
                configs.put(entry.name(), entry.value());
            }
            
            log.info("Successfully retrieved configuration for topic: {}", topicName);
            return configs;
        } catch (ExecutionException e) {
            log.error("Failed to get configuration for topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to get configuration for topic: " + topicName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while getting configuration for topic {}", topicName, e);
            throw new RuntimeException("Thread interrupted while getting configuration for topic: " + topicName, e);
        }
    }
    
    /**
     * Checks if a topic exists.
     * 
     * @param topicName The name of the topic to check
     * @return true if topic exists, false otherwise
     */
    public boolean topicExists(String topicName) {
        log.info("Checking if topic exists: {}", topicName);
        
        try {
            Set<String> topicNames = adminClient.listTopics().names().get();
            boolean exists = topicNames.contains(topicName);
            log.info("Topic {} exists: {}", topicName, exists);
            return exists;
        } catch (ExecutionException e) {
            log.error("Failed to check if topic exists {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to check if topic exists: " + topicName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while checking if topic exists {}", topicName, e);
            throw new RuntimeException("Thread interrupted while checking if topic exists: " + topicName, e);
        }
    }
    
    /**
     * Gets the number of partitions for a topic.
     * 
     * @param topicName The name of the topic
     * @return Number of partitions
     * @throws RuntimeException if getting partition count fails
     */
    public int getPartitionCount(String topicName) {
        log.info("Getting partition count for topic: {}", topicName);
        
        try {
            TopicDescription description = adminClient.describeTopics(Collections.singletonList(topicName))
                .allTopicNames().get().get(topicName);
            
            int partitionCount = description.partitions().size();
            log.info("Topic {} has {} partitions", topicName, partitionCount);
            return partitionCount;
        } catch (ExecutionException e) {
            log.error("Failed to get partition count for topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to get partition count for topic: " + topicName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while getting partition count for topic {}", topicName, e);
            throw new RuntimeException("Thread interrupted while getting partition count for topic: " + topicName, e);
        }
    }
}
