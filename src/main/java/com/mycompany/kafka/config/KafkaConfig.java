package com.mycompany.kafka.config;

import java.util.Properties;

/**
 * Configuration class for Kafka connection settings.
 * Supports both plain and SSL/JKS configurations.
 */
public class KafkaConfig {
    
    private String bootstrapServers;
    private String securityProtocol = "PLAINTEXT";
    private String sslTruststoreLocation;
    private String sslTruststorePassword;
    private String sslKeystoreLocation;
    private String sslKeystorePassword;
    private String sslKeyPassword;
    private String saslMechanism;
    private String saslJaasConfig;
    private String clientId = "kafka-management-library";
    private int requestTimeoutMs = 30000;
    private int retries = 3;
    private boolean enableAutoCommit = true;
    private String autoOffsetReset = "latest";
    
    public KafkaConfig(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
    
    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("security.protocol", securityProtocol);
        props.setProperty("client.id", clientId);
        props.setProperty("request.timeout.ms", String.valueOf(requestTimeoutMs));
        props.setProperty("retries", String.valueOf(retries));
        props.setProperty("enable.auto.commit", String.valueOf(enableAutoCommit));
        props.setProperty("auto.offset.reset", autoOffsetReset);
        
        // SSL Configuration
        if ("SSL".equals(securityProtocol) || "SASL_SSL".equals(securityProtocol)) {
            if (sslTruststoreLocation != null) {
                props.setProperty("ssl.truststore.location", sslTruststoreLocation);
                props.setProperty("ssl.truststore.password", sslTruststorePassword);
            }
            if (sslKeystoreLocation != null) {
                props.setProperty("ssl.keystore.location", sslKeystoreLocation);
                props.setProperty("ssl.keystore.password", sslKeystorePassword);
                props.setProperty("ssl.key.password", sslKeyPassword);
            }
        }
        
        // SASL Configuration
        if ("SASL_PLAINTEXT".equals(securityProtocol) || "SASL_SSL".equals(securityProtocol)) {
            if (saslMechanism != null) {
                props.setProperty("sasl.mechanism", saslMechanism);
            }
            if (saslJaasConfig != null) {
                props.setProperty("sasl.jaas.config", saslJaasConfig);
            }
        }
        
        return props;
    }
    
    // Getters and Setters
    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }
    
    public String getSecurityProtocol() { return securityProtocol; }
    public void setSecurityProtocol(String securityProtocol) { this.securityProtocol = securityProtocol; }
    
    public String getSslTruststoreLocation() { return sslTruststoreLocation; }
    public void setSslTruststoreLocation(String sslTruststoreLocation) { this.sslTruststoreLocation = sslTruststoreLocation; }
    
    public String getSslTruststorePassword() { return sslTruststorePassword; }
    public void setSslTruststorePassword(String sslTruststorePassword) { this.sslTruststorePassword = sslTruststorePassword; }
    
    public String getSslKeystoreLocation() { return sslKeystoreLocation; }
    public void setSslKeystoreLocation(String sslKeystoreLocation) { this.sslKeystoreLocation = sslKeystoreLocation; }
    
    public String getSslKeystorePassword() { return sslKeystorePassword; }
    public void setSslKeystorePassword(String sslKeystorePassword) { this.sslKeystorePassword = sslKeystorePassword; }
    
    public String getSslKeyPassword() { return sslKeyPassword; }
    public void setSslKeyPassword(String sslKeyPassword) { this.sslKeyPassword = sslKeyPassword; }
    
    public String getSaslMechanism() { return saslMechanism; }
    public void setSaslMechanism(String saslMechanism) { this.saslMechanism = saslMechanism; }
    
    public String getSaslJaasConfig() { return saslJaasConfig; }
    public void setSaslJaasConfig(String saslJaasConfig) { this.saslJaasConfig = saslJaasConfig; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public int getRequestTimeoutMs() { return requestTimeoutMs; }
    public void setRequestTimeoutMs(int requestTimeoutMs) { this.requestTimeoutMs = requestTimeoutMs; }
    
    public int getRetries() { return retries; }
    public void setRetries(int retries) { this.retries = retries; }
    
    public boolean isEnableAutoCommit() { return enableAutoCommit; }
    public void setEnableAutoCommit(boolean enableAutoCommit) { this.enableAutoCommit = enableAutoCommit; }
    
    public String getAutoOffsetReset() { return autoOffsetReset; }
    public void setAutoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }
}
