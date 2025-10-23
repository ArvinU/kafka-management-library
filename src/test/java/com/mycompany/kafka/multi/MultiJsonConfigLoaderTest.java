package com.mycompany.kafka.multi;

import com.mycompany.kafka.multi.config.MultiJsonConfigLoader;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MultiJsonConfigLoader.
 * Tests configuration loading from JSON files.
 */
public class MultiJsonConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoadKafkaConfigs_ValidJson() throws IOException {
        // Given
        String jsonContent = "[\n" +
            "  {\n" +
            "    \"name\": \"broker-1\",\n" +
            "    \"bootstrapServers\": \"localhost:9092\",\n" +
            "    \"securityProtocol\": \"SSL\",\n" +
            "    \"sslTruststoreLocation\": \"/path/to/truststore.jks\",\n" +
            "    \"sslTruststorePassword\": \"truststore-password\",\n" +
            "    \"sslKeystoreLocation\": \"/path/to/keystore.jks\",\n" +
            "    \"sslKeystorePassword\": \"keystore-password\",\n" +
            "    \"sslKeyPassword\": \"key-password\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"broker-2\",\n" +
            "    \"bootstrapServers\": \"localhost:9093\",\n" +
            "    \"securityProtocol\": \"SSL\",\n" +
            "    \"sslTruststoreLocation\": \"/path/to/truststore.jks\",\n" +
            "    \"sslTruststorePassword\": \"truststore-password\",\n" +
            "    \"sslKeystoreLocation\": \"/path/to/keystore.jks\",\n" +
            "    \"sslKeystorePassword\": \"keystore-password\",\n" +
            "    \"sslKeyPassword\": \"key-password\"\n" +
            "  }\n" +
            "]";

        Path configFile = tempDir.resolve("kafka-config.json");
        Files.write(configFile, jsonContent.getBytes());

        // When
        List<NamedKafkaConfig> configs = MultiJsonConfigLoader.loadKafkaConfigs(configFile.toString());

        // Then
        assertNotNull(configs);
        assertEquals(2, configs.size());

        NamedKafkaConfig broker1 = configs.get(0);
        assertEquals("broker-1", broker1.getName());
        assertEquals("localhost:9092", broker1.getBootstrapServers());
        assertEquals("SSL", broker1.getSecurityProtocol());

        NamedKafkaConfig broker2 = configs.get(1);
        assertEquals("broker-2", broker2.getName());
        assertEquals("localhost:9093", broker2.getBootstrapServers());
        assertEquals("SSL", broker2.getSecurityProtocol());
    }

    @Test
    public void testLoadSchemaRegistryConfigs_ValidJson() throws IOException {
        // Given
        String jsonContent = "[\n" +
            "  {\n" +
            "    \"name\": \"registry-1\",\n" +
            "    \"schemaRegistryUrl\": \"https://localhost:8081\",\n" +
            "    \"securityProtocol\": \"SSL\",\n" +
            "    \"sslTruststoreLocation\": \"/path/to/truststore.jks\",\n" +
            "    \"sslTruststorePassword\": \"truststore-password\",\n" +
            "    \"sslKeystoreLocation\": \"/path/to/keystore.jks\",\n" +
            "    \"sslKeystorePassword\": \"keystore-password\",\n" +
            "    \"sslKeyPassword\": \"key-password\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"registry-2\",\n" +
            "    \"schemaRegistryUrl\": \"https://localhost:8082\",\n" +
            "    \"securityProtocol\": \"SSL\",\n" +
            "    \"sslTruststoreLocation\": \"/path/to/truststore.jks\",\n" +
            "    \"sslTruststorePassword\": \"truststore-password\",\n" +
            "    \"sslKeystoreLocation\": \"/path/to/keystore.jks\",\n" +
            "    \"sslKeystorePassword\": \"keystore-password\",\n" +
            "    \"sslKeyPassword\": \"key-password\"\n" +
            "  }\n" +
            "]";

        Path configFile = tempDir.resolve("registry-config.json");
        Files.write(configFile, jsonContent.getBytes());

        // When
        List<NamedSchemaRegistryConfig> configs = MultiJsonConfigLoader.loadSchemaRegistryConfigs(configFile.toString());

        // Then
        assertNotNull(configs);
        assertEquals(2, configs.size());

        NamedSchemaRegistryConfig registry1 = configs.get(0);
        assertEquals("registry-1", registry1.getName());
        assertEquals("https://localhost:8081", registry1.getSchemaRegistryUrl());
        assertEquals("SSL", registry1.getSecurityProtocol());

        NamedSchemaRegistryConfig registry2 = configs.get(1);
        assertEquals("registry-2", registry2.getName());
        assertEquals("https://localhost:8082", registry2.getSchemaRegistryUrl());
        assertEquals("SSL", registry2.getSecurityProtocol());
    }

    @Test
    public void testLoadKafkaConfigs_EmptyArray() throws IOException {
        // Given
        String jsonContent = "[]";
        Path configFile = tempDir.resolve("empty-kafka-config.json");
        Files.write(configFile, jsonContent.getBytes());

        // When
        List<NamedKafkaConfig> configs = MultiJsonConfigLoader.loadKafkaConfigs(configFile.toString());

        // Then
        assertNotNull(configs);
        assertTrue(configs.isEmpty());
    }

    @Test
    public void testLoadSchemaRegistryConfigs_EmptyArray() throws IOException {
        // Given
        String jsonContent = "[]";
        Path configFile = tempDir.resolve("empty-registry-config.json");
        Files.write(configFile, jsonContent.getBytes());

        // When
        List<NamedSchemaRegistryConfig> configs = MultiJsonConfigLoader.loadSchemaRegistryConfigs(configFile.toString());

        // Then
        assertNotNull(configs);
        assertTrue(configs.isEmpty());
    }

    @Test
    public void testLoadKafkaConfigs_InvalidJson_ThrowsException() {
        // Given
        String invalidJson = "invalid json content";

        // When & Then
        assertThrows(IOException.class, () -> {
            MultiJsonConfigLoader.loadKafkaConfigs(invalidJson);
        });
    }

    @Test
    public void testLoadSchemaRegistryConfigs_InvalidJson_ThrowsException() {
        // Given
        String invalidJson = "invalid json content";

        // When & Then
        assertThrows(IOException.class, () -> {
            MultiJsonConfigLoader.loadSchemaRegistryConfigs(invalidJson);
        });
    }

    @Test
    public void testLoadKafkaConfigs_NonExistentFile_ThrowsException() {
        // When & Then
        assertThrows(IOException.class, () -> {
            MultiJsonConfigLoader.loadKafkaConfigs("non-existent-file.json");
        });
    }

    @Test
    public void testLoadSchemaRegistryConfigs_NonExistentFile_ThrowsException() {
        // When & Then
        assertThrows(IOException.class, () -> {
            MultiJsonConfigLoader.loadSchemaRegistryConfigs("non-existent-file.json");
        });
    }

    @Test
    public void testLoadKafkaConfigs_MinimalConfig() throws IOException {
        // Given
        String jsonContent = "[\n" +
            "  {\n" +
            "    \"name\": \"minimal-broker\",\n" +
            "    \"bootstrapServers\": \"localhost:9092\"\n" +
            "  }\n" +
            "]";

        Path configFile = tempDir.resolve("minimal-kafka-config.json");
        Files.write(configFile, jsonContent.getBytes());

        // When
        List<NamedKafkaConfig> configs = MultiJsonConfigLoader.loadKafkaConfigs(configFile.toString());

        // Then
        assertNotNull(configs);
        assertEquals(1, configs.size());

        NamedKafkaConfig broker = configs.get(0);
        assertEquals("minimal-broker", broker.getName());
        assertEquals("localhost:9092", broker.getBootstrapServers());
        // Default values should be set
        assertNotNull(broker.getSecurityProtocol());
    }

    @Test
    public void testLoadSchemaRegistryConfigs_MinimalConfig() throws IOException {
        // Given
        String jsonContent = "[\n" +
            "  {\n" +
            "    \"name\": \"minimal-registry\",\n" +
            "    \"schemaRegistryUrl\": \"http://localhost:8081\"\n" +
            "  }\n" +
            "]";

        Path configFile = tempDir.resolve("minimal-registry-config.json");
        Files.write(configFile, jsonContent.getBytes());

        // When
        List<NamedSchemaRegistryConfig> configs = MultiJsonConfigLoader.loadSchemaRegistryConfigs(configFile.toString());

        // Then
        assertNotNull(configs);
        assertEquals(1, configs.size());

        NamedSchemaRegistryConfig registry = configs.get(0);
        assertEquals("minimal-registry", registry.getName());
        assertEquals("http://localhost:8081", registry.getSchemaRegistryUrl());
        // Default values should be set
        assertNotNull(registry.getSecurityProtocol());
    }
}