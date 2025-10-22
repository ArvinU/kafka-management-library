package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-fast test class that tests error handling without any real connections.
 * All tests run in under 2 seconds.
 */
public class TestKafkaConfig {

    @Test
    public void testErrorConstants() {
        assertNotNull(ErrorConstants.KAFKA_CONNECTION_FAILED);
        assertNotNull(ErrorConstants.TOPIC_CREATION_FAILED);
        assertNotNull(ErrorConstants.MESSAGE_SEND_FAILED);
        assertNotNull(ErrorConstants.SCHEMA_REGISTRATION_FAILED);
    }
    
    @Test
    public void testErrorMessageFormatting() {
        String topicName = "test-topic";
        String errorMessage = ErrorConstants.formatMessage(
            ErrorConstants.TOPIC_CREATION_FAILED_MSG, 
            topicName, 
            "Topic already exists"
        );
        
        assertTrue(errorMessage.contains("Failed to create topic"));
        assertTrue(errorMessage.contains(topicName));
        assertTrue(errorMessage.contains("Topic already exists"));
    }
    
    @Test
    public void testKafkaManagementException() {
        KafkaManagementException exception = new KafkaManagementException(
            ErrorConstants.TOPIC_CREATION_FAILED,
            "Test error message"
        );
        
        assertEquals(ErrorConstants.TOPIC_CREATION_FAILED, exception.getErrorCode());
        assertEquals("Test error message", exception.getDetailedMessage());
        assertTrue(exception.isTopicError());
        assertFalse(exception.isConnectionError());
    }
    
    @Test
    public void testExceptionErrorCategories() {
        // Test connection error
        KafkaManagementException connectionError = new KafkaManagementException(
            ErrorConstants.KAFKA_CONNECTION_FAILED,
            "Connection failed"
        );
        assertTrue(connectionError.isConnectionError());
        assertFalse(connectionError.isTopicError());
        
        // Test topic error
        KafkaManagementException topicError = new KafkaManagementException(
            ErrorConstants.TOPIC_CREATION_FAILED,
            "Topic creation failed"
        );
        assertTrue(topicError.isTopicError());
        assertFalse(topicError.isConnectionError());
        
        // Test message error
        KafkaManagementException messageError = new KafkaManagementException(
            ErrorConstants.MESSAGE_SEND_FAILED,
            "Message send failed"
        );
        assertTrue(messageError.isMessageError());
        assertFalse(messageError.isTopicError());
        
        // Test schema error
        KafkaManagementException schemaError = new KafkaManagementException(
            ErrorConstants.SCHEMA_REGISTRATION_FAILED,
            "Schema registration failed"
        );
        assertTrue(schemaError.isSchemaError());
        assertFalse(schemaError.isMessageError());
    }
    
    @Test
    public void testExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        KafkaManagementException exception = new KafkaManagementException(
            ErrorConstants.KAFKA_CONNECTION_FAILED,
            "Connection failed",
            cause
        );
        
        assertEquals(ErrorConstants.KAFKA_CONNECTION_FAILED, exception.getErrorCode());
        assertEquals("Connection failed", exception.getDetailedMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    public void testExceptionToString() {
        KafkaManagementException exception = new KafkaManagementException(
            ErrorConstants.TOPIC_CREATION_FAILED,
            "Test error message"
        );
        
        String toString = exception.toString();
        assertTrue(toString.contains("KafkaManagementException"));
        assertTrue(toString.contains(ErrorConstants.TOPIC_CREATION_FAILED));
        assertTrue(toString.contains("Test error message"));
    }
    
    @Test
    public void testErrorCodeRetrieval() {
        String kafkaConnectionError = ErrorConstants.getErrorMessage(ErrorConstants.KAFKA_CONNECTION_FAILED);
        assertTrue(kafkaConnectionError.contains("Failed to connect to Kafka broker"));
        
        String topicCreationError = ErrorConstants.getErrorMessage(ErrorConstants.TOPIC_CREATION_FAILED);
        assertTrue(topicCreationError.contains("Failed to create topic"));
        
        String messageSendError = ErrorConstants.getErrorMessage(ErrorConstants.MESSAGE_SEND_FAILED);
        assertTrue(messageSendError.contains("Failed to send message"));
    }
    
    @Test
    public void testInvalidTopicNameValidation() {
        String invalidTopicName = "";
        String expectedMessage = ErrorConstants.formatMessage(
            ErrorConstants.VALIDATION_TOPIC_NAME_INVALID_MSG, 
            invalidTopicName
        );
        
        assertTrue(expectedMessage.contains("Invalid topic name"));
        assertTrue(expectedMessage.contains(invalidTopicName));
    }
    
    @Test
    public void testSuccessMessages() {
        String connectionSuccess = ErrorConstants.formatMessage(
            ErrorConstants.CONNECTION_SUCCESS_MSG, 
            "Kafka broker at localhost:9092"
        );
        assertTrue(connectionSuccess.contains("Successfully connected to"));
        
        String topicCreatedSuccess = ErrorConstants.formatMessage(
            ErrorConstants.TOPIC_CREATED_SUCCESS_MSG,
            "my-topic",
            3,
            1
        );
        assertTrue(topicCreatedSuccess.contains("Topic 'my-topic' created successfully"));
    }
    
    @Test
    public void testConfigCreation() {
        // Test that config objects can be created quickly
        KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
        assertNotNull(kafkaConfig);
        assertEquals("localhost:9092", kafkaConfig.getBootstrapServers());
        
        SchemaRegistryConfig schemaConfig = new SchemaRegistryConfig("http://localhost:8081");
        assertNotNull(schemaConfig);
        assertEquals("http://localhost:8081", schemaConfig.getSchemaRegistryUrl());
    }
    
    @Test
    public void testConfigProperties() {
        KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");
        java.util.Properties props = kafkaConfig.toProperties();
        assertNotNull(props);
        assertTrue(props.size() > 0);
    }
}
