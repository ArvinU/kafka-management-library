package com.mycompany.kafka.dto;

import java.util.Map;

/**
 * Data Transfer Object for Kafka topic information.
 */
public class TopicInfo {
    
    private String name;
    private int partitions;
    private short replicationFactor;
    private Map<String, String> configs;
    private boolean internal;
    
    public TopicInfo() {}
    
    public TopicInfo(String name, int partitions, short replicationFactor) {
        this.name = name;
        this.partitions = partitions;
        this.replicationFactor = replicationFactor;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }
    
    public short getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(short replicationFactor) { this.replicationFactor = replicationFactor; }
    
    public Map<String, String> getConfigs() { return configs; }
    public void setConfigs(Map<String, String> configs) { this.configs = configs; }
    
    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }
    
    @Override
    public String toString() {
        return "TopicInfo{" +
                "name='" + name + '\'' +
                ", partitions=" + partitions +
                ", replicationFactor=" + replicationFactor +
                ", internal=" + internal +
                '}';
    }
}
