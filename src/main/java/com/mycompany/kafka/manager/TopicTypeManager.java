package com.mycompany.kafka.manager;

import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Manager class for creating different types of Kafka topics with specific configurations.
 * Provides functionality to create topics with various retention policies, compaction settings, etc.
 */
public class TopicTypeManager {
    
    private static final Logger log = LoggerFactory.getLogger(TopicTypeManager.class);
    
    private final AdminClient adminClient;
    
    public TopicTypeManager(ConnectionFactory connectionFactory) {
        this.adminClient = connectionFactory.createAdminClient();
    }
    
    /**
     * Creates a compacted topic for log compaction.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createCompactedTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating compacted topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with time-based retention policy.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param retentionMs Retention time in milliseconds
     * @throws RuntimeException if topic creation fails
     */
    public void createTimeRetentionTopic(String topicName, int numPartitions, short replicationFactor, long retentionMs) {
        log.info("Creating time retention topic: {} with {} partitions, replication factor {}, retention: {}ms", 
                topicName, numPartitions, replicationFactor, retentionMs);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("retention.ms", String.valueOf(retentionMs));
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with size-based retention policy.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param retentionBytes Maximum size in bytes per partition
     * @throws RuntimeException if topic creation fails
     */
    public void createSizeRetentionTopic(String topicName, int numPartitions, short replicationFactor, long retentionBytes) {
        log.info("Creating size retention topic: {} with {} partitions, replication factor {}, retention: {} bytes", 
                topicName, numPartitions, replicationFactor, retentionBytes);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("retention.bytes", String.valueOf(retentionBytes));
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a high-throughput topic optimized for high message volume.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createHighThroughputTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating high throughput topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "lz4");
        configs.put("batch.size", "16384");
        configs.put("linger.ms", "5");
        configs.put("buffer.memory", "33554432");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "604800000"); // 7 days
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a low-latency topic optimized for real-time processing.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createLowLatencyTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating low latency topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "none");
        configs.put("batch.size", "1024");
        configs.put("linger.ms", "0");
        configs.put("buffer.memory", "33554432");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "3600000"); // 1 hour
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a durable topic with high durability guarantees.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createDurableTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating durable topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", String.valueOf(replicationFactor));
        configs.put("retention.ms", "2592000000"); // 30 days
        configs.put("segment.ms", "604800000"); // 7 days
        configs.put("segment.bytes", "1073741824"); // 1GB
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for event sourcing with long retention.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createEventSourcingTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating event sourcing topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "31536000000"); // 1 year
        configs.put("segment.ms", "2592000000"); // 30 days
        configs.put("segment.bytes", "1073741824"); // 1GB
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for change data capture (CDC) with compaction.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createCDCTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating CDC topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact,delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "604800000"); // 7 days
        configs.put("segment.ms", "3600000"); // 1 hour
        configs.put("segment.bytes", "268435456"); // 256MB
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for dead letter queues.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createDeadLetterTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating dead letter topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "2592000000"); // 30 days
        configs.put("segment.ms", "604800000"); // 7 days
        configs.put("segment.bytes", "134217728"); // 128MB
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for metrics and monitoring data.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createMetricsTopic(String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating metrics topic: {} with {} partitions and replication factor {}", 
                topicName, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "lz4");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "86400000"); // 1 day
        configs.put("segment.ms", "3600000"); // 1 hour
        configs.put("segment.bytes", "67108864"); // 64MB
        
        createTopicWithConfigs(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with custom configurations.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param configs Custom topic configurations
     * @throws RuntimeException if topic creation fails
     */
    public void createTopicWithConfigs(String topicName, int numPartitions, short replicationFactor, Map<String, String> configs) {
        log.info("Creating topic: {} with {} partitions, replication factor {}, and custom configs: {}", 
                topicName, numPartitions, replicationFactor, configs);
        
        try {
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            newTopic.configs(configs);
            
            adminClient.createTopics(java.util.Collections.singletonList(newTopic)).all().get();
            log.info("Successfully created topic: {} with custom configurations", topicName);
        } catch (ExecutionException e) {
            log.error("Failed to create topic {} with custom configs: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to create topic with custom configs: " + topicName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while creating topic {} with custom configs", topicName, e);
            throw new RuntimeException("Thread interrupted while creating topic with custom configs: " + topicName, e);
        }
    }
    
    /**
     * Creates a topic with predefined template configurations.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param template The template type (COMPACTED, TIME_RETENTION, SIZE_RETENTION, HIGH_THROUGHPUT, LOW_LATENCY, DURABLE, EVENT_SOURCING, CDC, DEAD_LETTER, METRICS)
     * @param templateParams Additional parameters for the template
     * @throws RuntimeException if topic creation fails
     */
    public void createTopicFromTemplate(String topicName, int numPartitions, short replicationFactor, 
                                      String template, Map<String, Object> templateParams) {
        log.info("Creating topic: {} from template: {} with {} partitions and replication factor {}", 
                topicName, template, numPartitions, replicationFactor);
        
        switch (template.toUpperCase()) {
            case "COMPACTED":
                createCompactedTopic(topicName, numPartitions, replicationFactor);
                break;
            case "TIME_RETENTION":
                long retentionMs = templateParams.containsKey("retentionMs") ? 
                    (Long) templateParams.get("retentionMs") : 604800000L; // 7 days default
                createTimeRetentionTopic(topicName, numPartitions, replicationFactor, retentionMs);
                break;
            case "SIZE_RETENTION":
                long retentionBytes = templateParams.containsKey("retentionBytes") ? 
                    (Long) templateParams.get("retentionBytes") : 1073741824L; // 1GB default
                createSizeRetentionTopic(topicName, numPartitions, replicationFactor, retentionBytes);
                break;
            case "HIGH_THROUGHPUT":
                createHighThroughputTopic(topicName, numPartitions, replicationFactor);
                break;
            case "LOW_LATENCY":
                createLowLatencyTopic(topicName, numPartitions, replicationFactor);
                break;
            case "DURABLE":
                createDurableTopic(topicName, numPartitions, replicationFactor);
                break;
            case "EVENT_SOURCING":
                createEventSourcingTopic(topicName, numPartitions, replicationFactor);
                break;
            case "CDC":
                createCDCTopic(topicName, numPartitions, replicationFactor);
                break;
            case "DEAD_LETTER":
                createDeadLetterTopic(topicName, numPartitions, replicationFactor);
                break;
            case "METRICS":
                createMetricsTopic(topicName, numPartitions, replicationFactor);
                break;
            default:
                throw new IllegalArgumentException("Unknown topic template: " + template);
        }
    }
    
    /**
     * Closes the AdminClient to release resources.
     */
    public void close() {
        if (adminClient != null) {
            adminClient.close();
            log.info("TopicTypeManager AdminClient closed successfully");
        }
    }
}
