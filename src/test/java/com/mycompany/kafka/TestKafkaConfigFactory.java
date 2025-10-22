package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import java.util.Properties;

/**
 * Test-specific configuration factory that creates fast test configurations
 * without any real network connections.
 */
public class TestKafkaConfigFactory {
    
    /**
     * Creates a KafkaConfig optimized for fast testing.
     * Uses minimal timeouts and no real connections.
     */
    public static KafkaConfig createFastTestKafkaConfig() {
        KafkaConfig config = new KafkaConfig("localhost:9092");
        
        // Override properties for fast testing
        Properties props = config.toProperties();
        props.setProperty("request.timeout.ms", "100");
        props.setProperty("connections.max.idle.ms", "1000");
        props.setProperty("retries", "0");
        props.setProperty("retry.backoff.ms", "10");
        props.setProperty("reconnect.backoff.ms", "10");
        props.setProperty("reconnect.backoff.max.ms", "100");
        props.setProperty("socket.connection.setup.timeout.ms", "100");
        props.setProperty("socket.connection.setup.timeout.max.ms", "100");
        
        return config;
    }
    
    /**
     * Creates a SchemaRegistryConfig optimized for fast testing.
     */
    public static SchemaRegistryConfig createFastTestSchemaRegistryConfig() {
        return new SchemaRegistryConfig("http://localhost:8081");
    }
}
