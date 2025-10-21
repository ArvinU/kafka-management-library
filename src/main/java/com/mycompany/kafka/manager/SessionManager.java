package com.mycompany.kafka.manager;

import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Manager class for Kafka sessions and transactions.
 * Provides functionality to manage transactional producers and consumers.
 */
public class SessionManager {
    
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    
    private final ConnectionFactory connectionFactory;
    private final Map<String, Producer<String, String>> transactionalProducers;
    private final Map<String, Consumer<String, String>> transactionalConsumers;
    
    public SessionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.transactionalProducers = new HashMap<>();
        this.transactionalConsumers = new HashMap<>();
    }
    
    /**
     * Creates a transactional producer for a specific transaction ID.
     * 
     * @param transactionId The unique transaction ID
     * @return Producer instance configured for transactions
     * @throws RuntimeException if creating transactional producer fails
     */
    public Producer<String, String> createTransactionalProducer(String transactionId) {
        log.info("Creating transactional producer with transaction ID: {}", transactionId);
        
        try {
            Properties props = connectionFactory.getKafkaConfig().toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionId);
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(connectionFactory.getKafkaConfig().getRetries()));
            props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
            
            Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
            producer.initTransactions();
            
            transactionalProducers.put(transactionId, producer);
            log.info("Successfully created transactional producer with transaction ID: {}", transactionId);
            return producer;
        } catch (Exception e) {
            log.error("Failed to create transactional producer with transaction ID {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to create transactional producer: " + transactionId, e);
        }
    }
    
    /**
     * Creates a transactional consumer for a specific transaction ID.
     * 
     * @param transactionId The unique transaction ID
     * @param groupId The consumer group ID
     * @return Consumer instance configured for transactions
     * @throws RuntimeException if creating transactional consumer fails
     */
    public Consumer<String, String> createTransactionalConsumer(String transactionId, String groupId) {
        log.info("Creating transactional consumer with transaction ID: {} and group ID: {}", transactionId, groupId);
        
        try {
            Properties props = connectionFactory.getKafkaConfig().toProperties();
            props.setProperty("group.id", groupId);
            props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty("isolation.level", "read_committed");
            props.setProperty("enable.auto.commit", "false");
            
            Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
            
            transactionalConsumers.put(transactionId, consumer);
            log.info("Successfully created transactional consumer with transaction ID: {} and group ID: {}", transactionId, groupId);
            return consumer;
        } catch (Exception e) {
            log.error("Failed to create transactional consumer with transaction ID {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to create transactional consumer: " + transactionId, e);
        }
    }
    
    /**
     * Begins a transaction for the specified transaction ID.
     * 
     * @param transactionId The transaction ID
     * @throws RuntimeException if beginning transaction fails
     */
    public void beginTransaction(String transactionId) {
        log.info("Beginning transaction: {}", transactionId);
        
        try {
            Producer<String, String> producer = transactionalProducers.get(transactionId);
            if (producer == null) {
                throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId);
            }
            
            producer.beginTransaction();
            log.info("Successfully began transaction: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to begin transaction {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to begin transaction: " + transactionId, e);
        }
    }
    
    /**
     * Commits a transaction for the specified transaction ID.
     * 
     * @param transactionId The transaction ID
     * @throws RuntimeException if committing transaction fails
     */
    public void commitTransaction(String transactionId) {
        log.info("Committing transaction: {}", transactionId);
        
        try {
            Producer<String, String> producer = transactionalProducers.get(transactionId);
            if (producer == null) {
                throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId);
            }
            
            producer.commitTransaction();
            log.info("Successfully committed transaction: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to commit transaction {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to commit transaction: " + transactionId, e);
        }
    }
    
    /**
     * Aborts a transaction for the specified transaction ID.
     * 
     * @param transactionId The transaction ID
     * @throws RuntimeException if aborting transaction fails
     */
    public void abortTransaction(String transactionId) {
        log.info("Aborting transaction: {}", transactionId);
        
        try {
            Producer<String, String> producer = transactionalProducers.get(transactionId);
            if (producer == null) {
                throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId);
            }
            
            producer.abortTransaction();
            log.info("Successfully aborted transaction: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to abort transaction {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to abort transaction: " + transactionId, e);
        }
    }
    
    /**
     * Sends a message within a transaction.
     * 
     * @param transactionId The transaction ID
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws RuntimeException if sending message fails
     */
    public Future<RecordMetadata> sendTransactionalMessage(String transactionId, String topicName, String key, String value) {
        log.info("Sending transactional message to topic: {} with transaction ID: {}", topicName, transactionId);
        
        try {
            Producer<String, String> producer = transactionalProducers.get(transactionId);
            if (producer == null) {
                throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId);
            }
            
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            Future<RecordMetadata> future = producer.send(record);
            log.info("Successfully sent transactional message to topic: {} with transaction ID: {}", topicName, transactionId);
            return future;
        } catch (Exception e) {
            log.error("Failed to send transactional message to topic {} with transaction ID {}: {}", topicName, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to send transactional message", e);
        }
    }
    
    /**
     * Sends multiple messages within a transaction.
     * 
     * @param transactionId The transaction ID
     * @param messages List of messages to send
     * @return List of Future objects containing RecordMetadata
     * @throws RuntimeException if sending messages fails
     */
    public List<Future<RecordMetadata>> sendTransactionalMessages(String transactionId, List<ProducerRecord<String, String>> messages) {
        log.info("Sending {} transactional messages with transaction ID: {}", messages.size(), transactionId);
        
        try {
            Producer<String, String> producer = transactionalProducers.get(transactionId);
            if (producer == null) {
                throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId);
            }
            
            List<Future<RecordMetadata>> futures = new ArrayList<>();
            for (ProducerRecord<String, String> record : messages) {
                Future<RecordMetadata> future = producer.send(record);
                futures.add(future);
            }
            
            log.info("Successfully sent {} transactional messages with transaction ID: {}", messages.size(), transactionId);
            return futures;
        } catch (Exception e) {
            log.error("Failed to send transactional messages with transaction ID {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to send transactional messages", e);
        }
    }
    
    /**
     * Consumes messages within a transaction.
     * 
     * @param transactionId The transaction ID
     * @param topicName The name of the topic
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if consuming messages fails
     */
    public List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> consumeTransactionalMessages(String transactionId, String topicName, int maxRecords) {
        log.info("Consuming transactional messages from topic: {} with transaction ID: {}", topicName, transactionId);
        
        try {
            Consumer<String, String> consumer = transactionalConsumers.get(transactionId);
            if (consumer == null) {
                throw new IllegalStateException("No transactional consumer found for transaction ID: " + transactionId);
            }
            
            consumer.subscribe(Collections.singletonList(topicName));
            
            List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> records = new ArrayList<>();
            int recordsConsumed = 0;
            
            while (recordsConsumed < maxRecords) {
                org.apache.kafka.clients.consumer.ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                
                for (org.apache.kafka.clients.consumer.ConsumerRecord<String, String> record : consumerRecords) {
                    records.add(record);
                    recordsConsumed++;
                    
                    if (recordsConsumed >= maxRecords) {
                        break;
                    }
                }
            }
            
            log.info("Successfully consumed {} transactional messages from topic: {} with transaction ID: {}", records.size(), topicName, transactionId);
            return records;
        } catch (Exception e) {
            log.error("Failed to consume transactional messages from topic {} with transaction ID {}: {}", topicName, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to consume transactional messages", e);
        }
    }
    
    /**
     * Commits offsets for a transactional consumer.
     * 
     * @param transactionId The transaction ID
     * @param offsets Map of TopicPartition to OffsetAndMetadata
     * @throws RuntimeException if committing offsets fails
     */
    public void commitTransactionalOffsets(String transactionId, Map<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets) {
        log.info("Committing transactional offsets for transaction ID: {}", transactionId);
        
        try {
            Producer<String, String> producer = transactionalProducers.get(transactionId);
            if (producer == null) {
                throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId);
            }
            
            producer.sendOffsetsToTransaction(offsets, 
                new org.apache.kafka.clients.consumer.ConsumerGroupMetadata("transactional-consumer-group"));
            log.info("Successfully committed transactional offsets for transaction ID: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to commit transactional offsets for transaction ID {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to commit transactional offsets", e);
        }
    }
    
    /**
     * Gets the current transaction state for a transaction ID.
     * 
     * @param transactionId The transaction ID
     * @return true if transaction is active, false otherwise
     */
    public boolean isTransactionActive(String transactionId) {
        return transactionalProducers.containsKey(transactionId);
    }
    
    /**
     * Closes a transactional producer and removes it from the session.
     * 
     * @param transactionId The transaction ID
     */
    public void closeTransactionalProducer(String transactionId) {
        log.info("Closing transactional producer for transaction ID: {}", transactionId);
        
        Producer<String, String> producer = transactionalProducers.remove(transactionId);
        if (producer != null) {
            producer.close();
            log.info("Successfully closed transactional producer for transaction ID: {}", transactionId);
        }
    }
    
    /**
     * Closes a transactional consumer and removes it from the session.
     * 
     * @param transactionId The transaction ID
     */
    public void closeTransactionalConsumer(String transactionId) {
        log.info("Closing transactional consumer for transaction ID: {}", transactionId);
        
        Consumer<String, String> consumer = transactionalConsumers.remove(transactionId);
        if (consumer != null) {
            consumer.close();
            log.info("Successfully closed transactional consumer for transaction ID: {}", transactionId);
        }
    }
    
    /**
     * Closes all transactional producers and consumers.
     */
    public void closeAll() {
        log.info("Closing all transactional producers and consumers");
        
        // Close all producers
        for (Map.Entry<String, Producer<String, String>> entry : transactionalProducers.entrySet()) {
            try {
                entry.getValue().close();
                log.info("Closed transactional producer for transaction ID: {}", entry.getKey());
            } catch (Exception e) {
                log.error("Error closing transactional producer for transaction ID {}: {}", entry.getKey(), e.getMessage(), e);
            }
        }
        transactionalProducers.clear();
        
        // Close all consumers
        for (Map.Entry<String, Consumer<String, String>> entry : transactionalConsumers.entrySet()) {
            try {
                entry.getValue().close();
                log.info("Closed transactional consumer for transaction ID: {}", entry.getKey());
            } catch (Exception e) {
                log.error("Error closing transactional consumer for transaction ID {}: {}", entry.getKey(), e.getMessage(), e);
            }
        }
        transactionalConsumers.clear();
        
        log.info("Successfully closed all transactional producers and consumers");
    }
}
