package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 100)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Size(max = 50)
    @ColumnDefault("'VIEWER'")
    @Column(name = "role", length = 50)
    private String role;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "uploadedBy")
    private Set<BatchPhoto> batchPhotos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Batch> batches = new LinkedHashSet<>();

    @OneToMany(mappedBy = "processedBy")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "inspector")
    private Set<QualityCheck> qualityChecks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "performedBy")
    private Set<TraceabilityEvent> traceabilityEvents = new LinkedHashSet<>();

}