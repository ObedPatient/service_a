package com.example.service_a.producer;

import com.example.service_a.dto.*;
import com.example.service_a.util.AuditLogUtil;
import com.example.service_a.util.UserIdGenerator;
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
    private final ThreadLocal<List<AuditLogData>> pendingLogs = ThreadLocal.withInitial(ArrayList::new);
    private static final String AUDIT_TOPIC = "audit-log-topic";

    @Override
    public void log(String message) throws Exception {
        AuditLogData logData = objectMapper.readValue(message, AuditLogData.class);

        // Validate fields
        Map<String, String> errors = new HashMap<>();
        if (!auditLogUtil.getValidLogLevels().contains(logData.getLogLevel())) {
            errors.put("logLevel", "Invalid log level: " + logData.getLogLevel());
        }
        if (!auditLogUtil.getValidArchiveStrategies().contains(logData.getArchiveStrategy())) {
            errors.put("archiveStrategy", "Invalid archive strategy: " + logData.getArchiveStrategy());
        }
        if (!auditLogUtil.getValidMetadataTypes().contains(logData.getMetadataType())) {
            errors.put("metadataType", "Invalid metadata type: " + logData.getMetadataType());
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        // Store log in ThreadLocal
        pendingLogs.get().add(logData);

        // Send immediately for non-ERROR logs or single ERROR log
        if (!logData.getLogLevel().equals("ERROR") || pendingLogs.get().size() == 1) {
            sendAuditLog(pendingLogs.get());
            pendingLogs.get().clear();
        } else if (logData.getLogLevel().equals("ERROR") && pendingLogs.get().size() == 2) {
            sendAuditLog(pendingLogs.get());
            pendingLogs.get().clear();
        }
    }

    private void sendAuditLog(List<AuditLogData> logDataList) {
        AuditLogData firstLog = logDataList.get(0);
        List<MetadataDto> metadata = new ArrayList<>();
        for (AuditLogData logData : logDataList) {
            metadata.add(MetadataDto.builder()
                    .content(logData.getContent())
                    .metadataType(logData.getMetadataType())
                    .build());
        }

        AuditLogDto auditLogDto = AuditLogDto.builder()
                .ipAddress(firstLog.getIpAddress())
                .serviceName(firstLog.getServiceName())
                .tokenId(firstLog.getTokenId())
                .timeToArchiveInDays(auditLogUtil.getTimeToArchive(firstLog.getArchiveStrategy(), firstLog.getLogLevel()))
                .performerId(UserIdGenerator.generateId())
                .logLevel(firstLog.getLogLevel())
                .archiveStrategy(firstLog.getArchiveStrategy())
                .action(List.of(
                        ActionDto.builder()
                                .name(firstLog.getActionName())
                                .description(firstLog.getDescription())
                                .build()
                ))
                .metadata(metadata)
                .build();

        kafkaTemplate.send(AUDIT_TOPIC, auditLogDto.getPerformerId(), auditLogDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException("Failed to send audit log to Kafka: " + ex.getMessage(), ex);
                    }
                });
    }
}
