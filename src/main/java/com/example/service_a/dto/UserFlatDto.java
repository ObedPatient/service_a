package com.example.service_a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFlatDto {

    @JsonProperty("performer_id")
    @NotBlank(message = "Performer ID is required")
    @Size(max = 50, message = "Performer ID must not exceed 50 characters")
    private String performerId;

    @NotBlank(message = "A first name or given name is required for a prospecting user")
    @Size(min = 2, max = 50, message = "The first name cannot be below 2 characters or more than 50 characters")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "A last name or family name is required for a prospecting user")
    @Size(min = 2, max = 50, message = "The last name cannot be below 2 characters or more than 50 characters")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "A functional work or company email address is required as a must")
    @Size(min = 2, max = 50, message = "The email cannot exceed 50 characters")
    @Email(message = "Invalid email address format")
    @JsonProperty("work_email")
    private String workEmail;

    @NotBlank(message = "An active phone number is required as a must, it should also include a country code without the '+' symbol.")
    @Size(min = 2, max = 50, message = "A phone number cannot exceed 30 numbers")
    @JsonProperty("phone_number")
    private String phoneNumber;
}