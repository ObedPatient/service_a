package com.example.service_a.dto;

import com.example.service_a.dto.base.BaseGeneralTimestampDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class MetadataDto extends BaseGeneralTimestampDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("content")
    @NotBlank(message = "Content is required")
    @Size(message = "Content must not exceed 255 characters")
    private String content;

    @JsonProperty("metadata_type")
    @NotBlank(message = "Metadata type is required")
    @Size(max = 50, message = "Metadata type must not exceed 50 characters")
    private String metadataType;

    @JsonProperty("is_user_creation")
    private boolean isUserCreation;
}