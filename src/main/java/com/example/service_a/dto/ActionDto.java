package com.example.service_a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ActionDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be null")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;


}
