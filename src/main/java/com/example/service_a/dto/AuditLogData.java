package com.example.service_a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogData {
    @JsonProperty("performer_id")
    private String performerId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("work_email")
    private String workEmail;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("ipaddress")
    private String ipAddress;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("log_level")
    private String logLevel;

    @JsonProperty("archive_strategy")
    private String archiveStrategy;

    @JsonProperty("time_to_archive_in_days")
    private String timeToArchiveInDays;

    @JsonProperty("action_name")
    private String actionName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("content")
    private String content;

    @JsonProperty("metadata_type")
    private String metadataType;
}