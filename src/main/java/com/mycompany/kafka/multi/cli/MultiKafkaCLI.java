package com.mycompany.kafka.multi.cli;

import com.mycompany.kafka.multi.MultiKafkaManagementLibrary;
import com.mycompany.kafka.multi.config.NamedKafkaConfig;
import com.mycompany.kafka.multi.config.NamedSchemaRegistryConfig;
import com.mycompany.kafka.multi.config.MultiJsonConfigLoader;
import com.mycompany.kafka.dto.ConsumerGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Multi-Broker Command Line Interface for the Kafka Management Library.
 * Provides interactive and non-interactive command execution with multi-broker support.
 */
public class MultiKafkaCLI {
    
    private static final Logger log = LoggerFactory.getLogger(MultiKafkaCLI.class);
    
    private final MultiKafkaManagementLibrary library;
    private final Scanner scanner;
    
    public MultiKafkaCLI(MultiKafkaManagementLibrary library) {
        this.library = library;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Main method for CLI execution.
     */
    public static void main(String[] args) {
        if (args.length == 1 && "--generate-configs".equals(args[0])) {
            generateSampleConfigs();
            return;
        }
        
        if (args.length < 2) {
            showUsage();
            System.exit(1);
        }
        
        try {
            MultiKafkaCLI cli = createCLI(args);
            
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
     * Generates sample multi-broker configuration files.
     */
    private static void generateSampleConfigs() {
        try {
            System.out.println("Generating sample multi-broker configuration files...");
            
            // Create sample multi-kafka-config.json
            String kafkaConfig = "{\n" +
                "  \"brokers\": [\n" +
                "    {\n" +
                "      \"name\": \"broker1\",\n" +
                "      \"bootstrapServers\": \"localhost:9092\",\n" +
                "      \"securityProtocol\": \"PLAINTEXT\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"broker2\",\n" +
                "      \"bootstrapServers\": \"localhost:9093\",\n" +
                "      \"securityProtocol\": \"PLAINTEXT\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
            
            // Create sample multi-schema-registry-config.json
            String schemaRegistryConfig = "{\n" +
                "  \"registries\": [\n" +
                "    {\n" +
                "      \"name\": \"registry1\",\n" +
                "      \"url\": \"http://localhost:8081\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"registry2\",\n" +
                "      \"url\": \"http://localhost:8082\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
            
            // Write files
            java.nio.file.Files.write(java.nio.file.Paths.get("config/multi/multi-kafka-config.json"), kafkaConfig.getBytes());
            java.nio.file.Files.write(java.nio.file.Paths.get("config/multi/multi-schema-registry-config.json"), schemaRegistryConfig.getBytes());
            
            System.out.println("Sample configuration files generated:");
            System.out.println("  - config/multi/multi-kafka-config.json");
            System.out.println("  - config/multi/multi-schema-registry-config.json");
            System.out.println();
            System.out.println("You can now use these files with the CLI:");
            System.out.println("  java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json");
            
        } catch (Exception e) {
            System.err.println("Error generating sample configs: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Creates a CLI instance based on the provided arguments.
     */
    private static MultiKafkaCLI createCLI(String[] args) throws Exception {
        String firstArg = args[0];
        String secondArg = args[1];
        
        if (firstArg.endsWith(".json")) {
            log.info("Loading multi-broker configurations from JSON files: {} and {}", firstArg, secondArg);
            
            List<NamedKafkaConfig> brokers = MultiJsonConfigLoader.loadKafkaConfigs(firstArg);
            List<NamedSchemaRegistryConfig> registries = MultiJsonConfigLoader.loadSchemaRegistryConfigs(secondArg);
            
            MultiKafkaManagementLibrary library = new MultiKafkaManagementLibrary(brokers, registries);
            return new MultiKafkaCLI(library);
        } else {
            throw new IllegalArgumentException("Multi-broker CLI requires JSON configuration files");
        }
    }
    
    /**
     * Shows usage information for the CLI.
     */
    private static void showUsage() {
        System.out.println("Multi-Broker Kafka Management Library CLI");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  Multi-broker mode:");
        System.out.println("    java -jar kafka-management-library.jar <multi-kafka-config.json> <multi-schema-registry-config.json> [command]");
        System.out.println("    Example: java -jar kafka-management-library.jar config/multi/multi-kafka-config.json config/multi/multi-schema-registry-config.json");
        System.out.println();
        System.out.println("  Generate sample configs:");
        System.out.println("    java -jar kafka-management-library.jar --generate-configs");
        System.out.println();
        System.out.println("Multi-Broker Commands:");
        System.out.println("  help                                    - Show help message");
        System.out.println("  brokers list                           - List all brokers and their status");
        System.out.println("  brokers status                         - Show broker connection status");
        System.out.println("  registries list                        - List all schema registries and their status");
        System.out.println("  registries status                      - Show schema registry connection status");
        System.out.println("  topics list <broker-id>                 - List topics on specific broker");
        System.out.println("  topics info <broker-id> <topic-name>   - Get topic information from specific broker");
        System.out.println("  topics create <broker-id> <name> <partitions> <replication> [type] - Create topic on specific broker");
        System.out.println("  topics create-across <broker-ids> <name> <partitions> <replication> <template> - Create topic across multiple brokers");
        System.out.println("  messages send <broker-id> <topic> <key> <value> - Send message to specific broker");
        System.out.println("  messages send-avro <broker-id> <topic> <key> <value> - Send Avro message to specific broker");
        System.out.println("  messages send-json <broker-id> <topic> <key> <value> - Send JSON Schema message to specific broker");
        System.out.println("  messages send-with-schema <broker-id> <topic> <key> <value> <subject> <schema> - Send message with auto schema registration");
        System.out.println("  messages send-with-schema-id <broker-id> <topic> <key> <value> <schema-id> - Send message with specific schema ID");
        System.out.println("  messages peek <broker-id> <topic> [count] - Peek at messages from specific broker");
        System.out.println("  consumers list <broker-id>              - List consumer groups on specific broker");
        System.out.println("  consumers info <broker-id> <group-id>   - Get consumer group information from specific broker");
        System.out.println("  consumers delete <broker-id> <group-id>  - Delete consumer group on specific broker");
        System.out.println("  consumers reset-earliest <broker-id> <group-id> - Reset consumer group offsets to earliest");
        System.out.println("  consumers reset-latest <broker-id> <group-id> - Reset consumer group offsets to latest");
        System.out.println("  consumers lag <broker-id> <group-id>   - Get consumer group lag");
        System.out.println("  sessions create-producer <broker-id> <transaction-id> - Create transactional producer");
        System.out.println("  sessions create-consumer <broker-id> <transaction-id> <group-id> - Create transactional consumer");
        System.out.println("  sessions begin <broker-id> <transaction-id> - Begin transaction");
        System.out.println("  sessions commit <broker-id> <transaction-id> - Commit transaction");
        System.out.println("  sessions abort <broker-id> <transaction-id> - Abort transaction");
        System.out.println("  sessions execute <broker-id> <transaction-id> <group-id> - Execute transaction with automatic cleanup");
        System.out.println("  schemas register <registry-id> <subject> <schema> - Register schema on specific registry");
        System.out.println("  schemas get <registry-id> <schema-id>    - Get schema by ID from specific registry");
        System.out.println("  schemas list <registry-id>               - List subjects on specific registry");
        System.out.println("  test create-topic <broker-id> <topic-name> - Create test topic");
        System.out.println("  test send-messages <broker-id> <topic-name> <count> - Send test messages");
        System.out.println("  test execute-transaction <broker-id> <transaction-id> <group-id> <topic-name> <count> - Execute test transaction");
        System.out.println("  test cleanup <broker-id> <topic-names> <group-ids> - Clean up test resources");
    }
    
    /**
     * Runs the CLI in interactive mode.
     */
    public void runInteractive() {
        System.out.println("Multi-Broker Kafka Management Library CLI");
        System.out.println("Type 'help' for available commands or 'exit' to quit");
        System.out.println("Connected brokers: " + library.getConnectedBrokers());
        System.out.println("Connected registries: " + library.getConnectedSchemaRegistries());
        
        while (true) {
            System.out.print("multi-kafka-cli> ");
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
     */
    public void executeCommand(String command, String[] args) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "help":
                showHelp();
                break;
            case "brokers":
                handleBrokersCommand(parts);
                break;
            case "registries":
                handleRegistriesCommand(parts);
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
            case "schemas":
                handleSchemasCommand(parts);
                break;
            case "test":
                handleTestCommand(parts);
                break;
            default:
                System.out.println("Unknown command: " + cmd);
                System.out.println("Type 'help' for available commands");
        }
    }
    
    private void showHelp() {
        System.out.println("Available Multi-Broker Commands:");
        System.out.println("  brokers list                           - List all brokers and their status");
        System.out.println("  brokers status                         - Show broker connection status");
        System.out.println("  registries list                        - List all schema registries and their status");
        System.out.println("  registries status                      - Show schema registry connection status");
        System.out.println("  topics list <broker-id>                 - List topics on specific broker");
        System.out.println("  topics info <broker-id> <topic-name>   - Get topic information from specific broker");
        System.out.println("  topics create <broker-id> <name> <partitions> <replication> [type] - Create topic on specific broker");
        System.out.println("  topics create-across <broker-ids> <name> <partitions> <replication> <template> - Create topic across multiple brokers");
        System.out.println("  messages send <broker-id> <topic> <key> <value> - Send message to specific broker");
        System.out.println("  messages send-avro <broker-id> <topic> <key> <value> - Send Avro message to specific broker");
        System.out.println("  messages send-json <broker-id> <topic> <key> <value> - Send JSON Schema message to specific broker");
        System.out.println("  messages send-with-schema <broker-id> <topic> <key> <value> <subject> <schema> - Send message with auto schema registration");
        System.out.println("  messages send-with-schema-id <broker-id> <topic> <key> <value> <schema-id> - Send message with specific schema ID");
        System.out.println("  messages peek <broker-id> <topic> [count] - Peek at messages from specific broker");
        System.out.println("  consumers list <broker-id>              - List consumer groups on specific broker");
        System.out.println("  consumers info <broker-id> <group-id>   - Get consumer group information from specific broker");
        System.out.println("  consumers delete <broker-id> <group-id>  - Delete consumer group on specific broker");
        System.out.println("  consumers reset-earliest <broker-id> <group-id> - Reset consumer group offsets to earliest");
        System.out.println("  consumers reset-latest <broker-id> <group-id> - Reset consumer group offsets to latest");
        System.out.println("  consumers lag <broker-id> <group-id>   - Get consumer group lag");
        System.out.println("  sessions create-producer <broker-id> <transaction-id> - Create transactional producer");
        System.out.println("  sessions create-consumer <broker-id> <transaction-id> <group-id> - Create transactional consumer");
        System.out.println("  sessions begin <broker-id> <transaction-id> - Begin transaction");
        System.out.println("  sessions commit <broker-id> <transaction-id> - Commit transaction");
        System.out.println("  sessions abort <broker-id> <transaction-id> - Abort transaction");
        System.out.println("  sessions execute <broker-id> <transaction-id> <group-id> - Execute transaction with automatic cleanup");
        System.out.println("  schemas register <registry-id> <subject> <schema> - Register schema on specific registry");
        System.out.println("  schemas get <registry-id> <schema-id>    - Get schema by ID from specific registry");
        System.out.println("  schemas list <registry-id>               - List subjects on specific registry");
        System.out.println("  test create-topic <broker-id> <topic-name> - Create test topic");
        System.out.println("  test send-messages <broker-id> <topic-name> <count> - Send test messages");
        System.out.println("  test execute-transaction <broker-id> <transaction-id> <group-id> <topic-name> <count> - Execute test transaction");
        System.out.println("  test cleanup <broker-id> <topic-names> <group-ids> - Clean up test resources");
    }
    
    private void handleBrokersCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: brokers <list|status>");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listBrokers();
                break;
            case "status":
                showBrokerStatus();
                break;
            default:
                System.out.println("Unknown brokers subcommand: " + subCommand);
        }
    }
    
    private void handleRegistriesCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: registries <list|status>");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listRegistries();
                break;
            case "status":
                showRegistryStatus();
                break;
            default:
                System.out.println("Unknown registries subcommand: " + subCommand);
        }
    }
    
    private void handleTopicsCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: topics <list|info|create|create-across> [args...]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                if (parts.length < 3) {
                    System.out.println("Usage: topics list <broker-id>");
                    return;
                }
                listTopics(parts[2]);
                break;
            case "info":
                if (parts.length < 4) {
                    System.out.println("Usage: topics info <broker-id> <topic-name>");
                    return;
                }
                getTopicInfo(parts[2], parts[3]);
                break;
            case "create":
                if (parts.length < 6) {
                    System.out.println("Usage: topics create <broker-id> <name> <partitions> <replication> [type]");
                    return;
                }
                createTopic(parts[2], parts[3], Integer.parseInt(parts[4]), Short.parseShort(parts[5]), 
                           parts.length > 6 ? parts[6] : "default");
                break;
            case "create-across":
                if (parts.length < 7) {
                    System.out.println("Usage: topics create-across <broker-ids> <name> <partitions> <replication> <template>");
                    return;
                }
                createTopicAcrossBrokers(parts[2], parts[3], Integer.parseInt(parts[4]), 
                                       Short.parseShort(parts[5]), parts[6]);
                break;
            default:
                System.out.println("Unknown topics subcommand: " + subCommand);
        }
    }
    
    private void handleMessagesCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: messages <send|send-avro|send-json|send-with-schema|send-with-schema-id|peek> [args...]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "send":
                if (parts.length < 6) {
                    System.out.println("Usage: messages send <broker-id> <topic> <key> <value>");
                    return;
                }
                sendMessage(parts[2], parts[3], parts[4], parts[5]);
                break;
            case "send-avro":
                if (parts.length < 6) {
                    System.out.println("Usage: messages send-avro <broker-id> <topic> <key> <value>");
                    return;
                }
                sendAvroMessage(parts[2], parts[3], parts[4], parts[5]);
                break;
            case "send-json":
                if (parts.length < 6) {
                    System.out.println("Usage: messages send-json <broker-id> <topic> <key> <value>");
                    return;
                }
                sendJsonMessage(parts[2], parts[3], parts[4], parts[5]);
                break;
            case "send-with-schema":
                if (parts.length < 8) {
                    System.out.println("Usage: messages send-with-schema <broker-id> <topic> <key> <value> <subject> <schema>");
                    return;
                }
                sendMessageWithSchema(parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
                break;
            case "send-with-schema-id":
                if (parts.length < 7) {
                    System.out.println("Usage: messages send-with-schema-id <broker-id> <topic> <key> <value> <schema-id>");
                    return;
                }
                sendMessageWithSchemaId(parts[2], parts[3], parts[4], parts[5], Integer.parseInt(parts[6]));
                break;
            case "peek":
                if (parts.length < 4) {
                    System.out.println("Usage: messages peek <broker-id> <topic> [count]");
                    return;
                }
                int count = parts.length > 4 ? Integer.parseInt(parts[4]) : 10;
                peekMessages(parts[2], parts[3], count);
                break;
            default:
                System.out.println("Unknown messages subcommand: " + subCommand);
        }
    }
    
    private void handleConsumersCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: consumers <list|info|delete|reset-earliest|reset-latest|lag> [args...]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                if (parts.length < 3) {
                    System.out.println("Usage: consumers list <broker-id>");
                    return;
                }
                listConsumerGroups(parts[2]);
                break;
            case "info":
                if (parts.length < 4) {
                    System.out.println("Usage: consumers info <broker-id> <group-id>");
                    return;
                }
                getConsumerGroupInfo(parts[2], parts[3]);
                break;
            case "delete":
                if (parts.length < 4) {
                    System.out.println("Usage: consumers delete <broker-id> <group-id>");
                    return;
                }
                deleteConsumerGroup(parts[2], parts[3]);
                break;
            case "reset-earliest":
                if (parts.length < 4) {
                    System.out.println("Usage: consumers reset-earliest <broker-id> <group-id>");
                    return;
                }
                resetConsumerGroupOffsetsToEarliest(parts[2], parts[3]);
                break;
            case "reset-latest":
                if (parts.length < 4) {
                    System.out.println("Usage: consumers reset-latest <broker-id> <group-id>");
                    return;
                }
                resetConsumerGroupOffsetsToLatest(parts[2], parts[3]);
                break;
            case "lag":
                if (parts.length < 4) {
                    System.out.println("Usage: consumers lag <broker-id> <group-id>");
                    return;
                }
                getConsumerGroupLag(parts[2], parts[3]);
                break;
            default:
                System.out.println("Unknown consumers subcommand: " + subCommand);
        }
    }
    
    private void handleSessionsCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: sessions <create-producer|create-consumer|begin|commit|abort|execute> [args...]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "create-producer":
                if (parts.length < 4) {
                    System.out.println("Usage: sessions create-producer <broker-id> <transaction-id>");
                    return;
                }
                createTransactionalProducer(parts[2], parts[3]);
                break;
            case "create-consumer":
                if (parts.length < 5) {
                    System.out.println("Usage: sessions create-consumer <broker-id> <transaction-id> <group-id>");
                    return;
                }
                createTransactionalConsumer(parts[2], parts[3], parts[4]);
                break;
            case "begin":
                if (parts.length < 4) {
                    System.out.println("Usage: sessions begin <broker-id> <transaction-id>");
                    return;
                }
                beginTransaction(parts[2], parts[3]);
                break;
            case "commit":
                if (parts.length < 4) {
                    System.out.println("Usage: sessions commit <broker-id> <transaction-id>");
                    return;
                }
                commitTransaction(parts[2], parts[3]);
                break;
            case "abort":
                if (parts.length < 4) {
                    System.out.println("Usage: sessions abort <broker-id> <transaction-id>");
                    return;
                }
                abortTransaction(parts[2], parts[3]);
                break;
            case "execute":
                if (parts.length < 5) {
                    System.out.println("Usage: sessions execute <broker-id> <transaction-id> <group-id>");
                    return;
                }
                executeTransaction(parts[2], parts[3], parts[4]);
                break;
            default:
                System.out.println("Unknown sessions subcommand: " + subCommand);
        }
    }
    
    private void handleSchemasCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: schemas <register|get|list> [args...]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "register":
                if (parts.length < 5) {
                    System.out.println("Usage: schemas register <registry-id> <subject> <schema>");
                    return;
                }
                registerSchema(parts[2], parts[3], parts[4]);
                break;
            case "get":
                if (parts.length < 4) {
                    System.out.println("Usage: schemas get <registry-id> <schema-id>");
                    return;
                }
                getSchemaById(parts[2], Integer.parseInt(parts[3]));
                break;
            case "list":
                if (parts.length < 3) {
                    System.out.println("Usage: schemas list <registry-id>");
                    return;
                }
                listSubjects(parts[2]);
                break;
            default:
                System.out.println("Unknown schemas subcommand: " + subCommand);
        }
    }
    
    private void handleTestCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: test <create-topic|send-messages|execute-transaction|cleanup> [args...]");
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "create-topic":
                if (parts.length < 4) {
                    System.out.println("Usage: test create-topic <broker-id> <topic-name>");
                    return;
                }
                testCreateTopic(parts[2], parts[3]);
                break;
            case "send-messages":
                if (parts.length < 5) {
                    System.out.println("Usage: test send-messages <broker-id> <topic-name> <count>");
                    return;
                }
                testSendMessages(parts[2], parts[3], Integer.parseInt(parts[4]));
                break;
            case "execute-transaction":
                if (parts.length < 7) {
                    System.out.println("Usage: test execute-transaction <broker-id> <transaction-id> <group-id> <topic-name> <count>");
                    return;
                }
                testExecuteTransaction(parts[2], parts[3], parts[4], parts[5], Integer.parseInt(parts[6]));
                break;
            case "cleanup":
                if (parts.length < 5) {
                    System.out.println("Usage: test cleanup <broker-id> <topic-names> <group-ids>");
                    return;
                }
                testCleanup(parts[2], parts[3], parts[4]);
                break;
            default:
                System.out.println("Unknown test subcommand: " + subCommand);
        }
    }
    
    // Implementation methods for all commands
    private void listBrokers() {
        try {
            Map<String, Boolean> status = library.getBrokerConnectionStatus();
            System.out.println("Brokers (" + status.size() + "):");
            for (Map.Entry<String, Boolean> entry : status.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + (entry.getValue() ? "Connected" : "Disconnected"));
            }
        } catch (Exception e) {
            System.err.println("Error listing brokers: " + e.getMessage());
        }
    }
    
    private void showBrokerStatus() {
        try {
            List<String> connected = library.getConnectedBrokers();
            System.out.println("Connected brokers: " + connected);
            System.out.println("Total brokers: " + library.getBrokerCount());
            System.out.println("Connected brokers: " + library.getConnectedBrokerCount());
        } catch (Exception e) {
            System.err.println("Error getting broker status: " + e.getMessage());
        }
    }
    
    private void listRegistries() {
        try {
            Map<String, Boolean> status = library.getSchemaRegistryConnectionStatus();
            System.out.println("Schema Registries (" + status.size() + "):");
            for (Map.Entry<String, Boolean> entry : status.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + (entry.getValue() ? "Connected" : "Disconnected"));
            }
        } catch (Exception e) {
            System.err.println("Error listing registries: " + e.getMessage());
        }
    }
    
    private void showRegistryStatus() {
        try {
            List<String> connected = library.getConnectedSchemaRegistries();
            System.out.println("Connected registries: " + connected);
            System.out.println("Total registries: " + library.getSchemaRegistryCount());
            System.out.println("Connected registries: " + library.getConnectedSchemaRegistryCount());
        } catch (Exception e) {
            System.err.println("Error getting registry status: " + e.getMessage());
        }
    }
    
    private void listTopics(String brokerId) {
        try {
            Set<String> topics = library.getMultiTopicManager().listTopics(brokerId);
            System.out.println("Topics on " + brokerId + " (" + topics.size() + "):");
            for (String topic : topics) {
                System.out.println("  " + topic);
            }
        } catch (Exception e) {
            System.err.println("Error listing topics: " + e.getMessage());
        }
    }
    
    private void getTopicInfo(String brokerId, String topicName) {
        try {
            org.apache.kafka.clients.admin.TopicDescription description = library.getMultiTopicManager().getTopicDescription(brokerId, topicName);
            System.out.println("Topic: " + description.name());
            System.out.println("  Partitions: " + description.partitions().size());
            System.out.println("  Replication Factor: " + description.partitions().get(0).replicas().size());
            System.out.println("  Internal: " + description.isInternal());
        } catch (Exception e) {
            System.err.println("Error getting topic info: " + e.getMessage());
        }
    }
    
    private void createTopic(String brokerId, String name, int partitions, short replication, String type) {
        try {
            if ("default".equals(type)) {
                library.getMultiTopicManager().createTopic(brokerId, name, partitions, replication);
            } else {
                library.getMultiTopicTypeManager().createTopicFromTemplate(brokerId, name, partitions, replication, type, new HashMap<>());
            }
            System.out.println("Topic created: " + name + " on broker: " + brokerId);
        } catch (Exception e) {
            System.err.println("Error creating topic: " + e.getMessage());
        }
    }
    
    private void createTopicAcrossBrokers(String brokerIds, String name, int partitions, short replication, String template) {
        try {
            List<String> brokerIdList = Arrays.asList(brokerIds.split(","));
            library.getMultiTopicTypeManager().createTopicAcrossBrokers(brokerIdList, name, partitions, replication, template, new HashMap<>());
            System.out.println("Topic created across brokers: " + brokerIds);
        } catch (Exception e) {
            System.err.println("Error creating topic across brokers: " + e.getMessage());
        }
    }
    
    private void sendMessage(String brokerId, String topic, String key, String value) {
        try {
            library.getMultiEnhancedMessageManager().sendMessage(brokerId, topic, key, value);
            System.out.println("Message sent to " + topic + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    private void sendAvroMessage(String brokerId, String topic, String key, String value) {
        try {
            library.getMultiEnhancedMessageManager().sendAvroMessage(brokerId, topic, key, value);
            System.out.println("Avro message sent to " + topic + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error sending Avro message: " + e.getMessage());
        }
    }
    
    private void sendJsonMessage(String brokerId, String topic, String key, String value) {
        try {
            library.getMultiEnhancedMessageManager().sendJsonSchemaMessage(brokerId, topic, key, value);
            System.out.println("JSON Schema message sent to " + topic + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error sending JSON Schema message: " + e.getMessage());
        }
    }
    
    private void sendMessageWithSchema(String brokerId, String topic, String key, String value, String subject, String schema) {
        try {
            library.getMultiEnhancedMessageManager().sendMessageWithAutoSchema(brokerId, "default", topic, key, value, subject, schema);
            System.out.println("Message with auto schema registration sent to " + topic + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error sending message with schema: " + e.getMessage());
        }
    }
    
    private void sendMessageWithSchemaId(String brokerId, String topic, String key, String value, int schemaId) {
        try {
            library.getMultiEnhancedMessageManager().sendMessageWithSchemaId(brokerId, topic, key, value, schemaId);
            System.out.println("Message with schema ID sent to " + topic + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error sending message with schema ID: " + e.getMessage());
        }
    }
    
    private void peekMessages(String brokerId, String topic, int count) {
        try {
            // Note: MultiMessageViewer doesn't exist yet, using basic consumer for now
            System.out.println("Peeking messages from " + topic + " on broker " + brokerId + " (count: " + count + ")");
            System.out.println("Note: Message peeking functionality needs to be implemented in MultiMessageViewer");
        } catch (Exception e) {
            System.err.println("Error peeking messages: " + e.getMessage());
        }
    }
    
    private void listConsumerGroups(String brokerId) {
        try {
            List<String> groups = library.getMultiConsumerManager().listConsumerGroups(brokerId);
            System.out.println("Consumer Groups on " + brokerId + " (" + groups.size() + "):");
            for (String group : groups) {
                System.out.println("  " + group);
            }
        } catch (Exception e) {
            System.err.println("Error listing consumer groups: " + e.getMessage());
        }
    }
    
    private void getConsumerGroupInfo(String brokerId, String groupId) {
        try {
            ConsumerGroupInfo info = library.getMultiConsumerManager().describeConsumerGroup(brokerId, groupId);
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
    
    private void deleteConsumerGroup(String brokerId, String groupId) {
        try {
            library.getMultiConsumerManager().deleteConsumerGroup(brokerId, groupId);
            System.out.println("Consumer group deleted: " + groupId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error deleting consumer group: " + e.getMessage());
        }
    }
    
    private void resetConsumerGroupOffsetsToEarliest(String brokerId, String groupId) {
        try {
            library.getMultiConsumerManager().resetConsumerGroupOffsetsToEarliest(brokerId, groupId, Collections.emptyList());
            System.out.println("Consumer group offsets reset to earliest: " + groupId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error resetting consumer group offsets: " + e.getMessage());
        }
    }
    
    private void resetConsumerGroupOffsetsToLatest(String brokerId, String groupId) {
        try {
            library.getMultiConsumerManager().resetConsumerGroupOffsetsToLatest(brokerId, groupId, Collections.emptyList());
            System.out.println("Consumer group offsets reset to latest: " + groupId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error resetting consumer group offsets: " + e.getMessage());
        }
    }
    
    private void getConsumerGroupLag(String brokerId, String groupId) {
        try {
            Map<org.apache.kafka.common.TopicPartition, Long> lag = library.getMultiConsumerManager().getConsumerGroupLag(brokerId, groupId);
            System.out.println("Consumer group lag for " + groupId + " on broker " + brokerId + ":");
            for (Map.Entry<org.apache.kafka.common.TopicPartition, Long> entry : lag.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Error getting consumer group lag: " + e.getMessage());
        }
    }
    
    private void createTransactionalProducer(String brokerId, String transactionId) {
        try {
            library.getMultiSessionManager().createTransactionalProducer(brokerId, transactionId);
            System.out.println("Transactional producer created: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error creating transactional producer: " + e.getMessage());
        }
    }
    
    private void createTransactionalConsumer(String brokerId, String transactionId, String groupId) {
        try {
            library.getMultiSessionManager().createTransactionalConsumer(brokerId, transactionId, groupId);
            System.out.println("Transactional consumer created: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error creating transactional consumer: " + e.getMessage());
        }
    }
    
    private void beginTransaction(String brokerId, String transactionId) {
        try {
            library.getMultiSessionManager().beginTransaction(brokerId, transactionId);
            System.out.println("Transaction begun: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error beginning transaction: " + e.getMessage());
        }
    }
    
    private void commitTransaction(String brokerId, String transactionId) {
        try {
            library.getMultiSessionManager().commitTransaction(brokerId, transactionId);
            System.out.println("Transaction committed: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error committing transaction: " + e.getMessage());
        }
    }
    
    private void abortTransaction(String brokerId, String transactionId) {
        try {
            library.getMultiSessionManager().abortTransaction(brokerId, transactionId);
            System.out.println("Transaction aborted: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error aborting transaction: " + e.getMessage());
        }
    }
    
    private void executeTransaction(String brokerId, String transactionId, String groupId) {
        try {
            library.getMultiSessionManager().executeTransaction(brokerId, transactionId, groupId, (producer, consumer) -> {
                System.out.println("Executing transaction logic...");
                // Transaction logic would go here
            });
            System.out.println("Transaction executed: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error executing transaction: " + e.getMessage());
        }
    }
    
    private void registerSchema(String registryId, String subject, String schema) {
        try {
            int schemaId = library.getMultiSimpleSchemaManager().registerSchema(registryId, subject, schema);
            System.out.println("Schema registered: " + subject + " with ID " + schemaId + " on registry " + registryId);
        } catch (Exception e) {
            System.err.println("Error registering schema: " + e.getMessage());
        }
    }
    
    private void getSchemaById(String registryId, int schemaId) {
        try {
            com.mycompany.kafka.dto.SchemaInfo schemaInfo = library.getMultiSimpleSchemaManager().getSchemaById(registryId, schemaId);
            System.out.println("Schema " + schemaId + " on registry " + registryId + ": " + schemaInfo.getSchema());
        } catch (Exception e) {
            System.err.println("Error getting schema: " + e.getMessage());
        }
    }
    
    private void listSubjects(String registryId) {
        try {
            List<String> subjects = library.getMultiSimpleSchemaManager().listSubjects(registryId);
            System.out.println("Subjects on registry " + registryId + " (" + subjects.size() + "):");
            for (String subject : subjects) {
                System.out.println("  " + subject);
            }
        } catch (Exception e) {
            System.err.println("Error listing subjects: " + e.getMessage());
        }
    }
    
    private void testCreateTopic(String brokerId, String topicName) {
        try {
            library.getMultiTopicManager().createTopic(brokerId, topicName, 3, (short) 1);
            System.out.println("Test topic created: " + topicName + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error creating test topic: " + e.getMessage());
        }
    }
    
    private void testSendMessages(String brokerId, String topicName, int count) {
        try {
            for (int i = 0; i < count; i++) {
                library.getMultiEnhancedMessageManager().sendMessage(brokerId, topicName, "key" + i, "Test message " + i);
            }
            System.out.println("Test messages sent: " + count + " to " + topicName + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error sending test messages: " + e.getMessage());
        }
    }
    
    private void testExecuteTransaction(String brokerId, String transactionId, String groupId, String topicName, int count) {
        try {
            library.getMultiSessionManager().executeTransaction(brokerId, transactionId, groupId, (producer, consumer) -> {
                for (int i = 0; i < count; i++) {
                    library.getMultiEnhancedMessageManager().sendMessage(brokerId, topicName, "tx-key" + i, "Transaction message " + i);
                }
            });
            System.out.println("Test transaction executed: " + transactionId + " on broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error executing test transaction: " + e.getMessage());
        }
    }
    
    private void testCleanup(String brokerId, String topicNames, String groupIds) {
        try {
            List<String> topicNameList = Arrays.asList(topicNames.split(","));
            for (String topicName : topicNameList) {
                library.getMultiTopicManager().deleteTopic(brokerId, topicName);
            }
            System.out.println("Test resources cleaned up for broker " + brokerId);
        } catch (Exception e) {
            System.err.println("Error cleaning up test resources: " + e.getMessage());
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
            if (library != null) {
                library.close();
            }
        } catch (Exception e) {
            log.error("Error closing CLI resources: {}", e.getMessage(), e);
        }
    }
}
