/**
 * Logs audit information to a Kafka topic.
 *
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.producer;

import com.example.service_a.dto.*;
import com.example.service_a.component.AuditLogUtil;
import com.example.service_a.util.logging.observer.ILogObserver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@RequiredArgsConstructor
public class KafkaLogger implements ILogObserver {
    private final KafkaTemplate<String, AuditLogDto> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuditLogUtil auditLogUtil;
    private final ThreadLocal<List<AuditLogDto>> pendingLogs = ThreadLocal.withInitial(ArrayList::new);
    private static final String AUDIT_TOPIC = "audit-log-topic";

    /**
     * Logs audit information by validating and storing it, then sending to Kafka.
     *
     * @param message the audit log message in JSON format
     * @throws Exception if validation fails or an error occurs during processing
     */
    @Override
    public void log(String message) throws Exception {
        AuditLogDto logDto = objectMapper.readValue(message, AuditLogDto.class);

        // Validate fields
        Map<String, String> errors = new HashMap<>();
        if (!auditLogUtil.getValidLogLevels().contains(logDto.getLogLevel())) {
            errors.put("logLevel", "Invalid log level: " + logDto.getLogLevel());
        }
        if (!auditLogUtil.getValidArchiveStrategies().contains(logDto.getArchiveStrategy())) {
            errors.put("archiveStrategy", "Invalid archive strategy: " + logDto.getArchiveStrategy());
        }
        if (logDto.getMetadata() != null) {
            for (MetadataDto metadata : logDto.getMetadata()) {
                if (!auditLogUtil.getValidMetadataTypes().contains(metadata.getMetadataType())) {
                    errors.put("metadataType", "Invalid metadata type: " + metadata.getMetadataType());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        // Store log in ThreadLocal
        pendingLogs.get().add(logDto);

        // Send immediately for non-ERROR logs or single ERROR log
        if (!logDto.getLogLevel().equals("ERROR") || pendingLogs.get().size() == 1) {
            sendAuditLog(pendingLogs.get());
            pendingLogs.get().clear();
        } else if (logDto.getLogLevel().equals("ERROR") && pendingLogs.get().size() == 2) {
            sendAuditLog(pendingLogs.get());
            pendingLogs.get().clear();
        }
    }

    /**
     * Sends a list of audit logs to the Kafka topic, combining metadata and actions.
     *
     * @param logDtoList the list of audit logs to send
     */
    private void sendAuditLog(List<AuditLogDto> logDtoList) {
        AuditLogDto firstLog = logDtoList.get(0);
        List<MetadataDto> metadata = new ArrayList<>();
        List<ActionDto> actions = new ArrayList<>();
        for (AuditLogDto logDto : logDtoList) {
            if (logDto.getMetadata() != null) {
                metadata.addAll(logDto.getMetadata());
            }
            if (logDto.getAction() != null) {
                actions.addAll(logDto.getAction());
            }
        }

        AuditLogDto auditLogDto = AuditLogDto.builder()
                .ipAddress(firstLog.getIpAddress())
                .serviceName(firstLog.getServiceName())
                .tokenId(firstLog.getTokenId())
                .timeToArchiveInDays(auditLogUtil.getTimeToArchive(firstLog.getArchiveStrategy(), firstLog.getLogLevel()))
                .performer(firstLog.getPerformer())
                .logLevel(firstLog.getLogLevel())
                .archiveStrategy(firstLog.getArchiveStrategy())
                .action(actions)
                .metadata(metadata)
                .build();

        kafkaTemplate.send(AUDIT_TOPIC, auditLogDto.getPerformer().getPerformerId(), auditLogDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException("Failed to send audit log to Kafka: " + ex.getMessage(), ex);
                    }
                });
    }
}