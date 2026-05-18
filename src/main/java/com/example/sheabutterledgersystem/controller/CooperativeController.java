package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.CooperativeRequest;
import com.example.sheabutterledgersystem.dto.response.CooperativeResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.CooperativeMapper;
import com.example.sheabutterledgersystem.model.Cooperative;
import com.example.sheabutterledgersystem.service.CooperativeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cooperatives")
@RequiredArgsConstructor
@Tag(name = "Cooperative Management", description = "Endpoints for managing cooperatives")
@CrossOrigin(origins = "*")
public class CooperativeController {

    private final CooperativeService cooperativeService;
    private final CooperativeMapper cooperativeMapper;

    @PostMapping
    @Operation(summary = "Create a new cooperative")
    public ResponseEntity<CooperativeResponse> createCooperative(@Valid @RequestBody CooperativeRequest request) {
        Cooperative cooperative = cooperativeMapper.toEntity(request);
        Cooperative createdCooperative = cooperativeService.createCooperative(cooperative);
        return new ResponseEntity<>(cooperativeMapper.toResponse(createdCooperative), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cooperative by ID")
    public ResponseEntity<CooperativeResponse> getCooperativeById(@PathVariable UUID id) {
        Cooperative cooperative = cooperativeService.getCooperativeById(id);
        return ResponseEntity.ok(cooperativeMapper.toResponse(cooperative));
    }

    @GetMapping
    @Operation(summary = "Get all cooperatives with pagination")
    public ResponseEntity<PageResponse<CooperativeResponse>> getAllCooperatives(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<Cooperative> cooperativePage = cooperativeService.getAllCooperatives(page, size, sortBy, sortDir);
        Page<CooperativeResponse> responsePage = cooperativePage.map(cooperativeMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/region/{region}")
    @Operation(summary = "Get cooperatives by region")
    public ResponseEntity<List<CooperativeResponse>> getCooperativesByRegion(@PathVariable String region) {
        List<Cooperative> cooperatives = cooperativeService.getCooperativesByRegion(region);
        List<CooperativeResponse> responses = cooperatives.stream()
                .map(cooperativeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    @Operation(summary = "Search cooperatives")
    public ResponseEntity<PageResponse<CooperativeResponse>> searchCooperatives(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Cooperative> cooperativePage = cooperativeService.searchCooperatives(q, page, size);
        Page<CooperativeResponse> responsePage = cooperativePage.map(cooperativeMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cooperative")
    public ResponseEntity<CooperativeResponse> updateCooperative(
            @PathVariable UUID id,
            @Valid @RequestBody CooperativeRequest request) {

        Cooperative cooperativeDetails = cooperativeMapper.toEntity(request);
        Cooperative updatedCooperative = cooperativeService.updateCooperative(id, cooperativeDetails);
        return ResponseEntity.ok(cooperativeMapper.toResponse(updatedCooperative));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update cooperative status")
    public ResponseEntity<CooperativeResponse> updateCooperativeStatus(
            @PathVariable UUID id,
            @RequestParam boolean active) {

        Cooperative updatedCooperative = cooperativeService.updateCooperativeStatus(id, active);
        return ResponseEntity.ok(cooperativeMapper.toResponse(updatedCooperative));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cooperative")
    public ResponseEntity<Void> deleteCooperative(@PathVariable UUID id) {
        cooperativeService.deleteCooperative(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/by-region")
    @Operation(summary = "Get cooperative statistics by region")
    public ResponseEntity<List<Map<String, Object>>> getCooperativeStatsByRegion() {
        List<Object[]> stats = cooperativeService.getCooperativesByRegionStats();
        List<Map<String, Object>> result = stats.stream()
                .map(stat -> Map.of(
                        "region", stat[0],
                        "count", stat[1]
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}