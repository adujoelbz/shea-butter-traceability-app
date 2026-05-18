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
@Table(name = "cooperatives")
public class Cooperative {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 100)
    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @Size(max = 100)
    @NotNull
    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "contact_person")
    private String contactPerson;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "cooperative")
    private Set<Batch> batches = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cooperative")
    private Set<Collector> collectors = new LinkedHashSet<>();

}