package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.dto.request.PaymentRequest;
import com.example.sheabutterledgersystem.dto.response.PaymentResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.PaymentMapper;
import com.example.sheabutterledgersystem.model.Payment;
import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.Collector;
import com.example.sheabutterledgersystem.model.User;
import com.example.sheabutterledgersystem.repository.PaymentRepository;
import com.example.sheabutterledgersystem.repository.BatchRepository;
import com.example.sheabutterledgersystem.repository.CollectorRepository;
import com.example.sheabutterledgersystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BatchRepository batchRepository;
    private final CollectorRepository collectorRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        // Map basic fields
        Payment payment = paymentMapper.toEntity(request);

        // Set batch if provided
        if (request.getBatchId() != null) {
            Batch batch = batchRepository.findById(request.getBatchId())
                    .orElseThrow(() -> new EntityNotFoundException("Batch not found"));
            payment.setBatch(batch);
        }

        // Set collector
        Collector collector = collectorRepository.findById(request.getCollectorId())
                .orElseThrow(() -> new EntityNotFoundException("Collector not found"));
        payment.setCollector(collector);

        // Set processed by user if provided
        if (request.getProcessedBy() != null) {
            User user = userRepository.findById(request.getProcessedBy())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            payment.setProcessedBy(user);
        }

        // Set dates
        if (request.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        payment.setCreatedAt(LocalDateTime.now());

        // Save
        Payment savedPayment = paymentRepository.save(payment);

        // Return response
        return paymentMapper.toResponse(savedPayment);
    }

    // Get payment by ID
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    // Get payment by transaction reference
    public PaymentResponse getPaymentByTransactionReference(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with reference: " + transactionReference));
        return paymentMapper.toResponse(payment);
    }

    // Get payments for a collector
    public List<PaymentResponse> getPaymentsByCollector(UUID collectorId) {
        return paymentRepository.findByCollectorIdOrderByPaymentDateDesc(collectorId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get payments for a collector with pagination
    public Page<PaymentResponse> getPaymentsByCollector(UUID collectorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        return paymentRepository.findByCollectorId(collectorId, pageable)
                .map(paymentMapper::toResponse);
    }

    // Get payment for a batch
    public PaymentResponse getPaymentByBatchId(UUID batchId) {
        Payment payment = paymentRepository.findByBatchId(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for batch: " + batchId));
        return paymentMapper.toResponse(payment);
    }

    // Get all payments with pagination
    public Page<PaymentResponse> getAllPayments(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toResponse);
    }

    // Get payments by method
    public List<PaymentResponse> getPaymentsByMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get payments by date range
    public List<PaymentResponse> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentDateBetween(start, end)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get payments by date range with pagination
    public Page<PaymentResponse> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        return paymentRepository.findByPaymentDateBetween(start, end, pageable)
                .map(paymentMapper::toResponse);
    }

    // Get total payments amount for a collector
    public BigDecimal getTotalPaymentsForCollector(UUID collectorId) {
        Double total = paymentRepository.getTotalPaymentsForCollector(collectorId);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    // Update payment
    @Transactional
    public PaymentResponse updatePayment(UUID id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        // Update fields
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setMobileMoneyNumber(request.getMobileMoneyNumber());
        payment.setNotes(request.getNotes());

        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    // Delete payment
    @Transactional
    public void deletePayment(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    // Get payment statistics
    public Map<String, Object> getPaymentStatistics(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPayments", paymentRepository.count());
        stats.put("totalAmount", paymentRepository.getTotalPaymentsAmount());
        stats.put("totalAmountInPeriod", paymentRepository.getTotalPaymentsAmountBetween(start, end));
        stats.put("paymentMethodBreakdown", paymentRepository.countByPaymentMethod());
        stats.put("dailyTotals", paymentRepository.getDailyPaymentTotals(start, end));
        stats.put("monthlyTrends", paymentRepository.getMonthlyPaymentTrends());

        return stats;
    }

    // Get payment summary by method for a period
    public List<Map<String, Object>> getPaymentSummaryByMethod(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = paymentRepository.getPaymentSummaryByMethod(start, end);
        return results.stream()
                .map(result -> Map.of(
                        "paymentMethod", result[0],
                        "totalAmount", result[1],
                        "count", result[2]
                ))
                .collect(Collectors.toList());
    }
}