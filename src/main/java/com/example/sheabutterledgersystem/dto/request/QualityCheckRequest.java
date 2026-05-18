package com.example.sheabutterledgersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class QualityCheckRequest {

    @NotNull(message = "Batch ID is required")
    private UUID batchId;

    private UUID inspectorId;
    private BigDecimal moistureContent;
    private BigDecimal impurityPercentage;

    @NotNull(message = "Quality grade is required")
    private String qualityGrade;

    @NotNull(message = "Pass/fail status is required")
    private Boolean isPassed;

    private String notes;
}