package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.BatchRequest;
import com.example.sheabutterledgersystem.dto.response.BatchResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.BatchMapper;
import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
@Tag(name = "Batch Management", description = "Endpoints for managing batches")
@CrossOrigin(origins = "*")
public class BatchController {

    private final BatchService batchService;
    private final BatchMapper batchMapper;

    @PostMapping
    @Operation(summary = "Create a new batch")
    public ResponseEntity<BatchResponse> createBatch(
            @Valid @RequestBody BatchRequest request,
            @RequestParam UUID collectorId) {

        Batch batch = batchMapper.toEntity(request);
        Batch createdBatch = batchService.createBatch(batch, collectorId);
        return new ResponseEntity<>(batchMapper.toResponse(createdBatch), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get batch by ID")
    public ResponseEntity<BatchResponse> getBatchById(@PathVariable UUID id) {
        Batch batch = batchService.getBatchById(id);
        return ResponseEntity.ok(batchMapper.toResponse(batch));
    }

    @GetMapping("/number/{batchNumber}")
    @Operation(summary = "Get batch by batch number")
    public ResponseEntity<BatchResponse> getBatchByNumber(@PathVariable String batchNumber) {
        Batch batch = batchService.getBatchByNumber(batchNumber);
        return ResponseEntity.ok(batchMapper.toResponse(batch));
    }

    @GetMapping("/qr/{qrCodeHash}")
    @Operation(summary = "Get batch by QR code")
    public ResponseEntity<BatchResponse> getBatchByQrCode(@PathVariable String qrCodeHash) {
        Batch batch = batchService.getBatchByQrCode(qrCodeHash);
        return ResponseEntity.ok(batchMapper.toResponse(batch));
    }

    @GetMapping
    @Operation(summary = "Get all batches with pagination")
    public ResponseEntity<PageResponse<BatchResponse>> getAllBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "collectionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<Batch> batchPage = batchService.getAllBatches(page, size, sortBy, sortDir);
        Page<BatchResponse> responsePage = batchPage.map(batchMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent batches")
    public ResponseEntity<PageResponse<BatchResponse>> getRecentBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Batch> batchPage = batchService.getRecentBatches(page, size);
        Page<BatchResponse> responsePage = batchPage.map(batchMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/search")
    @Operation(summary = "Search batches")
    public ResponseEntity<PageResponse<BatchResponse>> searchBatches(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Batch> batchPage = batchService.searchBatches(q, page, size);
        Page<BatchResponse> responsePage = batchPage.map(batchMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/collector/{collectorId}")
    @Operation(summary = "Get batches by collector")
    public ResponseEntity<PageResponse<BatchResponse>> getBatchesByCollector(
            @PathVariable UUID collectorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Batch> batchPage = batchService.getBatchesByCollector(collectorId, page, size);
        Page<BatchResponse> responsePage = batchPage.map(batchMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get batches by status")
    public ResponseEntity<PageResponse<BatchResponse>> getBatchesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Batch> batchPage = batchService.getBatchesByStatus(status, page, size);
        Page<BatchResponse> responsePage = batchPage.map(batchMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get batches by date range")
    public ResponseEntity<List<BatchResponse>> getBatchesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Batch> batches = batchService.getBatchesByDateRange(start, end);
        List<BatchResponse> responses = batches.stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/unpaid")
    @Operation(summary = "Get unpaid batches")
    public ResponseEntity<List<BatchResponse>> getUnpaidBatches() {
        List<Batch> batches = batchService.getUnpaidBatches();
        List<BatchResponse> responses = batches.stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update batch")
    public ResponseEntity<BatchResponse> updateBatch(
            @PathVariable UUID id,
            @Valid @RequestBody BatchRequest request) {

        Batch batchDetails = batchMapper.toEntity(request);
        Batch updatedBatch = batchService.updateBatch(id, batchDetails);
        return ResponseEntity.ok(batchMapper.toResponse(updatedBatch));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update batch status")
    public ResponseEntity<BatchResponse> updateBatchStatus(
            @PathVariable UUID id,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {

        Batch updatedBatch = batchService.updateBatchStatus(id, status, notes);
        return ResponseEntity.ok(batchMapper.toResponse(updatedBatch));
    }

    @PatchMapping("/{id}/mark-paid")
    @Operation(summary = "Mark batch as paid")
    public ResponseEntity<BatchResponse> markBatchAsPaid(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paymentDate) {

        Batch updatedBatch = batchService.markAsPaid(id, paymentDate);
        return ResponseEntity.ok(batchMapper.toResponse(updatedBatch));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete batch")
    public ResponseEntity<Void> deleteBatch(@PathVariable UUID id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get batch statistics")
    public ResponseEntity<Map<String, Object>> getBatchStatistics() {
        return ResponseEntity.ok(batchService.getBatchStatistics());
    }

    @GetMapping("/stats/status-counts")
    @Operation(summary = "Get batch counts by status")
    public ResponseEntity<Map<String, Long>> getStatusCounts() {
        return ResponseEntity.ok(batchService.getStatusCounts());
    }

    @GetMapping("/stats/status/{status}/count")
    @Operation(summary = "Get batch count by specific status")
    public ResponseEntity<Map<String, Serializable>> getBatchCountByStatus(@PathVariable String status) {
        return ResponseEntity.ok(Map.of(
                "status", status,
                "count", batchService.getBatchCountByStatus(status)
        ));
    }
}