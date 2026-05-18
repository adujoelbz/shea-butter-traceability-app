package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.ShipmentRequest;
import com.example.sheabutterledgersystem.dto.response.ShipmentResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipment Management", description = "Endpoints for managing shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @Operation(summary = "Create a new shipment")
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse response = shipmentService.createShipment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment by ID")
    public ResponseEntity<ShipmentResponse> getShipmentById(@PathVariable UUID id) {
        ShipmentResponse response = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{shipmentNumber}")
    @Operation(summary = "Get shipment by shipment number")
    public ResponseEntity<ShipmentResponse> getShipmentByNumber(@PathVariable String shipmentNumber) {
        ShipmentResponse response = shipmentService.getShipmentByNumber(shipmentNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all shipments with pagination")
    public ResponseEntity<PageResponse<ShipmentResponse>> getAllShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "shipmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<ShipmentResponse> responsePage = shipmentService.getAllShipments(page, size, sortBy, sortDir);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/search")
    @Operation(summary = "Search shipments")
    public ResponseEntity<PageResponse<ShipmentResponse>> searchShipments(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ShipmentResponse> responsePage = shipmentService.searchShipments(q, page, size);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/buyer")
    @Operation(summary = "Get shipments by buyer name")
    public ResponseEntity<PageResponse<ShipmentResponse>> getShipmentsByBuyer(
            @RequestParam String buyerName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ShipmentResponse> responsePage = shipmentService.getShipmentsByBuyer(buyerName, page, size);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/destination/{country}")
    @Operation(summary = "Get shipments by destination country")
    public ResponseEntity<List<ShipmentResponse>> getShipmentsByDestination(@PathVariable String country) {
        List<ShipmentResponse> responses = shipmentService.getShipmentsByDestination(country);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get shipments by status")
    public ResponseEntity<PageResponse<ShipmentResponse>> getShipmentsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ShipmentResponse> responsePage = shipmentService.getShipmentsByStatus(status, page, size);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get shipments by date range")
    public ResponseEntity<List<ShipmentResponse>> getShipmentsByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        List<ShipmentResponse> responses = shipmentService.getShipmentsByDateRange(start, end);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming shipments")
    public ResponseEntity<List<ShipmentResponse>> getUpcomingShipments() {
        List<ShipmentResponse> responses = shipmentService.getUpcomingShipments();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shipment")
    public ResponseEntity<ShipmentResponse> updateShipment(
            @PathVariable UUID id,
            @Valid @RequestBody ShipmentRequest request) {

        ShipmentResponse response = shipmentService.updateShipment(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update shipment status")
    public ResponseEntity<ShipmentResponse> updateShipmentStatus(
            @PathVariable UUID id,
            @RequestParam String status) {

        ShipmentResponse response = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shipment")
    public ResponseEntity<Void> deleteShipment(@PathVariable UUID id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shipmentId}/batches")
    @Operation(summary = "Add batch to shipment")
    public ResponseEntity<Void> addBatchToShipment(
            @PathVariable UUID shipmentId,
            @RequestParam UUID batchId,
            @RequestParam BigDecimal quantityKg) {

        shipmentService.addBatchToShipment(shipmentId, batchId, quantityKg);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{shipmentId}/batches/bulk")
    @Operation(summary = "Add multiple batches to shipment")
    public ResponseEntity<Void> addBatchesToShipment(
            @PathVariable UUID shipmentId,
            @RequestBody List<Map<String, Object>> batches) {

        shipmentService.addBatchesToShipment(shipmentId, batches);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shipmentId}/batches")
    @Operation(summary = "Get batches in shipment")
    public ResponseEntity<?> getBatchesInShipment(@PathVariable UUID shipmentId) {
        var batches = shipmentService.getBatchesInShipment(shipmentId);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/{shipmentId}/total-quantity")
    @Operation(summary = "Get total quantity in shipment")
    public ResponseEntity<Map<String, Object>> getTotalQuantityInShipment(@PathVariable UUID shipmentId) {
        BigDecimal total = shipmentService.getTotalQuantityInShipment(shipmentId);
        return ResponseEntity.ok(Map.of(
                "shipmentId", shipmentId,
                "totalQuantityKg", total
        ));
    }

    @GetMapping("/{shipmentId}/batch-count")
    @Operation(summary = "Get batch count in shipment")
    public ResponseEntity<Map<String, Object>> getBatchCountInShipment(@PathVariable UUID shipmentId) {
        long count = shipmentService.getBatchCountInShipment(shipmentId);
        return ResponseEntity.ok(Map.of(
                "shipmentId", shipmentId,
                "batchCount", count
        ));
    }

    @DeleteMapping("/{shipmentId}/batches/{batchId}")
    @Operation(summary = "Remove batch from shipment")
    public ResponseEntity<Void> removeBatchFromShipment(
            @PathVariable UUID shipmentId,
            @PathVariable UUID batchId) {

        shipmentService.removeBatchFromShipment(shipmentId, batchId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get shipment statistics")
    public ResponseEntity<Map<String, Object>> getShipmentStatistics() {
        return ResponseEntity.ok(shipmentService.getShipmentStatistics());
    }

    @GetMapping("/stats/status-counts")
    @Operation(summary = "Get shipment counts by status")
    public ResponseEntity<Map<String, Long>> getStatusCounts() {
        return ResponseEntity.ok(shipmentService.getStatusCounts());
    }
}