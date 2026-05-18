package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "shipment_number", nullable = false, length = 50, unique = true)
    private String shipmentNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @Size(max = 100)
    @NotNull
    @Column(name = "destination_country", nullable = false, length = 100)
    private String destinationCountry;

    @NotNull
    @Column(name = "shipment_date", nullable = false)
    private LocalDate shipmentDate;

    @Size(max = 50)
    @Column(name = "container_number", length = 50)
    private String containerNumber;

    @Size(max = 100)
    @Column(name = "bill_of_lading", length = 100)
    private String billOfLading;

    @Size(max = 50)
    @ColumnDefault("'PENDING'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp  // ADD THIS
    @Column(name = "updated_at")
    private Instant updatedAt;  // ADD THIS

    @OneToMany(mappedBy = "shipment")
    private Set<BatchShipment> batchShipments = new LinkedHashSet<>();
}