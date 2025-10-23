package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Enhanced Message Manager that works with named brokers.
 * Provides message production operations for specific brokers.
 */
public class MultiMessageManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiMessageManager.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    
    public MultiMessageManager(MultiConnectionFactory multiConnectionFactory) {
        this.multiConnectionFactory = multiConnectionFactory;
    }
    
    /**
     * Sends a message to a specific topic on a specific broker.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws KafkaManagementException if message sending fails
     */
    public Future<RecordMetadata> sendMessage(String brokerName, String topicName, String key, String value) throws KafkaManagementException {
        log.info("Sending message to topic '{}' on broker '{}' with key '{}'", topicName, brokerName, key);
        
        try {
            Producer<String, String> producer = multiConnectionFactory.createProducer(brokerName);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            Future<RecordMetadata> future = producer.send(record);
            
            log.info("Message sent successfully to topic '{}' on broker '{}'", topicName, brokerName);
            return future;
            
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to send message to topic '" + topicName + "' on broker '" + brokerName + "'",
                e);
        }
    }
    
    /**
     * Sends a message to a specific topic on a specific broker without a key.
     * 
     * @param brokerName The name of the broker to use
     * @param topicName The name of the topic
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws KafkaManagementException if message sending fails
     */
    public Future<RecordMetadata> sendMessageWithoutKey(String brokerName, String topicName, String value) throws KafkaManagementException {
        return sendMessage(brokerName, topicName, null, value);
    }
    
    /**
     * Sends a message to a specific topic on the first connected broker.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws KafkaManagementException if message sending fails
     */
    public Future<RecordMetadata> sendMessage(String topicName, String key, String value) throws KafkaManagementException {
        String brokerName = multiConnectionFactory.getMultiBrokerManager().getFirstConnectedBroker();
        if (brokerName == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "No connected brokers available");
        }
        
        return sendMessage(brokerName, topicName, key, value);
    }
    
    /**
     * Sends a message to a specific topic on the first connected broker without a key.
     * 
     * @param topicName The name of the topic
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws KafkaManagementException if message sending fails
     */
    public Future<RecordMetadata> sendMessageWithoutKey(String topicName, String value) throws KafkaManagementException {
        return sendMessage(topicName, null, value);
    }
}
