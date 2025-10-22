package com.mycompany.kafka.factory;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.validator.ConnectionValidator;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Factory class for creating Kafka and Schema Registry connections with JKS support.
 * Handles both plain and SSL configurations for secure connections.
 */
public class ConnectionFactory {
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    
    private final KafkaConfig kafkaConfig;
    private final SchemaRegistryConfig schemaRegistryConfig;
    private final ConnectionValidator connectionValidator;
    private boolean connectionsValidated = false;
    
    public ConnectionFactory(KafkaConfig kafkaConfig, SchemaRegistryConfig schemaRegistryConfig) {
        this.kafkaConfig = kafkaConfig;
        this.schemaRegistryConfig = schemaRegistryConfig;
        this.connectionValidator = new ConnectionValidator(kafkaConfig, schemaRegistryConfig);
    }
    
    /**
     * Creates a Kafka AdminClient with the configured settings.
     * Validates connection before creating the client.
     * 
     * @return AdminClient instance
     * @throws KafkaManagementException if connection validation fails
     */
    public AdminClient createAdminClient() throws KafkaManagementException {
        log.info("Creating Kafka AdminClient with bootstrap servers: {}", kafkaConfig.getBootstrapServers());
        
        // Validate connection before creating client
        if (!connectionsValidated) {
            connectionValidator.validateKafkaConnection();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
            
            AdminClient adminClient = AdminClient.create(props);
            log.info("Kafka AdminClient created successfully");
            return adminClient;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Producer with the configured settings.
     * Validates connection before creating the producer.
     * 
     * @return Producer instance
     * @throws KafkaManagementException if connection validation fails
     */
    public Producer<String, String> createProducer() throws KafkaManagementException {
        log.info("Creating Kafka Producer with bootstrap servers: {}", kafkaConfig.getBootstrapServers());
        
        // Validate connection before creating producer
        if (!connectionsValidated) {
            connectionValidator.validateKafkaConnection();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
            
            Producer<String, String> producer = new KafkaProducer<>(props);
            log.info("Kafka Producer created successfully");
            return producer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Consumer with the configured settings.
     * Validates connection before creating the consumer.
     * 
     * @param groupId Consumer group ID
     * @return Consumer instance
     * @throws KafkaManagementException if connection validation fails
     */
    public Consumer<String, String> createConsumer(String groupId) throws KafkaManagementException {
        log.info("Creating Kafka Consumer with group ID: {}", groupId);
        
        // Validate connection before creating consumer
        if (!connectionsValidated) {
            connectionValidator.validateKafkaConnection();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isEnableAutoCommit()));
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
            
            Consumer<String, String> consumer = new KafkaConsumer<>(props);
            log.info("Kafka Consumer created successfully");
            return consumer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Schema Registry Client with the configured settings.
     * Validates connection before creating the client.
     * 
     * @return SchemaRegistryClient instance
     * @throws KafkaManagementException if connection validation fails
     */
    public SchemaRegistryClient createSchemaRegistryClient() throws KafkaManagementException {
        log.info("Creating Schema Registry Client with URL: {}", schemaRegistryConfig.getSchemaRegistryUrl());
        
        // Validate connection before creating client
        if (!connectionsValidated) {
            connectionValidator.validateSchemaRegistryConnection();
            connectionsValidated = true;
        }
        
        try {
            SchemaRegistryClient client = new CachedSchemaRegistryClient(
                schemaRegistryConfig.getSchemaRegistryUrl(), 
                schemaRegistryConfig.getCacheCapacity()
            );
            
            log.info("Schema Registry Client created successfully");
            return client;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED_MSG, 
                    schemaRegistryConfig.getSchemaRegistryUrl()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Producer with Avro serialization support.
     * Validates connections before creating the producer.
     * 
     * @return Producer instance with Avro serializers
     * @throws KafkaManagementException if connection validation fails
     */
    public Producer<String, Object> createAvroProducer() throws KafkaManagementException {
        log.info("Creating Avro Kafka Producer");
        
        // Validate both Kafka and Schema Registry connections
        if (!connectionsValidated) {
            connectionValidator.validateAllConnections();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
            props.setProperty("schema.registry.url", schemaRegistryConfig.getSchemaRegistryUrl());
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
            
            // Add SSL configuration for Schema Registry if needed
            if ("SSL".equals(schemaRegistryConfig.getSecurityProtocol()) || "SASL_SSL".equals(schemaRegistryConfig.getSecurityProtocol())) {
                if (schemaRegistryConfig.getSslTruststoreLocation() != null) {
                    props.setProperty("schema.registry.ssl.truststore.location", schemaRegistryConfig.getSslTruststoreLocation());
                    props.setProperty("schema.registry.ssl.truststore.password", schemaRegistryConfig.getSslTruststorePassword());
                }
            }
            
            Producer<String, Object> producer = new KafkaProducer<>(props);
            log.info("Avro Kafka Producer created successfully");
            return producer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Consumer with Avro deserialization support.
     * Validates connections before creating the consumer.
     * 
     * @param groupId Consumer group ID
     * @return Consumer instance with Avro deserializers
     * @throws KafkaManagementException if connection validation fails
     */
    public Consumer<String, Object> createAvroConsumer(String groupId) throws KafkaManagementException {
        log.info("Creating Avro Kafka Consumer with group ID: {}", groupId);
        
        // Validate both Kafka and Schema Registry connections
        if (!connectionsValidated) {
            connectionValidator.validateAllConnections();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
            props.setProperty("schema.registry.url", schemaRegistryConfig.getSchemaRegistryUrl());
            props.setProperty("specific.avro.reader", "true");
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isEnableAutoCommit()));
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
            
            // Add SSL configuration for Schema Registry if needed
            if ("SSL".equals(schemaRegistryConfig.getSecurityProtocol()) || "SASL_SSL".equals(schemaRegistryConfig.getSecurityProtocol())) {
                if (schemaRegistryConfig.getSslTruststoreLocation() != null) {
                    props.setProperty("schema.registry.ssl.truststore.location", schemaRegistryConfig.getSslTruststoreLocation());
                    props.setProperty("schema.registry.ssl.truststore.password", schemaRegistryConfig.getSslTruststorePassword());
                }
            }
            
            Consumer<String, Object> consumer = new KafkaConsumer<>(props);
            log.info("Avro Kafka Consumer created successfully");
            return consumer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Producer with JSON Schema serialization support.
     * Validates connections before creating the producer.
     * 
     * @return Producer instance with JSON Schema serializers
     * @throws KafkaManagementException if connection validation fails
     */
    public Producer<String, Object> createJsonSchemaProducer() throws KafkaManagementException {
        log.info("Creating JSON Schema Kafka Producer");
        
        // Validate both Kafka and Schema Registry connections
        if (!connectionsValidated) {
            connectionValidator.validateAllConnections();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
            props.setProperty("schema.registry.url", schemaRegistryConfig.getSchemaRegistryUrl());
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
            
            Producer<String, Object> producer = new KafkaProducer<>(props);
            log.info("JSON Schema Kafka Producer created successfully");
            return producer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Consumer with JSON Schema deserialization support.
     * Validates connections before creating the consumer.
     * 
     * @param groupId Consumer group ID
     * @return Consumer instance with JSON Schema deserializers
     * @throws KafkaManagementException if connection validation fails
     */
    public Consumer<String, Object> createJsonSchemaConsumer(String groupId) throws KafkaManagementException {
        log.info("Creating JSON Schema Kafka Consumer with group ID: {}", groupId);
        
        // Validate both Kafka and Schema Registry connections
        if (!connectionsValidated) {
            connectionValidator.validateAllConnections();
            connectionsValidated = true;
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer");
            props.setProperty("schema.registry.url", schemaRegistryConfig.getSchemaRegistryUrl());
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isEnableAutoCommit()));
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
            
            Consumer<String, Object> consumer = new KafkaConsumer<>(props);
            log.info("JSON Schema Kafka Consumer created successfully");
            return consumer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    kafkaConfig.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Resets connection validation status.
     * This should be called when connections are lost or need to be revalidated.
     */
    public void resetConnectionValidation() {
        connectionsValidated = false;
        connectionValidator.resetConnectionStatus();
        log.info("Connection validation status reset");
    }
    
    /**
     * Validates all connections.
     * 
     * @throws KafkaManagementException if any connection validation fails
     */
    public void validateConnections() throws KafkaManagementException {
        connectionValidator.validateAllConnections();
        connectionsValidated = true;
    }
    
    // Getters
    public KafkaConfig getKafkaConfig() { return kafkaConfig; }
    public SchemaRegistryConfig getSchemaRegistryConfig() { return schemaRegistryConfig; }
    public ConnectionValidator getConnectionValidator() { return connectionValidator; }
}
