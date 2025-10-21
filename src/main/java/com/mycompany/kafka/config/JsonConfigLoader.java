package com.mycompany.kafka.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for loading configuration from JSON files.
 * Supports both Kafka client and Schema Registry configurations.
 */
public class JsonConfigLoader {
    
    private static final Logger log = LoggerFactory.getLogger(JsonConfigLoader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Loads Kafka configuration from a JSON file.
     * 
     * @param configFile Path to the JSON configuration file
     * @return KafkaConfig object
     * @throws IOException if file cannot be read or parsed
     */
    public static KafkaConfig loadKafkaConfig(String configFile) throws IOException {
        log.info("Loading Kafka configuration from: {}", configFile);
        
        JsonNode config = objectMapper.readTree(new File(configFile));
        
        // Get bootstrap servers (required)
        String bootstrapServers = "localhost:9092"; // default
        if (config.has("bootstrap.servers")) {
            bootstrapServers = config.get("bootstrap.servers").asText();
        }
        
        KafkaConfig kafkaConfig = new KafkaConfig(bootstrapServers);
        
        // Set security protocol
        if (config.has("security.protocol")) {
            kafkaConfig.setSecurityProtocol(config.get("security.protocol").asText());
        }
        
        // Set SSL configurations
        if (config.has("ssl.truststore.location")) {
            kafkaConfig.setSslTruststoreLocation(config.get("ssl.truststore.location").asText());
        }
        if (config.has("ssl.truststore.password")) {
            kafkaConfig.setSslTruststorePassword(config.get("ssl.truststore.password").asText());
        }
        if (config.has("ssl.keystore.location")) {
            kafkaConfig.setSslKeystoreLocation(config.get("ssl.keystore.location").asText());
        }
        if (config.has("ssl.keystore.password")) {
            kafkaConfig.setSslKeystorePassword(config.get("ssl.keystore.password").asText());
        }
        if (config.has("ssl.key.password")) {
            kafkaConfig.setSslKeyPassword(config.get("ssl.key.password").asText());
        }
        
        // Set SASL configurations
        if (config.has("sasl.mechanism")) {
            kafkaConfig.setSaslMechanism(config.get("sasl.mechanism").asText());
        }
        if (config.has("sasl.jaas.config")) {
            kafkaConfig.setSaslJaasConfig(config.get("sasl.jaas.config").asText());
        }
        
        // Set other configurations
        if (config.has("client.id")) {
            kafkaConfig.setClientId(config.get("client.id").asText());
        }
        if (config.has("request.timeout.ms")) {
            kafkaConfig.setRequestTimeoutMs(config.get("request.timeout.ms").asInt());
        }
        if (config.has("retries")) {
            kafkaConfig.setRetries(config.get("retries").asInt());
        }
        if (config.has("enable.auto.commit")) {
            kafkaConfig.setEnableAutoCommit(config.get("enable.auto.commit").asBoolean());
        }
        if (config.has("auto.offset.reset")) {
            kafkaConfig.setAutoOffsetReset(config.get("auto.offset.reset").asText());
        }
        
        log.info("Successfully loaded Kafka configuration");
        return kafkaConfig;
    }
    
    /**
     * Loads Schema Registry configuration from a JSON file.
     * 
     * @param configFile Path to the JSON configuration file
     * @return SchemaRegistryConfig object
     * @throws IOException if file cannot be read or parsed
     */
    public static SchemaRegistryConfig loadSchemaRegistryConfig(String configFile) throws IOException {
        log.info("Loading Schema Registry configuration from: {}", configFile);
        
        JsonNode config = objectMapper.readTree(new File(configFile));
        
        // Get schema registry URL (required)
        String schemaRegistryUrl = "http://localhost:8081"; // default
        if (config.has("schema.registry.url")) {
            schemaRegistryUrl = config.get("schema.registry.url").asText();
        }
        
        SchemaRegistryConfig schemaRegistryConfig = new SchemaRegistryConfig(schemaRegistryUrl);
        
        // Set security protocol
        if (config.has("security.protocol")) {
            schemaRegistryConfig.setSecurityProtocol(config.get("security.protocol").asText());
        }
        
        // Set SSL configurations
        if (config.has("ssl.truststore.location")) {
            schemaRegistryConfig.setSslTruststoreLocation(config.get("ssl.truststore.location").asText());
        }
        if (config.has("ssl.truststore.password")) {
            schemaRegistryConfig.setSslTruststorePassword(config.get("ssl.truststore.password").asText());
        }
        if (config.has("ssl.keystore.location")) {
            schemaRegistryConfig.setSslKeystoreLocation(config.get("ssl.keystore.location").asText());
        }
        if (config.has("ssl.keystore.password")) {
            schemaRegistryConfig.setSslKeystorePassword(config.get("ssl.keystore.password").asText());
        }
        if (config.has("ssl.key.password")) {
            schemaRegistryConfig.setSslKeyPassword(config.get("ssl.key.password").asText());
        }
        
        // Set SASL configurations
        if (config.has("sasl.mechanism")) {
            schemaRegistryConfig.setSaslMechanism(config.get("sasl.mechanism").asText());
        }
        if (config.has("sasl.jaas.config")) {
            schemaRegistryConfig.setSaslJaasConfig(config.get("sasl.jaas.config").asText());
        }
        
        // Set other configurations
        if (config.has("cache.capacity")) {
            schemaRegistryConfig.setCacheCapacity(config.get("cache.capacity").asInt());
        }
        if (config.has("request.timeout.ms")) {
            schemaRegistryConfig.setRequestTimeoutMs(config.get("request.timeout.ms").asInt());
        }
        if (config.has("retries")) {
            schemaRegistryConfig.setRetries(config.get("retries").asInt());
        }
        
        log.info("Successfully loaded Schema Registry configuration");
        return schemaRegistryConfig;
    }
    
    /**
     * Loads configuration from JSON and converts to Properties.
     * 
     * @param configFile Path to the JSON configuration file
     * @return Properties object
     * @throws IOException if file cannot be read or parsed
     */
    public static Properties loadPropertiesFromJson(String configFile) throws IOException {
        log.info("Loading properties from JSON file: {}", configFile);
        
        JsonNode config = objectMapper.readTree(new File(configFile));
        Properties properties = new Properties();
        
        // Convert all JSON properties to Properties object
        Iterator<Map.Entry<String, JsonNode>> fields = config.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            
            if (value.isTextual()) {
                properties.setProperty(key, value.asText());
            } else if (value.isNumber()) {
                properties.setProperty(key, value.asText());
            } else if (value.isBoolean()) {
                properties.setProperty(key, String.valueOf(value.asBoolean()));
            }
        }
        
        log.info("Successfully loaded {} properties from JSON file", properties.size());
        return properties;
    }
    
    /**
     * Creates a sample Kafka configuration JSON file.
     * 
     * @param outputFile Path where to save the sample configuration
     * @throws IOException if file cannot be written
     */
    public static void createSampleKafkaConfig(String outputFile) throws IOException {
        log.info("Creating sample Kafka configuration file: {}", outputFile);
        
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        
        // JKS SSL Configuration (uncomment and configure as needed)
        config.put("#security.protocol", "SSL");
        config.put("#ssl.truststore.location", "/path/to/truststore.jks");
        config.put("#ssl.truststore.password", "truststore-password");
        config.put("#ssl.keystore.location", "/path/to/keystore.jks");
        config.put("#ssl.keystore.password", "keystore-password");
        config.put("#ssl.key.password", "key-password");
        
        // Optional fields with defaults
        config.put("#client.id", "kafka-management-library");
        config.put("#request.timeout.ms", 30000);
        config.put("#retries", 3);
        config.put("#enable.auto.commit", true);
        config.put("#auto.offset.reset", "earliest");
        
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), config);
        log.info("Sample Kafka configuration file created successfully");
    }
    
    /**
     * Creates a sample Schema Registry configuration JSON file.
     * 
     * @param outputFile Path where to save the sample configuration
     * @throws IOException if file cannot be written
     */
    public static void createSampleSchemaRegistryConfig(String outputFile) throws IOException {
        log.info("Creating sample Schema Registry configuration file: {}", outputFile);
        
        Map<String, Object> config = new HashMap<>();
        config.put("schema.registry.url", "http://localhost:8081");
        
        // JKS SSL Configuration (uncomment and configure as needed)
        config.put("#ssl.truststore.location", "/path/to/truststore.jks");
        config.put("#ssl.truststore.password", "truststore-password");
        config.put("#ssl.keystore.location", "/path/to/keystore.jks");
        config.put("#ssl.keystore.password", "keystore-password");
        config.put("#ssl.key.password", "key-password");
        
        // Optional fields with defaults
        config.put("#cache.capacity", 1000);
        config.put("#request.timeout.ms", 30000);
        config.put("#retries", 3);
        
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), config);
        log.info("Sample Schema Registry configuration file created successfully");
    }
}
