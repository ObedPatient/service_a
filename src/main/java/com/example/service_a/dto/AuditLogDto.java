package com.example.service_a.dto;

import com.example.service_a.dto.base.BaseGeneralTimestampDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Data
public class AuditLogDto extends BaseGeneralTimestampDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("performer_id")
    private String performerId;

    @JsonProperty("server_ip_address")
    @NotBlank(message = "Server IP address is required")
    @Size(max = 30, message = "Server IP address must not exceed 30 characters")
    private String serverIpAddress;

    @JsonProperty("request_ip_address")
    @NotBlank(message = "Request IP address is required")
    @Size(max = 30, message = "Request IP address must not exceed 30 characters")
    private String requestIpAddress;

    @JsonProperty("port")
    @NotBlank(message = "The port is required")
    private Integer port;

    @JsonProperty("service_name")
    @NotBlank(message = "Service name is required")
    @Size(max = 255, message = "Service name must not exceed 255 characters")
    private String serviceName;

    @JsonProperty("server_user")
    @NotBlank(message = "server user name is required")
    @Size(max = 255, message = "server user name must not exceed 255 characters")
    private String serverUser;

    @JsonProperty("token_id")
    @Size(max = 500, message = "Token ID must not exceed 255 characters")
    private String tokenId;

    @JsonProperty("time_to_archive_in_days")
    @NotNull(message = "Time to Archive in days is required")
    private Integer timeToArchiveInDays;

    @JsonProperty("log_level")
    @NotBlank(message = "Log level is required")
    @Size(max = 50, message = "Log level must not exceed 50 characters")
    private String logLevel;

    @JsonProperty("archive_strategy")
    @NotBlank(message = "Archive strategy is required")
    @Size(max = 50, message = "Archive strategy must not exceed 50 characters")
    private String archiveStrategy;

    @JsonProperty("metadata")
    private MetadataDto metadata;

    @JsonProperty("action")
    private ActionDto action;
}