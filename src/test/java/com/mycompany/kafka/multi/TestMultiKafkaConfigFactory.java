package com.mycompany.kafka.multi;

import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import java.util.Properties;

/**
 * Test-specific configuration factory for multi-broker configurations.
 * Creates fast test configurations without real network connections.
 */
public class TestMultiKafkaConfigFactory {
    
    /**
     * Creates a NamedKafkaConfig optimized for fast testing.
     * Uses minimal timeouts and no real connections.
     */
    public static NamedKafkaConfig createFastTestNamedKafkaConfig(String name, String bootstrapServers) {
        NamedKafkaConfig config = new NamedKafkaConfig(name, bootstrapServers);
        
        // Override properties for fast testing - use very short timeouts to fail quickly
        Properties props = config.toProperties();
        props.setProperty("request.timeout.ms", "1");
        props.setProperty("connections.max.idle.ms", "100");
        props.setProperty("retries", "0");
        props.setProperty("retry.backoff.ms", "1");
        props.setProperty("reconnect.backoff.ms", "1");
        props.setProperty("reconnect.backoff.max.ms", "1");
        props.setProperty("socket.connection.setup.timeout.ms", "1");
        props.setProperty("socket.connection.setup.timeout.max.ms", "1");
        props.setProperty("metadata.max.age.ms", "1");
        props.setProperty("default.api.timeout.ms", "1");
        
        return config;
    }
    
    /**
     * Creates a NamedSchemaRegistryConfig optimized for fast testing.
     */
    public static NamedSchemaRegistryConfig createFastTestNamedSchemaRegistryConfig(String name, String url) {
        return new NamedSchemaRegistryConfig(name, url);
    }
    
    /**
     * Creates a test broker configuration with SSL settings.
     */
    public static NamedKafkaConfig createTestBrokerWithSSL(String name, String bootstrapServers) {
        NamedKafkaConfig config = createFastTestNamedKafkaConfig(name, bootstrapServers);
        config.setSecurityProtocol("SSL");
        config.setSslTruststoreLocation("/test/path/to/truststore.jks");
        config.setSslTruststorePassword("test-truststore-password");
        config.setSslKeystoreLocation("/test/path/to/keystore.jks");
        config.setSslKeystorePassword("test-keystore-password");
        config.setSslKeyPassword("test-key-password");
        return config;
    }
    
    /**
     * Creates a test schema registry configuration with SSL settings.
     */
    public static NamedSchemaRegistryConfig createTestSchemaRegistryWithSSL(String name, String url) {
        NamedSchemaRegistryConfig config = createFastTestNamedSchemaRegistryConfig(name, url);
        config.setSecurityProtocol("SSL");
        config.setSslTruststoreLocation("/test/path/to/truststore.jks");
        config.setSslTruststorePassword("test-truststore-password");
        config.setSslKeystoreLocation("/test/path/to/keystore.jks");
        config.setSslKeystorePassword("test-keystore-password");
        config.setSslKeyPassword("test-key-password");
        return config;
    }
}
