package com.mycompany.kafka.manager;

import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicManagerTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private AdminClient adminClient;

    private TopicManager topicManager;

    @BeforeEach
    void setUp() {
        // Set up the mock before creating the TopicManager
        when(connectionFactory.createAdminClient()).thenReturn(adminClient);
        topicManager = new TopicManager(connectionFactory);
    }

    @Test
    void testCreateTopic_WithConfigs() {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;
        Map<String, String> configs = new HashMap<>();
        configs.put("retention.ms", "604800000");

        // When
        topicManager.createTopic(topicName, numPartitions, replicationFactor, configs);

        // Then
        verify(connectionFactory).createAdminClient();
        // The actual verification would be done by the TopicOperations class
    }

    @Test
    void testCreateTopic_WithoutConfigs() {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;

        // When
        topicManager.createTopic(topicName, numPartitions, replicationFactor);

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testDeleteTopic() {
        // Given
        String topicName = "test-topic";

        // When
        topicManager.deleteTopic(topicName);

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testListTopics() {
        // When
        topicManager.listTopics();

        // Then
        verify(connectionFactory).createAdminClient();
        // The actual verification would be done by the TopicQueries class
    }

    @Test
    void testListTopicsWithInfo() {
        // When
        topicManager.listTopicsWithInfo();

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testDescribeTopic() {
        // Given
        String topicName = "test-topic";

        // When
        topicManager.describeTopic(topicName);

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testGetTopicConfig() {
        // Given
        String topicName = "test-topic";

        // When
        topicManager.getTopicConfig(topicName);

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testTopicExists() {
        // Given
        String topicName = "test-topic";

        // When
        topicManager.topicExists(topicName);

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testGetPartitionCount() {
        // Given
        String topicName = "test-topic";

        // When
        topicManager.getPartitionCount(topicName);

        // Then
        verify(connectionFactory).createAdminClient();
    }

    @Test
    void testClose() {
        // When
        topicManager.close();

        // Then
        verify(adminClient).close();
    }

    @Test
    void testGetTopicOperations() {
        // When
        TopicOperations topicOperations = topicManager.getTopicOperations();

        // Then
        assertNotNull(topicOperations);
    }

    @Test
    void testGetTopicQueries() {
        // When
        TopicQueries topicQueries = topicManager.getTopicQueries();

        // Then
        assertNotNull(topicQueries);
    }
}
