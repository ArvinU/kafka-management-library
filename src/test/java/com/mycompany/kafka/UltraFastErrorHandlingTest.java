package com.mycompany.kafka;

import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-fast test class that tests only the error handling system
 * without any real Kafka connections. All tests run in under 2 seconds.
 */
public class UltraFastErrorHandlingTest {

    @Test
    public void testErrorConstantsStructure() {
        // Test that error constants are properly defined
        assertNotNull(ErrorConstants.KAFKA_CONNECTION_FAILED);
        assertNotNull(ErrorConstants.TOPIC_CREATION_FAILED);
        assertNotNull(ErrorConstants.MESSAGE_SEND_FAILED);
        assertNotNull(ErrorConstants.SCHEMA_REGISTRATION_FAILED);
        
        // Test error code structure
        assertTrue(ErrorConstants.KAFKA_CONNECTION_FAILED.startsWith("KML_CONN_"));
        assertTrue(ErrorConstants.TOPIC_CREATION_FAILED.startsWith("KML_TOPIC_"));
        assertTrue(ErrorConstants.MESSAGE_SEND_FAILED.startsWith("KML_MSG_"));
        assertTrue(ErrorConstants.SCHEMA_REGISTRATION_FAILED.startsWith("KML_SCHEMA_"));
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
    public void testKafkaManagementExceptionCreation() {
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
    public void testValidationErrorMessages() {
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
    public void testAllErrorCategories() {
        // Test that all error categories are properly defined
        assertNotNull(ErrorConstants.KAFKA_CONNECTION_FAILED);
        assertNotNull(ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED);
        assertNotNull(ErrorConstants.CONNECTION_TIMEOUT);
        assertNotNull(ErrorConstants.CONNECTION_REFUSED);
        assertNotNull(ErrorConstants.CONNECTION_TIMEOUT);
        
        assertNotNull(ErrorConstants.TOPIC_CREATION_FAILED);
        assertNotNull(ErrorConstants.TOPIC_DELETION_FAILED);
        assertNotNull(ErrorConstants.TOPIC_NOT_FOUND);
        assertNotNull(ErrorConstants.TOPIC_ALREADY_EXISTS);
        
        assertNotNull(ErrorConstants.MESSAGE_SEND_FAILED);
        assertNotNull(ErrorConstants.MESSAGE_CONSUME_FAILED);
        assertNotNull(ErrorConstants.MESSAGE_SERIALIZATION_FAILED);
        assertNotNull(ErrorConstants.MESSAGE_DESERIALIZATION_FAILED);
        
        assertNotNull(ErrorConstants.SCHEMA_REGISTRATION_FAILED);
        assertNotNull(ErrorConstants.SCHEMA_RETRIEVAL_FAILED);
    }
    
    @Test
    public void testErrorCodeFormatting() {
        // Test that error codes follow the expected format
        String kafkaError = ErrorConstants.KAFKA_CONNECTION_FAILED;
        assertTrue(kafkaError.matches("KML_CONN_\\d+"));
        
        String topicError = ErrorConstants.TOPIC_CREATION_FAILED;
        assertTrue(topicError.matches("KML_TOPIC_\\d+"));
        
        String messageError = ErrorConstants.MESSAGE_SEND_FAILED;
        assertTrue(messageError.matches("KML_MSG_\\d+"));
        
        String schemaError = ErrorConstants.SCHEMA_REGISTRATION_FAILED;
        assertTrue(schemaError.matches("KML_SCHEMA_\\d+"));
    }
}
