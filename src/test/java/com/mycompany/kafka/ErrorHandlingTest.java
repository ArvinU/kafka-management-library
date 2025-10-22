package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to demonstrate and validate error handling in the Kafka Management Library.
 */
public class ErrorHandlingTest {
    
    private KafkaManagementLibrary library;
    
    @BeforeEach
    public void setUp() {
        // This test assumes Kafka and Schema Registry are not running
        // to test connection failure scenarios
    }
    
    @Test
    public void testConnectionFailure() {
        // Test connection failure with invalid endpoints
        assertThrows(KafkaManagementException.class, () -> {
            new KafkaManagementLibrary("invalid:9092", "http://invalid:8081");
        });
    }
    
    @Test
    public void testConnectionFailureWithErrorCode() {
        try {
            new KafkaManagementLibrary("invalid:9092", "http://invalid:8081");
            fail("Expected KafkaManagementException");
        } catch (KafkaManagementException e) {
            assertTrue(e.isConnectionError());
            assertTrue(e.getErrorCode().startsWith(ErrorConstants.CONNECTION_ERROR_PREFIX));
            assertNotNull(e.getDetailedMessage());
        }
    }
    
    @Test
    public void testInvalidTopicName() {
        // This test would require a valid connection, so we'll test the error constants
        String invalidTopicName = "";
        String expectedMessage = ErrorConstants.formatMessage(
            ErrorConstants.VALIDATION_TOPIC_NAME_INVALID_MSG, 
            invalidTopicName
        );
        
        assertTrue(expectedMessage.contains("Invalid topic name"));
        assertTrue(expectedMessage.contains(invalidTopicName));
    }
    
    @Test
    public void testErrorCodeStructure() {
        // Test that error codes follow the expected pattern
        assertTrue(ErrorConstants.KAFKA_CONNECTION_FAILED.startsWith("KML_CONN_"));
        assertTrue(ErrorConstants.TOPIC_CREATION_FAILED.startsWith("KML_TOPIC_"));
        assertTrue(ErrorConstants.MESSAGE_SEND_FAILED.startsWith("KML_MSG_"));
        assertTrue(ErrorConstants.CONSUMER_GROUP_NOT_FOUND.startsWith("KML_CONSUMER_"));
        assertTrue(ErrorConstants.SCHEMA_REGISTRATION_FAILED.startsWith("KML_SCHEMA_"));
        assertTrue(ErrorConstants.TRANSACTION_BEGIN_FAILED.startsWith("KML_SESSION_"));
        assertTrue(ErrorConstants.CONFIG_LOAD_FAILED.startsWith("KML_CONFIG_"));
        assertTrue(ErrorConstants.VALIDATION_TOPIC_NAME_INVALID.startsWith("KML_VAL_"));
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
    public void testErrorCodeRetrieval() {
        // Test that error codes can be retrieved by their constants
        String kafkaConnectionError = ErrorConstants.getErrorMessage(ErrorConstants.KAFKA_CONNECTION_FAILED);
        assertTrue(kafkaConnectionError.contains("Failed to connect to Kafka broker"));
        
        String topicCreationError = ErrorConstants.getErrorMessage(ErrorConstants.TOPIC_CREATION_FAILED);
        assertTrue(topicCreationError.contains("Failed to create topic"));
        
        String messageSendError = ErrorConstants.getErrorMessage(ErrorConstants.MESSAGE_SEND_FAILED);
        assertTrue(messageSendError.contains("Failed to send message"));
    }
    
    @Test
    public void testExceptionErrorCodeRetrieval() {
        try {
            throw new KafkaManagementException(
                ErrorConstants.TOPIC_CREATION_FAILED,
                "Test error message"
            );
        } catch (KafkaManagementException e) {
            assertEquals(ErrorConstants.TOPIC_CREATION_FAILED, e.getErrorCode());
            assertEquals("Test error message", e.getDetailedMessage());
            assertTrue(e.isTopicError());
            assertFalse(e.isConnectionError());
            assertFalse(e.isMessageError());
        }
    }
    
    @Test
    public void testExceptionErrorCategoryDetection() {
        // Test connection error detection
        KafkaManagementException connectionError = new KafkaManagementException(
            ErrorConstants.KAFKA_CONNECTION_FAILED,
            "Connection failed"
        );
        assertTrue(connectionError.isConnectionError());
        assertFalse(connectionError.isTopicError());
        
        // Test topic error detection
        KafkaManagementException topicError = new KafkaManagementException(
            ErrorConstants.TOPIC_CREATION_FAILED,
            "Topic creation failed"
        );
        assertTrue(topicError.isTopicError());
        assertFalse(topicError.isConnectionError());
        
        // Test message error detection
        KafkaManagementException messageError = new KafkaManagementException(
            ErrorConstants.MESSAGE_SEND_FAILED,
            "Message send failed"
        );
        assertTrue(messageError.isMessageError());
        assertFalse(messageError.isTopicError());
        
        // Test consumer error detection
        KafkaManagementException consumerError = new KafkaManagementException(
            ErrorConstants.CONSUMER_GROUP_NOT_FOUND,
            "Consumer group not found"
        );
        assertTrue(consumerError.isConsumerError());
        assertFalse(consumerError.isMessageError());
        
        // Test schema error detection
        KafkaManagementException schemaError = new KafkaManagementException(
            ErrorConstants.SCHEMA_REGISTRATION_FAILED,
            "Schema registration failed"
        );
        assertTrue(schemaError.isSchemaError());
        assertFalse(schemaError.isConsumerError());
        
        // Test session error detection
        KafkaManagementException sessionError = new KafkaManagementException(
            ErrorConstants.TRANSACTION_BEGIN_FAILED,
            "Transaction begin failed"
        );
        assertTrue(sessionError.isSessionError());
        assertFalse(sessionError.isSchemaError());
        
        // Test config error detection
        KafkaManagementException configError = new KafkaManagementException(
            ErrorConstants.CONFIG_LOAD_FAILED,
            "Config load failed"
        );
        assertTrue(configError.isConfigError());
        assertFalse(configError.isSessionError());
        
        // Test validation error detection
        KafkaManagementException validationError = new KafkaManagementException(
            ErrorConstants.VALIDATION_TOPIC_NAME_INVALID,
            "Invalid topic name"
        );
        assertTrue(validationError.isValidationError());
        assertFalse(validationError.isConfigError());
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
    public void testExceptionWithErrorCodeOnly() {
        KafkaManagementException exception = new KafkaManagementException(
            ErrorConstants.TOPIC_NOT_FOUND,
            new RuntimeException("Topic not found")
        );
        
        assertEquals(ErrorConstants.TOPIC_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getDetailedMessage().contains("Topic"));
        assertNotNull(exception.getCause());
    }
}
