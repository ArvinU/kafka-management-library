package com.mycompany.kafka.multi;

import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.multi.manager.MultiBrokerManager;
import com.mycompany.kafka.multi.manager.MultiSchemaRegistryManager;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for multi-broker functionality.
 * Tests core logic without attempting real connections.
 */
public class MultiBrokerSimpleTest {

    private MultiBrokerManager brokerManager;
    private MultiSchemaRegistryManager registryManager;

    @BeforeEach
    public void setUp() {
        brokerManager = new MultiBrokerManager();
        registryManager = new MultiSchemaRegistryManager();
    }

    @Test
    public void testBrokerConfigurationStorage() throws KafkaManagementException {
        // Given
        NamedKafkaConfig broker = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("broker-1", "localhost:9092");

        // When
        boolean connected = brokerManager.addBroker(broker);

        // Then
        assertNotNull(brokerManager.getBrokerConfig("broker-1"));
        assertEquals("broker-1", brokerManager.getBrokerConfig("broker-1").getName());
        assertEquals("localhost:9092", brokerManager.getBrokerConfig("broker-1").getBootstrapServers());
        // Connection will fail in test environment, but configuration should be stored
        assertFalse(connected); // Expected to fail in test environment
    }

    @Test
    public void testSchemaRegistryConfigurationStorage() throws KafkaManagementException {
        // Given
        NamedSchemaRegistryConfig registry = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("registry-1", "http://localhost:8081");

        // When
        boolean connected = registryManager.addSchemaRegistry(registry);

        // Then
        assertNotNull(registryManager.getSchemaRegistryConfig("registry-1"));
        assertEquals("registry-1", registryManager.getSchemaRegistryConfig("registry-1").getName());
        assertEquals("http://localhost:8081", registryManager.getSchemaRegistryConfig("registry-1").getSchemaRegistryUrl());
        // Connection will fail in test environment, but configuration should be stored
        assertFalse(connected); // Expected to fail in test environment
    }

    @Test
    public void testDuplicateBrokerNameValidation() throws KafkaManagementException {
        // Given
        NamedKafkaConfig broker1 = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("broker-1", "localhost:9092");
        NamedKafkaConfig broker2 = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("broker-1", "localhost:9093");

        // When
        brokerManager.addBroker(broker1);

        // Then
        KafkaManagementException exception = assertThrows(KafkaManagementException.class, () -> {
            brokerManager.addBroker(broker2);
        });

        assertEquals(ErrorConstants.KAFKA_CONNECTION_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Broker with name 'broker-1' already exists"));
    }

    @Test
    public void testDuplicateSchemaRegistryNameValidation() throws KafkaManagementException {
        // Given
        NamedSchemaRegistryConfig registry1 = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("registry-1", "http://localhost:8081");
        NamedSchemaRegistryConfig registry2 = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("registry-1", "http://localhost:8082");

        // When
        registryManager.addSchemaRegistry(registry1);

        // Then
        KafkaManagementException exception = assertThrows(KafkaManagementException.class, () -> {
            registryManager.addSchemaRegistry(registry2);
        });

        assertEquals(ErrorConstants.SCHEMA_REGISTRY_CONNECTION_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Schema registry with name 'registry-1' already exists"));
    }

    @Test
    public void testBrokerRemoval() throws KafkaManagementException {
        // Given
        NamedKafkaConfig broker = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("broker-1", "localhost:9092");
        brokerManager.addBroker(broker);

        // When
        boolean removed = brokerManager.removeBroker("broker-1");

        // Then
        assertTrue(removed);
        assertNull(brokerManager.getBrokerConfig("broker-1"));
    }

    @Test
    public void testSchemaRegistryRemoval() throws KafkaManagementException {
        // Given
        NamedSchemaRegistryConfig registry = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("registry-1", "http://localhost:8081");
        registryManager.addSchemaRegistry(registry);

        // When
        boolean removed = registryManager.removeSchemaRegistry("registry-1");

        // Then
        assertTrue(removed);
        assertNull(registryManager.getSchemaRegistryConfig("registry-1"));
    }

    @Test
    public void testBrokerConnectionStatusTracking() throws KafkaManagementException {
        // Given
        NamedKafkaConfig broker1 = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("broker-1", "localhost:9092");
        NamedKafkaConfig broker2 = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("broker-2", "localhost:9093");
        
        brokerManager.addBroker(broker1);
        brokerManager.addBroker(broker2);

        // When
        Map<String, Boolean> status = brokerManager.getConnectionStatus();

        // Then
        assertNotNull(status);
        assertEquals(2, status.size());
        assertTrue(status.containsKey("broker-1"));
        assertTrue(status.containsKey("broker-2"));
        // Both should be false in test environment
        assertFalse(status.get("broker-1"));
        assertFalse(status.get("broker-2"));
    }

    @Test
    public void testSchemaRegistryConnectionStatusTracking() throws KafkaManagementException {
        // Given
        NamedSchemaRegistryConfig registry1 = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("registry-1", "http://localhost:8081");
        NamedSchemaRegistryConfig registry2 = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("registry-2", "http://localhost:8082");
        
        registryManager.addSchemaRegistry(registry1);
        registryManager.addSchemaRegistry(registry2);

        // When
        Map<String, Boolean> status = registryManager.getConnectionStatus();

        // Then
        assertNotNull(status);
        assertEquals(2, status.size());
        assertTrue(status.containsKey("registry-1"));
        assertTrue(status.containsKey("registry-2"));
        // Both should be false in test environment
        assertFalse(status.get("registry-1"));
        assertFalse(status.get("registry-2"));
    }

    @Test
    public void testErrorHandling() {
        // Test null configurations
        assertThrows(IllegalArgumentException.class, () -> brokerManager.addBroker(null));
        assertThrows(IllegalArgumentException.class, () -> registryManager.addSchemaRegistry(null));
        
        assertThrows(IllegalArgumentException.class, () -> brokerManager.removeBroker(null));
        assertThrows(IllegalArgumentException.class, () -> registryManager.removeSchemaRegistry(null));
        
        assertThrows(IllegalArgumentException.class, () -> brokerManager.removeBroker(""));
        assertThrows(IllegalArgumentException.class, () -> registryManager.removeSchemaRegistry(""));
    }

    @Test
    public void testNonExistentBrokerOperations() {
        // When
        boolean connected = brokerManager.testConnection("non-existent-broker");
        boolean removed = brokerManager.removeBroker("non-existent-broker");
        NamedKafkaConfig broker = brokerManager.getBrokerConfig("non-existent-broker");

        // Then
        assertFalse(connected);
        assertFalse(removed);
        assertNull(broker);
    }

    @Test
    public void testNonExistentSchemaRegistryOperations() {
        // When
        boolean connected = registryManager.testConnection("non-existent-registry");
        boolean removed = registryManager.removeSchemaRegistry("non-existent-registry");
        NamedSchemaRegistryConfig registry = registryManager.getSchemaRegistryConfig("non-existent-registry");

        // Then
        assertFalse(connected);
        assertFalse(removed);
        assertNull(registry);
    }

    @Test
    public void testCrossCollectionNameValidation() throws KafkaManagementException {
        // Given - Brokers and registries can have the same name (they are separate collections)
        NamedKafkaConfig broker = TestMultiKafkaConfigFactory.createFastTestNamedKafkaConfig("service-1", "localhost:9092");
        NamedSchemaRegistryConfig registry = TestMultiKafkaConfigFactory.createFastTestNamedSchemaRegistryConfig("service-1", "http://localhost:8081");

        // When
        brokerManager.addBroker(broker);
        registryManager.addSchemaRegistry(registry);

        // Then - Should not throw exception
        assertNotNull(brokerManager.getBrokerConfig("service-1"));
        assertNotNull(registryManager.getSchemaRegistryConfig("service-1"));
        assertEquals("service-1", brokerManager.getBrokerConfig("service-1").getName());
        assertEquals("service-1", registryManager.getSchemaRegistryConfig("service-1").getName());
    }

    @Test
    public void testSSLConfiguration() throws KafkaManagementException {
        // Given
        NamedKafkaConfig sslBroker = TestMultiKafkaConfigFactory.createTestBrokerWithSSL("ssl-broker", "localhost:9092");
        NamedSchemaRegistryConfig sslRegistry = TestMultiKafkaConfigFactory.createTestSchemaRegistryWithSSL("ssl-registry", "https://localhost:8081");

        // When
        brokerManager.addBroker(sslBroker);
        registryManager.addSchemaRegistry(sslRegistry);

        // Then
        NamedKafkaConfig retrievedBroker = brokerManager.getBrokerConfig("ssl-broker");
        NamedSchemaRegistryConfig retrievedRegistry = registryManager.getSchemaRegistryConfig("ssl-registry");
        
        assertEquals("SSL", retrievedBroker.getSecurityProtocol());
        assertEquals("/test/path/to/truststore.jks", retrievedBroker.getSslTruststoreLocation());
        assertEquals("test-truststore-password", retrievedBroker.getSslTruststorePassword());
        
        assertEquals("SSL", retrievedRegistry.getSecurityProtocol());
        assertEquals("/test/path/to/truststore.jks", retrievedRegistry.getSslTruststoreLocation());
        assertEquals("test-truststore-password", retrievedRegistry.getSslTruststorePassword());
    }
}
