package com.example.sheabutterledgersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentRequest {

    private UUID batchId;

    @NotNull(message = "Collector ID is required")
    private UUID collectorId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String currency;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String transactionReference;
    private String mobileMoneyNumber;
    private String notes;
    private UUID processedBy;
}