package com.mycompany.kafka.manager;

import com.mycompany.kafka.dto.TopicInfo;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicQueriesTest {

    @Mock
    private AdminClient adminClient;

    @Mock
    private ListTopicsResult listTopicsResult;

    @Mock
    private DescribeTopicsResult describeTopicsResult;

    @Mock
    private DescribeConfigsResult describeConfigsResult;

    @Mock
    private KafkaFuture<Set<String>> topicsFuture;

    @Mock
    private KafkaFuture<Map<String, TopicDescription>> describeFuture;

    @Mock
    private KafkaFuture<Map<ConfigResource, Config>> configsFuture;

    private TopicQueries topicQueries;

    @BeforeEach
    void setUp() {
        topicQueries = new TopicQueries(adminClient);
    }

    @Test
    void testListTopics_Success() throws Exception {
        // Given
        Set<String> topicNames = new HashSet<>(Arrays.asList("topic1", "topic2", "topic3"));
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(topicsFuture);
        when(topicsFuture.get()).thenReturn(topicNames);

        // When
        List<String> result = topicQueries.listTopics();

        // Then
        assertEquals(3, result.size());
        assertTrue(result.contains("topic1"));
        assertTrue(result.contains("topic2"));
        assertTrue(result.contains("topic3"));
        verify(adminClient).listTopics();
        verify(listTopicsResult).names();
        verify(topicsFuture).get();
    }

    @Test
    void testListTopics_ExecutionException() throws Exception {
        // Given
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(topicsFuture);
        when(topicsFuture.get()).thenThrow(new ExecutionException("List failed", new RuntimeException("List failed")));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> topicQueries.listTopics());
        assertTrue(exception.getMessage().contains("Failed to list topics"));
    }

    @Test
    void testListTopics_InterruptedException() throws Exception {
        // Given
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(topicsFuture);
        when(topicsFuture.get()).thenThrow(new InterruptedException("Interrupted"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> topicQueries.listTopics());
        assertTrue(exception.getMessage().contains("Thread interrupted"));
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void testListTopicsWithInfo_Success() throws Exception {
        // Given
        Set<String> topicNames = new HashSet<>(Arrays.asList("topic1"));
        TopicDescription topicDescription = createMockTopicDescription("topic1", 3, 1, false);
        Map<String, TopicDescription> descriptions = new HashMap<>();
        descriptions.put("topic1", topicDescription);

        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(topicsFuture);
        when(topicsFuture.get()).thenReturn(topicNames);
        @SuppressWarnings("unchecked")
        java.util.Collection<String> anyCollection = any(java.util.Collection.class);
        when(adminClient.describeTopics(anyCollection)).thenReturn(describeTopicsResult);
        when(describeTopicsResult.allTopicNames()).thenReturn(describeFuture);
        when(describeFuture.get()).thenReturn(descriptions);

        // When
        List<TopicInfo> result = topicQueries.listTopicsWithInfo();

        // Then
        assertEquals(1, result.size());
        TopicInfo topicInfo = result.get(0);
        assertEquals("topic1", topicInfo.getName());
        assertEquals(3, topicInfo.getPartitions());
        assertEquals(1, topicInfo.getReplicationFactor());
        assertFalse(topicInfo.isInternal());
    }

    @Test
    void testDescribeTopic_Success() throws Exception {
        // Given
        String topicName = "test-topic";
        TopicDescription topicDescription = createMockTopicDescription(topicName, 3, 1, false);
        Map<String, TopicDescription> descriptions = new HashMap<>();
        descriptions.put(topicName, topicDescription);

        @SuppressWarnings("unchecked")
        java.util.Collection<String> anyCollection = any(java.util.Collection.class);
        when(adminClient.describeTopics(anyCollection)).thenReturn(describeTopicsResult);
        when(describeTopicsResult.allTopicNames()).thenReturn(describeFuture);
        when(describeFuture.get()).thenReturn(descriptions);

        // When
        TopicInfo result = topicQueries.describeTopic(topicName);

        // Then
        assertEquals(topicName, result.getName());
        assertEquals(3, result.getPartitions());
        assertEquals(1, result.getReplicationFactor());
        assertFalse(result.isInternal());
    }

    @Test
    void testTopicExists_Exists() throws Exception {
        // Given
        String topicName = "existing-topic";
        Set<String> topicNames = new HashSet<>(Arrays.asList("existing-topic", "other-topic"));
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(topicsFuture);
        when(topicsFuture.get()).thenReturn(topicNames);

        // When
        boolean result = topicQueries.topicExists(topicName);

        // Then
        assertTrue(result);
    }

    @Test
    void testTopicExists_NotExists() throws Exception {
        // Given
        String topicName = "non-existing-topic";
        Set<String> topicNames = new HashSet<>(Arrays.asList("existing-topic", "other-topic"));
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(topicsFuture);
        when(topicsFuture.get()).thenReturn(topicNames);

        // When
        boolean result = topicQueries.topicExists(topicName);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetPartitionCount_Success() throws Exception {
        // Given
        String topicName = "test-topic";
        TopicDescription topicDescription = createMockTopicDescription(topicName, 5, 1, false);
        Map<String, TopicDescription> descriptions = new HashMap<>();
        descriptions.put(topicName, topicDescription);

        @SuppressWarnings("unchecked")
        java.util.Collection<String> anyCollection = any(java.util.Collection.class);
        when(adminClient.describeTopics(anyCollection)).thenReturn(describeTopicsResult);
        when(describeTopicsResult.allTopicNames()).thenReturn(describeFuture);
        when(describeFuture.get()).thenReturn(descriptions);

        // When
        int result = topicQueries.getPartitionCount(topicName);

        // Then
        assertEquals(5, result);
    }

    @Test
    void testGetTopicConfig_Success() throws Exception {
        // Given
        String topicName = "test-topic";
        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
        Config config = mock(Config.class);
        ConfigEntry entry1 = new ConfigEntry("retention.ms", "604800000");
        ConfigEntry entry2 = new ConfigEntry("compression.type", "snappy");
        List<ConfigEntry> entries = Arrays.asList(entry1, entry2);
        
        Map<ConfigResource, Config> configs = new HashMap<>();
        configs.put(configResource, config);

        when(adminClient.describeConfigs(any())).thenReturn(describeConfigsResult);
        when(describeConfigsResult.all()).thenReturn(configsFuture);
        when(configsFuture.get()).thenReturn(configs);
        when(config.entries()).thenReturn(entries);

        // When
        Map<String, String> result = topicQueries.getTopicConfig(topicName);

        // Then
        assertEquals(2, result.size());
        assertEquals("604800000", result.get("retention.ms"));
        assertEquals("snappy", result.get("compression.type"));
    }

    private TopicDescription createMockTopicDescription(String name, int partitions, int replicationFactor, boolean isInternal) {
        TopicDescription description = mock(TopicDescription.class);
        lenient().when(description.name()).thenReturn(name);
        lenient().when(description.isInternal()).thenReturn(isInternal);
        
        List<TopicPartitionInfo> partitionInfos = new ArrayList<>();
        for (int i = 0; i < partitions; i++) {
            TopicPartitionInfo partitionInfo = mock(TopicPartitionInfo.class);
            lenient().when(partitionInfo.partition()).thenReturn(i);
            
            List<Node> replicas = new ArrayList<>();
            for (int j = 0; j < replicationFactor; j++) {
                Node node = mock(Node.class);
                lenient().when(node.id()).thenReturn(j);
                replicas.add(node);
            }
            lenient().when(partitionInfo.replicas()).thenReturn(replicas);
            partitionInfos.add(partitionInfo);
        }
        
        lenient().when(description.partitions()).thenReturn(partitionInfos);
        return description;
    }
}
