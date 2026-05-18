package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TraceabilityEventResponse {
    private UUID id;
    private UUID batchId;
    private String batchNumber;
    private String eventType;
    private Instant eventDate;
    private String location;
    private String description;
    private String performedBy;
    private Instant createdAt;
}