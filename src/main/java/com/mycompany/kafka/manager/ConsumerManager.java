package com.mycompany.kafka.manager;

import com.mycompany.kafka.dto.ConsumerGroupInfo;
import com.mycompany.kafka.factory.ConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Manager class for Kafka consumer group operations.
 * Provides functionality to manage consumer groups, offsets, and consumer group information.
 */
public class ConsumerManager {
    
    private static final Logger log = LoggerFactory.getLogger(ConsumerManager.class);
    
    private final ConnectionFactory connectionFactory;
    private final AdminClient adminClient;
    
    public ConsumerManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.adminClient = connectionFactory.createAdminClient();
    }
    
    /**
     * Lists all consumer groups.
     * 
     * @return List of consumer group IDs
     * @throws RuntimeException if listing consumer groups fails
     */
    public List<String> listConsumerGroups() {
        log.info("Listing all consumer groups");
        
        try {
            Collection<ConsumerGroupListing> consumerGroups = adminClient.listConsumerGroups().all().get();
            List<String> groupIds = consumerGroups.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
            
            log.info("Found {} consumer groups", groupIds.size());
            return groupIds;
        } catch (ExecutionException e) {
            log.error("Failed to list consumer groups: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list consumer groups", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while listing consumer groups", e);
            throw new RuntimeException("Thread interrupted while listing consumer groups", e);
        }
    }
    
    /**
     * Lists all consumer groups with detailed information.
     * 
     * @return List of ConsumerGroupInfo objects
     * @throws RuntimeException if listing consumer groups fails
     */
    public List<ConsumerGroupInfo> listConsumerGroupsWithInfo() {
        log.info("Listing all consumer groups with information");
        
        try {
            Collection<ConsumerGroupListing> consumerGroups = adminClient.listConsumerGroups().all().get();
            List<String> groupIds = consumerGroups.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
            
            if (groupIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            Map<String, ConsumerGroupDescription> descriptions = adminClient.describeConsumerGroups(groupIds).all().get();
            
            List<ConsumerGroupInfo> groupInfos = descriptions.entrySet().stream()
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
                .collect(Collectors.toList());
            
            log.info("Found {} consumer groups with information", groupInfos.size());
            return groupInfos;
        } catch (ExecutionException e) {
            log.error("Failed to list consumer groups with information: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list consumer groups with information", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while listing consumer groups with information", e);
            throw new RuntimeException("Thread interrupted while listing consumer groups with information", e);
        }
    }
    
    /**
     * Describes a specific consumer group.
     * 
     * @param groupId The consumer group ID
     * @return ConsumerGroupInfo object with group details
     * @throws RuntimeException if describing consumer group fails
     */
    public ConsumerGroupInfo describeConsumerGroup(String groupId) {
        log.info("Describing consumer group: {}", groupId);
        
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
            
            log.info("Successfully described consumer group: {}", groupId);
            return info;
        } catch (ExecutionException e) {
            log.error("Failed to describe consumer group {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to describe consumer group: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while describing consumer group {}", groupId, e);
            throw new RuntimeException("Thread interrupted while describing consumer group: " + groupId, e);
        }
    }
    
    /**
     * Deletes a consumer group.
     * 
     * @param groupId The consumer group ID to delete
     * @throws RuntimeException if deleting consumer group fails
     */
    public void deleteConsumerGroup(String groupId) {
        log.info("Deleting consumer group: {}", groupId);
        
        try {
            adminClient.deleteConsumerGroups(Collections.singletonList(groupId)).all().get();
            log.info("Successfully deleted consumer group: {}", groupId);
        } catch (ExecutionException e) {
            log.error("Failed to delete consumer group {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete consumer group: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while deleting consumer group {}", groupId, e);
            throw new RuntimeException("Thread interrupted while deleting consumer group: " + groupId, e);
        }
    }
    
    /**
     * Gets the current offsets for a consumer group.
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
     * Resets the offsets for a consumer group to the earliest available offsets.
     * 
     * @param groupId The consumer group ID
     * @param topicPartitions The topic partitions to reset
     * @throws RuntimeException if resetting offsets fails
     */
    public void resetConsumerGroupOffsetsToEarliest(String groupId, Collection<TopicPartition> topicPartitions) {
        log.info("Resetting offsets to earliest for consumer group: {} with {} partitions", groupId, topicPartitions.size());
        
        try {
            Map<TopicPartition, OffsetAndMetadata> earliestOffsets = new HashMap<>();
            
            for (TopicPartition tp : topicPartitions) {
                ListOffsetsResult result = adminClient.listOffsets(
                    Collections.singletonMap(tp, OffsetSpec.earliest())
                );
                ListOffsetsResult.ListOffsetsResultInfo offsetInfo = result.partitionResult(tp).get();
                if (offsetInfo != null) {
                    earliestOffsets.put(tp, new OffsetAndMetadata(offsetInfo.offset()));
                }
            }
            
            adminClient.alterConsumerGroupOffsets(groupId, earliestOffsets).all().get();
            log.info("Successfully reset offsets to earliest for consumer group: {}", groupId);
        } catch (ExecutionException e) {
            log.error("Failed to reset offsets to earliest for consumer group {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to reset offsets to earliest for consumer group: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while resetting offsets to earliest for consumer group {}", groupId, e);
            throw new RuntimeException("Thread interrupted while resetting offsets to earliest for consumer group: " + groupId, e);
        }
    }
    
    /**
     * Resets the offsets for a consumer group to the latest available offsets.
     * 
     * @param groupId The consumer group ID
     * @param topicPartitions The topic partitions to reset
     * @throws RuntimeException if resetting offsets fails
     */
    public void resetConsumerGroupOffsetsToLatest(String groupId, Collection<TopicPartition> topicPartitions) {
        log.info("Resetting offsets to latest for consumer group: {} with {} partitions", groupId, topicPartitions.size());
        
        try {
            Map<TopicPartition, OffsetAndMetadata> latestOffsets = new HashMap<>();
            
            for (TopicPartition tp : topicPartitions) {
                ListOffsetsResult result = adminClient.listOffsets(
                    Collections.singletonMap(tp, OffsetSpec.latest())
                );
                ListOffsetsResult.ListOffsetsResultInfo offsetInfo = result.partitionResult(tp).get();
                if (offsetInfo != null) {
                    latestOffsets.put(tp, new OffsetAndMetadata(offsetInfo.offset()));
                }
            }
            
            adminClient.alterConsumerGroupOffsets(groupId, latestOffsets).all().get();
            log.info("Successfully reset offsets to latest for consumer group: {}", groupId);
        } catch (ExecutionException e) {
            log.error("Failed to reset offsets to latest for consumer group {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to reset offsets to latest for consumer group: " + groupId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while resetting offsets to latest for consumer group {}", groupId, e);
            throw new RuntimeException("Thread interrupted while resetting offsets to latest for consumer group: " + groupId, e);
        }
    }
    
    /**
     * Gets the lag for a consumer group (difference between latest and current offsets).
     * 
     * @param groupId The consumer group ID
     * @return Map of TopicPartition to lag amount
     * @throws RuntimeException if getting lag fails
     */
    public Map<TopicPartition, Long> getConsumerGroupLag(String groupId) {
        log.info("Getting lag for consumer group: {}", groupId);
        
        try {
            // Get current offsets
            Map<TopicPartition, OffsetAndMetadata> currentOffsets = getConsumerGroupOffsets(groupId);
            
            // Get latest offsets
            Map<TopicPartition, OffsetAndMetadata> latestOffsets = new HashMap<>();
            for (TopicPartition tp : currentOffsets.keySet()) {
                ListOffsetsResult result = adminClient.listOffsets(
                    Collections.singletonMap(tp, OffsetSpec.latest())
                );
                ListOffsetsResult.ListOffsetsResultInfo offsetInfo = result.partitionResult(tp).get();
                if (offsetInfo != null) {
                    latestOffsets.put(tp, new OffsetAndMetadata(offsetInfo.offset()));
                }
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
     * Checks if a consumer group exists.
     * 
     * @param groupId The consumer group ID to check
     * @return true if consumer group exists, false otherwise
     */
    public boolean consumerGroupExists(String groupId) {
        log.info("Checking if consumer group exists: {}", groupId);
        
        try {
            List<String> groupIds = listConsumerGroups();
            boolean exists = groupIds.contains(groupId);
            log.info("Consumer group {} exists: {}", groupId, exists);
            return exists;
        } catch (Exception e) {
            log.error("Failed to check if consumer group exists {}: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("Failed to check if consumer group exists: " + groupId, e);
        }
    }
    
    /**
     * Closes the AdminClient to release resources.
     */
    public void close() {
        if (adminClient != null) {
            adminClient.close();
            log.info("ConsumerManager AdminClient closed successfully");
        }
    }
}
