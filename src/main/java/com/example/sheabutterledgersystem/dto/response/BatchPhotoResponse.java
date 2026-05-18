package com.example.sheabutterledgersystem.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BatchPhotoResponse {
    private UUID id;
    private UUID batchId;
    private String fileName;
    private String filePath;
    private String thumbnailPath;
    private Boolean isPrimary;
    private Instant uploadedAt;
    private String uploadedBy;
}