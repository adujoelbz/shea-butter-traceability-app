package com.example.sheabutterledgersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CollectorRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{10,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Village is required")
    @Size(max = 255, message = "Village must not exceed 255 characters")
    private String village;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @NotBlank(message = "Region is required")
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    private String gpsCoordinates;
    private Integer yearsOfExperience;
    private String bankName;
    private String bankAccountNumber;
    private String mobileMoneyNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private UUID cooperativeId;
    private String notes;
}