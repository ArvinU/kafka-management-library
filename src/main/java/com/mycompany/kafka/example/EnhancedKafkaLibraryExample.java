package com.mycompany.kafka.example;

import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.dto.ConsumerGroupInfo;
import com.mycompany.kafka.dto.TopicInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Enhanced example demonstrating all features of the Kafka Management Library.
 * This example shows how to use the new features including message viewing,
 * different topic types, session monitoring, and CLI usage.
 */
public class EnhancedKafkaLibraryExample {
    
    private static final Logger log = LoggerFactory.getLogger(EnhancedKafkaLibraryExample.class);
    
    public static void main(String[] args) {
        // Configuration
        final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
        final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
        
        KafkaManagementLibrary library = null;
        
        try {
            // Initialize the library
            library = new KafkaManagementLibrary(KAFKA_BOOTSTRAP_SERVERS, SCHEMA_REGISTRY_URL);
            
            // Demonstrate new features
            demonstrateMessageViewing(library);
            demonstrateTopicTypes(library);
            demonstrateSessionMonitoring(library);
            demonstrateCLIUsage();
            
            log.info("Enhanced example completed successfully!");
            
        } catch (Exception e) {
            log.error("Enhanced example failed: {}", e.getMessage(), e);
        } finally {
            if (library != null) {
                library.close();
            }
        }
    }
    
    /**
     * Demonstrates message viewing capabilities.
     */
    private static void demonstrateMessageViewing(KafkaManagementLibrary library) {
        log.info("=== Demonstrating Message Viewing ===");
        
        try {
            // Create a test topic
            String topicName = "message-viewing-topic";
            library.createTopic(topicName, 3, (short) 1);
            
            // Send some test messages
            library.sendMessage(topicName, "key1", "Message 1");
            library.sendMessage(topicName, "key2", "Message 2");
            library.sendMessage(topicName, "key3", "Message 3");
            
            // Peek at messages without consuming them
            List<ConsumerRecord<String, String>> messages = library.peekMessages(topicName, 5);
            log.info("Peeked at {} messages:", messages.size());
            for (ConsumerRecord<String, String> record : messages) {
                log.info("  Key: {}, Value: {}, Partition: {}, Offset: {}", 
                        record.key(), record.value(), record.partition(), record.offset());
            }
            
            // Get partition information
            Map<Integer, Long> partitionInfo = library.getMessageViewer().getPartitionInfo(topicName);
            log.info("Partition info: {}", partitionInfo);
            
            // Get message count for partition 0
            long messageCount = library.getMessageViewer().getMessageCount(topicName, 0);
            log.info("Message count for partition 0: {}", messageCount);
            
        } catch (Exception e) {
            log.error("Error in message viewing demonstration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates different topic types.
     */
    private static void demonstrateTopicTypes(KafkaManagementLibrary library) {
        log.info("=== Demonstrating Topic Types ===");
        
        try {
            // Create a compacted topic
            library.createCompactedTopic("compacted-topic", 3, (short) 1);
            log.info("Created compacted topic");
            
            // Create a high-throughput topic
            library.createHighThroughputTopic("high-throughput-topic", 6, (short) 1);
            log.info("Created high-throughput topic");
            
            // Create a low-latency topic
            library.createLowLatencyTopic("low-latency-topic", 3, (short) 1);
            log.info("Created low-latency topic");
            
            // Create a durable topic
            library.getTopicTypeManager().createDurableTopic("durable-topic", 3, (short) 1);
            log.info("Created durable topic");
            
            // Create a metrics topic
            library.getTopicTypeManager().createMetricsTopic("metrics-topic", 3, (short) 1);
            log.info("Created metrics topic");
            
            // Create a CDC topic
            library.getTopicTypeManager().createCDCTopic("cdc-topic", 3, (short) 1);
            log.info("Created CDC topic");
            
            // List all topics to see the new ones
            List<String> topics = library.listTopics();
            log.info("All topics: {}", topics);
            
        } catch (Exception e) {
            log.error("Error in topic types demonstration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates session monitoring capabilities.
     */
    private static void demonstrateSessionMonitoring(KafkaManagementLibrary library) {
        log.info("=== Demonstrating Session Monitoring ===");
        
        try {
            // Get active consumer groups
            List<ConsumerGroupInfo> activeGroups = library.getActiveConsumerGroups();
            log.info("Active consumer groups: {}", activeGroups.size());
            for (ConsumerGroupInfo group : activeGroups) {
                log.info("  Group: {}, State: {}, Members: {}", 
                        group.getGroupId(), group.getState(), group.getMembers().size());
            }
            
            // Get session summary
            Map<String, Object> sessionSummary = library.getSessionSummary();
            log.info("Session summary: {}", sessionSummary);
            
            // Get consumer group health for each group
            for (ConsumerGroupInfo group : activeGroups) {
                Map<String, Object> health = library.getSessionViewer().getConsumerGroupHealth(group.getGroupId());
                log.info("Health for group {}: {}", group.getGroupId(), health);
            }
            
        } catch (Exception e) {
            log.error("Error in session monitoring demonstration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates CLI usage.
     */
    private static void demonstrateCLIUsage() {
        log.info("=== Demonstrating CLI Usage ===");
        
        try {
            // Show how to use the CLI programmatically
            log.info("CLI can be used in two ways:");
            log.info("1. Interactive mode: java -jar kafka-management-library.jar localhost:9092 http://localhost:8081");
            log.info("2. Non-interactive mode: java -jar kafka-management-library.jar localhost:9092 http://localhost:8081 'topics list'");
            log.info("3. Available CLI commands:");
            log.info("   - topics list");
            log.info("   - topics info <topic-name>");
            log.info("   - messages peek <topic> [count]");
            log.info("   - consumers list");
            log.info("   - consumers info <group-id>");
            log.info("   - sessions summary");
            log.info("   - create-topic <name> <partitions> <replication> [type]");
            log.info("   - send-message <topic> <key> <value>");
            log.info("   - peek-messages <topic> [count]");
            log.info("   - list-consumers");
            log.info("   - consumer-health <group-id>");
            
        } catch (Exception e) {
            log.error("Error in CLI demonstration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates advanced message viewing features.
     */
    private static void demonstrateAdvancedMessageViewing(KafkaManagementLibrary library) {
        log.info("=== Demonstrating Advanced Message Viewing ===");
        
        try {
            String topicName = "advanced-viewing-topic";
            
            // Create topic and send messages
            library.createTopic(topicName, 3, (short) 1);
            library.sendMessage(topicName, "user1", "User 1 data");
            library.sendMessage(topicName, "user2", "User 2 data");
            library.sendMessage(topicName, "admin", "Admin data");
            
            // Peek messages by key pattern
            List<ConsumerRecord<String, String>> userMessages = 
                library.getMessageViewer().peekMessagesByKey(topicName, "user.*", 10);
            log.info("Messages with user keys: {}", userMessages.size());
            
            // Peek messages by value pattern
            List<ConsumerRecord<String, String>> dataMessages = 
                library.getMessageViewer().peekMessagesByValue(topicName, ".*data.*", 10);
            log.info("Messages with 'data' in value: {}", dataMessages.size());
            
            // Peek from specific partition
            List<ConsumerRecord<String, String>> partitionMessages = 
                library.getMessageViewer().peekMessagesFromPartition(topicName, 0, 0, 5);
            log.info("Messages from partition 0: {}", partitionMessages.size());
            
            // Get offset information
            long latestOffset = library.getMessageViewer().getLatestOffset(topicName, 0);
            long earliestOffset = library.getMessageViewer().getEarliestOffset(topicName, 0);
            log.info("Partition 0 offsets - Earliest: {}, Latest: {}", earliestOffset, latestOffset);
            
        } catch (Exception e) {
            log.error("Error in advanced message viewing demonstration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Demonstrates topic template usage.
     */
    private static void demonstrateTopicTemplates(KafkaManagementLibrary library) {
        log.info("=== Demonstrating Topic Templates ===");
        
        try {
            // Create topics using different templates
            Map<String, Object> params = new java.util.HashMap<>();
            params.put("retentionMs", 86400000L); // 1 day
            
            library.getTopicTypeManager().createTopicFromTemplate(
                "template-time-retention", 3, (short) 1, "TIME_RETENTION", params);
            log.info("Created topic from TIME_RETENTION template");
            
            params.clear();
            params.put("retentionBytes", 1073741824L); // 1GB
            
            library.getTopicTypeManager().createTopicFromTemplate(
                "template-size-retention", 3, (short) 1, "SIZE_RETENTION", params);
            log.info("Created topic from SIZE_RETENTION template");
            
            library.getTopicTypeManager().createTopicFromTemplate(
                "template-event-sourcing", 3, (short) 1, "EVENT_SOURCING", new java.util.HashMap<>());
            log.info("Created topic from EVENT_SOURCING template");
            
            library.getTopicTypeManager().createTopicFromTemplate(
                "template-dead-letter", 3, (short) 1, "DEAD_LETTER", new java.util.HashMap<>());
            log.info("Created topic from DEAD_LETTER template");
            
        } catch (Exception e) {
            log.error("Error in topic templates demonstration: {}", e.getMessage(), e);
        }
    }
}
