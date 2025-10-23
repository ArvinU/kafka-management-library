package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
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
 * Enhanced Session Manager for multiple brokers.
 */
public class MultiSessionManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiSessionManager.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    private final Map<String, Map<String, Producer<String, String>>> transactionalProducers;
    private final Map<String, Map<String, Consumer<String, String>>> transactionalConsumers;
    
    public MultiSessionManager(MultiConnectionFactory multiConnectionFactory) {
        this.multiConnectionFactory = multiConnectionFactory;
        this.transactionalProducers = new HashMap<>();
        this.transactionalConsumers = new HashMap<>();
    }
    
    /**
     * Creates a transactional producer for a specific transaction ID on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The unique transaction ID
     * @return Producer instance configured for transactions
     * @throws RuntimeException if creating transactional producer fails
     */
    public Producer<String, String> createTransactionalProducer(String brokerId, String transactionId) {
        log.info("Creating transactional producer with transaction ID: {} on broker: {}", transactionId, brokerId);
        
        try {
            Properties props = multiConnectionFactory.getMultiBrokerManager().getBrokerConfig(brokerId).toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionId);
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(multiConnectionFactory.getMultiBrokerManager().getBrokerConfig(brokerId).getRetries()));
            props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
            
            Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
            producer.initTransactions();
            
            // Store producer by broker and transaction ID
            transactionalProducers.computeIfAbsent(brokerId, k -> new HashMap<>()).put(transactionId, producer);
            
            log.info("Successfully created transactional producer with transaction ID: {} on broker: {}", transactionId, brokerId);
            return producer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.TRANSACTION_PRODUCER_CREATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TRANSACTION_PRODUCER_CREATION_FAILED_MSG, transactionId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Creates a transactional consumer for a specific transaction ID on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The unique transaction ID
     * @param groupId The consumer group ID
     * @return Consumer instance configured for transactions
     * @throws RuntimeException if creating transactional consumer fails
     */
    public Consumer<String, String> createTransactionalConsumer(String brokerId, String transactionId, String groupId) {
        log.info("Creating transactional consumer with transaction ID: {} and group ID: {} on broker: {}", transactionId, groupId, brokerId);
        
        try {
            Properties props = multiConnectionFactory.getMultiBrokerManager().getBrokerConfig(brokerId).toProperties();
            props.setProperty("group.id", groupId);
            props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty("isolation.level", "read_committed");
            props.setProperty("enable.auto.commit", "false");
            
            Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
            
            // Store consumer by broker and transaction ID
            transactionalConsumers.computeIfAbsent(brokerId, k -> new HashMap<>()).put(transactionId, consumer);
            
            log.info("Successfully created transactional consumer with transaction ID: {} and group ID: {} on broker: {}", transactionId, groupId, brokerId);
            return consumer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.TRANSACTION_CONSUMER_CREATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TRANSACTION_CONSUMER_CREATION_FAILED_MSG, transactionId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Begins a transaction for the specified transaction ID on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @throws RuntimeException if beginning transaction fails
     */
    public void beginTransaction(String brokerId, String transactionId) {
        log.info("Beginning transaction: {} on broker: {}", transactionId, brokerId);
        
        try {
            Producer<String, String> producer = getTransactionalProducer(brokerId, transactionId);
            producer.beginTransaction();
            log.info("Successfully began transaction: {} on broker: {}", transactionId, brokerId);
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.TRANSACTION_BEGIN_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TRANSACTION_BEGIN_FAILED_MSG, transactionId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Commits a transaction for the specified transaction ID on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @throws RuntimeException if committing transaction fails
     */
    public void commitTransaction(String brokerId, String transactionId) {
        log.info("Committing transaction: {} on broker: {}", transactionId, brokerId);
        
        try {
            Producer<String, String> producer = getTransactionalProducer(brokerId, transactionId);
            producer.commitTransaction();
            log.info("Successfully committed transaction: {} on broker: {}", transactionId, brokerId);
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.TRANSACTION_COMMIT_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TRANSACTION_COMMIT_FAILED_MSG, transactionId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Aborts a transaction for the specified transaction ID on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @throws RuntimeException if aborting transaction fails
     */
    public void abortTransaction(String brokerId, String transactionId) {
        log.info("Aborting transaction: {} on broker: {}", transactionId, brokerId);
        
        try {
            Producer<String, String> producer = getTransactionalProducer(brokerId, transactionId);
            producer.abortTransaction();
            log.info("Successfully aborted transaction: {} on broker: {}", transactionId, brokerId);
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.TRANSACTION_ABORT_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TRANSACTION_ABORT_FAILED_MSG, transactionId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Sends a message within a transaction on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws RuntimeException if sending message fails
     */
    public Future<RecordMetadata> sendTransactionalMessage(String brokerId, String transactionId, String topicName, String key, String value) {
        log.info("Sending transactional message to topic: {} with transaction ID: {} on broker: {}", topicName, transactionId, brokerId);
        
        try {
            Producer<String, String> producer = getTransactionalProducer(brokerId, transactionId);
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            Future<RecordMetadata> future = producer.send(record);
            log.info("Successfully sent transactional message to topic: {} with transaction ID: {} on broker: {}", topicName, transactionId, brokerId);
            return future;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.MESSAGE_SEND_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.MESSAGE_SEND_FAILED_MSG, topicName, e.getMessage()),
                e);
        }
    }
    
    /**
     * Sends multiple messages within a transaction on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @param messages List of messages to send
     * @return List of Future objects containing RecordMetadata
     * @throws RuntimeException if sending messages fails
     */
    public List<Future<RecordMetadata>> sendTransactionalMessages(String brokerId, String transactionId, List<ProducerRecord<String, String>> messages) {
        log.info("Sending {} transactional messages with transaction ID: {} on broker: {}", messages.size(), transactionId, brokerId);
        
        try {
            Producer<String, String> producer = getTransactionalProducer(brokerId, transactionId);
            
            List<Future<RecordMetadata>> futures = new ArrayList<>();
            for (ProducerRecord<String, String> record : messages) {
                Future<RecordMetadata> future = producer.send(record);
                futures.add(future);
            }
            
            log.info("Successfully sent {} transactional messages with transaction ID: {} on broker: {}", messages.size(), transactionId, brokerId);
            return futures;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.MESSAGE_SEND_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.MESSAGE_SEND_FAILED_MSG, "multiple topics", e.getMessage()),
                e);
        }
    }
    
    /**
     * Consumes messages within a transaction on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @param topicName The name of the topic
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     * @throws RuntimeException if consuming messages fails
     */
    public List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> consumeTransactionalMessages(String brokerId, String transactionId, String topicName, int maxRecords) {
        log.info("Consuming transactional messages from topic: {} with transaction ID: {} on broker: {}", topicName, transactionId, brokerId);
        
        try {
            Consumer<String, String> consumer = getTransactionalConsumer(brokerId, transactionId);
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
            
            log.info("Successfully consumed {} transactional messages from topic: {} with transaction ID: {} on broker: {}", records.size(), topicName, transactionId, brokerId);
            return records;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.MESSAGE_CONSUME_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.MESSAGE_CONSUME_FAILED_MSG, topicName, e.getMessage()),
                e);
        }
    }
    
    /**
     * Commits offsets for a transactional consumer on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @param offsets Map of TopicPartition to OffsetAndMetadata
     * @throws RuntimeException if committing offsets fails
     */
    public void commitTransactionalOffsets(String brokerId, String transactionId, Map<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets) {
        log.info("Committing transactional offsets for transaction ID: {} on broker: {}", transactionId, brokerId);
        
        try {
            Producer<String, String> producer = getTransactionalProducer(brokerId, transactionId);
            producer.sendOffsetsToTransaction(offsets, 
                new org.apache.kafka.clients.consumer.ConsumerGroupMetadata("transactional-consumer-group"));
            log.info("Successfully committed transactional offsets for transaction ID: {} on broker: {}", transactionId, brokerId);
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, "transactional", e.getMessage()),
                e);
        }
    }
    
    /**
     * Gets the current transaction state for a transaction ID on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @return true if transaction is active, false otherwise
     */
    public boolean isTransactionActive(String brokerId, String transactionId) {
        Map<String, Producer<String, String>> brokerProducers = transactionalProducers.get(brokerId);
        return brokerProducers != null && brokerProducers.containsKey(transactionId);
    }
    
    /**
     * Closes a transactional producer and removes it from the session on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     */
    public void closeTransactionalProducer(String brokerId, String transactionId) {
        log.info("Closing transactional producer for transaction ID: {} on broker: {}", transactionId, brokerId);
        
        Map<String, Producer<String, String>> brokerProducers = transactionalProducers.get(brokerId);
        if (brokerProducers != null) {
            Producer<String, String> producer = brokerProducers.remove(transactionId);
            if (producer != null) {
                producer.close();
                log.info("Successfully closed transactional producer for transaction ID: {} on broker: {}", transactionId, brokerId);
            }
        }
    }
    
    /**
     * Closes a transactional consumer and removes it from the session on a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     */
    public void closeTransactionalConsumer(String brokerId, String transactionId) {
        log.info("Closing transactional consumer for transaction ID: {} on broker: {}", transactionId, brokerId);
        
        Map<String, Consumer<String, String>> brokerConsumers = transactionalConsumers.get(brokerId);
        if (brokerConsumers != null) {
            Consumer<String, String> consumer = brokerConsumers.remove(transactionId);
            if (consumer != null) {
                consumer.close();
                log.info("Successfully closed transactional consumer for transaction ID: {} on broker: {}", transactionId, brokerId);
            }
        }
    }
    
    /**
     * Closes all transactional producers and consumers for a specific broker.
     * 
     * @param brokerId The broker ID
     */
    public void closeAllForBroker(String brokerId) {
        log.info("Closing all transactional producers and consumers for broker: {}", brokerId);
        
        // Close all producers for this broker
        Map<String, Producer<String, String>> brokerProducers = transactionalProducers.remove(brokerId);
        if (brokerProducers != null) {
            for (Map.Entry<String, Producer<String, String>> entry : brokerProducers.entrySet()) {
                try {
                    entry.getValue().close();
                    log.info("Closed transactional producer for transaction ID: {} on broker: {}", entry.getKey(), brokerId);
                } catch (Exception e) {
                    log.error("Error closing transactional producer for transaction ID {} on broker {}: {}", entry.getKey(), brokerId, e.getMessage(), e);
                }
            }
        }
        
        // Close all consumers for this broker
        Map<String, Consumer<String, String>> brokerConsumers = transactionalConsumers.remove(brokerId);
        if (brokerConsumers != null) {
            for (Map.Entry<String, Consumer<String, String>> entry : brokerConsumers.entrySet()) {
                try {
                    entry.getValue().close();
                    log.info("Closed transactional consumer for transaction ID: {} on broker: {}", entry.getKey(), brokerId);
                } catch (Exception e) {
                    log.error("Error closing transactional consumer for transaction ID {} on broker {}: {}", entry.getKey(), brokerId, e.getMessage(), e);
                }
            }
        }
        
        log.info("Successfully closed all transactional producers and consumers for broker: {}", brokerId);
    }
    
    /**
     * Closes all transactional producers and consumers across all brokers.
     */
    public void closeAll() {
        log.info("Closing all transactional producers and consumers across all brokers");
        
        // Close all producers
        for (Map.Entry<String, Map<String, Producer<String, String>>> brokerEntry : transactionalProducers.entrySet()) {
            String brokerId = brokerEntry.getKey();
            for (Map.Entry<String, Producer<String, String>> entry : brokerEntry.getValue().entrySet()) {
                try {
                    entry.getValue().close();
                    log.info("Closed transactional producer for transaction ID: {} on broker: {}", entry.getKey(), brokerId);
                } catch (Exception e) {
                    log.error("Error closing transactional producer for transaction ID {} on broker {}: {}", entry.getKey(), brokerId, e.getMessage(), e);
                }
            }
        }
        transactionalProducers.clear();
        
        // Close all consumers
        for (Map.Entry<String, Map<String, Consumer<String, String>>> brokerEntry : transactionalConsumers.entrySet()) {
            String brokerId = brokerEntry.getKey();
            for (Map.Entry<String, Consumer<String, String>> entry : brokerEntry.getValue().entrySet()) {
                try {
                    entry.getValue().close();
                    log.info("Closed transactional consumer for transaction ID: {} on broker: {}", entry.getKey(), brokerId);
                } catch (Exception e) {
                    log.error("Error closing transactional consumer for transaction ID {} on broker {}: {}", entry.getKey(), brokerId, e.getMessage(), e);
                }
            }
        }
        transactionalConsumers.clear();
        
        log.info("Successfully closed all transactional producers and consumers across all brokers");
    }
    
    /**
     * Gets a transactional producer for a specific broker and transaction ID.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @return Producer instance
     * @throws IllegalStateException if producer not found
     */
    private Producer<String, String> getTransactionalProducer(String brokerId, String transactionId) {
        Map<String, Producer<String, String>> brokerProducers = transactionalProducers.get(brokerId);
        if (brokerProducers == null) {
            throw new IllegalStateException("No transactional producers found for broker: " + brokerId);
        }
        
        Producer<String, String> producer = brokerProducers.get(transactionId);
        if (producer == null) {
            throw new IllegalStateException("No transactional producer found for transaction ID: " + transactionId + " on broker: " + brokerId);
        }
        
        return producer;
    }
    
    /**
     * Gets a transactional consumer for a specific broker and transaction ID.
     * 
     * @param brokerId The broker ID
     * @param transactionId The transaction ID
     * @return Consumer instance
     * @throws IllegalStateException if consumer not found
     */
    private Consumer<String, String> getTransactionalConsumer(String brokerId, String transactionId) {
        Map<String, Consumer<String, String>> brokerConsumers = transactionalConsumers.get(brokerId);
        if (brokerConsumers == null) {
            throw new IllegalStateException("No transactional consumers found for broker: " + brokerId);
        }
        
        Consumer<String, String> consumer = brokerConsumers.get(transactionId);
        if (consumer == null) {
            throw new IllegalStateException("No transactional consumer found for transaction ID: " + transactionId + " on broker: " + brokerId);
        }
        
        return consumer;
    }
    
    /**
     * Creates a transactional producer and consumer pair for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param transactionId The unique transaction ID
     * @param groupId The consumer group ID
     * @return Map containing both producer and consumer
     * @throws RuntimeException if creating transactional components fails
     */
    public Map<String, Object> createTransactionalPair(String brokerId, String transactionId, String groupId) {
        log.info("Creating transactional producer and consumer pair for transaction ID: {} on broker: {}", transactionId, brokerId);
        
        Map<String, Object> pair = new HashMap<>();
        pair.put("producer", createTransactionalProducer(brokerId, transactionId));
        pair.put("consumer", createTransactionalConsumer(brokerId, transactionId, groupId));
        
        log.info("Successfully created transactional pair for transaction ID: {} on broker: {}", transactionId, brokerId);
        return pair;
    }
    
    /**
     * Executes a transaction with automatic cleanup.
     * 
     * @param brokerId The broker ID
     * @param transactionId The unique transaction ID
     * @param groupId The consumer group ID
     * @param transactionLogic The logic to execute within the transaction
     * @throws RuntimeException if transaction execution fails
     */
    public void executeTransaction(String brokerId, String transactionId, String groupId, 
                                 TransactionLogic transactionLogic) {
        log.info("Executing transaction: {} on broker: {}", transactionId, brokerId);
        
        try {
            // Create transactional components
            Producer<String, String> producer = createTransactionalProducer(brokerId, transactionId);
            Consumer<String, String> consumer = createTransactionalConsumer(brokerId, transactionId, groupId);
            
            try {
                // Begin transaction
                beginTransaction(brokerId, transactionId);
                
                // Execute user logic
                transactionLogic.execute(producer, consumer);
                
                // Commit transaction
                commitTransaction(brokerId, transactionId);
                
                log.info("Successfully executed transaction: {} on broker: {}", transactionId, brokerId);
            } catch (Exception e) {
                // Abort transaction on error
                try {
                    abortTransaction(brokerId, transactionId);
                } catch (Exception abortException) {
                    log.error("Failed to abort transaction {} on broker {}: {}", transactionId, brokerId, abortException.getMessage(), abortException);
                }
                throw e;
            } finally {
                // Clean up
                closeTransactionalProducer(brokerId, transactionId);
                closeTransactionalConsumer(brokerId, transactionId);
            }
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.TRANSACTION_BEGIN_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TRANSACTION_BEGIN_FAILED_MSG, transactionId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Functional interface for transaction logic.
     */
    @FunctionalInterface
    public interface TransactionLogic {
        void execute(Producer<String, String> producer, Consumer<String, String> consumer) throws Exception;
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
