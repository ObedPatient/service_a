package com.example.service_a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode
public class AuditLogDto implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("ipaddress")
    @NotBlank(message = "Ipaddress is required")
    @Size(max = 255, message = "IP address must not exceed 255 characters")
    private String ipAddress;

    @JsonProperty("service_name")
    @NotBlank(message = "Service name is required")
    @Size(max = 255, message = "Service name must not exceed 255 characters")
    private String serviceName;

    @JsonProperty("token_id")
    @NotBlank(message = "TokenId is required")
    @Size(max = 255, message = "Token ID must not exceed 255 characters")
    private String tokenId;

    @JsonProperty("time_to_archive_in_days")
    @NotBlank(message = "Time to Archive in days is required")
    @Size(max = 255, message = "Time to archive must not exceed 255 characters")
    private String timeToArchiveInDays;

    @JsonProperty("log_level")
    @NotBlank(message = "Log level is required")
    @Size(max = 50, message = "Log level must not exceed 50 characters")
    private String logLevel;

    @JsonProperty("archive_strategy")
    @NotBlank(message = "Archive strategy is required")
    @Size(max = 50, message = "Archive strategy must not exceed 50 characters")
    private String archiveStrategy;

    @JsonProperty("performer")
    @NotBlank(message = "Performer is required")
    private UserFlatDto performer;

    @JsonProperty("metadata")
    private List<MetadataDto> metadata;

    @JsonProperty("action")
    private List<ActionDto> action;
}