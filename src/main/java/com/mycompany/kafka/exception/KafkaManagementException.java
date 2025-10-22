package com.mycompany.kafka.exception;

import com.mycompany.kafka.constants.ErrorConstants;

/**
 * Custom exception class for Kafka Management Library.
 * Provides standardized error handling with error codes and detailed messages.
 */
public class KafkaManagementException extends RuntimeException {
    
    private final String errorCode;
    private final String detailedMessage;
    
    /**
     * Creates a new KafkaManagementException with error code and message.
     * 
     * @param errorCode The standardized error code
     * @param message The error message
     */
    public KafkaManagementException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.detailedMessage = message;
    }
    
    /**
     * Creates a new KafkaManagementException with error code, message, and cause.
     * 
     * @param errorCode The standardized error code
     * @param message The error message
     * @param cause The underlying cause
     */
    public KafkaManagementException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.detailedMessage = message;
    }
    
    /**
     * Creates a new KafkaManagementException with error code and cause.
     * 
     * @param errorCode The standardized error code
     * @param cause The underlying cause
     */
    public KafkaManagementException(String errorCode, Throwable cause) {
        super(ErrorConstants.getErrorMessage(errorCode), cause);
        this.errorCode = errorCode;
        this.detailedMessage = ErrorConstants.getErrorMessage(errorCode);
    }
    
    /**
     * Gets the error code.
     * 
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the detailed error message.
     * 
     * @return The detailed error message
     */
    public String getDetailedMessage() {
        return detailedMessage;
    }
    
    /**
     * Checks if this is a connection error.
     * 
     * @return true if this is a connection error
     */
    public boolean isConnectionError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.CONNECTION_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a topic error.
     * 
     * @return true if this is a topic error
     */
    public boolean isTopicError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.TOPIC_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a message error.
     * 
     * @return true if this is a message error
     */
    public boolean isMessageError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.MESSAGE_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a consumer error.
     * 
     * @return true if this is a consumer error
     */
    public boolean isConsumerError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.CONSUMER_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a schema error.
     * 
     * @return true if this is a schema error
     */
    public boolean isSchemaError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.SCHEMA_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a session error.
     * 
     * @return true if this is a session error
     */
    public boolean isSessionError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.SESSION_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a configuration error.
     * 
     * @return true if this is a configuration error
     */
    public boolean isConfigError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.CONFIG_ERROR_PREFIX);
    }
    
    /**
     * Checks if this is a validation error.
     * 
     * @return true if this is a validation error
     */
    public boolean isValidationError() {
        return errorCode != null && errorCode.startsWith(ErrorConstants.VALIDATION_ERROR_PREFIX);
    }
    
    @Override
    public String toString() {
        return String.format("KafkaManagementException[errorCode=%s, message=%s]", 
                           errorCode, getMessage());
    }
}
