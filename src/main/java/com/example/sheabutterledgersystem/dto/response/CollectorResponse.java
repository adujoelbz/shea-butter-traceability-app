package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CollectorResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String village;
    private String district;
    private String region;
    private String gpsCoordinates;
    private Integer yearsOfExperience;
    private String bankName;
    private String bankAccountNumber;
    private String mobileMoneyNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String status;
    private String photoPath;
    private UUID cooperativeId;
    private String cooperativeName;
    private Instant registrationDate;
    private Instant lastCollectionDate;
    private Long totalBatches;
    private Double totalCollectionsKg;
    private Double totalEarnings;
}