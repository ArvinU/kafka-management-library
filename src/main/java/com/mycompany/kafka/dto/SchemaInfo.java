package com.mycompany.kafka.dto;

/**
 * Data Transfer Object for Schema Registry schema information.
 */
public class SchemaInfo {
    
    private int id;
    private int version;
    private String subject;
    private String schema;
    private String type;
    private String compatibility;
    
    public SchemaInfo() {}
    
    public SchemaInfo(int id, int version, String subject, String schema) {
        this.id = id;
        this.version = version;
        this.subject = subject;
        this.schema = schema;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getSchema() { return schema; }
    public void setSchema(String schema) { this.schema = schema; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getCompatibility() { return compatibility; }
    public void setCompatibility(String compatibility) { this.compatibility = compatibility; }
    
    @Override
    public String toString() {
        return "SchemaInfo{" +
                "id=" + id +
                ", version=" + version +
                ", subject='" + subject + '\'' +
                ", type='" + type + '\'' +
                ", compatibility='" + compatibility + '\'' +
                '}';
    }
}
