#!/bin/bash

# Kafka Management Library CLI Runner
# Usage: 
#   ./run-cli.sh [bootstrap-servers] [schema-registry-url] [command]                    # Single broker mode
#   ./run-cli.sh [kafka-config.json] [schema-registry-config.json] [command]         # Single broker JSON config mode
#   ./run-cli.sh --multi [multi-kafka-config.json] [multi-schema-registry-config.json] [command]  # Multi-broker mode
#   ./run-cli.sh --generate-configs                                                   # Generate single broker configs
#   ./run-cli.sh --generate-multi-configs                                             # Generate multi-broker configs

# Check for special commands
if [ "$1" = "--generate-configs" ]; then
    echo "Generating sample single-broker configuration files..."
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.cli.KafkaCLI --generate-configs
    exit 0
fi

if [ "$1" = "--generate-multi-configs" ]; then
    echo "Generating sample multi-broker configuration files..."
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.multi.cli.MultiKafkaCLI --generate-configs
    exit 0
fi

# Check for multi-broker mode
if [ "$1" = "--multi" ]; then
    MULTI_KAFKA_CONFIG=${2:-"config/multi/multi-kafka-config.json"}
    MULTI_SCHEMA_REGISTRY_CONFIG=${3:-"config/multi/multi-schema-registry-config.json"}
    COMMAND=${4:-""}
    
    # Build the project if needed
    if [ ! -f "target/kafka-management-library-1.0.0.jar" ]; then
        echo "Building the project..."
        mvn clean package -DskipTests
    fi
    
    # Run the Multi-Broker CLI
    if [ -n "$COMMAND" ]; then
        echo "Running Multi-Broker CLI in non-interactive mode with command: $COMMAND"
        java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.multi.cli.MultiKafkaCLI "$MULTI_KAFKA_CONFIG" "$MULTI_SCHEMA_REGISTRY_CONFIG" "$COMMAND"
    else
        echo "Running Multi-Broker CLI in interactive mode"
        java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.multi.cli.MultiKafkaCLI "$MULTI_KAFKA_CONFIG" "$MULTI_SCHEMA_REGISTRY_CONFIG"
    fi
    exit 0
fi

# Single broker mode (default)
# Default values
BOOTSTRAP_SERVERS=${1:-"localhost:9092"}
SCHEMA_REGISTRY_URL=${2:-"http://localhost:8081"}
COMMAND=${3:-""}

# Build the project if needed
if [ ! -f "target/kafka-management-library-1.0.0.jar" ]; then
    echo "Building the project..."
    mvn clean package -DskipTests
fi

# Run the Single-Broker CLI
if [ -n "$COMMAND" ]; then
    echo "Running Single-Broker CLI in non-interactive mode with command: $COMMAND"
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.cli.KafkaCLI "$BOOTSTRAP_SERVERS" "$SCHEMA_REGISTRY_URL" "$COMMAND"
else
    echo "Running Single-Broker CLI in interactive mode"
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.cli.KafkaCLI "$BOOTSTRAP_SERVERS" "$SCHEMA_REGISTRY_URL"
fi
