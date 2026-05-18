package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.Collector;
import com.example.sheabutterledgersystem.model.TraceabilityEvent;
import com.example.sheabutterledgersystem.repository.BatchRepository;
import com.example.sheabutterledgersystem.repository.CollectorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final CollectorRepository collectorRepository;
    private final TraceabilityEventService eventService;
    private final BatchPhotoService photoService;

    @Transactional
    public Batch createBatch(Batch batch, UUID collectorId) {
        // Get collector
        Collector collector = collectorRepository.findById(collectorId)
                .orElseThrow(() -> new EntityNotFoundException("Collector not found with id: " + collectorId));

        // Set collector
        batch.setCollector(collector);

        // Generate batch number if not provided
        if (batch.getBatchNumber() == null || batch.getBatchNumber().isEmpty()) {
            batch.setBatchNumber(generateBatchNumber());
        }

        // Ensure collection date is set
        if (batch.getCollectionDate() == null) {
            batch.setCollectionDate(LocalDateTime.now());
        }

        // Calculate total price if not set
        if (batch.getTotalPrice() == null && batch.getQuantityKg() != null && batch.getBasePricePerKg() != null) {
            BigDecimal total = batch.getQuantityKg().multiply(batch.getBasePricePerKg());
            if (batch.getQualityPremium() != null) {
                total = total.add(batch.getQualityPremium());
            }
            batch.setTotalPrice(total);
        }

        // Save batch
        Batch savedBatch = batchRepository.save(batch);

        // Create traceability event - FIXED: Convert LocalDateTime to Instant
        TraceabilityEvent event = new TraceabilityEvent();
        event.setBatch(savedBatch);
        event.setEventType("COLLECTION");
        event.setLocation(batch.getCollectionZone());
        event.setDescription("Batch created with number: " + savedBatch.getBatchNumber());
        event.setEventDate(Instant.now());  // Use Instant.now() directly
        eventService.createEvent(event);

        // Update collector's last collection date
        collector.setLastCollectionDate(Instant.now());
        collectorRepository.save(collector);

        return savedBatch;
    }

    private String generateBatchNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = batchRepository.count() + 1;
        return String.format("SHEA-%s-%04d", datePart, count);
    }

    public Batch getBatchById(UUID id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + id));
    }

    public Batch getBatchByNumber(String batchNumber) {
        return batchRepository.findByBatchNumber(batchNumber)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with number: " + batchNumber));
    }

    public Batch getBatchByQrCode(String qrCodeHash) {
        return batchRepository.findByQrCodeHash(qrCodeHash)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with QR code: " + qrCodeHash));
    }

    public List<Batch> getBatchesByCollector(UUID collectorId) {
        return batchRepository.findByCollectorId(collectorId);
    }

    public Page<Batch> getBatchesByCollector(UUID collectorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("collectionDate").descending());
        return batchRepository.findByCollectorId(collectorId, pageable);
    }

    public List<Batch> getBatchesByStatus(String status) {
        return batchRepository.findByStatus(status);
    }

    public Page<Batch> getBatchesByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("collectionDate").descending());
        return batchRepository.findByStatus(status, pageable);
    }

    public Page<Batch> getAllBatches(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return batchRepository.findAll(pageable);
    }

    public Page<Batch> getRecentBatches(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("collectionDate").descending());
        return batchRepository.findAllByOrderByCollectionDateDesc(pageable);
    }

    public Page<Batch> searchBatches(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("collectionDate").descending());
        return batchRepository.searchBatches(searchTerm, pageable);
    }

    public List<Batch> getBatchesByDateRange(LocalDateTime start, LocalDateTime end) {
        return batchRepository.findByCollectionDateBetween(start, end);
    }

    public List<Batch> getUnpaidBatches() {
        return batchRepository.findByIsPaidFalse();
    }

    @Transactional
    public Batch updateBatch(UUID id, Batch batchDetails) {
        Batch batch = getBatchById(id);

        // Update fields
        batch.setCollectionZone(batchDetails.getCollectionZone());
        batch.setGpsCoordinates(batchDetails.getGpsCoordinates());
        batch.setQuantityKg(batchDetails.getQuantityKg());
        batch.setQualityGrade(batchDetails.getQualityGrade());
        batch.setMoistureContent(batchDetails.getMoistureContent());
        batch.setImpurityPercentage(batchDetails.getImpurityPercentage());
        batch.setQualityNotes(batchDetails.getQualityNotes());
        batch.setProcessingDate(batchDetails.getProcessingDate());
        batch.setProcessedQuantityKg(batchDetails.getProcessedQuantityKg());
        batch.setBasePricePerKg(batchDetails.getBasePricePerKg());
        batch.setQualityPremium(batchDetails.getQualityPremium());
        batch.setNotes(batchDetails.getNotes());

        // Recalculate total price if quantities changed
        if (batch.getQuantityKg() != null && batch.getBasePricePerKg() != null) {
            BigDecimal total = batch.getQuantityKg().multiply(batch.getBasePricePerKg());
            if (batch.getQualityPremium() != null) {
                total = total.add(batch.getQualityPremium());
            }
            batch.setTotalPrice(total);
        }

        return batchRepository.save(batch);
    }

    @Transactional
    public Batch updateBatchStatus(UUID id, String status, String notes) {
        Batch batch = getBatchById(id);
        String oldStatus = batch.getStatus();
        batch.setStatus(status);

        // Create traceability event for status change - FIXED: Use Instant.now()
        TraceabilityEvent event = new TraceabilityEvent();
        event.setBatch(batch);
        event.setEventType(status);
        event.setLocation(batch.getCollectionZone());
        event.setDescription(String.format("Status changed from %s to %s. %s", oldStatus, status, notes));
        event.setEventDate(Instant.now());  // Use Instant.now()
        eventService.createEvent(event);

        return batchRepository.save(batch);
    }

    @Transactional
    public Batch markAsPaid(UUID id, LocalDateTime paymentDate) {
        Batch batch = getBatchById(id);
        batch.setIsPaid(true);

        // Convert LocalDateTime to Instant properly
        if (paymentDate != null) {
            batch.setPaymentDate(paymentDate.toInstant(ZoneOffset.UTC));
        } else {
            batch.setPaymentDate(Instant.now());
        }

        // Update status if not already paid
        if (!"PAID".equals(batch.getStatus())) {
            batch.setStatus("PAID");
        }

        // Create traceability event - FIXED: Use Instant.now()
        TraceabilityEvent event = new TraceabilityEvent();
        event.setBatch(batch);
        event.setEventType("PAYMENT");
        event.setDescription("Payment recorded for batch");
        event.setEventDate(Instant.now());  // Use Instant.now()
        eventService.createEvent(event);

        return batchRepository.save(batch);
    }

    @Transactional
    public void deleteBatch(UUID id) {
        if (!batchRepository.existsById(id)) {
            throw new EntityNotFoundException("Batch not found with id: " + id);
        }

        // Delete associated photos first
        photoService.deleteAllPhotosForBatch(id);

        batchRepository.deleteById(id);
    }

    public Map<String, Object> getBatchStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalBatches", batchRepository.count());
        stats.put("statusBreakdown", batchRepository.countByStatusGroup());
        stats.put("qualityBreakdown", batchRepository.getQualityGradeStats());
        stats.put("monthlyTrends", batchRepository.getMonthlyTrends());

        // Calculate total quantity
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1);
        Double totalQuantity = batchRepository.getTotalQuantityBetween(startOfYear, LocalDateTime.now());
        stats.put("totalQuantityYearToDate", totalQuantity != null ? totalQuantity : 0);

        return stats;
    }

    public Map<String, Long> getStatusCounts() {
        List<Object[]> results = batchRepository.countByStatusGroup();
        Map<String, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            counts.put((String) result[0], (Long) result[1]);
        }

        return counts;
    }

    public long getBatchCountByStatus(String status) {
        return batchRepository.countByStatus(status);
    }
}