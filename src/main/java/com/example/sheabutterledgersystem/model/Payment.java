package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collector_id", nullable = false)
    private Collector collector;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Size(max = 3)
    @ColumnDefault("'GHS'")
    @Column(name = "currency", length = 3)
    private String currency;

    @Size(max = 50)
    @ColumnDefault("'CASH'")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Size(max = 100)
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Size(max = 20)
    @Column(name = "mobile_money_number", length = 20)
    private String mobileMoneyNumber;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

}