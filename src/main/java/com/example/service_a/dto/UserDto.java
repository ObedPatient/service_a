package com.example.service_a.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private String id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "email is required")
    private String email;
    private LocalDateTime createdAt;
}