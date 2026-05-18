package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CooperativeResponse {
    private UUID id;
    private String name;
    private String registrationNumber;
    private String region;
    private String district;
    private String phoneNumber;
    private String email;
    private String contactPerson;
    private Boolean isActive;
    private Instant createdAt;
    private Long collectorCount;
}