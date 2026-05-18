package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ShipmentResponse {
    private UUID id;
    private String shipmentNumber;
    private String buyerName;
    private String destinationCountry;
    private LocalDate shipmentDate;
    private String containerNumber;
    private String billOfLading;
    private String status;
    private String notes;
    private Instant createdAt;
    private Integer batchCount;
    private Double totalQuantityKg;
    private List<ShipmentBatchItem> batches;


    @Data
    public static class ShipmentBatchItem {
        private UUID batchId;
        private String batchNumber;
        private Double quantityKg;
        private String qualityGrade;
    }
}