package com.mycompany.kafka.multi.factory;

import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.manager.MultiBrokerManager;
import com.mycompany.kafka.multi.manager.MultiSchemaRegistryManager;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.kafka.clients.admin.AdminClient;
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
 * Enhanced factory class for creating Kafka and Schema Registry connections with multi-broker support.
 * Handles both plain and SSL configurations for secure connections with named brokers and schema registries.
 */
public class MultiConnectionFactory {
    
    private static final Logger log = LoggerFactory.getLogger(MultiConnectionFactory.class);
    
    private final MultiBrokerManager multiBrokerManager;
    private final MultiSchemaRegistryManager multiSchemaRegistryManager;
    
    public MultiConnectionFactory(MultiBrokerManager multiBrokerManager, 
                                  MultiSchemaRegistryManager multiSchemaRegistryManager) {
        this.multiBrokerManager = multiBrokerManager;
        this.multiSchemaRegistryManager = multiSchemaRegistryManager;
    }
    
    /**
     * Creates a Kafka AdminClient for a specific broker.
     * 
     * @param brokerName The name of the broker to connect to
     * @return AdminClient instance
     * @throws KafkaManagementException if connection fails
     */
    public AdminClient createAdminClient(String brokerName) throws KafkaManagementException {
        log.info("Creating Kafka AdminClient for broker: {}", brokerName);
        
        return multiBrokerManager.getAdminClient(brokerName);
    }
    
    /**
     * Creates a Kafka Producer for a specific broker.
     * 
     * @param brokerName The name of the broker to connect to
     * @return Producer instance
     * @throws KafkaManagementException if connection fails
     */
    public Producer<String, String> createProducer(String brokerName) throws KafkaManagementException {
        log.info("Creating Kafka Producer for broker: {}", brokerName);
        
        NamedKafkaConfig config = multiBrokerManager.getBrokerConfig(brokerName);
        if (config == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (!config.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        try {
            Properties props = config.toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(config.getRetries()));
            
            Producer<String, String> producer = new KafkaProducer<>(props);
            log.info("Kafka Producer created successfully for broker: {}", brokerName);
            return producer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    config.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Kafka Consumer for a specific broker.
     * 
     * @param brokerName The name of the broker to connect to
     * @param groupId Consumer group ID
     * @return Consumer instance
     * @throws KafkaManagementException if connection fails
     */
    public Consumer<String, String> createConsumer(String brokerName, String groupId) throws KafkaManagementException {
        log.info("Creating Kafka Consumer for broker: {} with group ID: {}", brokerName, groupId);
        
        NamedKafkaConfig config = multiBrokerManager.getBrokerConfig(brokerName);
        if (config == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (!config.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        try {
            Properties props = config.toProperties();
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(config.isEnableAutoCommit()));
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getAutoOffsetReset());
            
            Consumer<String, String> consumer = new KafkaConsumer<>(props);
            log.info("Kafka Consumer created successfully for broker: {}", brokerName);
            return consumer;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.KAFKA_CONNECTION_FAILED_MSG, 
                    config.getBootstrapServers()),
                e);
        }
    }
    
    /**
     * Creates a Schema Registry Client for a specific registry.
     * 
     * @param registryName The name of the schema registry to connect to
     * @return SchemaRegistryClient instance
     * @throws KafkaManagementException if connection fails
     */
    public SchemaRegistryClient createSchemaRegistryClient(String registryName) throws KafkaManagementException {
        log.info("Creating Schema Registry Client for registry: {}", registryName);
        
        return multiSchemaRegistryManager.getSchemaRegistryClient(registryName);
    }
    
    /**
     * Creates a Kafka Producer with Avro serialization support for specific broker and schema registry.
     * 
     * @param brokerName The name of the broker to connect to
     * @param registryName The name of the schema registry to use
     * @return Producer instance with Avro serializers
     * @throws KafkaManagementException if connection fails
     */
    public Producer<String, Object> createAvroProducer(String brokerName, String registryName) throws KafkaManagementException {
        log.info("Creating Avro Kafka Producer for broker: {} and schema registry: {}", brokerName, registryName);
        
        NamedKafkaConfig kafkaConfig = multiBrokerManager.getBrokerConfig(brokerName);
        NamedSchemaRegistryConfig schemaConfig = multiSchemaRegistryManager.getSchemaRegistryConfig(registryName);
        
        if (kafkaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (schemaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry configuration not found: " + registryName);
        }
        
        if (!kafkaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        if (!schemaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry is not connected: " + registryName);
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
            props.setProperty("schema.registry.url", schemaConfig.getSchemaRegistryUrl());
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
            
            // Add SSL configuration for Schema Registry if needed
            if ("SSL".equals(schemaConfig.getSecurityProtocol()) || "SASL_SSL".equals(schemaConfig.getSecurityProtocol())) {
                if (schemaConfig.getSslTruststoreLocation() != null) {
                    props.setProperty("schema.registry.ssl.truststore.location", schemaConfig.getSslTruststoreLocation());
                    props.setProperty("schema.registry.ssl.truststore.password", schemaConfig.getSslTruststorePassword());
                }
            }
            
            Producer<String, Object> producer = new KafkaProducer<>(props);
            log.info("Avro Kafka Producer created successfully for broker: {} and schema registry: {}", brokerName, registryName);
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
     * Creates a Kafka Consumer with Avro deserialization support for specific broker and schema registry.
     * 
     * @param brokerName The name of the broker to connect to
     * @param registryName The name of the schema registry to use
     * @param groupId Consumer group ID
     * @return Consumer instance with Avro deserializers
     * @throws KafkaManagementException if connection fails
     */
    public Consumer<String, Object> createAvroConsumer(String brokerName, String registryName, String groupId) throws KafkaManagementException {
        log.info("Creating Avro Kafka Consumer for broker: {} and schema registry: {} with group ID: {}", brokerName, registryName, groupId);
        
        NamedKafkaConfig kafkaConfig = multiBrokerManager.getBrokerConfig(brokerName);
        NamedSchemaRegistryConfig schemaConfig = multiSchemaRegistryManager.getSchemaRegistryConfig(registryName);
        
        if (kafkaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (schemaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry configuration not found: " + registryName);
        }
        
        if (!kafkaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        if (!schemaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry is not connected: " + registryName);
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
            props.setProperty("schema.registry.url", schemaConfig.getSchemaRegistryUrl());
            props.setProperty("specific.avro.reader", "true");
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isEnableAutoCommit()));
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
            
            // Add SSL configuration for Schema Registry if needed
            if ("SSL".equals(schemaConfig.getSecurityProtocol()) || "SASL_SSL".equals(schemaConfig.getSecurityProtocol())) {
                if (schemaConfig.getSslTruststoreLocation() != null) {
                    props.setProperty("schema.registry.ssl.truststore.location", schemaConfig.getSslTruststoreLocation());
                    props.setProperty("schema.registry.ssl.truststore.password", schemaConfig.getSslTruststorePassword());
                }
            }
            
            Consumer<String, Object> consumer = new KafkaConsumer<>(props);
            log.info("Avro Kafka Consumer created successfully for broker: {} and schema registry: {}", brokerName, registryName);
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
     * Creates a Kafka Producer with JSON Schema serialization support for specific broker and schema registry.
     * 
     * @param brokerName The name of the broker to connect to
     * @param registryName The name of the schema registry to use
     * @return Producer instance with JSON Schema serializers
     * @throws KafkaManagementException if connection fails
     */
    public Producer<String, Object> createJsonSchemaProducer(String brokerName, String registryName) throws KafkaManagementException {
        log.info("Creating JSON Schema Kafka Producer for broker: {} and schema registry: {}", brokerName, registryName);
        
        NamedKafkaConfig kafkaConfig = multiBrokerManager.getBrokerConfig(brokerName);
        NamedSchemaRegistryConfig schemaConfig = multiSchemaRegistryManager.getSchemaRegistryConfig(registryName);
        
        if (kafkaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (schemaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry configuration not found: " + registryName);
        }
        
        if (!kafkaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        if (!schemaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry is not connected: " + registryName);
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
            props.setProperty("schema.registry.url", schemaConfig.getSchemaRegistryUrl());
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
            
            Producer<String, Object> producer = new KafkaProducer<>(props);
            log.info("JSON Schema Kafka Producer created successfully for broker: {} and schema registry: {}", brokerName, registryName);
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
     * Creates a Kafka Consumer with JSON Schema deserialization support for specific broker and schema registry.
     * 
     * @param brokerName The name of the broker to connect to
     * @param registryName The name of the schema registry to use
     * @param groupId Consumer group ID
     * @return Consumer instance with JSON Schema deserializers
     * @throws KafkaManagementException if connection fails
     */
    public Consumer<String, Object> createJsonSchemaConsumer(String brokerName, String registryName, String groupId) throws KafkaManagementException {
        log.info("Creating JSON Schema Kafka Consumer for broker: {} and schema registry: {} with group ID: {}", brokerName, registryName, groupId);
        
        NamedKafkaConfig kafkaConfig = multiBrokerManager.getBrokerConfig(brokerName);
        NamedSchemaRegistryConfig schemaConfig = multiSchemaRegistryManager.getSchemaRegistryConfig(registryName);
        
        if (kafkaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker configuration not found: " + brokerName);
        }
        
        if (schemaConfig == null) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry configuration not found: " + registryName);
        }
        
        if (!kafkaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Broker is not connected: " + brokerName);
        }
        
        if (!schemaConfig.isConnected()) {
            throw new KafkaManagementException(
                ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED,
                "Schema registry is not connected: " + registryName);
        }
        
        try {
            Properties props = kafkaConfig.toProperties();
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer");
            props.setProperty("schema.registry.url", schemaConfig.getSchemaRegistryUrl());
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isEnableAutoCommit()));
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
            
            Consumer<String, Object> consumer = new KafkaConsumer<>(props);
            log.info("JSON Schema Kafka Consumer created successfully for broker: {} and schema registry: {}", brokerName, registryName);
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
     * Reconnects to a specific broker
     */
    public boolean reconnectBroker(String brokerName) {
        return multiBrokerManager.reconnect(brokerName);
    }
    
    /**
     * Reconnects to a specific schema registry
     */
    public boolean reconnectSchemaRegistry(String registryName) {
        return multiSchemaRegistryManager.reconnect(registryName);
    }
    
    /**
     * Reconnects to all brokers and schema registries
     */
    public void reconnectAll() {
        multiBrokerManager.reconnectAll();
        multiSchemaRegistryManager.reconnectAll();
    }
    
    /**
     * Gets the multi-broker manager
     */
    public MultiBrokerManager getMultiBrokerManager() {
        return multiBrokerManager;
    }
    
    /**
     * Gets the multi-schema registry manager
     */
    public MultiSchemaRegistryManager getMultiSchemaRegistryManager() {
        return multiSchemaRegistryManager;
    }
    
    /**
     * Closes all connections and cleans up resources
     */
    public void close() {
        log.info("Closing all connections");
        multiBrokerManager.close();
        multiSchemaRegistryManager.close();
    }
}
