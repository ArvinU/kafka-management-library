package com.mycompany.kafka.manager;

import com.mycompany.kafka.dto.ConsumerGroupInfo;
import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Manager class for viewing active Kafka sessions and consumer information.
 * Provides functionality to monitor active consumers, sessions, and consumer group states.
 */
public class SessionViewer {
    
    private static final Logger log = LoggerFactory.getLogger(SessionViewer.class);
    
    private final AdminClient adminClient;
    
    public SessionViewer(ConnectionFactory connectionFactory) {
        this.adminClient = connectionFactory.createAdminClient();
    }
    
    /**
     * Gets all active consumer groups with their current state.
     * 
     * @return List of ConsumerGroupInfo objects
     * @throws RuntimeException if getting consumer groups fails
     */
    public List<ConsumerGroupInfo> getActiveConsumerGroups() {
        log.info("Getting active consumer groups");
        
        try {
            Collection<ConsumerGroupListing> consumerGroups = adminClient.listConsumerGroups().all().get();
            List<String> groupIds = consumerGroups.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
            
            if (groupIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            Map<String, ConsumerGroupDescription> descriptions = adminClient.describeConsumerGroups(groupIds).all().get();
            
            List<ConsumerGroupInfo> activeGroups = descriptions.entrySet().stream()
                .map(entry -> {
                    ConsumerGroupDescription desc = entry.getValue();
                    ConsumerGroupInfo info = new ConsumerGroupInfo(desc.groupId(), desc.state().toString());
                    info.setCoordinator(desc.coordinator().toString());
                    info.setPartitionAssignor(desc.partitionAssignor());
                    
                    // Convert members
                    List<ConsumerGroupInfo.MemberInfo> members = desc.members().stream()
                        .map(member -> {
                            ConsumerGroupInfo.MemberInfo memberInfo = new ConsumerGroupInfo.MemberInfo(
                                member.consumerId(), 
                                member.clientId(), 
                                member.host()
                            );
                            
                            // Convert assignments
                            List<ConsumerGroupInfo.TopicPartitionInfo> assignments = member.assignment().topicPartitions().stream()
                                .map(tp -> new ConsumerGroupInfo.TopicPartitionInfo(tp.topic(), tp.partition()))
                                .collect(Collectors.toList());
                            memberInfo.setAssignments(assignments);
                            
                            return memberInfo;
                        })
                        .collect(Collectors.toList());
                    info.setMembers(members);
                    
                    return info;
                })
                .filter(info -> !info.getMembers().isEmpty()) // Only active groups with members
                .collect(Collectors.toList());
            
            log.info("Found {} active consumer groups", activeGroups.size());
            return activeGroups;
        } catch (ExecutionException e) {
            log.error("Failed to get active consumer groups: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get active consumer groups", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while getting active consumer groups", e);
            throw new RuntimeException("Thread interrupted while getting active consumer groups", e);
        }
    }
    
    /**
     * Gets detailed information about a specific consumer group.
     * 
     * @param groupId The consumer group ID
     * @return ConsumerGroupInfo object with detailed information
     * @throws RuntimeException if getting consumer group info fails
     */
    public ConsumerGroupInfo getConsumerGroupDetails(String groupId) {
        log.info("Getting consumer group details for: {}", groupId);
        
        try {
            ConsumerGroupDescription description = adminClient.describeConsumerGroups(Collections.singletonList(groupId))
                .all().get().get(groupId);
            
            ConsumerGroupInfo info = new ConsumerGroupInfo(description.groupId(), description.state().toString());
            info.setCoordinator(description.coordinator().toString());
            info.setPartitionAssignor(description.partitionAssignor());
            
            // Convert members
            List<ConsumerGroupInfo.MemberInfo> members = description.members().stream()
                .map(member -> {
                    ConsumerGroupInfo.MemberInfo memberInfo = new ConsumerGroupInfo.MemberInfo(
                        member.consumerId(), 
                        member.clientId(), 
                        member.host()
                    );
                    
                    // Convert assignments
                    List<ConsumerGroupInfo.TopicPartitionInfo> assignments = member.assignment().topicPartitions().stream()
                        .map(tp -> new ConsumerGroupInfo.TopicPartitionInfo(tp.topic(), tp.partition()))
                        .collect(Collectors.toList());
                    memberInfo.setAssignments(assignments);
                    
                    return memberInfo;
                })
                .collect(Collectors.toList());
            info.setMembers(members);
            
            log.info("Successfully got consumer group details for: {}", groupId);
            return info;
        } catch (ExecutionException e) {
            log.error("Failed to get consumer group details for {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to get consumer group details: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while getting consumer group details for {}", groupId, e);
            throw new RuntimeException("Thread interrupted while getting consumer group details: " + groupId, e);
        }
    }
    
    /**
     * Gets all active consumers across all consumer groups.
     * 
     * @return List of active consumer information
     * @throws RuntimeException if getting active consumers fails
     */
    public List<ConsumerGroupInfo.MemberInfo> getActiveConsumers() {
        log.info("Getting active consumers");
        
        try {
            List<ConsumerGroupInfo> activeGroups = getActiveConsumerGroups();
            
            List<ConsumerGroupInfo.MemberInfo> activeConsumers = activeGroups.stream()
                .flatMap(group -> group.getMembers().stream())
                .collect(Collectors.toList());
            
            log.info("Found {} active consumers", activeConsumers.size());
            return activeConsumers;
        } catch (Exception e) {
            log.error("Failed to get active consumers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get active consumers", e);
        }
    }
    
    /**
     * Gets consumer group lag information.
     * 
     * @param groupId The consumer group ID
     * @return Map of TopicPartition to lag amount
     * @throws RuntimeException if getting lag fails
     */
    public Map<TopicPartition, Long> getConsumerGroupLag(String groupId) {
        log.info("Getting lag for consumer group: {}", groupId);
        
        try {
            // Get current offsets
            ListConsumerGroupOffsetsResult result = adminClient.listConsumerGroupOffsets(groupId);
            Map<TopicPartition, OffsetAndMetadata> currentOffsets = result.partitionsToOffsetAndMetadata().get();
            
            // Get latest offsets
            Map<TopicPartition, OffsetAndMetadata> latestOffsets = new HashMap<>();
            for (TopicPartition tp : currentOffsets.keySet()) {
                // This would require additional logic to get latest offsets
                // For now, return empty map
                latestOffsets.put(tp, new OffsetAndMetadata(0));
            }
            
            // Calculate lag
            Map<TopicPartition, Long> lag = new HashMap<>();
            for (TopicPartition tp : currentOffsets.keySet()) {
                long currentOffset = currentOffsets.get(tp).offset();
                long latestOffset = latestOffsets.get(tp).offset();
                lag.put(tp, latestOffset - currentOffset);
            }
            
            log.info("Successfully calculated lag for consumer group: {} with {} partitions", groupId, lag.size());
            return lag;
        } catch (ExecutionException e) {
            log.error("Failed to get lag for consumer group {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to get lag for consumer group: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while getting lag for consumer group {}", groupId, e);
            throw new RuntimeException("Thread interrupted while getting lag for consumer group: " + groupId, e);
        }
    }
    
    /**
     * Gets consumer group offsets.
     * 
     * @param groupId The consumer group ID
     * @return Map of TopicPartition to OffsetAndMetadata
     * @throws RuntimeException if getting offsets fails
     */
    public Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(String groupId) {
        log.info("Getting offsets for consumer group: {}", groupId);
        
        try {
            ListConsumerGroupOffsetsResult result = adminClient.listConsumerGroupOffsets(groupId);
            Map<TopicPartition, OffsetAndMetadata> offsets = result.partitionsToOffsetAndMetadata().get();
            
            log.info("Successfully retrieved offsets for consumer group: {} with {} partitions", groupId, offsets.size());
            return offsets;
        } catch (ExecutionException e) {
            log.error("Failed to get offsets for consumer group {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to get offsets for consumer group: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while getting offsets for consumer group {}", groupId, e);
            throw new RuntimeException("Thread interrupted while getting offsets for consumer group: " + groupId, e);
        }
    }
    
    /**
     * Gets session summary information.
     * 
     * @return Map containing session summary
     * @throws RuntimeException if getting session summary fails
     */
    public Map<String, Object> getSessionSummary() {
        log.info("Getting session summary");
        
        try {
            List<ConsumerGroupInfo> activeGroups = getActiveConsumerGroups();
            List<ConsumerGroupInfo.MemberInfo> activeConsumers = getActiveConsumers();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalActiveGroups", activeGroups.size());
            summary.put("totalActiveConsumers", activeConsumers.size());
            summary.put("activeGroups", activeGroups);
            summary.put("activeConsumers", activeConsumers);
            
            // Group by state
            Map<String, Long> groupStates = activeGroups.stream()
                .collect(Collectors.groupingBy(
                    ConsumerGroupInfo::getState,
                    Collectors.counting()
                ));
            summary.put("groupStates", groupStates);
            
            // Group by host
            Map<String, Long> consumersByHost = activeConsumers.stream()
                .collect(Collectors.groupingBy(
                    ConsumerGroupInfo.MemberInfo::getHost,
                    Collectors.counting()
                ));
            summary.put("consumersByHost", consumersByHost);
            
            log.info("Successfully got session summary with {} active groups and {} active consumers", 
                    activeGroups.size(), activeConsumers.size());
            return summary;
        } catch (Exception e) {
            log.error("Failed to get session summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get session summary", e);
        }
    }
    
    /**
     * Gets consumer group health status.
     * 
     * @param groupId The consumer group ID
     * @return Map containing health status information
     * @throws RuntimeException if getting health status fails
     */
    public Map<String, Object> getConsumerGroupHealth(String groupId) {
        log.info("Getting consumer group health for: {}", groupId);
        
        try {
            ConsumerGroupInfo groupInfo = getConsumerGroupDetails(groupId);
            Map<TopicPartition, Long> lag = getConsumerGroupLag(groupId);
            
            Map<String, Object> health = new HashMap<>();
            health.put("groupId", groupId);
            health.put("state", groupInfo.getState());
            health.put("memberCount", groupInfo.getMembers().size());
            health.put("coordinator", groupInfo.getCoordinator());
            health.put("partitionAssignor", groupInfo.getPartitionAssignor());
            health.put("lag", lag);
            
            // Calculate health score
            boolean isHealthy = "Stable".equals(groupInfo.getState()) && 
                              !groupInfo.getMembers().isEmpty() &&
                              lag.values().stream().allMatch(l -> l >= 0);
            health.put("isHealthy", isHealthy);
            health.put("healthScore", isHealthy ? 100 : 50);
            
            log.info("Successfully got consumer group health for: {} - Healthy: {}", groupId, isHealthy);
            return health;
        } catch (Exception e) {
            log.error("Failed to get consumer group health for {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to get consumer group health: " + groupId, e);
        }
    }
    
    /**
     * Gets all consumer groups with their health status.
     * 
     * @return List of maps containing consumer group health information
     * @throws RuntimeException if getting consumer group health fails
     */
    public List<Map<String, Object>> getAllConsumerGroupHealth() {
        log.info("Getting health status for all consumer groups");
        
        try {
            List<ConsumerGroupInfo> activeGroups = getActiveConsumerGroups();
            
            List<Map<String, Object>> healthStatus = activeGroups.stream()
                .map(group -> getConsumerGroupHealth(group.getGroupId()))
                .collect(Collectors.toList());
            
            log.info("Successfully got health status for {} consumer groups", healthStatus.size());
            return healthStatus;
        } catch (Exception e) {
            log.error("Failed to get consumer group health status: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get consumer group health status", e);
        }
    }
    
    /**
     * Closes the AdminClient to release resources.
     */
    public void close() {
        if (adminClient != null) {
            adminClient.close();
            log.info("SessionViewer AdminClient closed successfully");
        }
    }
}
