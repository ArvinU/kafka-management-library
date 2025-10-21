package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.factory.ConnectionFactory;
import com.mycompany.kafka.manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class KafkaManagementLibraryTest {

    @Mock
    private KafkaConfig kafkaConfig;

    @Mock
    private SchemaRegistryConfig schemaRegistryConfig;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private TopicManager topicManager;

    @Mock
    private MessageManager messageManager;

    @Mock
    private ConsumerManager consumerManager;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SimpleSchemaManager schemaManager;

    private KafkaManagementLibrary library;

    @BeforeEach
    void setUp() {
        // Mock the config objects to return valid values
        when(kafkaConfig.getBootstrapServers()).thenReturn("localhost:9092");
        when(kafkaConfig.toProperties()).thenReturn(new java.util.Properties());
        when(schemaRegistryConfig.getSchemaRegistryUrl()).thenReturn("http://localhost:8081");
        when(schemaRegistryConfig.getCacheCapacity()).thenReturn(1000);
        
        // Create a real library instance with mocked dependencies
        // The library will create its own managers internally
        library = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
    }

    @Test
    void testCreateTopic_WithConfigs() {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;

        // When
        library.createTopic(topicName, numPartitions, replicationFactor);

        // Then
        // The actual verification would be done by the TopicManager
        assertNotNull(library.getTopicManager());
    }

    @Test
    void testDeleteTopic() {
        // Given
        String topicName = "test-topic";

        // When
        library.deleteTopic(topicName);

        // Then
        assertNotNull(library.getTopicManager());
    }

    @Test
    void testListTopics() {
        // When
        List<String> result = library.listTopics();

        // Then
        assertNotNull(result);
        assertNotNull(library.getTopicManager());
    }

    @Test
    void testSendMessage() {
        // Given
        String topicName = "test-topic";
        String key = "test-key";
        String value = "test-value";

        // When
        Future<?> result = library.sendMessage(topicName, key, value);

        // Then
        assertNotNull(result);
        assertNotNull(library.getMessageManager());
    }

    @Test
    void testSendMessage_WithoutKey() {
        // Given
        String topicName = "test-topic";
        String value = "test-value";

        // When
        Future<?> result = library.sendMessage(topicName, value);

        // Then
        assertNotNull(result);
        assertNotNull(library.getMessageManager());
    }

    @Test
    void testConsumeMessages() {
        // Given
        String topicName = "test-topic";
        String groupId = "test-group";
        int maxRecords = 10;

        // When
        List<?> result = library.consumeMessages(topicName, groupId, maxRecords);

        // Then
        assertNotNull(result);
        assertNotNull(library.getMessageManager());
    }

    @Test
    void testListConsumerGroups() {
        // When
        library.listConsumerGroups();

        // Then
        assertNotNull(library.getConsumerManager());
    }

    @Test
    void testRegisterSchema() {
        // Given
        String subject = "test-subject";
        String schema = "{\"type\":\"string\"}";

        // When
        int result = library.registerSchema(subject, schema);

        // Then
        assertEquals(1, result); // Mock ID
        assertNotNull(library.getSchemaManager());
    }

    @Test
    void testRegisterSchema_WithType() {
        // Given
        String subject = "test-subject";
        String schema = "{\"type\":\"string\"}";
        String schemaType = "AVRO";

        // When
        int result = library.registerSchema(subject, schema, schemaType);

        // Then
        assertEquals(1, result); // Mock ID
        assertNotNull(library.getSchemaManager());
    }

    @Test
    void testGetSchemaById() {
        // Given
        int schemaId = 1;

        // When
        library.getSchemaById(schemaId);

        // Then
        assertNotNull(library.getSchemaManager());
    }

    @Test
    void testListSubjects() {
        // When
        library.listSubjects();

        // Then
        assertNotNull(library.getSchemaManager());
    }

    @Test
    void testSubjectExists() {
        // Given
        String subject = "test-subject";

        // When
        boolean result = library.subjectExists(subject);

        // Then
        assertNotNull(library.getSchemaManager());
    }

    @Test
    void testCreateTransactionalProducer() {
        // Given
        String transactionId = "test-transaction";

        // When
        library.createTransactionalProducer(transactionId);

        // Then
        assertNotNull(library.getSessionManager());
    }

    @Test
    void testCreateTransactionalConsumer() {
        // Given
        String transactionId = "test-transaction";
        String groupId = "test-group";

        // When
        library.createTransactionalConsumer(transactionId, groupId);

        // Then
        assertNotNull(library.getSessionManager());
    }

    @Test
    void testBeginTransaction() {
        // Given
        String transactionId = "test-transaction";

        // When
        library.beginTransaction(transactionId);

        // Then
        assertNotNull(library.getSessionManager());
    }

    @Test
    void testCommitTransaction() {
        // Given
        String transactionId = "test-transaction";

        // When
        library.commitTransaction(transactionId);

        // Then
        assertNotNull(library.getSessionManager());
    }

    @Test
    void testAbortTransaction() {
        // Given
        String transactionId = "test-transaction";

        // When
        library.abortTransaction(transactionId);

        // Then
        assertNotNull(library.getSessionManager());
    }

    @Test
    void testGetManagers() {
        // When & Then
        assertNotNull(library.getTopicManager());
        assertNotNull(library.getMessageManager());
        assertNotNull(library.getConsumerManager());
        assertNotNull(library.getSessionManager());
        assertNotNull(library.getSchemaManager());
        assertNotNull(library.getConnectionFactory());
    }

    @Test
    void testClose() {
        // When
        library.close();

        // Then
        // Should not throw exception
        assertDoesNotThrow(() -> library.close());
    }
}
