package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.response.BatchPhotoResponse;
import com.example.sheabutterledgersystem.dto.mapper.BatchMapper;
import com.example.sheabutterledgersystem.model.BatchPhoto;
import com.example.sheabutterledgersystem.service.BatchPhotoService;
import com.example.sheabutterledgersystem.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/batches/{batchId}/photos")
@RequiredArgsConstructor
@Tag(name = "Batch Photo Management", description = "Endpoints for managing batch photos")
@CrossOrigin(origins = "*")
public class BatchPhotoController {

    private final BatchPhotoService batchPhotoService;
    private final BatchMapper batchMapper;
    private final FileStorageService fileStorageService;

    @PostMapping
    @Operation(summary = "Upload a photo for a batch")
    public ResponseEntity<BatchPhotoResponse> uploadPhoto(
            @PathVariable UUID batchId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) UUID uploadedBy) {

        try {
            // Create BatchPhoto object with file information
            BatchPhoto photo = new BatchPhoto();

            // Store the file and get the path
            String fileName = fileStorageService.storeFile(file, batchId.toString());
            String filePath = "/uploads/batches/" + batchId + "/" + fileName;

            // Set photo properties
            photo.setFileName(file.getOriginalFilename());
            photo.setFilePath(filePath);
            photo.setFileType(file.getContentType());
            photo.setFileSize(file.getSize());
            photo.setUploadedAt(Instant.now());

            // Save to database
            BatchPhoto createdPhoto = batchPhotoService.addPhoto(batchId, photo, uploadedBy);
            return new ResponseEntity<>(batchMapper.toPhotoResponse(createdPhoto), HttpStatus.CREATED);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/multiple")
    @Operation(summary = "Upload multiple photos for a batch")
    public ResponseEntity<List<BatchPhotoResponse>> uploadMultiplePhotos(
            @PathVariable UUID batchId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) UUID uploadedBy) {

        try {
            List<BatchPhoto> photos = files.stream().map(file -> {
                try {
                    BatchPhoto photo = new BatchPhoto();
                    String fileName = fileStorageService.storeFile(file, batchId.toString());
                    String filePath = "/uploads/batches/" + batchId + "/" + fileName;

                    photo.setFileName(file.getOriginalFilename());
                    photo.setFilePath(filePath);
                    photo.setFileType(file.getContentType());
                    photo.setFileSize(file.getSize());
                    photo.setUploadedAt(Instant.now());

                    return photo;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
                }
            }).collect(Collectors.toList());

            List<BatchPhoto> createdPhotos = batchPhotoService.addMultiplePhotos(batchId, photos, uploadedBy);
            List<BatchPhotoResponse> responses = createdPhotos.stream()
                    .map(batchMapper::toPhotoResponse)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(responses, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ... rest of your methods remain the same
    @GetMapping
    @Operation(summary = "Get all photos for a batch")
    public ResponseEntity<List<BatchPhotoResponse>> getPhotosForBatch(@PathVariable UUID batchId) {
        List<BatchPhoto> photos = batchPhotoService.getPhotosForBatch(batchId);
        List<BatchPhotoResponse> responses = photos.stream()
                .map(batchMapper::toPhotoResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/primary")
    @Operation(summary = "Get primary photo for a batch")
    public ResponseEntity<BatchPhotoResponse> getPrimaryPhotoForBatch(@PathVariable UUID batchId) {
        BatchPhoto photo = batchPhotoService.getPrimaryPhotoForBatch(batchId);
        return photo != null ? ResponseEntity.ok(batchMapper.toPhotoResponse(photo)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{photoId}")
    @Operation(summary = "Get photo by ID")
    public ResponseEntity<BatchPhotoResponse> getPhotoById(@PathVariable UUID photoId) {
        BatchPhoto photo = batchPhotoService.getPhotoById(photoId);
        return ResponseEntity.ok(batchMapper.toPhotoResponse(photo));
    }

    @PutMapping("/{photoId}/primary")
    @Operation(summary = "Set photo as primary")
    public ResponseEntity<BatchPhotoResponse> setPhotoAsPrimary(@PathVariable UUID photoId) {
        BatchPhoto updatedPhoto = batchPhotoService.setAsPrimary(photoId);
        return ResponseEntity.ok(batchMapper.toPhotoResponse(updatedPhoto));
    }

    @DeleteMapping("/{photoId}")
    @Operation(summary = "Delete a photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable UUID photoId) {
        batchPhotoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete all photos for a batch")
    public ResponseEntity<Void> deleteAllPhotosForBatch(@PathVariable UUID batchId) {
        batchPhotoService.deleteAllPhotosForBatch(batchId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get photo count for a batch")
    public ResponseEntity<Map<String, Object>> getPhotoCountForBatch(@PathVariable UUID batchId) {
        long count = batchPhotoService.getPhotoCountForBatch(batchId);
        return ResponseEntity.ok(Map.of(
                "batchId", batchId,
                "photoCount", count
        ));
    }
}