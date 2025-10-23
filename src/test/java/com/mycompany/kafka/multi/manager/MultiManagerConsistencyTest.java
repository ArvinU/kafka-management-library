package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class demonstrating the consistency between original and multi manager classes.
 * Shows how the improved API makes testing much clearer and more maintainable.
 */
@ExtendWith(MockitoExtension.class)
public class MultiManagerConsistencyTest {
    
    @Mock
    private MultiConnectionFactory multiConnectionFactory;
    
    private MultiManagerTestHelper testHelper;
    private MultiEnhancedMessageManager messageManager;
    private MultiConsumerManager consumerManager;
    private MultiSimpleSchemaManager schemaManager;
    private MultiTopicTypeManager topicTypeManager;
    private MultiSessionManager sessionManager;
    
    @BeforeEach
    void setUp() {
        testHelper = new MultiManagerTestHelper(multiConnectionFactory);
        messageManager = testHelper.getMessageManager();
        consumerManager = testHelper.getConsumerManager();
        schemaManager = testHelper.getSchemaManager();
        topicTypeManager = testHelper.getTopicTypeManager();
        sessionManager = testHelper.getSessionManager();
    }
    
    @Test
    void testMessageSendingConsistency() {
        // Test basic message sending - similar to original MessageManager
        String brokerId = "broker-1";
        String topicName = "test-topic";
        String key = "test-key";
        String value = "test-value";
        
        // This demonstrates the consistent API - same method names as original
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future = messageManager.sendMessage(brokerId, topicName, key, value);
            assertNotNull(future);
        });
        
        // Test overloaded method without key - same as original
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future = messageManager.sendMessage(brokerId, topicName, value);
            assertNotNull(future);
        });
    }
    
    @Test
    void testAvroMessageSendingConsistency() {
        // Test Avro message sending - similar to original MessageManager
        String brokerId = "broker-1";
        String topicName = "avro-topic";
        String key = "avro-key";
        Object value = new Object(); // Mock Avro object
        
        // Same method names as original MessageManager
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future = messageManager.sendAvroMessage(brokerId, topicName, key, value);
            assertNotNull(future);
        });
        
        // Test overloaded method without key
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future = messageManager.sendAvroMessage(brokerId, topicName, value);
            assertNotNull(future);
        });
    }
    
    @Test
    void testJsonSchemaMessageSendingConsistency() {
        // Test JSON Schema message sending - similar to original MessageManager
        String brokerId = "broker-1";
        String topicName = "json-topic";
        String key = "json-key";
        Object value = new Object(); // Mock JSON Schema object
        
        // Same method names as original MessageManager
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future = messageManager.sendJsonSchemaMessage(brokerId, topicName, key, value);
            assertNotNull(future);
        });
        
        // Test overloaded method without key
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future = messageManager.sendJsonSchemaMessage(brokerId, topicName, value);
            assertNotNull(future);
        });
    }
    
    @Test
    void testTopicCreationConsistency() {
        // Test topic creation - similar to original TopicManager
        String brokerId = "broker-1";
        String topicName = "test-topic";
        int numPartitions = 3;
        short replicationFactor = 1;
        
        // Same method names as original TopicManager
        assertDoesNotThrow(() -> {
            topicTypeManager.createTopic(brokerId, topicName, numPartitions, replicationFactor);
        });
        
        // Test overloaded method with default settings
        assertDoesNotThrow(() -> {
            topicTypeManager.createTopic(brokerId, topicName);
        });
    }
    
    @Test
    void testConsumerGroupOperationsConsistency() {
        // Test consumer group operations - similar to original ConsumerManager
        String brokerId = "broker-1";
        String groupId = "test-group";
        
        // Same method names as original ConsumerManager
        assertDoesNotThrow(() -> {
            List<String> groups = consumerManager.listConsumerGroups(brokerId);
            assertNotNull(groups);
        });
        
        assertDoesNotThrow(() -> {
            boolean exists = consumerManager.consumerGroupExists(brokerId, groupId);
            assertFalse(exists); // Should be false for non-existent group
        });
    }
    
    @Test
    void testSchemaOperationsConsistency() {
        // Test schema operations - similar to original SimpleSchemaManager
        String schemaRegistryId = "registry-1";
        String subject = "test-subject";
        String schema = "{\"type\":\"string\"}";
        
        // Same method names as original SimpleSchemaManager
        assertDoesNotThrow(() -> {
            int schemaId = schemaManager.registerSchema(schemaRegistryId, subject, schema);
            assertTrue(schemaId > 0);
        });
        
        assertDoesNotThrow(() -> {
            boolean exists = schemaManager.subjectExists(schemaRegistryId, subject);
            assertNotNull(exists);
        });
    }
    
    @Test
    void testSessionManagementConsistency() {
        // Test session management - similar to original SessionManager
        String brokerId = "broker-1";
        String transactionId = "tx-1";
        String groupId = "test-group";
        
        // Same method names as original SessionManager
        assertDoesNotThrow(() -> {
            // Create transactional producer
            Object producer = sessionManager.createTransactionalProducer(brokerId, transactionId);
            assertNotNull(producer);
        });
        
        assertDoesNotThrow(() -> {
            // Create transactional consumer
            Object consumer = sessionManager.createTransactionalConsumer(brokerId, transactionId, groupId);
            assertNotNull(consumer);
        });
    }
    
    @Test
    void testMultiBrokerOperations() {
        // Test operations across multiple brokers
        List<String> brokerIds = Arrays.asList("broker-1", "broker-2", "broker-3");
        String topicName = "multi-broker-topic";
        String template = "HIGH_THROUGHPUT";
        Map<String, Object> templateParams = new HashMap<>();
        
        // Test creating topics across multiple brokers
        assertDoesNotThrow(() -> {
            topicTypeManager.createTopicAcrossBrokers(brokerIds, topicName, 3, (short) 1, template, templateParams);
        });
        
        // Test sending messages to multiple brokers
        Map<String, String> brokerTopicMap = new HashMap<>();
        brokerTopicMap.put("broker-1", "topic-1");
        brokerTopicMap.put("broker-2", "topic-2");
        brokerTopicMap.put("broker-3", "topic-3");
        
        assertDoesNotThrow(() -> {
            Map<String, List<Future<RecordMetadata>>> results = 
                testHelper.sendTestMessagesToMultipleBrokers(brokerTopicMap, 5);
            assertNotNull(results);
            assertEquals(3, results.size());
        });
    }
    
    @Test
    void testTransactionExecution() {
        // Test transaction execution with automatic cleanup
        String brokerId = "broker-1";
        String transactionId = "tx-1";
        String groupId = "test-group";
        String topicName = "transaction-topic";
        int messageCount = 3;
        
        assertDoesNotThrow(() -> {
            boolean success = testHelper.executeTestTransaction(brokerId, transactionId, groupId, topicName, messageCount);
            assertTrue(success);
        });
    }
    
    @Test
    void testResourceCleanup() {
        // Test resource cleanup
        String brokerId = "broker-1";
        List<String> topicNames = Arrays.asList("topic-1", "topic-2");
        List<String> groupIds = Arrays.asList("group-1", "group-2");
        
        assertDoesNotThrow(() -> {
            testHelper.cleanupTestResources(brokerId, topicNames, groupIds);
        });
    }
    
    @Test
    void testConvenienceMethods() {
        // Test convenience methods that make testing easier
        String brokerId = "broker-1";
        String topicName = "convenience-topic";
        
        // Test helper methods
        assertDoesNotThrow(() -> {
            boolean success = testHelper.createTestTopic(brokerId, topicName);
            assertTrue(success);
        });
        
        assertDoesNotThrow(() -> {
            List<Future<RecordMetadata>> futures = testHelper.sendTestMessages(brokerId, topicName, 3);
            assertNotNull(futures);
            assertEquals(3, futures.size());
        });
    }
    
    @Test
    void testErrorHandlingConsistency() {
        // Test that error handling is consistent with original managers
        String brokerId = "invalid-broker";
        String topicName = "test-topic";
        
        // Should throw KafkaManagementException (same as original managers)
        assertThrows(KafkaManagementException.class, () -> {
            messageManager.sendMessage(brokerId, topicName, "key", "value");
        });
        
        assertThrows(KafkaManagementException.class, () -> {
            topicTypeManager.createTopic(brokerId, topicName);
        });
    }
    
    @Test
    void testMethodOverloadingConsistency() {
        // Test that method overloading works the same as original managers
        String brokerId = "broker-1";
        String topicName = "test-topic";
        String value = "test-value";
        
        // Both methods should work (overloading)
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future1 = messageManager.sendMessage(brokerId, topicName, "key", value);
            assertNotNull(future1);
        });
        
        assertDoesNotThrow(() -> {
            Future<RecordMetadata> future2 = messageManager.sendMessage(brokerId, topicName, value);
            assertNotNull(future2);
        });
    }
}
