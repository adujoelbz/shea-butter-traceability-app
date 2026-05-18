package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quality_checks")
public class QualityCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private User inspector;

    @Column(name = "moisture_content", precision = 5, scale = 2)
    private BigDecimal moistureContent;

    @Column(name = "impurity_percentage", precision = 5, scale = 2)
    private BigDecimal impurityPercentage;

    @Size(max = 50)
    @NotNull
    @Column(name = "quality_grade", nullable = false, length = 50)
    private String qualityGrade;

    @NotNull
    @Column(name = "is_passed", nullable = false)
    private Boolean isPassed = false;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "check_date")
    private Instant checkDate;

}