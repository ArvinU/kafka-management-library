@echo off
REM Kafka Management Library CLI Runner
REM Usage: 
REM   run-cli.bat [bootstrap-servers] [schema-registry-url] [command]
REM   run-cli.bat [kafka-config.json] [schema-registry-config.json] [command]
REM   run-cli.bat --generate-configs

REM Check for special commands
if "%1"=="--generate-configs" (
    echo Generating sample configuration files...
    java -cp "target\kafka-management-library-1.0.0.jar;target\lib\*" com.mycompany.kafka.cli.KafkaCLI --generate-configs
    exit /b 0
)

REM Default values
set BOOTSTRAP_SERVERS=%1
if "%BOOTSTRAP_SERVERS%"=="" set BOOTSTRAP_SERVERS=localhost:9092

set SCHEMA_REGISTRY_URL=%2
if "%SCHEMA_REGISTRY_URL%"=="" set SCHEMA_REGISTRY_URL=http://localhost:8081

set COMMAND=%3

REM Build the project if needed
if not exist "target\kafka-management-library-1.0.0.jar" (
    echo Building the project...
    mvn clean package -DskipTests
)

REM Run the CLI
if not "%COMMAND%"=="" (
    echo Running CLI in non-interactive mode with command: %COMMAND%
    java -cp "target\kafka-management-library-1.0.0.jar;target\lib\*" com.mycompany.kafka.cli.KafkaCLI "%BOOTSTRAP_SERVERS%" "%SCHEMA_REGISTRY_URL%" "%COMMAND%"
) else (
    echo Running CLI in interactive mode
    java -cp "target\kafka-management-library-1.0.0.jar;target\lib\*" com.mycompany.kafka.cli.KafkaCLI "%BOOTSTRAP_SERVERS%" "%SCHEMA_REGISTRY_URL%"
)
