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

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaLogger implements ILogObserver {
    private final KafkaTemplate<String, AuditLogDto> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuditLogUtil auditLogUtil;
    private static final String AUDIT_TOPIC = "audit-log-topic";

    /**
     * Logs audit information by validating and sending it to Kafka.
     *
     * @param message the audit log message in JSON format
     * @throws Exception if validation fails or an error occurs during processing
     */
    @Override
    public void log(String message) throws Exception {
        // Deserialize JSON message to AuditLogDto
        AuditLogDto logDto;
        try {
            logDto = objectMapper.readValue(message, AuditLogDto.class);
        } catch (Exception e) {
            System.err.println("Failed to deserialize audit log message: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }

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
            System.err.println("Validation failed: " + errors);
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        // Send log directly to Kafka
        sendAuditLog(logDto);
    }

    /**
     * Sends a single audit log to the Kafka topic.
     *
     * @param logDto the audit log to send
     */
    private void sendAuditLog(AuditLogDto logDto) {
        try {
            kafkaTemplate.send(AUDIT_TOPIC, logDto.getPerformer().getPerformerId(), logDto)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.err.println("Failed to send audit log to Kafka topic '" + AUDIT_TOPIC + "': " + ex.getMessage());
                            throw new RuntimeException("Failed to send audit log to Kafka: " + ex.getMessage(), ex);
                        } else {
                            System.out.println("Successfully sent audit log to Kafka topic '" + AUDIT_TOPIC + "': " + logDto.getPerformer().getPerformerId());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Error initiating Kafka send: " + e.getMessage());
            throw new RuntimeException("Error initiating Kafka send: " + e.getMessage(), e);
        }
    }
}