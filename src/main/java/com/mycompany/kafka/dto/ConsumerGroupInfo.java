package com.mycompany.kafka.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for Kafka consumer group information.
 */
public class ConsumerGroupInfo {
    
    private String groupId;
    private String state;
    private String coordinator;
    private String partitionAssignor;
    private List<MemberInfo> members;
    private Map<String, String> metadata;
    
    public ConsumerGroupInfo() {}
    
    public ConsumerGroupInfo(String groupId, String state) {
        this.groupId = groupId;
        this.state = state;
    }
    
    // Getters and Setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCoordinator() { return coordinator; }
    public void setCoordinator(String coordinator) { this.coordinator = coordinator; }
    
    public String getPartitionAssignor() { return partitionAssignor; }
    public void setPartitionAssignor(String partitionAssignor) { this.partitionAssignor = partitionAssignor; }
    
    public List<MemberInfo> getMembers() { return members; }
    public void setMembers(List<MemberInfo> members) { this.members = members; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    @Override
    public String toString() {
        return "ConsumerGroupInfo{" +
                "groupId='" + groupId + '\'' +
                ", state='" + state + '\'' +
                ", coordinator='" + coordinator + '\'' +
                '}';
    }
    
    /**
     * Inner class for consumer group member information.
     */
    public static class MemberInfo {
        private String memberId;
        private String clientId;
        private String host;
        private List<TopicPartitionInfo> assignments;
        
        public MemberInfo() {}
        
        public MemberInfo(String memberId, String clientId, String host) {
            this.memberId = memberId;
            this.clientId = clientId;
            this.host = host;
        }
        
        // Getters and Setters
        public String getMemberId() { return memberId; }
        public void setMemberId(String memberId) { this.memberId = memberId; }
        
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public List<TopicPartitionInfo> getAssignments() { return assignments; }
        public void setAssignments(List<TopicPartitionInfo> assignments) { this.assignments = assignments; }
        
        @Override
        public String toString() {
            return "MemberInfo{" +
                    "memberId='" + memberId + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", host='" + host + '\'' +
                    '}';
        }
    }
    
    /**
     * Inner class for topic partition information.
     */
    public static class TopicPartitionInfo {
        private String topic;
        private int partition;
        
        public TopicPartitionInfo() {}
        
        public TopicPartitionInfo(String topic, int partition) {
            this.topic = topic;
            this.partition = partition;
        }
        
        // Getters and Setters
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        
        public int getPartition() { return partition; }
        public void setPartition(int partition) { this.partition = partition; }
        
        @Override
        public String toString() {
            return "TopicPartitionInfo{" +
                    "topic='" + topic + '\'' +
                    ", partition=" + partition +
                    '}';
        }
    }
}
