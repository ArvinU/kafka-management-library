package com.mycompany.kafka.cli;

import com.mycompany.kafka.KafkaManagementLibrary;
import com.mycompany.kafka.config.KafkaConfig;
import com.mycompany.kafka.config.SchemaRegistryConfig;
import com.mycompany.kafka.config.JsonConfigLoader;
import com.mycompany.kafka.dto.ConsumerGroupInfo;
import com.mycompany.kafka.dto.TopicInfo;
import com.mycompany.kafka.manager.MessageViewer;
import com.mycompany.kafka.manager.SessionViewer;
import com.mycompany.kafka.manager.TopicTypeManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command Line Interface for the Kafka Management Library.
 * Provides interactive and non-interactive command execution.
 */
public class KafkaCLI {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaCLI.class);
    
    private final KafkaManagementLibrary library;
    private final MessageViewer messageViewer;
    private final SessionViewer sessionViewer;
    private final TopicTypeManager topicTypeManager;
    private final Scanner scanner;
    
    public KafkaCLI(String bootstrapServers, String schemaRegistryUrl) {
        this.library = new KafkaManagementLibrary(bootstrapServers, schemaRegistryUrl);
        this.messageViewer = new MessageViewer(library.getConnectionFactory());
        this.sessionViewer = new SessionViewer(library.getConnectionFactory());
        this.topicTypeManager = new TopicTypeManager(library.getConnectionFactory());
        this.scanner = new Scanner(System.in);
    }
    
    public KafkaCLI(KafkaConfig kafkaConfig, SchemaRegistryConfig schemaRegistryConfig) {
        this.library = new KafkaManagementLibrary(kafkaConfig, schemaRegistryConfig);
        this.messageViewer = new MessageViewer(library.getConnectionFactory());
        this.sessionViewer = new SessionViewer(library.getConnectionFactory());
        this.topicTypeManager = new TopicTypeManager(library.getConnectionFactory());
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Main method for CLI execution.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Handle special commands
        if (args.length == 1 && "--generate-configs".equals(args[0])) {
            generateSampleConfigs();
            return;
        }
        
        if (args.length < 2) {
            showUsage();
            System.exit(1);
        }
        
        try {
            KafkaCLI cli = createCLI(args);
            
            if (args.length > 2) {
                // Non-interactive mode
                String command = args[2];
                cli.executeCommand(command, args);
            } else {
                // Interactive mode
                cli.runInteractive();
            }
        } catch (Exception e) {
            log.error("CLI execution failed: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Generates sample configuration files.
     */
    private static void generateSampleConfigs() {
        try {
            System.out.println("Generating sample configuration files...");
            
            JsonConfigLoader.createSampleKafkaConfig("kafka-config.json");
            JsonConfigLoader.createSampleSchemaRegistryConfig("schema-registry-config.json");
            
            System.out.println("Sample configuration files generated:");
            System.out.println("  - kafka-config.json");
            System.out.println("  - schema-registry-config.json");
            System.out.println();
            System.out.println("You can now use these files with the CLI:");
            System.out.println("  java -jar kafka-management-library.jar kafka-config.json schema-registry-config.json");
            
        } catch (Exception e) {
            System.err.println("Error generating sample configs: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Creates a CLI instance based on the provided arguments.
     * Supports both simple connection strings and JSON configuration files.
     * 
     * @param args Command line arguments
     * @return KafkaCLI instance
     * @throws Exception if CLI creation fails
     */
    private static KafkaCLI createCLI(String[] args) throws Exception {
        String firstArg = args[0];
        String secondArg = args[1];
        
        // Check if first argument is a JSON config file
        if (firstArg.endsWith(".json")) {
            if (args.length < 3) {
                throw new IllegalArgumentException("When using JSON config files, you must provide both kafka-config.json and schema-registry-config.json");
            }
            
            String kafkaConfigFile = firstArg;
            String schemaRegistryConfigFile = secondArg;
            
            log.info("Loading configurations from JSON files: {} and {}", kafkaConfigFile, schemaRegistryConfigFile);
            
            KafkaConfig kafkaConfig = JsonConfigLoader.loadKafkaConfig(kafkaConfigFile);
            SchemaRegistryConfig schemaRegistryConfig = JsonConfigLoader.loadSchemaRegistryConfig(schemaRegistryConfigFile);
            
            return new KafkaCLI(kafkaConfig, schemaRegistryConfig);
        } else {
            // Simple connection string mode
            String bootstrapServers = firstArg;
            String schemaRegistryUrl = secondArg;
            
            log.info("Using simple connection strings: {} and {}", bootstrapServers, schemaRegistryUrl);
            return new KafkaCLI(bootstrapServers, schemaRegistryUrl);
        }
    }
    
    /**
     * Shows usage information for the CLI.
     */
    private static void showUsage() {
        System.out.println("Kafka Management Library CLI");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  Simple mode:");
        System.out.println("    java -jar kafka-management-library.jar <bootstrap-servers> <schema-registry-url> [command]");
        System.out.println("    Example: java -jar kafka-management-library.jar localhost:9092 http://localhost:8081");
        System.out.println();
        System.out.println("  JSON config mode:");
        System.out.println("    java -jar kafka-management-library.jar <kafka-config.json> <schema-registry-config.json> [command]");
        System.out.println("    Example: java -jar kafka-management-library.jar kafka-config.json schema-registry-config.json");
        System.out.println();
        System.out.println("  Generate sample configs:");
        System.out.println("    java -jar kafka-management-library.jar --generate-configs");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  help                           - Show help message");
        System.out.println("  topics list                    - List all topics");
        System.out.println("  topics info <topic-name>       - Get topic information");
        System.out.println("  messages peek <topic> [count] - Peek at messages without consuming");
        System.out.println("  consumers list                 - List all consumer groups");
        System.out.println("  consumers info <group-id>     - Get consumer group information");
        System.out.println("  sessions summary               - Get session summary");
        System.out.println("  create-topic <name> <partitions> <replication> [type] - Create a topic");
        System.out.println("  send-message <topic> <key> <value> - Send a message");
        System.out.println("  peek-messages <topic> [count]  - Peek at messages");
        System.out.println("  list-consumers                 - List active consumers");
        System.out.println("  consumer-health <group-id>    - Get consumer group health");
    }
    
    /**
     * Runs the CLI in interactive mode.
     */
    public void runInteractive() {
        System.out.println("Kafka Management Library CLI");
        System.out.println("Type 'help' for available commands or 'exit' to quit");
        
        while (true) {
            System.out.print("kafka-cli> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                break;
            }
            
            try {
                executeCommand(input, new String[]{});
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Executes a command.
     * 
     * @param command The command to execute
     * @param args Additional arguments
     */
    public void executeCommand(String command, String[] args) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "help":
                showHelp();
                break;
            case "topics":
                handleTopicsCommand(parts);
                break;
            case "messages":
                handleMessagesCommand(parts);
                break;
            case "consumers":
                handleConsumersCommand(parts);
                break;
            case "sessions":
                handleSessionsCommand(parts);
                break;
            case "create-topic":
                handleCreateTopicCommand(parts);
                break;
            case "send-message":
                handleSendMessageCommand(parts);
                break;
            case "peek-messages":
                handlePeekMessagesCommand(parts);
                break;
            case "list-consumers":
                handleListConsumersCommand(parts);
                break;
            case "consumer-health":
                handleConsumerHealthCommand(parts);
                break;
            default:
                System.out.println("Unknown command: " + cmd);
                System.out.println("Type 'help' for available commands");
        }
    }
    
    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  help                           - Show this help message");
        System.out.println("  topics list                    - List all topics");
        System.out.println("  topics info <topic-name>       - Get topic information");
        System.out.println("  messages peek <topic> [count] - Peek at messages without consuming");
        System.out.println("  consumers list                 - List all consumer groups");
        System.out.println("  consumers info <group-id>     - Get consumer group information");
        System.out.println("  sessions summary               - Get session summary");
        System.out.println("  create-topic <name> <partitions> <replication> [type] - Create a topic");
        System.out.println("  send-message <topic> <key> <value> - Send a message");
        System.out.println("  peek-messages <topic> [count]  - Peek at messages");
        System.out.println("  list-consumers                 - List active consumers");
        System.out.println("  consumer-health <group-id>    - Get consumer group health");
    }
    
    private void handleTopicsCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: topics <list|info> [topic-name]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listTopics();
                break;
            case "info":
                if (parts.length < 3) {
                    System.out.println("Usage: topics info <topic-name>");
                    return;
                }
                getTopicInfo(parts[2]);
                break;
            default:
                System.out.println("Unknown topics subcommand: " + subCommand);
        }
    }
    
    private void handleMessagesCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: messages peek <topic> [count]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "peek":
                String topic = parts[2];
                int count = parts.length > 3 ? Integer.parseInt(parts[3]) : 10;
                peekMessages(topic, count);
                break;
            default:
                System.out.println("Unknown messages subcommand: " + subCommand);
        }
    }
    
    private void handleConsumersCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: consumers <list|info> [group-id]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listConsumerGroups();
                break;
            case "info":
                if (parts.length < 3) {
                    System.out.println("Usage: consumers info <group-id>");
                    return;
                }
                getConsumerGroupInfo(parts[2]);
                break;
            default:
                System.out.println("Unknown consumers subcommand: " + subCommand);
        }
    }
    
    private void handleSessionsCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: sessions <summary>");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "summary":
                getSessionSummary();
                break;
            default:
                System.out.println("Unknown sessions subcommand: " + subCommand);
        }
    }
    
    private void handleCreateTopicCommand(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: create-topic <name> <partitions> <replication> [type]");
            return;
        }
        
        String name = parts[1];
        int partitions = Integer.parseInt(parts[2]);
        short replication = Short.parseShort(parts[3]);
        String type = parts.length > 4 ? parts[4] : "default";
        
        createTopic(name, partitions, replication, type);
    }
    
    private void handleSendMessageCommand(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: send-message <topic> <key> <value>");
            return;
        }
        
        String topic = parts[1];
        String key = parts[2];
        String value = parts[3];
        
        sendMessage(topic, key, value);
    }
    
    private void handlePeekMessagesCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: peek-messages <topic> [count]");
            return;
        }
        
        String topic = parts[1];
        int count = parts.length > 2 ? Integer.parseInt(parts[2]) : 10;
        
        peekMessages(topic, count);
    }
    
    private void handleListConsumersCommand(String[] parts) {
        listActiveConsumers();
    }
    
    private void handleConsumerHealthCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: consumer-health <group-id>");
            return;
        }
        
        String groupId = parts[1];
        getConsumerGroupHealth(groupId);
    }
    
    private void listTopics() {
        try {
            List<String> topics = library.listTopics();
            System.out.println("Topics (" + topics.size() + "):");
            for (String topic : topics) {
                System.out.println("  " + topic);
            }
        } catch (Exception e) {
            System.err.println("Error listing topics: " + e.getMessage());
        }
    }
    
    private void getTopicInfo(String topicName) {
        try {
            TopicInfo info = library.describeTopic(topicName);
            System.out.println("Topic: " + info.getName());
            System.out.println("  Partitions: " + info.getPartitions());
            System.out.println("  Replication Factor: " + info.getReplicationFactor());
            System.out.println("  Internal: " + info.isInternal());
            System.out.println("  Configs: " + info.getConfigs());
        } catch (Exception e) {
            System.err.println("Error getting topic info: " + e.getMessage());
        }
    }
    
    private void peekMessages(String topic, int count) {
        try {
            List<ConsumerRecord<String, String>> messages = messageViewer.peekMessages(topic, count);
            System.out.println("Messages from " + topic + " (" + messages.size() + "):");
            for (ConsumerRecord<String, String> record : messages) {
                System.out.println("  Key: " + record.key() + ", Value: " + record.value() + 
                                 ", Partition: " + record.partition() + ", Offset: " + record.offset());
            }
        } catch (Exception e) {
            System.err.println("Error peeking messages: " + e.getMessage());
        }
    }
    
    private void listConsumerGroups() {
        try {
            List<String> groups = library.listConsumerGroups();
            System.out.println("Consumer Groups (" + groups.size() + "):");
            for (String group : groups) {
                System.out.println("  " + group);
            }
        } catch (Exception e) {
            System.err.println("Error listing consumer groups: " + e.getMessage());
        }
    }
    
    private void getConsumerGroupInfo(String groupId) {
        try {
            ConsumerGroupInfo info = library.describeConsumerGroup(groupId);
            System.out.println("Consumer Group: " + info.getGroupId());
            System.out.println("  State: " + info.getState());
            System.out.println("  Coordinator: " + info.getCoordinator());
            System.out.println("  Members: " + info.getMembers().size());
            for (ConsumerGroupInfo.MemberInfo member : info.getMembers()) {
                System.out.println("    " + member.getMemberId() + " (" + member.getClientId() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error getting consumer group info: " + e.getMessage());
        }
    }
    
    private void getSessionSummary() {
        try {
            Map<String, Object> summary = sessionViewer.getSessionSummary();
            System.out.println("Session Summary:");
            System.out.println("  Active Groups: " + summary.get("totalActiveGroups"));
            System.out.println("  Active Consumers: " + summary.get("totalActiveConsumers"));
            System.out.println("  Group States: " + summary.get("groupStates"));
            System.out.println("  Consumers by Host: " + summary.get("consumersByHost"));
        } catch (Exception e) {
            System.err.println("Error getting session summary: " + e.getMessage());
        }
    }
    
    private void createTopic(String name, int partitions, short replication, String type) {
        try {
            if ("default".equals(type)) {
                library.createTopic(name, partitions, replication);
            } else {
                Map<String, Object> params = new java.util.HashMap<>();
                topicTypeManager.createTopicFromTemplate(name, partitions, replication, type, params);
            }
            System.out.println("Topic created: " + name);
        } catch (Exception e) {
            System.err.println("Error creating topic: " + e.getMessage());
        }
    }
    
    private void sendMessage(String topic, String key, String value) {
        try {
            library.sendMessage(topic, key, value);
            System.out.println("Message sent to " + topic);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    private void listActiveConsumers() {
        try {
            List<ConsumerGroupInfo.MemberInfo> consumers = sessionViewer.getActiveConsumers();
            System.out.println("Active Consumers (" + consumers.size() + "):");
            for (ConsumerGroupInfo.MemberInfo consumer : consumers) {
                System.out.println("  " + consumer.getMemberId() + " (" + consumer.getClientId() + ") - " + consumer.getHost());
            }
        } catch (Exception e) {
            System.err.println("Error listing active consumers: " + e.getMessage());
        }
    }
    
    private void getConsumerGroupHealth(String groupId) {
        try {
            Map<String, Object> health = sessionViewer.getConsumerGroupHealth(groupId);
            System.out.println("Consumer Group Health: " + groupId);
            System.out.println("  State: " + health.get("state"));
            System.out.println("  Member Count: " + health.get("memberCount"));
            System.out.println("  Healthy: " + health.get("isHealthy"));
            System.out.println("  Health Score: " + health.get("healthScore"));
        } catch (Exception e) {
            System.err.println("Error getting consumer group health: " + e.getMessage());
        }
    }
    
    /**
     * Closes all resources.
     */
    public void close() {
        try {
            if (scanner != null) {
                scanner.close();
            }
            // MessageViewer doesn't have a close method
            if (sessionViewer != null) {
                sessionViewer.close();
            }
            if (topicTypeManager != null) {
                topicTypeManager.close();
            }
            if (library != null) {
                library.close();
            }
        } catch (Exception e) {
            log.error("Error closing CLI resources: {}", e.getMessage(), e);
        }
    }
}
