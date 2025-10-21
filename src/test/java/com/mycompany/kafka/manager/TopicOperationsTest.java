package com.mycompany.kafka.manager;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicOperationsTest {

    @Mock
    private AdminClient adminClient;

    @Mock
    private CreateTopicsResult createTopicsResult;

    @Mock
    private DeleteTopicsResult deleteTopicsResult;

    @Mock
    private KafkaFuture<Void> kafkaFuture;

    private TopicOperations topicOperations;

    @BeforeEach
    void setUp() {
        topicOperations = new TopicOperations(adminClient);
    }

    @Test
    void testCreateTopic_Success() throws Exception {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;
        Map<String, String> configs = new HashMap<>();
        configs.put("retention.ms", "604800000");

        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenReturn(null);

        // When
        topicOperations.createTopic(topicName, numPartitions, replicationFactor, configs);

        // Then
        verify(adminClient).createTopics(any());
        verify(createTopicsResult).all();
        verify(kafkaFuture).get();
    }

    @Test
    void testCreateTopic_WithDefaultConfigs() throws Exception {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;

        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenReturn(null);

        // When
        topicOperations.createTopic(topicName, numPartitions, replicationFactor);

        // Then
        verify(adminClient).createTopics(any());
        verify(createTopicsResult).all();
        verify(kafkaFuture).get();
    }

    @Test
    void testCreateTopic_TopicExists() throws Exception {
        // Given
        String topicName = "existing-topic";
        int numPartitions = 3;
        short replicationFactor = 1;

        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenThrow(new ExecutionException("Topic exists", new TopicExistsException("Topic already exists")));

        // When & Then - Should not throw exception for existing topic
        assertDoesNotThrow(() -> topicOperations.createTopic(topicName, numPartitions, replicationFactor));
    }

    @Test
    void testCreateTopic_OtherExecutionException() throws Exception {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;

        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenThrow(new ExecutionException("Other error", new RuntimeException("Other error")));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> topicOperations.createTopic(topicName, numPartitions, replicationFactor));
        assertTrue(exception.getMessage().contains("Failed to create topic"));
    }

    @Test
    void testCreateTopic_InterruptedException() throws Exception {
        // Given
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;

        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenThrow(new InterruptedException("Interrupted"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> topicOperations.createTopic(topicName, numPartitions, replicationFactor));
        assertTrue(exception.getMessage().contains("Thread interrupted"));
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void testDeleteTopic_Success() throws Exception {
        // Given
        String topicName = "test-topic";

        @SuppressWarnings("unchecked")
        java.util.Collection<String> anyCollection = any(java.util.Collection.class);
        when(adminClient.deleteTopics(anyCollection)).thenReturn(deleteTopicsResult);
        when(deleteTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenReturn(null);

        // When
        topicOperations.deleteTopic(topicName);

        // Then
        verify(adminClient).deleteTopics(any(java.util.Collection.class));
        verify(deleteTopicsResult).all();
        verify(kafkaFuture).get();
    }

    @Test
    void testDeleteTopic_ExecutionException() throws Exception {
        // Given
        String topicName = "test-topic";

        @SuppressWarnings("unchecked")
        java.util.Collection<String> anyCollection = any(java.util.Collection.class);
        when(adminClient.deleteTopics(anyCollection)).thenReturn(deleteTopicsResult);
        when(deleteTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenThrow(new ExecutionException("Delete failed", new RuntimeException("Delete failed")));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> topicOperations.deleteTopic(topicName));
        assertTrue(exception.getMessage().contains("Failed to delete topic"));
    }

    @Test
    void testDeleteTopic_InterruptedException() throws Exception {
        // Given
        String topicName = "test-topic";

        @SuppressWarnings("unchecked")
        java.util.Collection<String> anyCollection = any(java.util.Collection.class);
        when(adminClient.deleteTopics(anyCollection)).thenReturn(deleteTopicsResult);
        when(deleteTopicsResult.all()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenThrow(new InterruptedException("Interrupted"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> topicOperations.deleteTopic(topicName));
        assertTrue(exception.getMessage().contains("Thread interrupted"));
        assertTrue(Thread.currentThread().isInterrupted());
    }
}
