package com.mycompany.kafka.multi.manager;

import com.mycompany.kafka.constants.ErrorConstants;
import com.mycompany.kafka.dto.ConsumerGroupInfo;
import com.mycompany.kafka.exception.KafkaManagementException;
import com.mycompany.kafka.multi.factory.MultiConnectionFactory;
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
 * Enhanced Consumer Manager for multiple brokers.
 */
public class MultiConsumerManager {
    
    private static final Logger log = LoggerFactory.getLogger(MultiConsumerManager.class);
    
    private final MultiConnectionFactory multiConnectionFactory;
    
    public MultiConsumerManager(MultiConnectionFactory multiConnectionFactory) {
        this.multiConnectionFactory = multiConnectionFactory;
    }
    
    /**
     * Lists all consumer groups for a specific broker.
     * 
     * @param brokerId The broker ID
     * @return List of consumer group IDs
     * @throws RuntimeException if listing consumer groups fails
     */
    public List<String> listConsumerGroups(String brokerId) {
        log.info("Listing all consumer groups for broker: {}", brokerId);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
            Collection<ConsumerGroupListing> consumerGroups = adminClient.listConsumerGroups().all().get();
            List<String> groupIds = consumerGroups.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
            
            log.info("Found {} consumer groups for broker: {}", groupIds.size(), brokerId);
            return groupIds;
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_LIST_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_LIST_FAILED_MSG, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_LIST_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_LIST_FAILED_MSG, e.getMessage()),
                e);
        }
    }
    
    /**
     * Lists all consumer groups with detailed information for a specific broker.
     * 
     * @param brokerId The broker ID
     * @return List of ConsumerGroupInfo objects
     * @throws RuntimeException if listing consumer groups fails
     */
    public List<ConsumerGroupInfo> listConsumerGroupsWithInfo(String brokerId) {
        log.info("Listing all consumer groups with information for broker: {}", brokerId);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
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
            
            log.info("Found {} consumer groups with information for broker: {}", groupInfos.size(), brokerId);
            return groupInfos;
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_LIST_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_LIST_FAILED_MSG, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_LIST_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_LIST_FAILED_MSG, e.getMessage()),
                e);
        }
    }
    
    /**
     * Describes a specific consumer group for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID
     * @return ConsumerGroupInfo object with group details
     * @throws RuntimeException if describing consumer group fails
     */
    public ConsumerGroupInfo describeConsumerGroup(String brokerId, String groupId) {
        log.info("Describing consumer group: {} for broker: {}", groupId, brokerId);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
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
            
            log.info("Successfully described consumer group: {} for broker: {}", groupId, brokerId);
            return info;
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_DESCRIBE_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_DESCRIBE_FAILED_MSG, groupId, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_DESCRIBE_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_DESCRIBE_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Deletes a consumer group for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID to delete
     * @throws RuntimeException if deleting consumer group fails
     */
    public void deleteConsumerGroup(String brokerId, String groupId) {
        log.info("Deleting consumer group: {} for broker: {}", groupId, brokerId);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
            adminClient.deleteConsumerGroups(Collections.singletonList(groupId)).all().get();
            log.info("Successfully deleted consumer group: {} for broker: {}", groupId, brokerId);
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_DELETE_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_DELETE_FAILED_MSG, groupId, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_DELETE_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_DELETE_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Gets the current offsets for a consumer group for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID
     * @return Map of TopicPartition to OffsetAndMetadata
     * @throws RuntimeException if getting offsets fails
     */
    public Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(String brokerId, String groupId) {
        log.info("Getting offsets for consumer group: {} for broker: {}", groupId, brokerId);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
            ListConsumerGroupOffsetsResult result = adminClient.listConsumerGroupOffsets(groupId);
            Map<TopicPartition, OffsetAndMetadata> offsets = result.partitionsToOffsetAndMetadata().get();
            
            log.info("Successfully retrieved offsets for consumer group: {} for broker: {} with {} partitions", groupId, brokerId, offsets.size());
            return offsets;
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, groupId, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Resets the offsets for a consumer group to the earliest available offsets for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID
     * @param topicPartitions The topic partitions to reset
     * @throws RuntimeException if resetting offsets fails
     */
    public void resetConsumerGroupOffsetsToEarliest(String brokerId, String groupId, Collection<TopicPartition> topicPartitions) {
        log.info("Resetting offsets to earliest for consumer group: {} for broker: {} with {} partitions", groupId, brokerId, topicPartitions.size());
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
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
            log.info("Successfully reset offsets to earliest for consumer group: {} for broker: {}", groupId, brokerId);
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, groupId, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Resets the offsets for a consumer group to the latest available offsets for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID
     * @param topicPartitions The topic partitions to reset
     * @throws RuntimeException if resetting offsets fails
     */
    public void resetConsumerGroupOffsetsToLatest(String brokerId, String groupId, Collection<TopicPartition> topicPartitions) {
        log.info("Resetting offsets to latest for consumer group: {} for broker: {} with {} partitions", groupId, brokerId, topicPartitions.size());
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
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
            log.info("Successfully reset offsets to latest for consumer group: {} for broker: {}", groupId, brokerId);
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, groupId, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_OFFSET_RESET_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_OFFSET_RESET_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Gets the lag for a consumer group (difference between latest and current offsets) for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID
     * @return Map of TopicPartition to lag amount
     * @throws RuntimeException if getting lag fails
     */
    public Map<TopicPartition, Long> getConsumerGroupLag(String brokerId, String groupId) {
        log.info("Getting lag for consumer group: {} for broker: {}", groupId, brokerId);
        
        try (AdminClient adminClient = multiConnectionFactory.createAdminClient(brokerId)) {
            // Get current offsets
            Map<TopicPartition, OffsetAndMetadata> currentOffsets = getConsumerGroupOffsets(brokerId, groupId);
            
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
            
            log.info("Successfully calculated lag for consumer group: {} for broker: {} with {} partitions", groupId, brokerId, lag.size());
            return lag;
        } catch (ExecutionException e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_LAG_CALCULATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_LAG_CALCULATION_FAILED_MSG, groupId, e.getMessage()),
                e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_LAG_CALCULATION_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_LAG_CALCULATION_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Checks if a consumer group exists for a specific broker.
     * 
     * @param brokerId The broker ID
     * @param groupId The consumer group ID to check
     * @return true if consumer group exists, false otherwise
     */
    public boolean consumerGroupExists(String brokerId, String groupId) {
        log.info("Checking if consumer group exists: {} for broker: {}", groupId, brokerId);
        
        try {
            List<String> groupIds = listConsumerGroups(brokerId);
            boolean exists = groupIds.contains(groupId);
            log.info("Consumer group {} exists for broker {}: {}", groupId, brokerId, exists);
            return exists;
        } catch (Exception e) {
            throw new KafkaManagementException(
                ErrorConstants.CONSUMER_GROUP_DESCRIBE_FAILED,
                ErrorConstants.formatMessage(ErrorConstants.CONSUMER_GROUP_DESCRIBE_FAILED_MSG, groupId, e.getMessage()),
                e);
        }
    }
    
    /**
     * Lists all consumer groups across all configured brokers.
     * 
     * @return Map of broker ID to list of consumer group IDs
     * @throws RuntimeException if listing consumer groups fails
     */
    public Map<String, List<String>> listConsumerGroupsForAllBrokers() {
        log.info("Listing all consumer groups across all brokers");
        
        Map<String, List<String>> allGroups = new HashMap<>();
        // This would need to be implemented based on how brokers are configured
        // For now, return empty map as placeholder
        log.info("Consumer groups listing across all brokers completed");
        return allGroups;
    }
    
    /**
     * Gets the multi-connection factory.
     * 
     * @return MultiConnectionFactory instance
     */
    public MultiConnectionFactory getMultiConnectionFactory() {
        return multiConnectionFactory;
    }
}
