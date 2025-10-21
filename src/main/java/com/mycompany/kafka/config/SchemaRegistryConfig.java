package com.mycompany.kafka.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Schema Registry connection settings.
 * Supports both plain and SSL/JKS configurations.
 */
public class SchemaRegistryConfig {
    
    private String schemaRegistryUrl;
    private String securityProtocol = "PLAINTEXT";
    private String sslTruststoreLocation;
    private String sslTruststorePassword;
    private String sslKeystoreLocation;
    private String sslKeystorePassword;
    private String sslKeyPassword;
    private String saslMechanism;
    private String saslJaasConfig;
    private int cacheCapacity = 100;
    private int requestTimeoutMs = 30000;
    private int retries = 3;
    
    public SchemaRegistryConfig(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
    
    public Map<String, Object> toProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("schema.registry.security.protocol", securityProtocol);
        props.put("schema.registry.request.timeout.ms", requestTimeoutMs);
        props.put("schema.registry.retries", retries);
        
        // SSL Configuration
        if ("SSL".equals(securityProtocol) || "SASL_SSL".equals(securityProtocol)) {
            if (sslTruststoreLocation != null) {
                props.put("schema.registry.ssl.truststore.location", sslTruststoreLocation);
                props.put("schema.registry.ssl.truststore.password", sslTruststorePassword);
            }
            if (sslKeystoreLocation != null) {
                props.put("schema.registry.ssl.keystore.location", sslKeystoreLocation);
                props.put("schema.registry.ssl.keystore.password", sslKeystorePassword);
                props.put("schema.registry.ssl.key.password", sslKeyPassword);
            }
        }
        
        // SASL Configuration
        if ("SASL_PLAINTEXT".equals(securityProtocol) || "SASL_SSL".equals(securityProtocol)) {
            if (saslMechanism != null) {
                props.put("schema.registry.sasl.mechanism", saslMechanism);
            }
            if (saslJaasConfig != null) {
                props.put("schema.registry.sasl.jaas.config", saslJaasConfig);
            }
        }
        
        return props;
    }
    
    // Getters and Setters
    public String getSchemaRegistryUrl() { return schemaRegistryUrl; }
    public void setSchemaRegistryUrl(String schemaRegistryUrl) { this.schemaRegistryUrl = schemaRegistryUrl; }
    
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
    
    public int getCacheCapacity() { return cacheCapacity; }
    public void setCacheCapacity(int cacheCapacity) { this.cacheCapacity = cacheCapacity; }
    
    public int getRequestTimeoutMs() { return requestTimeoutMs; }
    public void setRequestTimeoutMs(int requestTimeoutMs) { this.requestTimeoutMs = requestTimeoutMs; }
    
    public int getRetries() { return retries; }
    public void setRetries(int retries) { this.retries = retries; }
}
