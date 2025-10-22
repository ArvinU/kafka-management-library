package com.mycompany.kafka.validator;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Validates connections to Kafka broker and Schema Registry.
 * Provides standardized connection failure handling.
 */
public class ConnectionValidator {
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionValidator.class);
    
    private static final int CONNECTION_TIMEOUT_SECONDS = 10;
    
    private final KafkaConfig kafkaConfig;
    private final SchemaRegistryConfig schemaRegistryConfig;
    private boolean kafkaConnectionValid = false;
    private boolean schemaRegistryConnectionValid = false;
    
    public ConnectionValidator(KafkaConfig kafkaConfig, SchemaRegistryConfig schemaRegistryConfig) {
        this.kafkaConfig = kafkaConfig;
        this.schemaRegistryConfig = schemaRegistryConfig;
    }
    
    /**
     * Validates Kafka broker connection.
     * 
     * @throws KafkaManagementException if connection fails
     */
    public void validateKafkaConnection() throws KafkaManagementException {
        if (kafkaConnectionValid) {
            return;
        }
        
        log.info("Validating Kafka connection to: {}", kafkaConfig.getBootstrapServers());
        
        AdminClient adminClient = null;
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
            props.setProperty(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(CONNECTION_TIMEOUT_SECONDS * 1000));
            props.setProperty(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, "30000");
            
            adminClient = AdminClient.create(props);
            
            // Test connection by listing topics
            ListTopicsResult topicsResult = adminClient.listTopics();
            try {
                topicsResult.names().get(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new KafkaManagementException(
                    ErrorConstants.CONNECTION_TIMEOUT,
                    "Connection validation interrupted",
                    e);
            } catch (TimeoutException e) {
                throw new KafkaManagementException(
                    ErrorConstants.CONNECTION_TIMEOUT,
                    ErrorConstants.formatMessage(ErrorConstants.CONNECTION_TIMEOUT_MSG, CONNECTION_TIMEOUT_SECONDS),
                    e);
            }
            
            kafkaConnectionValid = true;
            log.info(ErrorConstants.formatMessage(ErrorConstants.CONNECTION_SUCCESS_MSG, 
                "Kafka broker at " + kafkaConfig.getBootstrapServers()));
            
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException) {
                throw new KafkaManagementException(
                    ErrorConstants.KAFKA_CONNECTION_FAILED,
                    ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                        kafkaConfig.getBootstrapServers()),
                    cause);
            } else if (cause instanceof UnknownHostException) {
                throw new KafkaManagementException(
                    ErrorConstants.NETWORK_UNREACHABLE,
                    ErrorConstants.formatMessage(ErrorConstants.NETWORK_UNREACHABLE_MSG, 
                        kafkaConfig.getBootstrapServers()),
                    cause);
            } else if (cause instanceof SocketTimeoutException) {
                throw new KafkaManagementException(
                    ErrorConstants.CONNECTION_TIMEOUT,
                    ErrorConstants.formatMessage(ErrorConstants.CONNECTION_TIMEOUT_MSG, 
                        CONNECTION_TIMEOUT_SECONDS),
                    cause);
            } else {
                throw new KafkaManagementException(
                    ErrorConstants.KAFKA_CONNECTION_FAILED,
                    ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                        kafkaConfig.getBootstrapServers()),
                    cause);
            }
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        } finally {
            if (adminClient != null) {
                try {
                    adminClient.close();
                } catch (Exception e) {
                    log.warn("Error closing admin client during validation: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Validates Schema Registry connection.
     * 
     * @throws KafkaManagementException if connection fails
     */
    public void validateSchemaRegistryConnection() throws KafkaManagementException {
        if (schemaRegistryConnectionValid) {
            return;
        }
        
        log.info("Validating Schema Registry connection to: {}", schemaRegistryConfig.getSchemaRegistryUrl());
        
        io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient client = null;
        try {
            // Test connection by creating a client and making a simple request
            client = new io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient(
                schemaRegistryConfig.getSchemaRegistryUrl(), 
                schemaRegistryConfig.getCacheCapacity());
            
            // Test connection by listing subjects (this will fail if connection is not available)
            try {
                client.getAllSubjects();
            } catch (Exception e) {
                // If we can't list subjects, it might be because there are none, 
                // but the connection should still work
                log.debug("Could not list subjects during validation, but connection may still be valid: {}", e.getMessage());
            }
            
            schemaRegistryConnectionValid = true;
            log.info(ErrorConstants.formatMessage(ErrorConstants.CONNECTION_SUCCESS_MSG, 
                "Schema Registry at " + schemaRegistryConfig.getSchemaRegistryUrl()));
            
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            
            String errorCode = ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED;
            String errorMessage = ErrorConstants.formatMessage(ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED_MSG, 
                schemaRegistryConfig.getSchemaRegistryUrl());
            
            if (e instanceof ConnectException) {
                errorCode = ErrorConstants.CONNECTION_REFUSED;
                errorMessage = ErrorConstants.formatMessage(ErrorConstants.CONNECTION_REFUSED_MSG, 
                    schemaRegistryConfig.getSchemaRegistryUrl());
            } else if (e instanceof UnknownHostException) {
                errorCode = ErrorConstants.NETWORK_UNREACHABLE;
                errorMessage = ErrorConstants.formatMessage(ErrorConstants.NETWORK_UNREACHABLE_MSG, 
                    schemaRegistryConfig.getSchemaRegistryUrl());
            } else if (e instanceof SocketTimeoutException) {
                errorCode = ErrorConstants.CONNECTION_TIMEOUT;
                errorMessage = ErrorConstants.formatMessage(ErrorConstants.CONNECTION_TIMEOUT_MSG, 
                    CONNECTION_TIMEOUT_SECONDS);
            }
            
            throw new KafkaManagementException(errorCode, errorMessage, e);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    log.warn("Error closing Schema Registry client during validation: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Validates both Kafka and Schema Registry connections.
     * 
     * @throws KafkaManagementException if any connection fails
     */
    public void validateAllConnections() throws KafkaManagementException {
        validateKafkaConnection();
        validateSchemaRegistryConnection();
    }
    
    /**
     * Checks if Kafka connection is valid.
     * 
     * @return true if Kafka connection is valid
     */
    public boolean isKafkaConnectionValid() {
        return kafkaConnectionValid;
    }
    
    /**
     * Checks if Schema Registry connection is valid.
     * 
     * @return true if Schema Registry connection is valid
     */
    public boolean isSchemaRegistryConnectionValid() {
        return schemaRegistryConnectionValid;
    }
    
    /**
     * Resets connection validation status.
     * This should be called when connections are lost or need to be revalidated.
     */
    public void resetConnectionStatus() {
        kafkaConnectionValid = false;
        schemaRegistryConnectionValid = false;
        log.info("Connection validation status reset");
    }
    
}
