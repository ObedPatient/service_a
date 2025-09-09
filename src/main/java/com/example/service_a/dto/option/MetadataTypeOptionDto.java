/**
 * MetadataTypeOptionDto is a DTO responsible for implementing the specific properties and
 * behaviors of the MetadataTypeOptionDto.
 * The different getters, setters, constructor overloads, and builder implementations are implicitly written with the help of the
 * different Lombok annotations.
 *
 * @author  - Obed Patient
 * @version - 1.0
 * @since   - 1.0
 */
package com.example.service_a.dto.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MetadataTypeOptionDto {

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