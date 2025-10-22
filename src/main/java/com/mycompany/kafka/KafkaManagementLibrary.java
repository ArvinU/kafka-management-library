package com.mycompany.kafka;

import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.factory.ConnectionFactory;
import com.mycompany.kafka.manager.*;
import com.mycompany.kafka.manager.SimpleSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main KafkaManagementLibrary class that orchestrates all managers.
 * Provides a unified interface for managing Kafka brokers and Schema Registry.
 */
public class KafkaManagementLibrary {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaManagementLibrary.class);
    
    private final ConnectionFactory connectionFactory;
    private final TopicManager topicManager;
    private final MessageManager messageManager;
    private final ConsumerManager consumerManager;
    private final SessionManager sessionManager;
    private final SimpleSchemaManager schemaManager;
    private final MessageViewer messageViewer;
    private final SessionViewer sessionViewer;
    private final TopicTypeManager topicTypeManager;
    
    /**
     * Constructor to initialize KafkaManagementLibrary with Kafka and Schema Registry configurations.
     * Validates connections during initialization.
     * 
     * @param kafkaConfig Kafka configuration
     * @param schemaRegistryConfig Schema Registry configuration
     * @throws KafkaManagementException if connection validation fails
     */
    public KafkaManagementLibrary(KafkaConfig kafkaConfig, SchemaRegistryConfig schemaRegistryConfig) throws KafkaManagementException {
        log.info("Initializing KafkaManagementLibrary");
        
        try {
            this.connectionFactory = new ConnectionFactory(kafkaConfig, schemaRegistryConfig);
            
            // Validate connections before initializing managers
            connectionFactory.validateConnections();
            
            this.topicManager = new TopicManager(connectionFactory);
            this.messageManager = new MessageManager(connectionFactory);
            this.consumerManager = new ConsumerManager(connectionFactory);
            this.sessionManager = new SessionManager(connectionFactory);
            this.schemaManager = new SimpleSchemaManager(connectionFactory);
            this.messageViewer = new MessageViewer(connectionFactory);
            this.sessionViewer = new SessionViewer(connectionFactory);
            this.topicTypeManager = new TopicTypeManager(connectionFactory);
            
            log.info("KafkaManagementLibrary initialized successfully");
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            throw new KafkaManagementException(
                ErrorConstants.KAFKA_CONNECTION_FAILED,
                "Failed to initialize KafkaManagementLibrary",
                e);
        }
    }
    
    /**
     * Constructor with simple configuration for basic usage.
     * 
     * @param bootstrapServers Kafka bootstrap servers (e.g., "localhost:9092")
     * @param schemaRegistryUrl Schema Registry URL (e.g., "http://localhost:8081")
     * @throws KafkaManagementException if connection validation fails
     */
    public KafkaManagementLibrary(String bootstrapServers, String schemaRegistryUrl) throws KafkaManagementException {
        this(new KafkaConfig(bootstrapServers), new SchemaRegistryConfig(schemaRegistryUrl));
    }
    
    // Topic Management Methods
    
    /**
     * Creates a new Kafka topic.
     * 
     * Success: Topic is created successfully with specified partitions and replication factor.
     * Failure: Throws KafkaManagementException with error code KML_TOPIC_2001 if topic creation fails,
     * KML_TOPIC_2004 if topic already exists, or KML_VAL_8004/KML_VAL_8005 for invalid parameters.
     * 
     * @param topicName The name of the topic to create
     * @param numPartitions The number of partitions for the topic
     * @param replicationFactor The replication factor for the topic
     * @throws KafkaManagementException if topic creation fails
     */
    public void createTopic(String topicName, int numPartitions, short replicationFactor) throws KafkaManagementException {
        try {
            topicManager.createTopic(topicName, numPartitions, replicationFactor);
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            throw new KafkaManagementException(
                ErrorConstants.TOPIC_CREATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TOPIC_CREATION_FAILED_MSG, topicName, e.getMessage()),
                e);
        }
    }
    
    /**
     * Creates a new Kafka topic with custom configurations.
     * 
     * @param topicName The name of the topic to create
     * @param numPartitions The number of partitions for the topic
     * @param replicationFactor The replication factor for the topic
     * @param configs Additional topic configurations
     */
    public void createTopic(String topicName, int numPartitions, short replicationFactor, java.util.Map<String, String> configs) {
        topicManager.createTopic(topicName, numPartitions, replicationFactor, configs);
    }
    
    /**
     * Deletes a Kafka topic.
     * 
     * @param topicName The name of the topic to delete
     */
    public void deleteTopic(String topicName) {
        topicManager.deleteTopic(topicName);
    }
    
    /**
     * Lists all available topics.
     * 
     * Success: Returns a list of topic names from the Kafka cluster.
     * Failure: Throws KafkaManagementException with error code KML_TOPIC_2005 if listing topics fails.
     * 
     * @return List of topic names
     * @throws KafkaManagementException if listing topics fails
     */
    public java.util.List<String> listTopics() throws KafkaManagementException {
        try {
            return topicManager.listTopics();
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            throw new KafkaManagementException(
                ErrorConstants.TOPIC_LIST_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.TOPIC_LIST_FAILED_MSG, e.getMessage()),
                e);
        }
    }
    
    /**
     * Lists all available topics with their information.
     * 
     * @return List of TopicInfo objects
     */
    public java.util.List<com.mycompany.kafka.dto.TopicInfo> listTopicsWithInfo() {
        return topicManager.listTopicsWithInfo();
    }
    
    /**
     * Describes a specific topic.
     * 
     * @param topicName The name of the topic to describe
     * @return TopicInfo object with topic details
     */
    public com.mycompany.kafka.dto.TopicInfo describeTopic(String topicName) {
        return topicManager.describeTopic(topicName);
    }
    
    /**
     * Checks if a topic exists.
     * 
     * @param topicName The name of the topic to check
     * @return true if topic exists, false otherwise
     */
    public boolean topicExists(String topicName) {
        return topicManager.topicExists(topicName);
    }
    
    // Message Management Methods
    
    /**
     * Sends a message to a Kafka topic.
     * 
     * Success: Message is sent successfully and returns a Future containing RecordMetadata.
     * Failure: Throws KafkaManagementException with error code KML_MSG_3001 if message sending fails,
     * KML_MSG_3003 if serialization fails, or KML_MSG_3008 if operation times out.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value
     * @return Future containing RecordMetadata
     * @throws KafkaManagementException if message sending fails
     */
    public java.util.concurrent.Future<org.apache.kafka.clients.producer.RecordMetadata> sendMessage(String topicName, String key, String value) throws KafkaManagementException {
        try {
            return messageManager.sendMessage(topicName, key, value);
        } catch (Exception e) {
            if (e instanceof KafkaManagementException) {
                throw e;
            }
            throw new KafkaManagementException(
                ErrorConstants.MESSAGE_SEND_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.MESSAGE_SEND_FAILED_MSG, topicName, e.getMessage()),
                e);
        }
    }
    
    /**
     * Sends a message to a Kafka topic without a key.
     * 
     * @param topicName The name of the topic
     * @param value The message value
     * @return Future containing RecordMetadata
     */
    public java.util.concurrent.Future<org.apache.kafka.clients.producer.RecordMetadata> sendMessage(String topicName, String value) {
        return messageManager.sendMessage(topicName, value);
    }
    
    /**
     * Sends an Avro message to a Kafka topic.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value (Avro object)
     * @return Future containing RecordMetadata
     */
    public java.util.concurrent.Future<org.apache.kafka.clients.producer.RecordMetadata> sendAvroMessage(String topicName, String key, Object value) {
        return messageManager.sendAvroMessage(topicName, key, value);
    }
    
    /**
     * Sends a JSON Schema message to a Kafka topic.
     * 
     * @param topicName The name of the topic
     * @param key The message key
     * @param value The message value (JSON Schema object)
     * @return Future containing RecordMetadata
     */
    public java.util.concurrent.Future<org.apache.kafka.clients.producer.RecordMetadata> sendJsonSchemaMessage(String topicName, String key, Object value) {
        return messageManager.sendJsonSchemaMessage(topicName, key, value);
    }
    
    /**
     * Consumes messages from a Kafka topic.
     * 
     * @param topicName The name of the topic
     * @param groupId The consumer group ID
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     */
    public java.util.List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> consumeMessages(String topicName, String groupId, int maxRecords) {
        return messageManager.consumeMessages(topicName, groupId, maxRecords);
    }
    
    /**
     * Consumes Avro messages from a Kafka topic.
     * 
     * @param topicName The name of the topic
     * @param groupId The consumer group ID
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     */
    public java.util.List<org.apache.kafka.clients.consumer.ConsumerRecord<String, Object>> consumeAvroMessages(String topicName, String groupId, int maxRecords) {
        return messageManager.consumeAvroMessages(topicName, groupId, maxRecords);
    }
    
    /**
     * Consumes JSON Schema messages from a Kafka topic.
     * 
     * @param topicName The name of the topic
     * @param groupId The consumer group ID
     * @param maxRecords Maximum number of records to consume
     * @return List of ConsumerRecord objects
     */
    public java.util.List<org.apache.kafka.clients.consumer.ConsumerRecord<String, Object>> consumeJsonSchemaMessages(String topicName, String groupId, int maxRecords) {
        return messageManager.consumeJsonSchemaMessages(topicName, groupId, maxRecords);
    }
    
    // Consumer Group Management Methods
    
    /**
     * Lists all consumer groups.
     * 
     * @return List of consumer group IDs
     */
    public java.util.List<String> listConsumerGroups() {
        return consumerManager.listConsumerGroups();
    }
    
    /**
     * Lists all consumer groups with detailed information.
     * 
     * @return List of ConsumerGroupInfo objects
     */
    public java.util.List<com.mycompany.kafka.dto.ConsumerGroupInfo> listConsumerGroupsWithInfo() {
        return consumerManager.listConsumerGroupsWithInfo();
    }
    
    /**
     * Describes a specific consumer group.
     * 
     * @param groupId The consumer group ID
     * @return ConsumerGroupInfo object with group details
     */
    public com.mycompany.kafka.dto.ConsumerGroupInfo describeConsumerGroup(String groupId) {
        return consumerManager.describeConsumerGroup(groupId);
    }
    
    /**
     * Deletes a consumer group.
     * 
     * @param groupId The consumer group ID to delete
     */
    public void deleteConsumerGroup(String groupId) {
        consumerManager.deleteConsumerGroup(groupId);
    }
    
    // Schema Registry Management Methods
    
    /**
     * Registers a new schema for a subject.
     * 
     * @param subject The subject name
     * @param schema The schema definition
     * @return The schema ID
     */
    public int registerSchema(String subject, String schema) {
        return schemaManager.registerSchema(subject, schema);
    }
    
    /**
     * Registers a new schema for a subject with a specific type.
     * 
     * @param subject The subject name
     * @param schema The schema definition
     * @param schemaType The schema type (AVRO, JSON, PROTOBUF)
     * @return The schema ID
     */
    public int registerSchema(String subject, String schema, String schemaType) {
        return schemaManager.registerSchema(subject, schema);
    }
    
    /**
     * Gets a schema by its ID.
     * 
     * @param schemaId The schema ID
     * @return SchemaInfo object with schema details
     */
    public com.mycompany.kafka.dto.SchemaInfo getSchemaById(int schemaId) {
        return schemaManager.getSchemaById(schemaId);
    }
    
    /**
     * Lists all subjects.
     * 
     * @return List of subject names
     */
    public java.util.List<String> listSubjects() {
        return schemaManager.listSubjects();
    }
    
    /**
     * Checks if a subject exists.
     * 
     * @param subject The subject name to check
     * @return true if subject exists, false otherwise
     */
    public boolean subjectExists(String subject) {
        return schemaManager.subjectExists(subject);
    }
    
    // Transaction Management Methods
    
    /**
     * Creates a transactional producer for a specific transaction ID.
     * 
     * @param transactionId The unique transaction ID
     * @return Producer instance configured for transactions
     */
    public org.apache.kafka.clients.producer.Producer<String, String> createTransactionalProducer(String transactionId) {
        return sessionManager.createTransactionalProducer(transactionId);
    }
    
    /**
     * Creates a transactional consumer for a specific transaction ID.
     * 
     * @param transactionId The unique transaction ID
     * @param groupId The consumer group ID
     * @return Consumer instance configured for transactions
     */
    public org.apache.kafka.clients.consumer.Consumer<String, String> createTransactionalConsumer(String transactionId, String groupId) {
        return sessionManager.createTransactionalConsumer(transactionId, groupId);
    }
    
    /**
     * Begins a transaction for the specified transaction ID.
     * 
     * @param transactionId The transaction ID
     */
    public void beginTransaction(String transactionId) {
        sessionManager.beginTransaction(transactionId);
    }
    
    /**
     * Commits a transaction for the specified transaction ID.
     * 
     * @param transactionId The transaction ID
     */
    public void commitTransaction(String transactionId) {
        sessionManager.commitTransaction(transactionId);
    }
    
    /**
     * Aborts a transaction for the specified transaction ID.
     * 
     * @param transactionId The transaction ID
     */
    public void abortTransaction(String transactionId) {
        sessionManager.abortTransaction(transactionId);
    }
    
    // Manager Access Methods
    
    /**
     * Gets the TopicManager instance.
     * 
     * @return TopicManager instance
     */
    public TopicManager getTopicManager() {
        return topicManager;
    }
    
    /**
     * Gets the MessageManager instance.
     * 
     * @return MessageManager instance
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    /**
     * Gets the ConsumerManager instance.
     * 
     * @return ConsumerManager instance
     */
    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }
    
    /**
     * Gets the SessionManager instance.
     * 
     * @return SessionManager instance
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }
    
    /**
     * Gets the SchemaManager instance.
     * 
     * @return SchemaManager instance
     */
    public SimpleSchemaManager getSchemaManager() {
        return schemaManager;
    }
    
    /**
     * Gets the ConnectionFactory instance.
     * 
     * @return ConnectionFactory instance
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
    
    /**
     * Gets the MessageViewer instance.
     * 
     * @return MessageViewer instance
     */
    public MessageViewer getMessageViewer() {
        return messageViewer;
    }
    
    /**
     * Gets the SessionViewer instance.
     * 
     * @return SessionViewer instance
     */
    public SessionViewer getSessionViewer() {
        return sessionViewer;
    }
    
    /**
     * Gets the TopicTypeManager instance.
     * 
     * @return TopicTypeManager instance
     */
    public TopicTypeManager getTopicTypeManager() {
        return topicTypeManager;
    }
    
    // Additional useful methods
    
    /**
     * Peeks at messages from a topic without consuming them.
     * 
     * @param topicName The name of the topic
     * @param maxRecords Maximum number of records to peek
     * @return List of ConsumerRecord objects
     */
    public java.util.List<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> peekMessages(String topicName, int maxRecords) {
        return messageViewer.peekMessages(topicName, maxRecords);
    }
    
    /**
     * Gets active consumer groups.
     * 
     * @return List of ConsumerGroupInfo objects
     */
    public java.util.List<com.mycompany.kafka.dto.ConsumerGroupInfo> getActiveConsumerGroups() {
        return sessionViewer.getActiveConsumerGroups();
    }
    
    /**
     * Gets session summary information.
     * 
     * @return Map containing session summary
     */
    public java.util.Map<String, Object> getSessionSummary() {
        return sessionViewer.getSessionSummary();
    }
    
    /**
     * Creates a compacted topic.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     */
    public void createCompactedTopic(String topicName, int numPartitions, short replicationFactor) {
        topicTypeManager.createCompactedTopic(topicName, numPartitions, replicationFactor);
    }
    
    /**
     * Creates a high-throughput topic.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     */
    public void createHighThroughputTopic(String topicName, int numPartitions, short replicationFactor) {
        topicTypeManager.createHighThroughputTopic(topicName, numPartitions, replicationFactor);
    }
    
    /**
     * Creates a low-latency topic.
     * 
     * @param topicName The name of the topic
     * @param numPartitions The number of partitions
     * @param replicationFactor The replication factor
     */
    public void createLowLatencyTopic(String topicName, int numPartitions, short replicationFactor) {
        topicTypeManager.createLowLatencyTopic(topicName, numPartitions, replicationFactor);
    }
    
    /**
     * Closes all resources and connections.
     * This method should be called when the KafkaManagementLibrary is no longer needed.
     */
    public void close() {
        log.info("Closing KafkaManagementLibrary");
        
        try {
            if (topicManager != null) {
                topicManager.close();
            }
            if (consumerManager != null) {
                consumerManager.close();
            }
            if (sessionManager != null) {
                sessionManager.closeAll();
            }
            if (messageViewer != null) {
                // MessageViewer doesn't have a close method, but we can add it if needed
            }
            if (sessionViewer != null) {
                sessionViewer.close();
            }
            if (topicTypeManager != null) {
                topicTypeManager.close();
            }
            log.info("KafkaManagementLibrary closed successfully");
        } catch (Exception e) {
            log.error("Error closing KafkaManagementLibrary: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Main method for testing the KafkaManagementLibrary functionality.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Test configuration
        final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
        final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
        final String TEST_TOPIC_NAME = "test-topic";
        final int NUM_PARTITIONS = 3;
        final short REPLICATION_FACTOR = 1;
        
        KafkaManagementLibrary library = null;
        
        try {
            // Initialize the library
            library = new KafkaManagementLibrary(KAFKA_BOOTSTRAP_SERVERS, SCHEMA_REGISTRY_URL);
            
            // Create a test topic
            library.createTopic(TEST_TOPIC_NAME, NUM_PARTITIONS, REPLICATION_FACTOR);
            
            // Send a test message
            library.sendMessage(TEST_TOPIC_NAME, "test-key", "test-value");
            
            // List topics
            java.util.List<String> topics = library.listTopics();
            log.info("Available topics: {}", topics);
            
            log.info("Test completed successfully!");
            
        } catch (Exception e) {
            log.error("Test failed: {}", e.getMessage(), e);
        } finally {
            // Ensure resources are cleaned up
            if (library != null) {
                library.close();
            }
        }
    }
}
