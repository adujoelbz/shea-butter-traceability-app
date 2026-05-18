package com.example.sheabutterledgersystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CooperativeRequest {

    @NotBlank(message = "Cooperative name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 100, message = "Registration number must not exceed 100 characters")
    private String registrationNumber;

    @NotBlank(message = "Region is required")
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @Pattern(regexp = "^[0-9+\\-\\s]{10,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String contactPerson;
}