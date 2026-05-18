package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "collectors")
public class Collector {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Size(max = 50)
    @Column(name = "national_id", length = 50)
    private String nationalId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "village", nullable = false)
    private String village;

    @Size(max = 100)
    @NotNull
    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Size(max = 100)
    @NotNull
    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @Size(max = 100)
    @Column(name = "gps_coordinates", length = 100)
    private String gpsCoordinates;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Size(max = 100)
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Size(max = 50)
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Size(max = 20)
    @Column(name = "mobile_money_number", length = 20)
    private String mobileMoneyNumber;

    @Size(max = 255)
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Size(max = 20)
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Size(max = 50)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 500)
    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "registration_date")
    private Instant registrationDate;

    @Column(name = "last_collection_date")
    private Instant lastCollectionDate;

    @OneToMany(mappedBy = "collector")
    private Set<Batch> batches = new LinkedHashSet<>();

    @OneToMany(mappedBy = "collector")
    private Set<Payment> payments = new LinkedHashSet<>();

}