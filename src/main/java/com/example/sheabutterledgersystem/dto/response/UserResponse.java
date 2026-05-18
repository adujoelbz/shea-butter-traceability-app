package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
    private Instant createdAt;
}