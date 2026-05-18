package com.example.sheabutterledgersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ShipmentRequest {

    private String shipmentNumber;

    @NotBlank(message = "Buyer name is required")
    @Size(max = 255, message = "Buyer name must not exceed 255 characters")
    private String buyerName;

    @NotBlank(message = "Destination country is required")
    @Size(max = 100, message = "Destination country must not exceed 100 characters")
    private String destinationCountry;

    @NotNull(message = "Shipment date is required")
    private LocalDate shipmentDate;

    private String containerNumber;
    private String billOfLading;
    private String notes;
    private UUID createdBy;

    private List<BatchShipmentItem> batches;

    @Data
    public static class BatchShipmentItem {
        private UUID batchId;
        private BigDecimal quantityKg;
    }
}