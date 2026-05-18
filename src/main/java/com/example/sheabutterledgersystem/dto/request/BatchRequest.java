package com.example.sheabutterledgersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BatchRequest {

    @NotNull(message = "Collector ID is required")
    private UUID collectorId;

    private UUID cooperativeId;
    private LocalDateTime collectionDate;  // Keep as LocalDateTime

    @NotBlank(message = "Collection zone is required")
    private String collectionZone;

    private String gpsCoordinates;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantityKg;

    private String qualityGrade;
    private BigDecimal moistureContent;
    private BigDecimal impurityPercentage;
    private String qualityNotes;
    private LocalDateTime processingDate;  // Keep as LocalDateTime (will be converted)
    private BigDecimal processedQuantityKg;

    @NotNull(message = "Base price per kg is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePricePerKg;

    private BigDecimal qualityPremium;
    private String notes;
    private UUID createdBy;
}