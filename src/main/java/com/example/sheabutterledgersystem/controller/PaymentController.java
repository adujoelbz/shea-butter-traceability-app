package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.PaymentRequest;
import com.example.sheabutterledgersystem.dto.response.PaymentResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Complete CRUD and query endpoints for payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    // ============== CREATE ==============

    @PostMapping
    @Operation(summary = "Create a new payment")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{transactionReference}")
    @Operation(summary = "Get payment by transaction reference")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionReference(
            @PathVariable String transactionReference) {
        PaymentResponse response = paymentService.getPaymentByTransactionReference(transactionReference);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch/{batchId}")
    @Operation(summary = "Get payment by batch ID")
    public ResponseEntity<PaymentResponse> getPaymentByBatchId(@PathVariable UUID batchId) {
        PaymentResponse response = paymentService.getPaymentByBatchId(batchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/collector/{collectorId}")
    @Operation(summary = "Get all payments for a collector")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCollector(@PathVariable UUID collectorId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByCollector(collectorId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/collector/{collectorId}/paged")
    @Operation(summary = "Get paged payments for a collector")
    public ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByCollectorPaged(
            @PathVariable UUID collectorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PaymentResponse> paymentPage = paymentService.getPaymentsByCollector(collectorId, page, size);
        return ResponseEntity.ok(new PageResponse<>(paymentPage));
    }

    @GetMapping
    @Operation(summary = "Get all payments with pagination")
    public ResponseEntity<PageResponse<PaymentResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<PaymentResponse> paymentPage = paymentService.getAllPayments(page, size, sortBy, sortDir);
        return ResponseEntity.ok(new PageResponse<>(paymentPage));
    }

    @GetMapping("/method/{paymentMethod}")
    @Operation(summary = "Get payments by payment method")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        List<PaymentResponse> responses = paymentService.getPaymentsByMethod(paymentMethod);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<PaymentResponse> responses = paymentService.getPaymentsByDateRange(start, end);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range/paged")
    @Operation(summary = "Get paged payments by date range")
    public ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByDateRangePaged(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PaymentResponse> paymentPage = paymentService.getPaymentsByDateRange(start, end, page, size);
        return ResponseEntity.ok(new PageResponse<>(paymentPage));
    }

    // ============== AGGREGATES ==============

    @GetMapping("/collector/{collectorId}/total")
    @Operation(summary = "Get total payment amount for a collector")
    public ResponseEntity<Map<String, Object>> getTotalPaymentsForCollector(@PathVariable UUID collectorId) {
        BigDecimal total = paymentService.getTotalPaymentsForCollector(collectorId);
        return ResponseEntity.ok(Map.of(
                "collectorId", collectorId,
                "totalAmount", total,
                "currency", "GHS"
        ));
    }

    // ============== UPDATE ==============

    @PutMapping("/{id}")
    @Operation(summary = "Update a payment")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.updatePayment(id, request);
        return ResponseEntity.ok(response);
    }

    // ============== DELETE ==============

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    // ============== STATISTICS ==============

    @GetMapping("/statistics")
    @Operation(summary = "Get payment statistics for a period")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Map<String, Object> stats = paymentService.getPaymentStatistics(start, end);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/by-method")
    @Operation(summary = "Get payment summary by method for a period")
    public ResponseEntity<List<Map<String, Object>>> getPaymentSummaryByMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Map<String, Object>> summary = paymentService.getPaymentSummaryByMethod(start, end);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/statistics/summary")
    @Operation(summary = "Get quick payment summary")
    public ResponseEntity<Map<String, Object>> getQuickPaymentSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);

        return ResponseEntity.ok(Map.of(
                "totalAllTime", paymentService.getPaymentStatistics(now.minusYears(10), now).get("totalAmount"),
                "totalThisMonth", paymentService.getPaymentStatistics(startOfMonth, now).get("totalAmountInPeriod"),
                "totalThisYear", paymentService.getPaymentStatistics(startOfYear, now).get("totalAmountInPeriod"),
                "paymentMethods", paymentService.getPaymentSummaryByMethod(startOfMonth, now)
        ));
    }
}