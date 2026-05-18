package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QualityCheckResponse {
    private UUID id;
    private UUID batchId;
    private String batchNumber;
    private UUID inspectorId;
    private String inspectorName;
    private BigDecimal moistureContent;
    private BigDecimal impurityPercentage;
    private String qualityGrade;
    private Boolean isPassed;
    private String notes;
    private LocalDateTime checkDate;  // This should be LocalDateTime
}