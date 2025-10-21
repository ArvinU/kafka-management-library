package com.mycompany.kafka.factory;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
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
    
    public ConnectionFactory(KafkaConfig kafkaConfig, SchemaRegistryConfig schemaRegistryConfig) {
        this.kafkaConfig = kafkaConfig;
        this.schemaRegistryConfig = schemaRegistryConfig;
    }
    
    /**
     * Creates a Kafka AdminClient with the configured settings.
     * 
     * @return AdminClient instance
     */
    public AdminClient createAdminClient() {
        log.info("Creating Kafka AdminClient with bootstrap servers: {}", kafkaConfig.getBootstrapServers());
        
        Properties props = kafkaConfig.toProperties();
        props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        
        AdminClient adminClient = AdminClient.create(props);
        log.info("Kafka AdminClient created successfully");
        return adminClient;
    }
    
    /**
     * Creates a Kafka Producer with the configured settings.
     * 
     * @return Producer instance
     */
    public Producer<String, String> createProducer() {
        log.info("Creating Kafka Producer with bootstrap servers: {}", kafkaConfig.getBootstrapServers());
        
        Properties props = kafkaConfig.toProperties();
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
        
        Producer<String, String> producer = new KafkaProducer<>(props);
        log.info("Kafka Producer created successfully");
        return producer;
    }
    
    /**
     * Creates a Kafka Consumer with the configured settings.
     * 
     * @param groupId Consumer group ID
     * @return Consumer instance
     */
    public Consumer<String, String> createConsumer(String groupId) {
        log.info("Creating Kafka Consumer with group ID: {}", groupId);
        
        Properties props = kafkaConfig.toProperties();
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isEnableAutoCommit()));
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
        
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        log.info("Kafka Consumer created successfully");
        return consumer;
    }
    
    /**
     * Creates a Schema Registry Client with the configured settings.
     * 
     * @return SchemaRegistryClient instance
     */
    public SchemaRegistryClient createSchemaRegistryClient() {
        log.info("Creating Schema Registry Client with URL: {}", schemaRegistryConfig.getSchemaRegistryUrl());
        
        SchemaRegistryClient client = new CachedSchemaRegistryClient(
            schemaRegistryConfig.getSchemaRegistryUrl(), 
            schemaRegistryConfig.getCacheCapacity()
        );
        
        log.info("Schema Registry Client created successfully");
        return client;
    }
    
    /**
     * Creates a Kafka Producer with Avro serialization support.
     * 
     * @return Producer instance with Avro serializers
     */
    public Producer<String, Object> createAvroProducer() {
        log.info("Creating Avro Kafka Producer");
        
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
    }
    
    /**
     * Creates a Kafka Consumer with Avro deserialization support.
     * 
     * @param groupId Consumer group ID
     * @return Consumer instance with Avro deserializers
     */
    public Consumer<String, Object> createAvroConsumer(String groupId) {
        log.info("Creating Avro Kafka Consumer with group ID: {}", groupId);
        
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
    }
    
    /**
     * Creates a Kafka Producer with JSON Schema serialization support.
     * 
     * @return Producer instance with JSON Schema serializers
     */
    public Producer<String, Object> createJsonSchemaProducer() {
        log.info("Creating JSON Schema Kafka Producer");
        
        Properties props = kafkaConfig.toProperties();
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
        props.setProperty("schema.registry.url", schemaRegistryConfig.getSchemaRegistryUrl());
        props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        props.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(kafkaConfig.getRetries()));
        
        Producer<String, Object> producer = new KafkaProducer<>(props);
        log.info("JSON Schema Kafka Producer created successfully");
        return producer;
    }
    
    /**
     * Creates a Kafka Consumer with JSON Schema deserialization support.
     * 
     * @param groupId Consumer group ID
     * @return Consumer instance with JSON Schema deserializers
     */
    public Consumer<String, Object> createJsonSchemaConsumer(String groupId) {
        log.info("Creating JSON Schema Kafka Consumer with group ID: {}", groupId);
        
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
    }
    
    // Getters
    public KafkaConfig getKafkaConfig() { return kafkaConfig; }
    public SchemaRegistryConfig getSchemaRegistryConfig() { return schemaRegistryConfig; }
}
