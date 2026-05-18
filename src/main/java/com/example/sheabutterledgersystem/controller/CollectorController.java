package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.CollectorRequest;
import com.example.sheabutterledgersystem.dto.response.CollectorResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.CollectorMapper;
import com.example.sheabutterledgersystem.model.Collector;
import com.example.sheabutterledgersystem.service.CollectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/collectors")
@RequiredArgsConstructor
@Tag(name = "Collector Management", description = "Endpoints for managing collectors")
@CrossOrigin(origins = "*")
public class CollectorController {

    private final CollectorService collectorService;
    private final CollectorMapper collectorMapper;

    @PostMapping
    @Operation(summary = "Register a new collector")
    public ResponseEntity<CollectorResponse> createCollector(@Valid @RequestBody CollectorRequest request) {
        Collector collector = collectorMapper.toEntity(request);
        Collector createdCollector = collectorService.createCollector(collector);
        return new ResponseEntity<>(collectorMapper.toResponse(createdCollector), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get collector by ID")
    public ResponseEntity<CollectorResponse> getCollectorById(@PathVariable UUID id) {
        Collector collector = collectorService.getCollectorById(id);
        return ResponseEntity.ok(collectorMapper.toResponse(collector));
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get collector by phone number")
    public ResponseEntity<CollectorResponse> getCollectorByPhoneNumber(@PathVariable String phoneNumber) {
        Collector collector = collectorService.getCollectorByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(collectorMapper.toResponse(collector));
    }

    @GetMapping
    @Operation(summary = "Get all collectors with pagination")
    public ResponseEntity<PageResponse<CollectorResponse>> getAllCollectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "registrationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<Collector> collectorPage = collectorService.getAllCollectors(page, size, sortBy, sortDir);
        Page<CollectorResponse> responsePage = collectorPage.map(collectorMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/search")
    @Operation(summary = "Search collectors")
    public ResponseEntity<PageResponse<CollectorResponse>> searchCollectors(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Collector> collectorPage = collectorService.searchCollectors(q, page, size);
        Page<CollectorResponse> responsePage = collectorPage.map(collectorMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/village/{village}")
    @Operation(summary = "Get collectors by village")
    public ResponseEntity<List<CollectorResponse>> getCollectorsByVillage(@PathVariable String village) {
        List<Collector> collectors = collectorService.getCollectorsByVillage(village);
        List<CollectorResponse> responses = collectors.stream()
                .map(collectorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/region/{region}")
    @Operation(summary = "Get collectors by region")
    public ResponseEntity<List<CollectorResponse>> getCollectorsByRegion(@PathVariable String region) {
        List<Collector> collectors = collectorService.getCollectorsByRegion(region);
        List<CollectorResponse> responses = collectors.stream()
                .map(collectorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cooperative/{cooperativeId}")
    @Operation(summary = "Get collectors by cooperative")
    public ResponseEntity<PageResponse<CollectorResponse>> getCollectorsByCooperative(
            @PathVariable UUID cooperativeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Collector> collectorPage = collectorService.getCollectorsByCooperative(cooperativeId, page, size);
        Page<CollectorResponse> responsePage = collectorPage.map(collectorMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update collector")
    public ResponseEntity<CollectorResponse> updateCollector(
            @PathVariable UUID id,
            @Valid @RequestBody CollectorRequest request) {

        Collector collectorDetails = collectorMapper.toEntity(request);
        Collector updatedCollector = collectorService.updateCollector(id, collectorDetails);
        return ResponseEntity.ok(collectorMapper.toResponse(updatedCollector));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update collector status")
    public ResponseEntity<CollectorResponse> updateCollectorStatus(
            @PathVariable UUID id,
            @RequestParam String status) {

        Collector updatedCollector = collectorService.updateCollectorStatus(id, status);
        return ResponseEntity.ok(collectorMapper.toResponse(updatedCollector));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete collector")
    public ResponseEntity<Void> deleteCollector(@PathVariable UUID id) {
        collectorService.deleteCollector(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get collector statistics")
    public ResponseEntity<Map<String, Object>> getCollectorStatistics() {
        return ResponseEntity.ok(collectorService.getCollectorStatistics());
    }

    @GetMapping("/active/count")
    @Operation(summary = "Get active collector count")
    public ResponseEntity<Map<String, Long>> getActiveCollectorCount() {
        return ResponseEntity.ok(Map.of(
                "activeCount", collectorService.getActiveCollectorCount()
        ));
    }

    @GetMapping("/active/since")
    @Operation(summary = "Get active collectors since date")
    public ResponseEntity<List<CollectorResponse>> getActiveCollectorsSince(
            @RequestParam LocalDateTime since) {

        List<Collector> collectors = collectorService.getActiveCollectors(since);
        List<CollectorResponse> responses = collectors.stream()
                .map(collectorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}