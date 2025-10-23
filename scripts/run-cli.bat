@echo off
REM Kafka Management Library CLI Runner
REM Usage: 
REM   run-cli.bat [bootstrap-servers] [schema-registry-url] [command]                    # Single broker mode
REM   run-cli.bat [kafka-config.json] [schema-registry-config.json] [command]         # Single broker JSON config mode
REM   run-cli.bat --multi [multi-kafka-config.json] [multi-schema-registry-config.json] [command]  # Multi-broker mode
REM   run-cli.bat --generate-configs                                                   # Generate single broker configs
REM   run-cli.bat --generate-multi-configs                                             # Generate multi-broker configs

REM Check for special commands
if "%1"=="--generate-configs" (
    echo Generating sample single-broker configuration files...
    java -cp "target\kafka-management-library-1.1.0.jar;target\lib\*" com.mycompany.kafka.cli.KafkaCLI --generate-configs
    exit /b 0
)

if "%1"=="--generate-multi-configs" (
    echo Generating sample multi-broker configuration files...
    java -cp "target\kafka-management-library-1.1.0.jar;target\lib\*" com.mycompany.kafka.multi.cli.MultiKafkaCLI --generate-configs
    exit /b 0
)

REM Check for multi-broker mode
if "%1"=="--multi" (
    set MULTI_KAFKA_CONFIG=%2
    if "%MULTI_KAFKA_CONFIG%"=="" set MULTI_KAFKA_CONFIG=config\multi\multi-kafka-config.json
    
    set MULTI_SCHEMA_REGISTRY_CONFIG=%3
    if "%MULTI_SCHEMA_REGISTRY_CONFIG%"=="" set MULTI_SCHEMA_REGISTRY_CONFIG=config\multi\multi-schema-registry-config.json
    
    set COMMAND=%4
    
    REM Build the project if needed
    if not exist "target\kafka-management-library-1.1.0.jar" (
        echo Building the project...
        mvn clean package -DskipTests
    )
    
    REM Run the Multi-Broker CLI
    if not "%COMMAND%"=="" (
        echo Running Multi-Broker CLI in non-interactive mode with command: %COMMAND%
        java -cp "target\kafka-management-library-1.1.0.jar;target\lib\*" com.mycompany.kafka.multi.cli.MultiKafkaCLI "%MULTI_KAFKA_CONFIG%" "%MULTI_SCHEMA_REGISTRY_CONFIG%" "%COMMAND%"
    ) else (
        echo Running Multi-Broker CLI in interactive mode
        java -cp "target\kafka-management-library-1.1.0.jar;target\lib\*" com.mycompany.kafka.multi.cli.MultiKafkaCLI "%MULTI_KAFKA_CONFIG%" "%MULTI_SCHEMA_REGISTRY_CONFIG%"
    )
    exit /b 0
)

REM Single broker mode (default)
REM Default values
set BOOTSTRAP_SERVERS=%1
if "%BOOTSTRAP_SERVERS%"=="" set BOOTSTRAP_SERVERS=localhost:9092

set SCHEMA_REGISTRY_URL=%2
if "%SCHEMA_REGISTRY_URL%"=="" set SCHEMA_REGISTRY_URL=http://localhost:8081

set COMMAND=%3

REM Build the project if needed
if not exist "target\kafka-management-library-1.1.0.jar" (
    echo Building the project...
    mvn clean package -DskipTests
)

REM Run the Single-Broker CLI
if not "%COMMAND%"=="" (
    echo Running Single-Broker CLI in non-interactive mode with command: %COMMAND%
    java -cp "target\kafka-management-library-1.1.0.jar;target\lib\*" com.mycompany.kafka.cli.KafkaCLI "%BOOTSTRAP_SERVERS%" "%SCHEMA_REGISTRY_URL%" "%COMMAND%"
) else (
    echo Running Single-Broker CLI in interactive mode
    java -cp "target\kafka-management-library-1.1.0.jar;target\lib\*" com.mycompany.kafka.cli.KafkaCLI "%BOOTSTRAP_SERVERS%" "%SCHEMA_REGISTRY_URL%"
)
