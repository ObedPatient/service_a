package com.example.service_a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MetadataDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("content")
    @Size(max = 255, message = "Content must not exceed 255 characters")
    private String content;

    @JsonProperty("metadata_type")
    private String metadataType;

    @JsonProperty("audit_log_id")
    @Size(max = 50, message = "Audit log ID must not exceed 50 characters")
    private String auditLogId;
}