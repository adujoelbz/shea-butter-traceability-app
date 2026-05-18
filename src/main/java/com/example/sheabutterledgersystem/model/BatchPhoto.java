package com.example.sheabutterledgersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "batch_photos")
public class BatchPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Size(max = 255)
    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Size(max = 500)
    @NotNull
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Size(max = 500)
    @Column(name = "thumbnail_path", length = 500)
    private String thumbnailPath;

    @Column(name = "file_type", length = 100)  // ADD THIS
    private String fileType;

    @Column(name = "file_size")  // ADD THIS
    private Long fileSize;

    @ColumnDefault("false")
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}