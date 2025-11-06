package com.example.service_a.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class BaseGeneralTimestampDto extends BaseDto{

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
