package com.mycompany.kafka.manager;

import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * Manager class for viewing Kafka messages without consuming them.
 * Provides functionality to peek at messages, view message metadata, and browse topic contents.
 */
public class MessageViewer {
    
    private static final Logger log = LoggerFactory.getLogger(MessageViewer.class);
    
    private final ConnectionFactory connectionFactory;
    
    public MessageViewer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    /**
     * Views messages from a topic without consuming them (peek operation).
     * 
     * @param topicName The name of the topic
     * @param maxRecords Maximum number of records to view
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if viewing messages fails
     */
    public List<ConsumerRecord<String, String>> peekMessages(String topicName, int maxRecords) {
        log.info("Peeking at messages from topic: {} (max: {})", topicName, maxRecords);
        
        String tempGroupId = "peek-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsViewed = 0;
            
            while (recordsViewed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsViewed++;
                    
                    if (recordsViewed >= maxRecords) {
                        break;
                    }
                }
            }
            
            // Don't commit offsets - this is just for viewing
            log.info("Successfully peeked at {} messages from topic: {}", records.size(), topicName);
            return records;
        } catch (Exception e) {
            log.error("Failed to peek at messages from topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to peek at messages from topic: " + topicName, e);
        }
    }
    
    /**
     * Views messages from a specific partition without consuming them.
     * 
     * @param topicName The name of the topic
     * @param partition The partition number
     * @param offset The offset to start from
     * @param maxRecords Maximum number of records to view
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if viewing messages fails
     */
    public List<ConsumerRecord<String, String>> peekMessagesFromPartition(String topicName, int partition, long offset, int maxRecords) {
        log.info("Peeking at messages from topic: {} partition: {} offset: {} (max: {})", topicName, partition, offset, maxRecords);
        
        String tempGroupId = "peek-partition-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            TopicPartition topicPartition = new TopicPartition(topicName, partition);
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.seek(topicPartition, offset);
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsViewed = 0;
            
            while (recordsViewed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsViewed++;
                    
                    if (recordsViewed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully peeked at {} messages from topic: {} partition: {}", records.size(), topicName, partition);
            return records;
        } catch (Exception e) {
            log.error("Failed to peek at messages from topic {} partition {}: {}", topicName, partition, e.getMessage(), e);
            throw new RuntimeException("Failed to peek at messages from topic partition: " + topicName + ":" + partition, e);
        }
    }
    
    /**
     * Views messages from a topic starting from the earliest available offset.
     * 
     * @param topicName The name of the topic
     * @param maxRecords Maximum number of records to view
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if viewing messages fails
     */
    public List<ConsumerRecord<String, String>> peekMessagesFromEarliest(String topicName, int maxRecords) {
        log.info("Peeking at messages from topic: {} from earliest (max: {})", topicName, maxRecords);
        
        String tempGroupId = "peek-earliest-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            consumer.seekToBeginning(consumer.assignment());
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsViewed = 0;
            
            while (recordsViewed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsViewed++;
                    
                    if (recordsViewed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully peeked at {} messages from topic: {} from earliest", records.size(), topicName);
            return records;
        } catch (Exception e) {
            log.error("Failed to peek at messages from topic {} from earliest: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to peek at messages from topic from earliest: " + topicName, e);
        }
    }
    
    /**
     * Views messages from a topic starting from the latest available offset.
     * 
     * @param topicName The name of the topic
     * @param maxRecords Maximum number of records to view
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if viewing messages fails
     */
    public List<ConsumerRecord<String, String>> peekMessagesFromLatest(String topicName, int maxRecords) {
        log.info("Peeking at messages from topic: {} from latest (max: {})", topicName, maxRecords);
        
        String tempGroupId = "peek-latest-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            consumer.seekToEnd(consumer.assignment());
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsViewed = 0;
            
            while (recordsViewed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsViewed++;
                    
                    if (recordsViewed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully peeked at {} messages from topic: {} from latest", records.size(), topicName);
            return records;
        } catch (Exception e) {
            log.error("Failed to peek at messages from topic {} from latest: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to peek at messages from topic from latest: " + topicName, e);
        }
    }
    
    /**
     * Gets the latest offset for a topic partition.
     * 
     * @param topicName The name of the topic
     * @param partition The partition number
     * @return The latest offset
     * @throws RuntimeException if getting offset fails
     */
    public long getLatestOffset(String topicName, int partition) {
        log.info("Getting latest offset for topic: {} partition: {}", topicName, partition);
        
        String tempGroupId = "offset-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            TopicPartition topicPartition = new TopicPartition(topicName, partition);
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.seekToEnd(Collections.singletonList(topicPartition));
            
            long offset = consumer.position(topicPartition);
            log.info("Latest offset for topic: {} partition: {} is: {}", topicName, partition, offset);
            return offset;
        } catch (Exception e) {
            log.error("Failed to get latest offset for topic {} partition {}: {}", topicName, partition, e.getMessage(), e);
            throw new RuntimeException("Failed to get latest offset for topic partition: " + topicName + ":" + partition, e);
        }
    }
    
    /**
     * Gets the earliest offset for a topic partition.
     * 
     * @param topicName The name of the topic
     * @param partition The partition number
     * @return The earliest offset
     * @throws RuntimeException if getting offset fails
     */
    public long getEarliestOffset(String topicName, int partition) {
        log.info("Getting earliest offset for topic: {} partition: {}", topicName, partition);
        
        String tempGroupId = "offset-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            TopicPartition topicPartition = new TopicPartition(topicName, partition);
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.seekToBeginning(Collections.singletonList(topicPartition));
            
            long offset = consumer.position(topicPartition);
            log.info("Earliest offset for topic: {} partition: {} is: {}", topicName, partition, offset);
            return offset;
        } catch (Exception e) {
            log.error("Failed to get earliest offset for topic {} partition {}: {}", topicName, partition, e.getMessage(), e);
            throw new RuntimeException("Failed to get earliest offset for topic partition: " + topicName + ":" + partition, e);
        }
    }
    
    /**
     * Gets message count for a topic partition.
     * 
     * @param topicName The name of the topic
     * @param partition The partition number
     * @return The number of messages in the partition
     * @throws RuntimeException if getting message count fails
     */
    public long getMessageCount(String topicName, int partition) {
        log.info("Getting message count for topic: {} partition: {}", topicName, partition);
        
        try {
            long earliestOffset = getEarliestOffset(topicName, partition);
            long latestOffset = getLatestOffset(topicName, partition);
            long messageCount = latestOffset - earliestOffset;
            
            log.info("Message count for topic: {} partition: {} is: {}", topicName, partition, messageCount);
            return messageCount;
        } catch (Exception e) {
            log.error("Failed to get message count for topic {} partition {}: {}", topicName, partition, e.getMessage(), e);
            throw new RuntimeException("Failed to get message count for topic partition: " + topicName + ":" + partition, e);
        }
    }
    
    /**
     * Gets partition information for a topic.
     * 
     * @param topicName The name of the topic
     * @return Map of partition number to message count
     * @throws RuntimeException if getting partition info fails
     */
    public Map<Integer, Long> getPartitionInfo(String topicName) {
        log.info("Getting partition info for topic: {}", topicName);
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer("partition-info-consumer-" + System.currentTimeMillis())) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            // Wait for assignment
            while (consumer.assignment().isEmpty()) {
                consumer.poll(Duration.ofMillis(100));
            }
            
            Map<Integer, Long> partitionInfo = new HashMap<>();
            for (TopicPartition tp : consumer.assignment()) {
                if (tp.topic().equals(topicName)) {
                    long messageCount = getMessageCount(topicName, tp.partition());
                    partitionInfo.put(tp.partition(), messageCount);
                }
            }
            
            log.info("Successfully got partition info for topic: {} with {} partitions", topicName, partitionInfo.size());
            return partitionInfo;
        } catch (Exception e) {
            log.error("Failed to get partition info for topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to get partition info for topic: " + topicName, e);
        }
    }
    
    /**
     * Views messages with filtering by key pattern.
     * 
     * @param topicName The name of the topic
     * @param keyPattern The key pattern to filter by (supports regex)
     * @param maxRecords Maximum number of records to view
     * @return List of ConsumerRecord objects matching the key pattern
     * @throws RuntimeException if viewing messages fails
     */
    public List<ConsumerRecord<String, String>> peekMessagesByKey(String topicName, String keyPattern, int maxRecords) {
        log.info("Peeking at messages from topic: {} with key pattern: {} (max: {})", topicName, keyPattern, maxRecords);
        
        String tempGroupId = "peek-key-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsViewed = 0;
            
            while (recordsViewed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    if (record.key() != null && record.key().matches(keyPattern)) {
                        records.add(record);
                        recordsViewed++;
                        
                        if (recordsViewed >= maxRecords) {
                            break;
                        }
                    }
                }
            }
            
            log.info("Successfully peeked at {} messages from topic: {} with key pattern: {}", records.size(), topicName, keyPattern);
            return records;
        } catch (Exception e) {
            log.error("Failed to peek at messages from topic {} with key pattern {}: {}", topicName, keyPattern, e.getMessage(), e);
            throw new RuntimeException("Failed to peek at messages from topic with key pattern: " + topicName, e);
        }
    }
    
    /**
     * Views messages with filtering by value pattern.
     * 
     * @param topicName The name of the topic
     * @param valuePattern The value pattern to filter by (supports regex)
     * @param maxRecords Maximum number of records to view
     * @return List of ConsumerRecord objects matching the value pattern
     * @throws RuntimeException if viewing messages fails
     */
    public List<ConsumerRecord<String, String>> peekMessagesByValue(String topicName, String valuePattern, int maxRecords) {
        log.info("Peeking at messages from topic: {} with value pattern: {} (max: {})", topicName, valuePattern, maxRecords);
        
        String tempGroupId = "peek-value-consumer-" + System.currentTimeMillis();
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(tempGroupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsViewed = 0;
            
            while (recordsViewed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    if (record.value() != null && record.value().matches(valuePattern)) {
                        records.add(record);
                        recordsViewed++;
                        
                        if (recordsViewed >= maxRecords) {
                            break;
                        }
                    }
                }
            }
            
            log.info("Successfully peeked at {} messages from topic: {} with value pattern: {}", records.size(), topicName, valuePattern);
            return records;
        } catch (Exception e) {
            log.error("Failed to peek at messages from topic {} with value pattern {}: {}", topicName, valuePattern, e.getMessage(), e);
            throw new RuntimeException("Failed to peek at messages from topic with value pattern: " + topicName, e);
        }
    }
}
