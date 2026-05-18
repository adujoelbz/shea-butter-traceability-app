package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.QualityCheckRequest;
import com.example.sheabutterledgersystem.dto.response.QualityCheckResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.QualityCheckMapper;
import com.example.sheabutterledgersystem.model.QualityCheck;
import com.example.sheabutterledgersystem.service.QualityCheckService;
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
@RequestMapping("/api/v1/quality-checks")
@RequiredArgsConstructor
@Tag(name = "Quality Check Management", description = "Endpoints for managing quality checks")
@CrossOrigin(origins = "*")
public class QualityCheckController {

    private final QualityCheckService qualityCheckService;
    private final QualityCheckMapper qualityCheckMapper;

    @PostMapping("/batch")
    @Operation(summary = "Perform quality check on a batch")
    public ResponseEntity<QualityCheckResponse> performQualityCheck(
            @Valid @RequestBody QualityCheckRequest request) {

        QualityCheck check = qualityCheckMapper.toEntity(request);
        QualityCheck performedCheck = qualityCheckService.performQualityCheck( request.getBatchId(), check);
        return new ResponseEntity<>(qualityCheckMapper.toResponse(performedCheck), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quality check by ID")
    public ResponseEntity<QualityCheckResponse> getQualityCheckById(@PathVariable UUID id) {
        QualityCheck check = qualityCheckService.getQualityCheckById(id);
        return ResponseEntity.ok(qualityCheckMapper.toResponse(check));
    }

    @GetMapping("/batch/{batchId}")
    @Operation(summary = "Get quality checks for a batch")
    public ResponseEntity<PageResponse<QualityCheckResponse>> getQualityChecksForBatch(
            @PathVariable UUID batchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<QualityCheck> checkPage = qualityCheckService.getQualityChecksForBatch(batchId, page, size);
        Page<QualityCheckResponse> responsePage = checkPage.map(qualityCheckMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/batch/{batchId}/latest")
    @Operation(summary = "Get latest quality check for a batch")
    public ResponseEntity<QualityCheckResponse> getLatestQualityCheckForBatch(@PathVariable UUID batchId) {
        QualityCheck check = qualityCheckService.getLatestQualityCheckForBatch(batchId);
        return check != null ? ResponseEntity.ok(qualityCheckMapper.toResponse(check)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/inspector/{inspectorId}")
    @Operation(summary = "Get quality checks by inspector")
    public ResponseEntity<List<QualityCheckResponse>> getChecksByInspector(@PathVariable UUID inspectorId) {
        List<QualityCheck> checks = qualityCheckService.getChecksByInspector(inspectorId);
        List<QualityCheckResponse> responses = checks.stream()
                .map(qualityCheckMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/passed")
    @Operation(summary = "Get all passed quality checks")
    public ResponseEntity<List<QualityCheckResponse>> getPassedChecks() {
        List<QualityCheck> checks = qualityCheckService.getPassedChecks();
        List<QualityCheckResponse> responses = checks.stream()
                .map(qualityCheckMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/failed")
    @Operation(summary = "Get all failed quality checks")
    public ResponseEntity<List<QualityCheckResponse>> getFailedChecks() {
        List<QualityCheck> checks = qualityCheckService.getFailedChecks();
        List<QualityCheckResponse> responses = checks.stream()
                .map(qualityCheckMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/grade/{qualityGrade}")
    @Operation(summary = "Get quality checks by grade")
    public ResponseEntity<List<QualityCheckResponse>> getChecksByGrade(@PathVariable String qualityGrade) {
        List<QualityCheck> checks = qualityCheckService.getChecksByGrade(qualityGrade);
        List<QualityCheckResponse> responses = checks.stream()
                .map(qualityCheckMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quality check")
    public ResponseEntity<QualityCheckResponse> updateQualityCheck(
            @PathVariable UUID id,
            @Valid @RequestBody QualityCheckRequest request) {

        QualityCheck checkDetails = qualityCheckMapper.toEntity(request);
        QualityCheck updatedCheck = qualityCheckService.updateQualityCheck(id, checkDetails);
        return ResponseEntity.ok(qualityCheckMapper.toResponse(updatedCheck));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quality check")
    public ResponseEntity<Void> deleteQualityCheck(@PathVariable UUID id) {
        qualityCheckService.deleteQualityCheck(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get quality check statistics")
    public ResponseEntity<Map<String, Object>> getQualityCheckStats() {
        return ResponseEntity.ok(Map.of(
                "gradeDistribution", qualityCheckService.getQualityGradeDistribution(),
                "passedCount", qualityCheckService.getPassedChecksCount(),
                "failedCount", qualityCheckService.getFailedChecksCount(),
                "passRate", qualityCheckService.getPassRate()
        ));
    }

    @GetMapping("/batch/{batchId}/average-moisture")
    @Operation(summary = "Get average moisture for a batch")
    public ResponseEntity<Map<String, Object>> getAverageMoistureForBatch(@PathVariable UUID batchId) {
        Double avgMoisture = qualityCheckService.getAverageMoistureForBatch(batchId);
        return ResponseEntity.ok(Map.of(
                "batchId", batchId,
                "averageMoisture", avgMoisture != null ? avgMoisture : 0
        ));
    }
}