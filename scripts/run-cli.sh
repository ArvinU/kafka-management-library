#!/bin/bash

# Kafka Management Library CLI Runner
# Usage: 
#   ./run-cli.sh [bootstrap-servers] [schema-registry-url] [command]
#   ./run-cli.sh [kafka-config.json] [schema-registry-config.json] [command]
#   ./run-cli.sh --generate-configs

# Check for special commands
if [ "$1" = "--generate-configs" ]; then
    echo "Generating sample configuration files..."
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.cli.KafkaCLI --generate-configs
    exit 0
fi

# Default values
BOOTSTRAP_SERVERS=${1:-"localhost:9092"}
SCHEMA_REGISTRY_URL=${2:-"http://localhost:8081"}
COMMAND=${3:-""}

# Build the project if needed
if [ ! -f "target/kafka-management-library-1.0.0.jar" ]; then
    echo "Building the project..."
    mvn clean package -DskipTests
fi

# Run the CLI
if [ -n "$COMMAND" ]; then
    echo "Running CLI in non-interactive mode with command: $COMMAND"
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.cli.KafkaCLI "$BOOTSTRAP_SERVERS" "$SCHEMA_REGISTRY_URL" "$COMMAND"
else
    echo "Running CLI in interactive mode"
    java -cp "target/kafka-management-library-1.0.0.jar:target/lib/*" com.mycompany.kafka.cli.KafkaCLI "$BOOTSTRAP_SERVERS" "$SCHEMA_REGISTRY_URL"
fi
