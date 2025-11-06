package com.example.service_a.producer;

import com.example.service_a.component.AuditLogUtil;
import com.example.service_a.dto.AuditLogDto;
import com.example.service_a.dto.MetadataDto;
import com.example.service_a.dto.UserFlatDto;
import com.example.service_a.util.logging.observer.ILogObserver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaLogger implements ILogObserver {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuditLogUtil auditLogUtil;

    private static final String AUDIT_TOPIC = "audit-log-topic";
    private static final String USER_SIGNUP_TOPIC = "user-signup-topic";

    @Override
    public void log(String message) throws Exception {
        AuditLogDto logDto = objectMapper.readValue(message, AuditLogDto.class);

        Map<String, String> errors = validate(logDto);
        if (!errors.isEmpty()) {
            System.err.println("Validation failed: " + errors);
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        boolean isUserCreation = logDto.getMetadata() != null && logDto.getMetadata().isUserCreation();
        if (isUserCreation) {
            UserFlatDto userDto = extractUserDetailsFromMetadata(logDto.getMetadata());
            sendUserSignup(userDto);
        }

        sendAuditLog(logDto);
    }

    private Map<String, String> validate(AuditLogDto dto) {
        Map<String, String> errors = new HashMap<>();
        if (!auditLogUtil.getValidLogLevels().contains(dto.getLogLevel())) {
            errors.put("logLevel", "Invalid: " + dto.getLogLevel());
        }
        if (!auditLogUtil.getValidArchiveStrategies().contains(dto.getArchiveStrategy())) {
            errors.put("archiveStrategy", "Invalid: " + dto.getArchiveStrategy());
        }
        if (dto.getMetadata() != null && dto.getMetadata().getMetadataType() != null) {
            if (!auditLogUtil.getValidMetadataTypes().contains(dto.getMetadata().getMetadataType())) {
                errors.put("metadataType", "Invalid: " + dto.getMetadata().getMetadataType());
            }
        }
        return errors;
    }

    private UserFlatDto extractUserDetailsFromMetadata(MetadataDto metadata) {
        try {
            if (metadata.getContent() != null && !metadata.getContent().isBlank()) {
                UserFlatDto userDto = objectMapper.readValue(metadata.getContent(), UserFlatDto.class);
                if (userDto.getId() == null || userDto.getId().isBlank()) {
                    throw new IllegalArgumentException("Performer ID is required");
                }
                return userDto;
            }
        } catch (Exception e) {
            System.err.println("Failed to parse user from metadata: " + e.getMessage());
        }

        return UserFlatDto.builder()
                .id("unknown")
                .firstName("Unknown")
                .lastName("Unknown")
                .workEmail("unknown@example.com")
                .phoneNumber("unknown")
                .build();
    }

    private void sendAuditLog(AuditLogDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(AUDIT_TOPIC, dto.getPerformerId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("Sent audit log: " + dto.getPerformerId());
                        } else {
                            System.err.println("Failed to send audit log: " + ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize AuditLogDto", e);
        }
    }

    private void sendUserSignup(UserFlatDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(USER_SIGNUP_TOPIC, dto.getId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("Sent user signup: " + dto.getId());
                        } else {
                            System.err.println("Failed to send user signup: " + ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize UserFlatDto", e);
        }
    }
}