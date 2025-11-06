package com.example.service_a.dto;

import com.example.service_a.dto.base.BaseGeneralTimestampDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Data
public class UserFlatDto extends BaseGeneralTimestampDto {

    @JsonProperty("performer_id")
    @NotBlank(message = "Performer ID is required")
    @Size(max = 50, message = "Performer ID must not exceed 50 characters")
    private String id;

    @JsonProperty("first_name")
    @NotBlank(message = "A first name or given name is required for a prospecting user")
    @Size(min = 2, max = 50, message = "The first name cannot be below 2 characters or more than 50 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "A last name or family name is required for a prospecting user")
    @Size(min = 2, max = 50, message = "The last name cannot be below 2 characters or more than 50 characters")
    private String lastName;

    @JsonProperty("work_email")
    @NotBlank(message = "A functional work or company email address is required as a must")
    @Size(min = 2, max = 50, message = "The email cannot exceed 50 characters")
    @Email(message = "Invalid email address format")
    private String workEmail;

    @JsonProperty("phone_number")
    @NotBlank(message = "An active phone number is required as a must, it should also include a country code without the '+' symbol.")
    @Size(min = 2, max = 50, message = "A phone number cannot exceed 50 numbers")
    private String phoneNumber;
}