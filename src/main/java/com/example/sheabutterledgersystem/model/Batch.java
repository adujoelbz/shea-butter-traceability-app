package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "batches")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "batch_number", nullable = false, length = 50)
    private String batchNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collector_id", nullable = false)
    private Collector collector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    @NotNull
    @Column(name = "collection_date", nullable = false)
    private LocalDateTime collectionDate;

    @Size(max = 255)
    @NotNull
    @Column(name = "collection_zone", nullable = false)
    private String collectionZone;

    @Size(max = 100)
    @Column(name = "gps_coordinates", length = 100)
    private String gpsCoordinates;

    @NotNull
    @Column(name = "quantity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;

    @Size(max = 50)
    @ColumnDefault("'GRADE_B'")
    @Column(name = "quality_grade", length = 50)
    private String qualityGrade;

    @Column(name = "moisture_content", precision = 5, scale = 2)
    private BigDecimal moistureContent;

    @Column(name = "impurity_percentage", precision = 5, scale = 2)
    private BigDecimal impurityPercentage;

    @Column(name = "quality_notes", length = Integer.MAX_VALUE)
    private String qualityNotes;

    @Column(name = "processing_date")
    private Instant processingDate;

    @Column(name = "processed_quantity_kg", precision = 10, scale = 2)
    private BigDecimal processedQuantityKg;

    @Column(name = "base_price_per_kg", precision = 10, scale = 2)
    private BigDecimal basePricePerKg;

    @ColumnDefault("0")
    @Column(name = "quality_premium", precision = 10, scale = 2)
    private BigDecimal qualityPremium;

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Size(max = 3)
    @ColumnDefault("'GHS'")
    @Column(name = "currency", length = 3)
    private String currency;

    @ColumnDefault("false")
    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Size(max = 255)
    @Column(name = "qr_code_hash")
    private String qrCodeHash;

    @Size(max = 500)
    @Column(name = "qr_code_path", length = 500)
    private String qrCodePath;

    @Size(max = 50)
    @ColumnDefault("'COLLECTED'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "batch")
    private Set<BatchPhoto> batchPhotos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "batch")
    private Set<BatchShipment> batchShipments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "batch")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "batch")
    private Set<QualityCheck> qualityChecks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "batch")
    private Set<TraceabilityEvent> traceabilityEvents = new LinkedHashSet<>();

}