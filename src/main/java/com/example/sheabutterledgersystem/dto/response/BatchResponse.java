package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BatchResponse {
    private UUID id;
    private String batchNumber;
    private UUID collectorId;
    private String collectorName;
    private String collectorPhone;
    private UUID cooperativeId;
    private String cooperativeName;
    private LocalDateTime collectionDate;  // Match entity (LocalDateTime)
    private String collectionZone;
    private String gpsCoordinates;
    private BigDecimal quantityKg;
    private String qualityGrade;
    private BigDecimal moistureContent;
    private BigDecimal impurityPercentage;
    private String qualityNotes;
    private LocalDateTime processingDate;  // Converted from Instant
    private BigDecimal processedQuantityKg;
    private BigDecimal basePricePerKg;
    private BigDecimal qualityPremium;
    private BigDecimal totalPrice;
    private String currency;
    private Boolean isPaid;
    private Instant paymentDate;  // Keep as Instant
    private String qrCodeHash;
    private String qrCodePath;
    private String status;
    private String notes;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer photosCount;
    private Integer eventsCount;
    private List<BatchPhotoResponse> photos;
    private BatchPhotoResponse primaryPhoto;
}