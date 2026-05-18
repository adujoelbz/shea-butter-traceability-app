package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.service.BatchService;
import com.example.sheabutterledgersystem.service.CollectorService;
import com.example.sheabutterledgersystem.service.PaymentService;
import com.example.sheabutterledgersystem.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for dashboard summaries")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final BatchService batchService;
    private final CollectorService collectorService;
    private final PaymentService paymentService;
    private final ShipmentService shipmentService;

    @GetMapping("/summary")
    @Operation(summary = "Get complete dashboard summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        return ResponseEntity.ok(Map.of(
                "collectors", collectorService.getCollectorStatistics(),
                "batches", batchService.getBatchStatistics(),
                "shipments", shipmentService.getShipmentStatistics(),
                "statusCounts", Map.of(
                        "batches", batchService.getStatusCounts(),
                        "shipments", shipmentService.getStatusCounts()
                )
        ));
    }

    @GetMapping("/overview")
    @Operation(summary = "Get quick overview statistics")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return ResponseEntity.ok(Map.of(
                "totalCollectors", collectorService.getActiveCollectorCount(),
                "totalBatches", batchService.getBatchStatistics().get("totalBatches"),
                "totalShipments", shipmentService.getShipmentStatistics().get("totalShipments"),
                "pendingShipments", shipmentService.getStatusCounts().getOrDefault("PENDING", 0L)
        ));
    }

    @GetMapping("/recent-activity")
    @Operation(summary = "Get recent activity summary")
    public ResponseEntity<Map<String, Object>> getRecentActivity() {
        return ResponseEntity.ok(Map.of(
                "recentBatches", batchService.getRecentBatches(0, 5).getContent(),
                "recentShipments", shipmentService.getUpcomingShipments()
        ));
    }

    @GetMapping("/quality-summary")
    @Operation(summary = "Get quality check summary")
    public ResponseEntity<Map<String, Object>> getQualitySummary() {
        // Note: You'll need to inject QualityCheckService here
        return ResponseEntity.ok(Map.of(
                "message", "Quality summary endpoint - implement as needed"
        ));
    }
}