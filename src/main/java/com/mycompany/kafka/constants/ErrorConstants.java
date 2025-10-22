package com.mycompany.kafka.constants;

/**
 * Standardized error constants for the Kafka Management Library.
 * All error messages and codes are centralized here for consistency.
 */
public class ErrorConstants {
    
    // Error Code Prefixes
    public static final String CONNECTION_ERROR_PREFIX = "KML_CONN_";
    public static final String TOPIC_ERROR_PREFIX = "KML_TOPIC_";
    public static final String MESSAGE_ERROR_PREFIX = "KML_MSG_";
    public static final String CONSUMER_ERROR_PREFIX = "KML_CONSUMER_";
    public static final String SCHEMA_ERROR_PREFIX = "KML_SCHEMA_";
    public static final String SESSION_ERROR_PREFIX = "KML_SESSION_";
    public static final String CONFIG_ERROR_PREFIX = "KML_CONFIG_";
    public static final String VALIDATION_ERROR_PREFIX = "KML_VAL_";
    
    // Connection Error Codes (1000-1999)
    public static final String KAFKA_CONNECTION_FAILED = "KML_CONN_1001";
    public static final String SCHEMA_REGISTRY_CONNECTION_FAILED = "KML_CONN_1002";
    public static final String CONNECTION_TIMEOUT = "KML_CONN_1003";
    public static final String CONNECTION_REFUSED = "KML_CONN_1004";
    public static final String SSL_HANDSHAKE_FAILED = "KML_CONN_1005";
    public static final String AUTHENTICATION_FAILED = "KML_CONN_1006";
    public static final String AUTHORIZATION_FAILED = "KML_CONN_1007";
    public static final String NETWORK_UNREACHABLE = "KML_CONN_1008";
    public static final String CONNECTION_LOST = "KML_CONN_1009";
    public static final String CONNECTION_ALREADY_CLOSED = "KML_CONN_1010";
    
    // Topic Error Codes (2000-2999)
    public static final String TOPIC_CREATION_FAILED = "KML_TOPIC_2001";
    public static final String TOPIC_DELETION_FAILED = "KML_TOPIC_2002";
    public static final String TOPIC_NOT_FOUND = "KML_TOPIC_2003";
    public static final String TOPIC_ALREADY_EXISTS = "KML_TOPIC_2004";
    public static final String TOPIC_LIST_FAILED = "KML_TOPIC_2005";
    public static final String TOPIC_DESCRIBE_FAILED = "KML_TOPIC_2006";
    public static final String TOPIC_CONFIG_INVALID = "KML_TOPIC_2007";
    public static final String PARTITION_COUNT_INVALID = "KML_TOPIC_2008";
    public static final String REPLICATION_FACTOR_INVALID = "KML_TOPIC_2009";
    public static final String TOPIC_OPERATION_TIMEOUT = "KML_TOPIC_2010";
    
    // Message Error Codes (3000-3999)
    public static final String MESSAGE_SEND_FAILED = "KML_MSG_3001";
    public static final String MESSAGE_CONSUME_FAILED = "KML_MSG_3002";
    public static final String MESSAGE_SERIALIZATION_FAILED = "KML_MSG_3003";
    public static final String MESSAGE_DESERIALIZATION_FAILED = "KML_MSG_3004";
    public static final String MESSAGE_VIEW_FAILED = "KML_MSG_3005";
    public static final String MESSAGE_OFFSET_INVALID = "KML_MSG_3006";
    public static final String MESSAGE_PARTITION_INVALID = "KML_MSG_3007";
    public static final String MESSAGE_TIMEOUT = "KML_MSG_3008";
    public static final String MESSAGE_SIZE_EXCEEDED = "KML_MSG_3009";
    public static final String MESSAGE_PRODUCER_CLOSED = "KML_MSG_3010";
    
    // Consumer Error Codes (4000-4999)
    public static final String CONSUMER_GROUP_NOT_FOUND = "KML_CONSUMER_4001";
    public static final String CONSUMER_GROUP_LIST_FAILED = "KML_CONSUMER_4002";
    public static final String CONSUMER_GROUP_DELETE_FAILED = "KML_CONSUMER_4003";
    public static final String CONSUMER_GROUP_DESCRIBE_FAILED = "KML_CONSUMER_4004";
    public static final String CONSUMER_OFFSET_RESET_FAILED = "KML_CONSUMER_4005";
    public static final String CONSUMER_LAG_CALCULATION_FAILED = "KML_CONSUMER_4006";
    public static final String CONSUMER_HEALTH_CHECK_FAILED = "KML_CONSUMER_4007";
    public static final String CONSUMER_GROUP_ID_INVALID = "KML_CONSUMER_4008";
    public static final String CONSUMER_SUBSCRIPTION_FAILED = "KML_CONSUMER_4009";
    public static final String CONSUMER_POLL_TIMEOUT = "KML_CONSUMER_4010";
    
    // Schema Registry Error Codes (5000-5999)
    public static final String SCHEMA_REGISTRATION_FAILED = "KML_SCHEMA_5001";
    public static final String SCHEMA_RETRIEVAL_FAILED = "KML_SCHEMA_5002";
    public static final String SCHEMA_NOT_FOUND = "KML_SCHEMA_5003";
    public static final String SCHEMA_INVALID = "KML_SCHEMA_5004";
    public static final String SCHEMA_VERSION_NOT_FOUND = "KML_SCHEMA_5005";
    public static final String SCHEMA_SUBJECT_NOT_FOUND = "KML_SCHEMA_5006";
    public static final String SCHEMA_SUBJECT_LIST_FAILED = "KML_SCHEMA_5007";
    public static final String SCHEMA_COMPATIBILITY_CHECK_FAILED = "KML_SCHEMA_5008";
    public static final String SCHEMA_CACHE_ERROR = "KML_SCHEMA_5009";
    public static final String SCHEMA_REGISTRY_UNAVAILABLE = "KML_SCHEMA_5010";
    
    // Session/Transaction Error Codes (6000-6999)
    public static final String TRANSACTION_BEGIN_FAILED = "KML_SESSION_6001";
    public static final String TRANSACTION_COMMIT_FAILED = "KML_SESSION_6002";
    public static final String TRANSACTION_ABORT_FAILED = "KML_SESSION_6003";
    public static final String TRANSACTION_ID_INVALID = "KML_SESSION_6004";
    public static final String TRANSACTION_TIMEOUT = "KML_SESSION_6005";
    public static final String TRANSACTION_PRODUCER_CREATION_FAILED = "KML_SESSION_6006";
    public static final String TRANSACTION_CONSUMER_CREATION_FAILED = "KML_SESSION_6007";
    public static final String SESSION_MONITORING_FAILED = "KML_SESSION_6008";
    public static final String SESSION_SUMMARY_FAILED = "KML_SESSION_6009";
    public static final String SESSION_HEALTH_CHECK_FAILED = "KML_SESSION_6010";
    
    // Configuration Error Codes (7000-7999)
    public static final String CONFIG_LOAD_FAILED = "KML_CONFIG_7001";
    public static final String CONFIG_INVALID = "KML_CONFIG_7002";
    public static final String CONFIG_MISSING_REQUIRED = "KML_CONFIG_7003";
    public static final String CONFIG_SSL_INVALID = "KML_CONFIG_7004";
    public static final String CONFIG_SERIALIZER_INVALID = "KML_CONFIG_7005";
    public static final String CONFIG_DESERIALIZER_INVALID = "KML_CONFIG_7006";
    public static final String CONFIG_BOOTSTRAP_SERVERS_INVALID = "KML_CONFIG_7007";
    public static final String CONFIG_SCHEMA_REGISTRY_URL_INVALID = "KML_CONFIG_7008";
    public static final String CONFIG_FILE_NOT_FOUND = "KML_CONFIG_7009";
    public static final String CONFIG_JSON_PARSE_ERROR = "KML_CONFIG_7010";
    
    // Validation Error Codes (8000-8999)
    public static final String VALIDATION_TOPIC_NAME_INVALID = "KML_VAL_8001";
    public static final String VALIDATION_GROUP_ID_INVALID = "KML_VAL_8002";
    public static final String VALIDATION_SUBJECT_INVALID = "KML_VAL_8003";
    public static final String VALIDATION_PARTITION_COUNT_INVALID = "KML_VAL_8004";
    public static final String VALIDATION_REPLICATION_FACTOR_INVALID = "KML_VAL_8005";
    public static final String VALIDATION_MESSAGE_NULL = "KML_VAL_8006";
    public static final String VALIDATION_SCHEMA_NULL = "KML_VAL_8007";
    public static final String VALIDATION_TRANSACTION_ID_INVALID = "KML_VAL_8008";
    public static final String VALIDATION_MAX_RECORDS_INVALID = "KML_VAL_8009";
    public static final String VALIDATION_OFFSET_INVALID = "KML_VAL_8010";
    
    // Error Messages
    public static final String KAFKA_CONNECTION_FAILED_MSG = "Failed to connect to Kafka broker at %s. Please check if Kafka is running and accessible.";
    public static final String SCHEMA_REGISTRY_CONNECTION_FAILED_MSG = "Failed to connect to Schema Registry at %s. Please check if Schema Registry is running and accessible.";
    public static final String CONNECTION_TIMEOUT_MSG = "Connection timeout after %d seconds. Please check network connectivity and server availability.";
    public static final String CONNECTION_REFUSED_MSG = "Connection refused to %s. Please verify the server is running and the address is correct.";
    public static final String SSL_HANDSHAKE_FAILED_MSG = "SSL handshake failed. Please check SSL configuration and certificates.";
    public static final String AUTHENTICATION_FAILED_MSG = "Authentication failed. Please check credentials and authentication configuration.";
    public static final String AUTHORIZATION_FAILED_MSG = "Authorization failed. Please check permissions and access control configuration.";
    public static final String NETWORK_UNREACHABLE_MSG = "Network unreachable. Please check network connectivity to %s.";
    public static final String CONNECTION_LOST_MSG = "Connection lost to %s. Attempting to reconnect...";
    public static final String CONNECTION_ALREADY_CLOSED_MSG = "Connection already closed. Cannot perform operation on closed connection.";
    
    public static final String TOPIC_CREATION_FAILED_MSG = "Failed to create topic '%s'. %s";
    public static final String TOPIC_DELETION_FAILED_MSG = "Failed to delete topic '%s'. %s";
    public static final String TOPIC_NOT_FOUND_MSG = "Topic '%s' not found.";
    public static final String TOPIC_ALREADY_EXISTS_MSG = "Topic '%s' already exists.";
    public static final String TOPIC_LIST_FAILED_MSG = "Failed to list topics. %s";
    public static final String TOPIC_DESCRIBE_FAILED_MSG = "Failed to describe topic '%s'. %s";
    public static final String TOPIC_CONFIG_INVALID_MSG = "Invalid topic configuration: %s";
    public static final String PARTITION_COUNT_INVALID_MSG = "Invalid partition count: %d. Must be greater than 0.";
    public static final String REPLICATION_FACTOR_INVALID_MSG = "Invalid replication factor: %d. Must be between 1 and number of brokers.";
    public static final String TOPIC_OPERATION_TIMEOUT_MSG = "Topic operation timed out after %d seconds.";
    
    public static final String MESSAGE_SEND_FAILED_MSG = "Failed to send message to topic '%s'. %s";
    public static final String MESSAGE_CONSUME_FAILED_MSG = "Failed to consume messages from topic '%s'. %s";
    public static final String MESSAGE_SERIALIZATION_FAILED_MSG = "Message serialization failed. %s";
    public static final String MESSAGE_DESERIALIZATION_FAILED_MSG = "Message deserialization failed. %s";
    public static final String MESSAGE_VIEW_FAILED_MSG = "Failed to view messages from topic '%s'. %s";
    public static final String MESSAGE_OFFSET_INVALID_MSG = "Invalid offset: %d. Must be non-negative.";
    public static final String MESSAGE_PARTITION_INVALID_MSG = "Invalid partition: %d. Must be between 0 and %d.";
    public static final String MESSAGE_TIMEOUT_MSG = "Message operation timed out after %d seconds.";
    public static final String MESSAGE_SIZE_EXCEEDED_MSG = "Message size %d bytes exceeds maximum allowed size %d bytes.";
    public static final String MESSAGE_PRODUCER_CLOSED_MSG = "Producer is closed. Cannot send messages.";
    
    public static final String CONSUMER_GROUP_NOT_FOUND_MSG = "Consumer group '%s' not found.";
    public static final String CONSUMER_GROUP_LIST_FAILED_MSG = "Failed to list consumer groups. %s";
    public static final String CONSUMER_GROUP_DELETE_FAILED_MSG = "Failed to delete consumer group '%s'. %s";
    public static final String CONSUMER_GROUP_DESCRIBE_FAILED_MSG = "Failed to describe consumer group '%s'. %s";
    public static final String CONSUMER_OFFSET_RESET_FAILED_MSG = "Failed to reset consumer offset for group '%s'. %s";
    public static final String CONSUMER_LAG_CALCULATION_FAILED_MSG = "Failed to calculate consumer lag for group '%s'. %s";
    public static final String CONSUMER_HEALTH_CHECK_FAILED_MSG = "Failed to check consumer health for group '%s'. %s";
    public static final String CONSUMER_GROUP_ID_INVALID_MSG = "Invalid consumer group ID: '%s'. Must not be null or empty.";
    public static final String CONSUMER_SUBSCRIPTION_FAILED_MSG = "Failed to subscribe consumer to topic '%s'. %s";
    public static final String CONSUMER_POLL_TIMEOUT_MSG = "Consumer poll timed out after %d seconds.";
    
    public static final String SCHEMA_REGISTRATION_FAILED_MSG = "Failed to register schema for subject '%s'. %s";
    public static final String SCHEMA_RETRIEVAL_FAILED_MSG = "Failed to retrieve schema with ID %d. %s";
    public static final String SCHEMA_NOT_FOUND_MSG = "Schema with ID %d not found.";
    public static final String SCHEMA_INVALID_MSG = "Invalid schema: %s";
    public static final String SCHEMA_VERSION_NOT_FOUND_MSG = "Schema version %d not found for subject '%s'.";
    public static final String SCHEMA_SUBJECT_NOT_FOUND_MSG = "Subject '%s' not found.";
    public static final String SCHEMA_SUBJECT_LIST_FAILED_MSG = "Failed to list subjects. %s";
    public static final String SCHEMA_COMPATIBILITY_CHECK_FAILED_MSG = "Schema compatibility check failed for subject '%s'. %s";
    public static final String SCHEMA_CACHE_ERROR_MSG = "Schema cache error. %s";
    public static final String SCHEMA_REGISTRY_UNAVAILABLE_MSG = "Schema Registry is unavailable. %s";
    
    public static final String TRANSACTION_BEGIN_FAILED_MSG = "Failed to begin transaction '%s'. %s";
    public static final String TRANSACTION_COMMIT_FAILED_MSG = "Failed to commit transaction '%s'. %s";
    public static final String TRANSACTION_ABORT_FAILED_MSG = "Failed to abort transaction '%s'. %s";
    public static final String TRANSACTION_ID_INVALID_MSG = "Invalid transaction ID: '%s'. Must not be null or empty.";
    public static final String TRANSACTION_TIMEOUT_MSG = "Transaction '%s' timed out after %d seconds.";
    public static final String TRANSACTION_PRODUCER_CREATION_FAILED_MSG = "Failed to create transactional producer for transaction '%s'. %s";
    public static final String TRANSACTION_CONSUMER_CREATION_FAILED_MSG = "Failed to create transactional consumer for transaction '%s'. %s";
    public static final String SESSION_MONITORING_FAILED_MSG = "Failed to monitor session. %s";
    public static final String SESSION_SUMMARY_FAILED_MSG = "Failed to get session summary. %s";
    public static final String SESSION_HEALTH_CHECK_FAILED_MSG = "Failed to check session health. %s";
    
    public static final String CONFIG_LOAD_FAILED_MSG = "Failed to load configuration from file '%s'. %s";
    public static final String CONFIG_INVALID_MSG = "Invalid configuration: %s";
    public static final String CONFIG_MISSING_REQUIRED_MSG = "Missing required configuration: %s";
    public static final String CONFIG_SSL_INVALID_MSG = "Invalid SSL configuration: %s";
    public static final String CONFIG_SERIALIZER_INVALID_MSG = "Invalid serializer configuration: %s";
    public static final String CONFIG_DESERIALIZER_INVALID_MSG = "Invalid deserializer configuration: %s";
    public static final String CONFIG_BOOTSTRAP_SERVERS_INVALID_MSG = "Invalid bootstrap servers: '%s'. Must not be null or empty.";
    public static final String CONFIG_SCHEMA_REGISTRY_URL_INVALID_MSG = "Invalid Schema Registry URL: '%s'. Must be a valid URL.";
    public static final String CONFIG_FILE_NOT_FOUND_MSG = "Configuration file not found: '%s'";
    public static final String CONFIG_JSON_PARSE_ERROR_MSG = "JSON parsing error in configuration file '%s': %s";
    
    public static final String VALIDATION_TOPIC_NAME_INVALID_MSG = "Invalid topic name: '%s'. Must not be null, empty, or contain invalid characters.";
    public static final String VALIDATION_GROUP_ID_INVALID_MSG = "Invalid consumer group ID: '%s'. Must not be null or empty.";
    public static final String VALIDATION_SUBJECT_INVALID_MSG = "Invalid subject name: '%s'. Must not be null or empty.";
    public static final String VALIDATION_PARTITION_COUNT_INVALID_MSG = "Invalid partition count: %d. Must be greater than 0.";
    public static final String VALIDATION_REPLICATION_FACTOR_INVALID_MSG = "Invalid replication factor: %d. Must be between 1 and number of brokers.";
    public static final String VALIDATION_MESSAGE_NULL_MSG = "Message value cannot be null.";
    public static final String VALIDATION_SCHEMA_NULL_MSG = "Schema cannot be null.";
    public static final String VALIDATION_TRANSACTION_ID_INVALID_MSG = "Invalid transaction ID: '%s'. Must not be null or empty.";
    public static final String VALIDATION_MAX_RECORDS_INVALID_MSG = "Invalid max records: %d. Must be greater than 0.";
    public static final String VALIDATION_OFFSET_INVALID_MSG = "Invalid offset: %d. Must be non-negative.";
    
    // Success Messages
    public static final String CONNECTION_SUCCESS_MSG = "Successfully connected to %s";
    public static final String TOPIC_CREATED_SUCCESS_MSG = "Topic '%s' created successfully with %d partitions and replication factor %d";
    public static final String TOPIC_DELETED_SUCCESS_MSG = "Topic '%s' deleted successfully";
    public static final String MESSAGE_SENT_SUCCESS_MSG = "Message sent successfully to topic '%s'";
    public static final String MESSAGE_CONSUMED_SUCCESS_MSG = "Successfully consumed %d messages from topic '%s'";
    public static final String SCHEMA_REGISTERED_SUCCESS_MSG = "Schema registered successfully for subject '%s' with ID %d";
    public static final String TRANSACTION_BEGUN_SUCCESS_MSG = "Transaction '%s' begun successfully";
    public static final String TRANSACTION_COMMITTED_SUCCESS_MSG = "Transaction '%s' committed successfully";
    public static final String TRANSACTION_ABORTED_SUCCESS_MSG = "Transaction '%s' aborted successfully";
    
    // Utility Methods
    public static String formatMessage(String template, Object... args) {
        return String.format(template, args);
    }
    
    public static String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case KAFKA_CONNECTION_FAILED:
                return KAFKA_CONNECTION_FAILED_MSG;
            case SCHEMA_REGISTRY_CONNECTION_FAILED:
                return SCHEMA_REGISTRY_CONNECTION_FAILED_MSG;
            case CONNECTION_TIMEOUT:
                return CONNECTION_TIMEOUT_MSG;
            case CONNECTION_REFUSED:
                return CONNECTION_REFUSED_MSG;
            case SSL_HANDSHAKE_FAILED:
                return SSL_HANDSHAKE_FAILED_MSG;
            case AUTHENTICATION_FAILED:
                return AUTHENTICATION_FAILED_MSG;
            case AUTHORIZATION_FAILED:
                return AUTHORIZATION_FAILED_MSG;
            case NETWORK_UNREACHABLE:
                return NETWORK_UNREACHABLE_MSG;
            case CONNECTION_LOST:
                return CONNECTION_LOST_MSG;
            case CONNECTION_ALREADY_CLOSED:
                return CONNECTION_ALREADY_CLOSED_MSG;
            case TOPIC_CREATION_FAILED:
                return TOPIC_CREATION_FAILED_MSG;
            case TOPIC_DELETION_FAILED:
                return TOPIC_DELETION_FAILED_MSG;
            case TOPIC_NOT_FOUND:
                return TOPIC_NOT_FOUND_MSG;
            case TOPIC_ALREADY_EXISTS:
                return TOPIC_ALREADY_EXISTS_MSG;
            case TOPIC_LIST_FAILED:
                return TOPIC_LIST_FAILED_MSG;
            case TOPIC_DESCRIBE_FAILED:
                return TOPIC_DESCRIBE_FAILED_MSG;
            case TOPIC_CONFIG_INVALID:
                return TOPIC_CONFIG_INVALID_MSG;
            case PARTITION_COUNT_INVALID:
                return PARTITION_COUNT_INVALID_MSG;
            case REPLICATION_FACTOR_INVALID:
                return REPLICATION_FACTOR_INVALID_MSG;
            case TOPIC_OPERATION_TIMEOUT:
                return TOPIC_OPERATION_TIMEOUT_MSG;
            case MESSAGE_SEND_FAILED:
                return MESSAGE_SEND_FAILED_MSG;
            case MESSAGE_CONSUME_FAILED:
                return MESSAGE_CONSUME_FAILED_MSG;
            case MESSAGE_SERIALIZATION_FAILED:
                return MESSAGE_SERIALIZATION_FAILED_MSG;
            case MESSAGE_DESERIALIZATION_FAILED:
                return MESSAGE_DESERIALIZATION_FAILED_MSG;
            case MESSAGE_VIEW_FAILED:
                return MESSAGE_VIEW_FAILED_MSG;
            case MESSAGE_OFFSET_INVALID:
                return MESSAGE_OFFSET_INVALID_MSG;
            case MESSAGE_PARTITION_INVALID:
                return MESSAGE_PARTITION_INVALID_MSG;
            case MESSAGE_TIMEOUT:
                return MESSAGE_TIMEOUT_MSG;
            case MESSAGE_SIZE_EXCEEDED:
                return MESSAGE_SIZE_EXCEEDED_MSG;
            case MESSAGE_PRODUCER_CLOSED:
                return MESSAGE_PRODUCER_CLOSED_MSG;
            case CONSUMER_GROUP_NOT_FOUND:
                return CONSUMER_GROUP_NOT_FOUND_MSG;
            case CONSUMER_GROUP_LIST_FAILED:
                return CONSUMER_GROUP_LIST_FAILED_MSG;
            case CONSUMER_GROUP_DELETE_FAILED:
                return CONSUMER_GROUP_DELETE_FAILED_MSG;
            case CONSUMER_GROUP_DESCRIBE_FAILED:
                return CONSUMER_GROUP_DESCRIBE_FAILED_MSG;
            case CONSUMER_OFFSET_RESET_FAILED:
                return CONSUMER_OFFSET_RESET_FAILED_MSG;
            case CONSUMER_LAG_CALCULATION_FAILED:
                return CONSUMER_LAG_CALCULATION_FAILED_MSG;
            case CONSUMER_HEALTH_CHECK_FAILED:
                return CONSUMER_HEALTH_CHECK_FAILED_MSG;
            case CONSUMER_GROUP_ID_INVALID:
                return CONSUMER_GROUP_ID_INVALID_MSG;
            case CONSUMER_SUBSCRIPTION_FAILED:
                return CONSUMER_SUBSCRIPTION_FAILED_MSG;
            case CONSUMER_POLL_TIMEOUT:
                return CONSUMER_POLL_TIMEOUT_MSG;
            case SCHEMA_REGISTRATION_FAILED:
                return SCHEMA_REGISTRATION_FAILED_MSG;
            case SCHEMA_RETRIEVAL_FAILED:
                return SCHEMA_RETRIEVAL_FAILED_MSG;
            case SCHEMA_NOT_FOUND:
                return SCHEMA_NOT_FOUND_MSG;
            case SCHEMA_INVALID:
                return SCHEMA_INVALID_MSG;
            case SCHEMA_VERSION_NOT_FOUND:
                return SCHEMA_VERSION_NOT_FOUND_MSG;
            case SCHEMA_SUBJECT_NOT_FOUND:
                return SCHEMA_SUBJECT_NOT_FOUND_MSG;
            case SCHEMA_SUBJECT_LIST_FAILED:
                return SCHEMA_SUBJECT_LIST_FAILED_MSG;
            case SCHEMA_COMPATIBILITY_CHECK_FAILED:
                return SCHEMA_COMPATIBILITY_CHECK_FAILED_MSG;
            case SCHEMA_CACHE_ERROR:
                return SCHEMA_CACHE_ERROR_MSG;
            case SCHEMA_REGISTRY_UNAVAILABLE:
                return SCHEMA_REGISTRY_UNAVAILABLE_MSG;
            case TRANSACTION_BEGIN_FAILED:
                return TRANSACTION_BEGIN_FAILED_MSG;
            case TRANSACTION_COMMIT_FAILED:
                return TRANSACTION_COMMIT_FAILED_MSG;
            case TRANSACTION_ABORT_FAILED:
                return TRANSACTION_ABORT_FAILED_MSG;
            case TRANSACTION_ID_INVALID:
                return TRANSACTION_ID_INVALID_MSG;
            case TRANSACTION_TIMEOUT:
                return TRANSACTION_TIMEOUT_MSG;
            case TRANSACTION_PRODUCER_CREATION_FAILED:
                return TRANSACTION_PRODUCER_CREATION_FAILED_MSG;
            case TRANSACTION_CONSUMER_CREATION_FAILED:
                return TRANSACTION_CONSUMER_CREATION_FAILED_MSG;
            case SESSION_MONITORING_FAILED:
                return SESSION_MONITORING_FAILED_MSG;
            case SESSION_SUMMARY_FAILED:
                return SESSION_SUMMARY_FAILED_MSG;
            case SESSION_HEALTH_CHECK_FAILED:
                return SESSION_HEALTH_CHECK_FAILED_MSG;
            case CONFIG_LOAD_FAILED:
                return CONFIG_LOAD_FAILED_MSG;
            case CONFIG_INVALID:
                return CONFIG_INVALID_MSG;
            case CONFIG_MISSING_REQUIRED:
                return CONFIG_MISSING_REQUIRED_MSG;
            case CONFIG_SSL_INVALID:
                return CONFIG_SSL_INVALID_MSG;
            case CONFIG_SERIALIZER_INVALID:
                return CONFIG_SERIALIZER_INVALID_MSG;
            case CONFIG_DESERIALIZER_INVALID:
                return CONFIG_DESERIALIZER_INVALID_MSG;
            case CONFIG_BOOTSTRAP_SERVERS_INVALID:
                return CONFIG_BOOTSTRAP_SERVERS_INVALID_MSG;
            case CONFIG_SCHEMA_REGISTRY_URL_INVALID:
                return CONFIG_SCHEMA_REGISTRY_URL_INVALID_MSG;
            case CONFIG_FILE_NOT_FOUND:
                return CONFIG_FILE_NOT_FOUND_MSG;
            case CONFIG_JSON_PARSE_ERROR:
                return CONFIG_JSON_PARSE_ERROR_MSG;
            case VALIDATION_TOPIC_NAME_INVALID:
                return VALIDATION_TOPIC_NAME_INVALID_MSG;
            case VALIDATION_GROUP_ID_INVALID:
                return VALIDATION_GROUP_ID_INVALID_MSG;
            case VALIDATION_SUBJECT_INVALID:
                return VALIDATION_SUBJECT_INVALID_MSG;
            case VALIDATION_PARTITION_COUNT_INVALID:
                return VALIDATION_PARTITION_COUNT_INVALID_MSG;
            case VALIDATION_REPLICATION_FACTOR_INVALID:
                return VALIDATION_REPLICATION_FACTOR_INVALID_MSG;
            case VALIDATION_MESSAGE_NULL:
                return VALIDATION_MESSAGE_NULL_MSG;
            case VALIDATION_SCHEMA_NULL:
                return VALIDATION_SCHEMA_NULL_MSG;
            case VALIDATION_TRANSACTION_ID_INVALID:
                return VALIDATION_TRANSACTION_ID_INVALID_MSG;
            case VALIDATION_MAX_RECORDS_INVALID:
                return VALIDATION_MAX_RECORDS_INVALID_MSG;
            case VALIDATION_OFFSET_INVALID:
                return VALIDATION_OFFSET_INVALID_MSG;
            default:
                return "Unknown error: " + errorCode;
        }
    }
}
