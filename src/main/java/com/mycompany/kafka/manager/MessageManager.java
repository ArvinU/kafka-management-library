package com.mycompany.kafka.manager;

import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Manager class for Kafka message operations.
 * Provides functionality to produce and consume messages with various serialization formats.
 */
public class MessageManager {
    
    private static final Logger log = LoggerFactory.getLogger(MessageManager.class);
    
    private final ConnectionFactory connectionFactory;
    
    public MessageManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    /**
     * Sends a message to a Kafka topic using String serialization.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws RuntimeException if message sending fails
     */
    public Future<RecordMetadata> sendMessage(String topicName, String key, String value) {
        log.info("Sending message to topic: {} with key: {}", topicName, key);
        
        try (Producer<String, String> producer = connectionFactory.createProducer()) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            Future<RecordMetadata> future = producer.send(record);
            log.info("Message sent successfully to topic: {}", topicName);
            return future;
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to send message to topic: " + topicName, e);
        }
    }
    
    /**
     * Sends a message to a Kafka topic without a key.
     * 
     * @param topicName The name of the topic
     * @param value The message value
     * @return Future containing RecordMetadata
     */
    public Future<RecordMetadata> sendMessage(String topicName, String value) {
        return sendMessage(topicName, null, value);
    }
    
    /**
     * Sends a message to a Kafka topic using Avro serialization.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value (Avro object)
     * @return Future containing RecordMetadata
     * @throws RuntimeException if message sending fails
     */
    public Future<RecordMetadata> sendAvroMessage(String topicName, String key, Object value) {
        log.info("Sending Avro message to topic: {} with key: {}", topicName, key);
        
        try (Producer<String, Object> producer = connectionFactory.createAvroProducer()) {
            ProducerRecord<String, Object> record = new ProducerRecord<>(topicName, key, value);
            Future<RecordMetadata> future = producer.send(record);
            log.info("Avro message sent successfully to topic: {}", topicName);
            return future;
        } catch (Exception e) {
            log.error("Failed to send Avro message to topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to send Avro message to topic: " + topicName, e);
        }
    }
    
    /**
     * Sends a message to a Kafka topic using JSON Schema serialization.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value (JSON Schema object)
     * @return Future containing RecordMetadata
     * @throws RuntimeException if message sending fails
     */
    public Future<RecordMetadata> sendJsonSchemaMessage(String topicName, String key, Object value) {
        log.info("Sending JSON Schema message to topic: {} with key: {}", topicName, key);
        
        try (Producer<String, Object> producer = connectionFactory.createJsonSchemaProducer()) {
            ProducerRecord<String, Object> record = new ProducerRecord<>(topicName, key, value);
            Future<RecordMetadata> future = producer.send(record);
            log.info("JSON Schema message sent successfully to topic: {}", topicName);
            return future;
        } catch (Exception e) {
            log.error("Failed to send JSON Schema message to topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to send JSON Schema message to topic: " + topicName, e);
        }
    }
    
    /**
     * Consumes messages from a Kafka topic using String deserialization.
     * 
     * @param topicName The name of the topic
     * @param groupId The consumer group ID
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if message consumption fails
     */
    public List<ConsumerRecord<String, String>> consumeMessages(String topicName, String groupId, int maxRecords) {
        log.info("Consuming messages from topic: {} with group ID: {}", topicName, groupId);
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(groupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsConsumed = 0;
            
            while (recordsConsumed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsConsumed++;
                    
                    if (recordsConsumed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully consumed {} messages from topic: {}", records.size(), topicName);
            return records;
        } catch (Exception e) {
            log.error("Failed to consume messages from topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to consume messages from topic: " + topicName, e);
        }
    }
    
    /**
     * Consumes messages from a Kafka topic using Avro deserialization.
     * 
     * @param topicName The name of the topic
     * @param groupId The consumer group ID
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if message consumption fails
     */
    public List<ConsumerRecord<String, Object>> consumeAvroMessages(String topicName, String groupId, int maxRecords) {
        log.info("Consuming Avro messages from topic: {} with group ID: {}", topicName, groupId);
        
        try (Consumer<String, Object> consumer = connectionFactory.createAvroConsumer(groupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<ConsumerRecord<String, Object>> records = new ArrayList<>();
            int recordsConsumed = 0;
            
            while (recordsConsumed < maxRecords) {
                ConsumerRecords<String, Object> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Object> record : consumerRecords) {
                    records.add(record);
                    recordsConsumed++;
                    
                    if (recordsConsumed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully consumed {} Avro messages from topic: {}", records.size(), topicName);
            return records;
        } catch (Exception e) {
            log.error("Failed to consume Avro messages from topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to consume Avro messages from topic: " + topicName, e);
        }
    }
    
    /**
     * Consumes messages from a Kafka topic using JSON Schema deserialization.
     * 
     * @param topicName The name of the topic
     * @param groupId The consumer group ID
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if message consumption fails
     */
    public List<ConsumerRecord<String, Object>> consumeJsonSchemaMessages(String topicName, String groupId, int maxRecords) {
        log.info("Consuming JSON Schema messages from topic: {} with group ID: {}", topicName, groupId);
        
        try (Consumer<String, Object> consumer = connectionFactory.createJsonSchemaConsumer(groupId)) {
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<ConsumerRecord<String, Object>> records = new ArrayList<>();
            int recordsConsumed = 0;
            
            while (recordsConsumed < maxRecords) {
                ConsumerRecords<String, Object> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Object> record : consumerRecords) {
                    records.add(record);
                    recordsConsumed++;
                    
                    if (recordsConsumed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully consumed {} JSON Schema messages from topic: {}", records.size(), topicName);
            return records;
        } catch (Exception e) {
            log.error("Failed to consume JSON Schema messages from topic {}: {}", topicName, e.getMessage(), e);
            throw new RuntimeException("Failed to consume JSON Schema messages from topic: " + topicName, e);
        }
    }
    
    /**
     * Consumes messages from a specific partition of a topic.
     * 
     * @param topicName The name of the topic
     * @param partition The partition number
     * @param offset The offset to start from
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if message consumption fails
     */
    public List<ConsumerRecord<String, String>> consumeMessagesFromPartition(String topicName, int partition, long offset, int maxRecords) {
        log.info("Consuming messages from topic: {} partition: {} offset: {}", topicName, partition, offset);
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer("partition-consumer-" + System.currentTimeMillis())) {
            TopicPartition topicPartition = new TopicPartition(topicName, partition);
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.seek(topicPartition, offset);
            
            List<ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsConsumed = 0;
            
            while (recordsConsumed < maxRecords) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsConsumed++;
                    
                    if (recordsConsumed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully consumed {} messages from topic: {} partition: {}", records.size(), topicName, partition);
            return records;
        } catch (Exception e) {
            log.error("Failed to consume messages from topic {} partition {}: {}", topicName, partition, e.getMessage(), e);
            throw new RuntimeException("Failed to consume messages from topic partition: " + topicName + ":" + partition, e);
        }
    }
    
    /**
     * Commits the current offset for a consumer group.
     * 
     * @param groupId The consumer group ID
     * @param topicName The name of the topic
     * @param partition The partition number
     * @param offset The offset to commit
     * @throws RuntimeException if committing offset fails
     */
    public void commitOffset(String groupId, String topicName, int partition, long offset) {
        log.info("Committing offset for group: {} topic: {} partition: {} offset: {}", groupId, topicName, partition, offset);
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer(groupId)) {
            TopicPartition topicPartition = new TopicPartition(topicName, partition);
            OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(offset);
            
            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            offsets.put(topicPartition, offsetAndMetadata);
            
            consumer.commitSync(offsets);
            log.info("Successfully committed offset for group: {} topic: {} partition: {} offset: {}", groupId, topicName, partition, offset);
        } catch (Exception e) {
            log.error("Failed to commit offset for group: {} topic: {} partition: {}: {}", groupId, topicName, partition, e.getMessage(), e);
            throw new RuntimeException("Failed to commit offset", e);
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
        
        try (Consumer<String, String> consumer = connectionFactory.createConsumer("offset-consumer-" + System.currentTimeMillis())) {
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
}
