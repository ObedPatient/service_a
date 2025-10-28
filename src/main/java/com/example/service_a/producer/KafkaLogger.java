/**
 * Logs audit information to a Kafka topic.
 *
 * @author Obed Patient
 * @version 1.3
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
    private final KafkaTemplate<String, UserFlatDto> userSignupKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuditLogUtil auditLogUtil;
    private static final String AUDIT_TOPIC = "audit-log-topic";
    private static final String USER_SIGNUP_TOPIC = "user-signup-topic";

    @Override
    public void log(String message) throws Exception {
        // Deserialize JSON message to AuditLogDto
        AuditLogDto logDto;
        try {
            logDto = objectMapper.readValue(message, AuditLogDto.class);
        } catch (Exception e) {
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
            if (!auditLogUtil.getValidMetadataTypes().contains(logDto.getMetadata().getMetadataType())){
                errors.put("metadatatypes", "Invalid metadata type:" + logDto.getMetadata().getMetadataType());
            }
        }
        if (!errors.isEmpty()) {
            System.err.println("Validation failed: " + errors);
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        // Check if this is a user creation event and extract user details
        boolean isUserCreation = false;
        UserFlatDto userDto = null;
        if (logDto.getMetadata() != null && logDto.getMetadata().isUserCreation()) {
            isUserCreation = true;
            userDto = extractUserDetailsFromMetadata(logDto.getMetadata());
        }

        // Send user signup if it's a user creation event
        if (isUserCreation && userDto != null) {
            sendUserSignup(userDto);
        }

        // Send the full audit log DTO
        sendAuditLog(logDto);
    }

    private UserFlatDto extractUserDetailsFromMetadata(MetadataDto metadata) {
        try {
            if (metadata.getContent() != null) {
                UserFlatDto userDto = objectMapper.readValue(metadata.getContent(), UserFlatDto.class);
                // Validate user fields
                if (userDto.getPerformerId() == null || userDto.getPerformerId().isEmpty()) {
                    throw new IllegalArgumentException("Performer ID is required in UserFlatDto");
                }
                return userDto;
            }
        } catch (Exception e) {
            System.err.println("Failed to extract user details from metadata: " + e.getMessage());
        }
        // Fallback to default values if extraction fails
        return UserFlatDto.builder()
                .performerId("unknown")
                .firstName("Unknown")
                .lastName("Unknown")
                .workEmail("unknown@gmail.com")
                .phoneNumber("unknown")
                .build();
    }

    private void sendAuditLog(AuditLogDto logDto) {
        try {
            kafkaTemplate.send(AUDIT_TOPIC, logDto.getPerformerId(), logDto)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.err.println("Failed to send audit log to Kafka topic '" + AUDIT_TOPIC + "': " + ex.getMessage());
                            throw new RuntimeException("Failed to send audit log to Kafka: " + ex.getMessage(), ex);
                        } else {
                            System.out.println("Successfully sent audit log to Kafka topic '" + AUDIT_TOPIC + "': " + logDto.getPerformerId());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Error initiating Kafka send: " + e.getMessage());
            throw new RuntimeException("Error initiating Kafka send: " + e.getMessage(), e);
        }
    }

    private void sendUserSignup(UserFlatDto userDto) {
        try {
            userSignupKafkaTemplate.send(USER_SIGNUP_TOPIC, userDto.getPerformerId(), userDto)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.err.println("Failed to send user signup to Kafka topic '" + USER_SIGNUP_TOPIC + "': " + ex.getMessage());
                            throw new RuntimeException("Failed to send user signup to Kafka: " + ex.getMessage(), ex);
                        } else {
                            System.out.println("Successfully sent user signup to Kafka topic '" + USER_SIGNUP_TOPIC + "': " + userDto.getPerformerId());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error initiating Kafka send for user signup: " + e.getMessage(), e);
        }
    }
}