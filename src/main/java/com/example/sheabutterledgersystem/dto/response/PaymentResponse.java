package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID batchId;
    private String batchNumber;
    private UUID collectorId;
    private String collectorName;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String transactionReference;
    private String mobileMoneyNumber;
    private String notes;
    private UUID processedBy;  // This is UUID, not User
    private LocalDateTime createdAt;
}