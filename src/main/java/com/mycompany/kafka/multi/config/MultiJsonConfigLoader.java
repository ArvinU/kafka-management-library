package com.mycompany.kafka.multi.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration loader for multiple brokers and schema registries from JSON files.
 */
public class MultiJsonConfigLoader {
    
    private static final Logger log = LoggerFactory.getLogger(MultiJsonConfigLoader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Loads multiple Kafka broker configurations from a JSON file.
     * 
     * @param configFile Path to the JSON configuration file
     * @return List of NamedKafkaConfig objects
     * @throws IOException if file cannot be read or parsed
     */
    public static List<NamedKafkaConfig> loadKafkaConfigs(String configFile) throws IOException {
        log.info("Loading Kafka configurations from: {}", configFile);
        
        JsonNode config = objectMapper.readTree(new File(configFile));
        
        List<NamedKafkaConfig> configs = new ArrayList<>();
        
        if (config.has("brokers")) {
            JsonNode brokersNode = config.get("brokers");
            
            if (brokersNode.isArray()) {
                for (JsonNode brokerNode : brokersNode) {
                    NamedKafkaConfig brokerConfig = parseBrokerConfig(brokerNode);
                    configs.add(brokerConfig);
                }
            } else {
                // Single broker configuration
                NamedKafkaConfig brokerConfig = parseBrokerConfig(brokersNode);
                configs.add(brokerConfig);
            }
        } else {
            // Legacy format - single broker configuration
            NamedKafkaConfig brokerConfig = parseLegacyBrokerConfig(config);
            configs.add(brokerConfig);
        }
        
        log.info("Loaded {} Kafka broker configurations", configs.size());
        return configs;
    }
    
    /**
     * Loads multiple Schema Registry configurations from a JSON file.
     * 
     * @param configFile Path to the JSON configuration file
     * @return List of NamedSchemaRegistryConfig objects
     * @throws IOException if file cannot be read or parsed
     */
    public static List<NamedSchemaRegistryConfig> loadSchemaRegistryConfigs(String configFile) throws IOException {
        log.info("Loading Schema Registry configurations from: {}", configFile);
        
        JsonNode config = objectMapper.readTree(new File(configFile));
        
        List<NamedSchemaRegistryConfig> configs = new ArrayList<>();
        
        if (config.has("schemaRegistries")) {
            JsonNode registriesNode = config.get("schemaRegistries");
            
            if (registriesNode.isArray()) {
                for (JsonNode registryNode : registriesNode) {
                    NamedSchemaRegistryConfig registryConfig = parseSchemaRegistryConfig(registryNode);
                    configs.add(registryConfig);
                }
            } else {
                // Single schema registry configuration
                NamedSchemaRegistryConfig registryConfig = parseSchemaRegistryConfig(registriesNode);
                configs.add(registryConfig);
            }
        } else {
            // Legacy format - single schema registry configuration
            NamedSchemaRegistryConfig registryConfig = parseLegacySchemaRegistryConfig(config);
            configs.add(registryConfig);
        }
        
        log.info("Loaded {} Schema Registry configurations", configs.size());
        return configs;
    }
    
    /**
     * Parses a broker configuration from JSON node.
     */
    private static NamedKafkaConfig parseBrokerConfig(JsonNode brokerNode) {
        String name = brokerNode.get("name").asText();
        String bootstrapServers = brokerNode.get("bootstrapServers").asText();
        
        NamedKafkaConfig config = new NamedKafkaConfig(name, bootstrapServers);
        
        // Set optional properties
        if (brokerNode.has("securityProtocol")) {
            config.setSecurityProtocol(brokerNode.get("securityProtocol").asText());
        }
        
        if (brokerNode.has("clientId")) {
            config.setClientId(brokerNode.get("clientId").asText());
        }
        
        if (brokerNode.has("requestTimeoutMs")) {
            config.setRequestTimeoutMs(brokerNode.get("requestTimeoutMs").asInt());
        }
        
        if (brokerNode.has("retries")) {
            config.setRetries(brokerNode.get("retries").asInt());
        }
        
        if (brokerNode.has("enableAutoCommit")) {
            config.setEnableAutoCommit(brokerNode.get("enableAutoCommit").asBoolean());
        }
        
        if (brokerNode.has("autoOffsetReset")) {
            config.setAutoOffsetReset(brokerNode.get("autoOffsetReset").asText());
        }
        
        // SSL Configuration
        if (brokerNode.has("sslTruststoreLocation")) {
            config.setSslTruststoreLocation(brokerNode.get("sslTruststoreLocation").asText());
        }
        if (brokerNode.has("sslTruststorePassword")) {
            config.setSslTruststorePassword(brokerNode.get("sslTruststorePassword").asText());
        }
        if (brokerNode.has("sslKeystoreLocation")) {
            config.setSslKeystoreLocation(brokerNode.get("sslKeystoreLocation").asText());
        }
        if (brokerNode.has("sslKeystorePassword")) {
            config.setSslKeystorePassword(brokerNode.get("sslKeystorePassword").asText());
        }
        if (brokerNode.has("sslKeyPassword")) {
            config.setSslKeyPassword(brokerNode.get("sslKeyPassword").asText());
        }
        
        // SASL Configuration
        if (brokerNode.has("saslMechanism")) {
            config.setSaslMechanism(brokerNode.get("saslMechanism").asText());
        }
        if (brokerNode.has("saslJaasConfig")) {
            config.setSaslJaasConfig(brokerNode.get("saslJaasConfig").asText());
        }
        
        return config;
    }
    
    /**
     * Parses a schema registry configuration from JSON node.
     */
    private static NamedSchemaRegistryConfig parseSchemaRegistryConfig(JsonNode registryNode) {
        String name = registryNode.get("name").asText();
        String schemaRegistryUrl = registryNode.get("schemaRegistryUrl").asText();
        
        NamedSchemaRegistryConfig config = new NamedSchemaRegistryConfig(name, schemaRegistryUrl);
        
        // Set optional properties
        if (registryNode.has("securityProtocol")) {
            config.setSecurityProtocol(registryNode.get("securityProtocol").asText());
        }
        
        if (registryNode.has("cacheCapacity")) {
            config.setCacheCapacity(registryNode.get("cacheCapacity").asInt());
        }
        
        if (registryNode.has("requestTimeoutMs")) {
            config.setRequestTimeoutMs(registryNode.get("requestTimeoutMs").asInt());
        }
        
        if (registryNode.has("retries")) {
            config.setRetries(registryNode.get("retries").asInt());
        }
        
        // SSL Configuration
        if (registryNode.has("sslTruststoreLocation")) {
            config.setSslTruststoreLocation(registryNode.get("sslTruststoreLocation").asText());
        }
        if (registryNode.has("sslTruststorePassword")) {
            config.setSslTruststorePassword(registryNode.get("sslTruststorePassword").asText());
        }
        if (registryNode.has("sslKeystoreLocation")) {
            config.setSslKeystoreLocation(registryNode.get("sslKeystoreLocation").asText());
        }
        if (registryNode.has("sslKeystorePassword")) {
            config.setSslKeystorePassword(registryNode.get("sslKeystorePassword").asText());
        }
        if (registryNode.has("sslKeyPassword")) {
            config.setSslKeyPassword(registryNode.get("sslKeyPassword").asText());
        }
        
        // SASL Configuration
        if (registryNode.has("saslMechanism")) {
            config.setSaslMechanism(registryNode.get("saslMechanism").asText());
        }
        if (registryNode.has("saslJaasConfig")) {
            config.setSaslJaasConfig(registryNode.get("saslJaasConfig").asText());
        }
        
        return config;
    }
    
    /**
     * Parses legacy single broker configuration format.
     */
    private static NamedKafkaConfig parseLegacyBrokerConfig(JsonNode config) {
        String bootstrapServers = "localhost:9092"; // default
        if (config.has("bootstrap.servers")) {
            bootstrapServers = config.get("bootstrap.servers").asText();
        }
        
        NamedKafkaConfig brokerConfig = new NamedKafkaConfig("default-broker", bootstrapServers);
        
        // Set security protocol
        if (config.has("security.protocol")) {
            brokerConfig.setSecurityProtocol(config.get("security.protocol").asText());
        }
        
        // Set SSL configurations
        if (config.has("ssl.truststore.location")) {
            brokerConfig.setSslTruststoreLocation(config.get("ssl.truststore.location").asText());
        }
        if (config.has("ssl.truststore.password")) {
            brokerConfig.setSslTruststorePassword(config.get("ssl.truststore.password").asText());
        }
        if (config.has("ssl.keystore.location")) {
            brokerConfig.setSslKeystoreLocation(config.get("ssl.keystore.location").asText());
        }
        if (config.has("ssl.keystore.password")) {
            brokerConfig.setSslKeystorePassword(config.get("ssl.keystore.password").asText());
        }
        if (config.has("ssl.key.password")) {
            brokerConfig.setSslKeyPassword(config.get("ssl.key.password").asText());
        }
        
        // Set SASL configurations
        if (config.has("sasl.mechanism")) {
            brokerConfig.setSaslMechanism(config.get("sasl.mechanism").asText());
        }
        if (config.has("sasl.jaas.config")) {
            brokerConfig.setSaslJaasConfig(config.get("sasl.jaas.config").asText());
        }
        
        // Set other configurations
        if (config.has("client.id")) {
            brokerConfig.setClientId(config.get("client.id").asText());
        }
        if (config.has("request.timeout.ms")) {
            brokerConfig.setRequestTimeoutMs(config.get("request.timeout.ms").asInt());
        }
        if (config.has("retries")) {
            brokerConfig.setRetries(config.get("retries").asInt());
        }
        if (config.has("enable.auto.commit")) {
            brokerConfig.setEnableAutoCommit(config.get("enable.auto.commit").asBoolean());
        }
        if (config.has("auto.offset.reset")) {
            brokerConfig.setAutoOffsetReset(config.get("auto.offset.reset").asText());
        }
        
        return brokerConfig;
    }
    
    /**
     * Parses legacy single schema registry configuration format.
     */
    private static NamedSchemaRegistryConfig parseLegacySchemaRegistryConfig(JsonNode config) {
        String schemaRegistryUrl = "http://localhost:8081"; // default
        if (config.has("schema.registry.url")) {
            schemaRegistryUrl = config.get("schema.registry.url").asText();
        }
        
        NamedSchemaRegistryConfig schemaRegistryConfig = new NamedSchemaRegistryConfig("default-schema-registry", schemaRegistryUrl);
        
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
        
        return schemaRegistryConfig;
    }
}
