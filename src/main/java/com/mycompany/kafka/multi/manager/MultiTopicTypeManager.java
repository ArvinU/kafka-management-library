package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Enhanced Topic Type Manager for multiple brokers.
 */
public class MultiTopicTypeManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiTopicTypeManager.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    
    public MultiTopicTypeManager(MultiConnectionFactory multiConnectionFactory) {
        this.multiConnectionFactory = multiConnectionFactory;
    }
    
    /**
     * Creates a compacted topic for log compaction on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createCompactedTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating compacted topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with time-based retention policy on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param retentionMs Retention time in milliseconds
     * @throws RuntimeException if topic creation fails
     */
    public void createTimeRetentionTopic(String brokerId, String topicName, int numPartitions, short replicationFactor, long retentionMs) {
        log.info("Creating time retention topic: {} on broker: {} with {} partitions, replication factor {}, retention: {}ms", 
                topicName, brokerId, numPartitions, replicationFactor, retentionMs);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("retention.ms", String.valueOf(retentionMs));
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with size-based retention policy on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param retentionBytes Maximum size in bytes per partition
     * @throws RuntimeException if topic creation fails
     */
    public void createSizeRetentionTopic(String brokerId, String topicName, int numPartitions, short replicationFactor, long retentionBytes) {
        log.info("Creating size retention topic: {} on broker: {} with {} partitions, replication factor {}, retention: {} bytes", 
                topicName, brokerId, numPartitions, replicationFactor, retentionBytes);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("retention.bytes", String.valueOf(retentionBytes));
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a high-throughput topic optimized for high message volume on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createHighThroughputTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating high throughput topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "lz4");
        configs.put("batch.size", "16384");
        configs.put("linger.ms", "5");
        configs.put("buffer.memory", "33554432");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "604800000"); // 7 days
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a low-latency topic optimized for real-time processing on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createLowLatencyTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating low latency topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "none");
        configs.put("batch.size", "1024");
        configs.put("linger.ms", "0");
        configs.put("buffer.memory", "33554432");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "3600000"); // 1 hour
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a durable topic with high durability guarantees on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createDurableTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating durable topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", String.valueOf(replicationFactor));
        configs.put("retention.ms", "2592000000"); // 30 days
        configs.put("segment.ms", "604800000"); // 7 days
        configs.put("segment.bytes", "1073741824"); // 1GB
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for event sourcing with long retention on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createEventSourcingTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating event sourcing topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "31536000000"); // 1 year
        configs.put("segment.ms", "2592000000"); // 30 days
        configs.put("segment.bytes", "1073741824"); // 1GB
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for change data capture (CDC) with compaction on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createCDCTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating CDC topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact,delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "604800000"); // 7 days
        configs.put("segment.ms", "3600000"); // 1 hour
        configs.put("segment.bytes", "268435456"); // 256MB
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for dead letter queues on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createDeadLetterTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating dead letter topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "2592000000"); // 30 days
        configs.put("segment.ms", "604800000"); // 7 days
        configs.put("segment.bytes", "134217728"); // 128MB
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic for metrics and monitoring data on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createMetricsTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating metrics topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "lz4");
        configs.put("min.insync.replicas", "1");
        configs.put("retention.ms", "86400000"); // 1 day
        configs.put("segment.ms", "3600000"); // 1 hour
        configs.put("segment.bytes", "67108864"); // 64MB
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with custom configurations on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param configs Custom topic configurations
     * @throws RuntimeException if topic creation fails
     */
    public void createTopicWithConfigs(String brokerId, String topicName, int numPartitions, short replicationFactor, Map<String, String> configs) {
        log.info("Creating topic: {} on broker: {} with {} partitions, replication factor {}, and custom configs: {}", 
                topicName, brokerId, numPartitions, replicationFactor, configs);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            newTopic.configs(configs);
            
            adminClient.createTopics(java.util.Collections.singletonList(newTopic)).all().get();
            log.info("Successfully created topic: {} on broker: {} with custom configurations", topicName, brokerId);
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.TOPIC_CREATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TOPIC_CREATION_FAILED_MSG, topicName, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.TOPIC_CREATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TOPIC_CREATION_FAILED_MSG, topicName, e.getMessage()),
                e);
        }
    }
    
    /**
     * Creates a topic with predefined template configurations on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param template The template type (COMPACTED, TIME_RETENTION, SIZE_RETENTION, HIGH_THROUGHPUT, LOW_LATENCY, DURABLE, EVENT_SOURCING, CDC, DEAD_LETTER, METRICS)
     * @param templateParams Additional parameters for the template
     * @throws RuntimeException if topic creation fails
     */
    public void createTopicFromTemplate(String brokerId, String topicName, int numPartitions, short replicationFactor, 
                                      String template, Map<String, Object> templateParams) {
        log.info("Creating topic: {} on broker: {} from template: {} with {} partitions and replication factor {}", 
                topicName, brokerId, template, numPartitions, replicationFactor);
        
        switch (template.toUpperCase()) {
            case "COMPACTED":
                createCompactedTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "TIME_RETENTION":
                long retentionMs = templateParams.containsKey("retentionMs") ? 
                    (Long) templateParams.get("retentionMs") : 604800000L; // 7 days default
                createTimeRetentionTopic(brokerId, topicName, numPartitions, replicationFactor, retentionMs);
                break;
            case "SIZE_RETENTION":
                long retentionBytes = templateParams.containsKey("retentionBytes") ? 
                    (Long) templateParams.get("retentionBytes") : 1073741824L; // 1GB default
                createSizeRetentionTopic(brokerId, topicName, numPartitions, replicationFactor, retentionBytes);
                break;
            case "HIGH_THROUGHPUT":
                createHighThroughputTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "LOW_LATENCY":
                createLowLatencyTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "DURABLE":
                createDurableTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "EVENT_SOURCING":
                createEventSourcingTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "CDC":
                createCDCTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "DEAD_LETTER":
                createDeadLetterTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            case "METRICS":
                createMetricsTopic(brokerId, topicName, numPartitions, replicationFactor);
                break;
            default:
                throw new IllegalArgumentException("Unknown topic template: " + template);
        }
    }
    
    /**
     * Creates a topic with default configurations on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @throws RuntimeException if topic creation fails
     */
    public void createTopic(String brokerId, String topicName, int numPartitions, short replicationFactor) {
        log.info("Creating topic: {} on broker: {} with {} partitions and replication factor {}", 
                topicName, brokerId, numPartitions, replicationFactor);
        
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "delete");
        configs.put("compression.type", "snappy");
        configs.put("min.insync.replicas", "1");
        
        createTopicWithConfigs(brokerId, topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Creates a topic with default configurations on a specific broker (single partition).
     * 
     * @param brokerId The broker ID
     * @param topicName The name of the topic
     * @throws RuntimeException if topic creation fails
     */
    public void createTopic(String brokerId, String topicName) {
        createTopic(brokerId, topicName, 1, (short) 1);
    }
    
    /**
     * Creates topics with the same configuration across multiple brokers.
     * 
     * @param brokerIds List of broker IDs
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     * @param template The template type
     * @param templateParams Additional parameters for the template
     * @throws RuntimeException if topic creation fails
     */
    public void createTopicAcrossBrokers(List<String> brokerIds, String topicName, int numPartitions, 
                                       short replicationFactor, String template, Map<String, Object> templateParams) {
        log.info("Creating topic: {} across {} brokers with template: {}", topicName, brokerIds.size(), template);
        
        for (String brokerId : brokerIds) {
            try {
                createTopicFromTemplate(brokerId, topicName, numPartitions, replicationFactor, template, templateParams);
                log.info("Successfully created topic: {} on broker: {}", topicName, brokerId);
            } catch (Exception e) {
                log.error("Failed to create topic: {} on broker: {}: {}", topicName, brokerId, e.getMessage(), e);
                // Continue with other brokers even if one fails
            }
        }
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
