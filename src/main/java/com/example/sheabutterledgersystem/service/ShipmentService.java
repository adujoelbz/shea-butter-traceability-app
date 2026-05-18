package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.dto.request.ShipmentRequest;
import com.example.sheabutterledgersystem.dto.response.ShipmentResponse;
import com.example.sheabutterledgersystem.dto.mapper.ShipmentMapper;
import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.BatchShipment;
import com.example.sheabutterledgersystem.model.Shipment;
import com.example.sheabutterledgersystem.repository.BatchRepository;
import com.example.sheabutterledgersystem.repository.BatchShipmentRepository;
import com.example.sheabutterledgersystem.repository.ShipmentRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final BatchRepository batchRepository;
    private final BatchShipmentRepository batchShipmentRepository;
    private final BatchService batchService;
    private final ShipmentMapper shipmentMapper;

    @Transactional
    public ShipmentResponse createShipment(ShipmentRequest request) {
        // Create shipment from request
        Shipment shipment = shipmentMapper.toEntity(request);

        // Generate shipment number if not provided
        if (shipment.getShipmentNumber() == null || shipment.getShipmentNumber().isEmpty()) {
            shipment.setShipmentNumber(generateShipmentNumber());
        }

        // Set default status
        if (shipment.getStatus() == null) {
            shipment.setStatus("PENDING");
        }

        // Set timestamps
        shipment.setCreatedAt(Instant.now());

        // Save shipment first
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Handle batches if provided
        List<BatchShipment> batchShipments = new ArrayList<>();
        if (request.getBatches() != null && !request.getBatches().isEmpty()) {
            for (ShipmentRequest.BatchShipmentItem item : request.getBatches()) {
                Batch batch = batchRepository.findById(item.getBatchId())
                        .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + item.getBatchId()));

                // Check if batch already in shipment
                if (batchShipmentRepository.findByBatchIdAndShipmentId(item.getBatchId(), savedShipment.getId()).isPresent()) {
                    throw new IllegalArgumentException("Batch already added to this shipment: " + item.getBatchId());
                }

                BatchShipment batchShipment = new BatchShipment();
                batchShipment.setShipment(savedShipment);
                batchShipment.setBatch(batch);
                batchShipment.setQuantityKg(item.getQuantityKg());
                batchShipment.setCreatedAt(Instant.now());

                BatchShipment savedBatchShipment = batchShipmentRepository.save(batchShipment);
                batchShipments.add(savedBatchShipment);

                // Update batch status
                batchService.updateBatchStatus(item.getBatchId(), "SHIPPED",
                        "Added to shipment: " + savedShipment.getShipmentNumber());
            }
        }

        // Return response with batches
        return shipmentMapper.toResponseWithBatches(savedShipment, batchShipments);
    }

    private String generateShipmentNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = shipmentRepository.count() + 1;
        return String.format("SHIP-%s-%04d", datePart, count);
    }

    public ShipmentResponse getShipmentById(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + id));

        List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(id);
        return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
    }

    public ShipmentResponse getShipmentByNumber(String shipmentNumber) {
        Shipment shipment = shipmentRepository.findByShipmentNumber(shipmentNumber)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with number: " + shipmentNumber));

        List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
        return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
    }

    public List<ShipmentResponse> getShipmentsByBuyer(String buyerName) {
        List<Shipment> shipments = shipmentRepository.findByBuyerNameContainingIgnoreCase(buyerName);
        return shipments.stream()
                .map(shipment -> {
                    List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
                    return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
                })
                .toList();
    }

    public Page<ShipmentResponse> getShipmentsByBuyer(String buyerName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("shipmentDate").descending());
        Page<Shipment> shipmentPage = shipmentRepository.findByBuyerNameContainingIgnoreCase(buyerName, pageable);

        return shipmentPage.map(shipment -> {
            List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
            return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
        });
    }

    public List<ShipmentResponse> getShipmentsByDestination(String country) {
        List<Shipment> shipments = shipmentRepository.findByDestinationCountry(country);
        return shipments.stream()
                .map(shipment -> {
                    List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
                    return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
                })
                .toList();
    }

    public List<ShipmentResponse> getShipmentsByStatus(String status) {
        List<Shipment> shipments = shipmentRepository.findByStatus(status);
        return shipments.stream()
                .map(shipment -> {
                    List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
                    return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
                })
                .toList();
    }

    public Page<ShipmentResponse> getShipmentsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("shipmentDate").descending());
        Page<Shipment> shipmentPage = shipmentRepository.findByStatus(status, pageable);

        return shipmentPage.map(shipment -> {
            List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
            return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
        });
    }

    public List<ShipmentResponse> getShipmentsByDateRange(LocalDate start, LocalDate end) {
        List<Shipment> shipments = shipmentRepository.findByShipmentDateBetween(start, end);
        return shipments.stream()
                .map(shipment -> {
                    List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
                    return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
                })
                .toList();
    }

    public List<ShipmentResponse> getUpcomingShipments() {
        List<Shipment> shipments = shipmentRepository.findByShipmentDateAfterOrderByShipmentDateAsc(LocalDate.now());
        return shipments.stream()
                .map(shipment -> {
                    List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
                    return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
                })
                .toList();
    }

    public Page<ShipmentResponse> getAllShipments(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Shipment> shipmentPage = shipmentRepository.findAll(pageable);

        return shipmentPage.map(shipment -> {
            List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
            return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
        });
    }

    public Page<ShipmentResponse> searchShipments(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("shipmentDate").descending());
        Page<Shipment> shipmentPage = shipmentRepository.searchShipments(searchTerm, pageable);

        return shipmentPage.map(shipment -> {
            List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(shipment.getId());
            return shipmentMapper.toResponseWithBatches(shipment, batchShipments);
        });
    }

    @Transactional
    public void addBatchToShipment(UUID shipmentId, UUID batchId, BigDecimal quantityKg) {
        Shipment shipment = getShipmentByIdForUpdate(shipmentId);
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));

        // Check if batch already in shipment
        if (batchShipmentRepository.findByBatchIdAndShipmentId(batchId, shipmentId).isPresent()) {
            throw new IllegalArgumentException("Batch already added to this shipment");
        }

        BatchShipment batchShipment = new BatchShipment();
        batchShipment.setShipment(shipment);
        batchShipment.setBatch(batch);
        batchShipment.setQuantityKg(quantityKg);
        batchShipment.setCreatedAt(Instant.now());

        batchShipmentRepository.save(batchShipment);

        // Update batch status
        batchService.updateBatchStatus(batchId, "SHIPPED", "Added to shipment: " + shipment.getShipmentNumber());
    }

    @Transactional
    public void addBatchesToShipment(UUID shipmentId, List<Map<String, Object>> batches) {
        for (Map<String, Object> item : batches) {
            UUID batchId = UUID.fromString(item.get("batchId").toString());
            BigDecimal quantity = BigDecimal.valueOf((Double) item.get("quantityKg"));
            addBatchToShipment(shipmentId, batchId, quantity);
        }
    }

    public List<BatchShipment> getBatchesInShipment(UUID shipmentId) {
        return batchShipmentRepository.findByShipmentId(shipmentId);
    }

    public BigDecimal getTotalQuantityInShipment(UUID shipmentId) {
        Double total = batchShipmentRepository.getTotalQuantityInShipment(shipmentId);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    public long getBatchCountInShipment(UUID shipmentId) {
        return batchShipmentRepository.countByShipmentId(shipmentId);
    }

    @Transactional
    public ShipmentResponse updateShipment(UUID id, ShipmentRequest request) {
        Shipment shipment = getShipmentByIdForUpdate(id);

        Shipment shipmentDetails = shipmentMapper.toEntity(request);
        shipment.setBuyerName(shipmentDetails.getBuyerName());
        shipment.setDestinationCountry(shipmentDetails.getDestinationCountry());
        shipment.setShipmentDate(shipmentDetails.getShipmentDate());
        shipment.setContainerNumber(shipmentDetails.getContainerNumber());
        shipment.setBillOfLading(shipmentDetails.getBillOfLading());
        shipment.setNotes(shipmentDetails.getNotes());
        shipment.setUpdatedAt(Instant.now());

        Shipment updatedShipment = shipmentRepository.save(shipment);

        List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(id);
        return shipmentMapper.toResponseWithBatches(updatedShipment, batchShipments);
    }

    @Transactional
    public ShipmentResponse updateShipmentStatus(UUID id, String status) {
        Shipment shipment = getShipmentByIdForUpdate(id);
        shipment.setStatus(status);
        shipment.setUpdatedAt(Instant.now());

        Shipment updatedShipment = shipmentRepository.save(shipment);

        List<BatchShipment> batchShipments = batchShipmentRepository.findByShipmentId(id);
        return shipmentMapper.toResponseWithBatches(updatedShipment, batchShipments);
    }

    @Transactional
    public void removeBatchFromShipment(UUID shipmentId, UUID batchId) {
        batchShipmentRepository.deleteByBatchIdAndShipmentId(batchId, shipmentId);
    }

    @Transactional
    public void deleteShipment(UUID id) {
        if (!shipmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Shipment not found with id: " + id);
        }

        // Remove all batch associations first
        batchShipmentRepository.deleteByShipmentId(id);

        shipmentRepository.deleteById(id);
    }

    public Map<String, Object> getShipmentStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalShipments", shipmentRepository.count());
        stats.put("statusBreakdown", shipmentRepository.countByStatusGroup());
        stats.put("destinationBreakdown", shipmentRepository.countByDestination());

        // Count by status
        stats.put("pending", shipmentRepository.countByStatus("PENDING"));
        stats.put("shipped", shipmentRepository.countByStatus("SHIPPED"));
        stats.put("delivered", shipmentRepository.countByStatus("DELIVERED"));

        return stats;
    }

    public Map<String, Long> getStatusCounts() {
        List<Object[]> results = shipmentRepository.countByStatusGroup();
        Map<String, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            counts.put((String) result[0], (Long) result[1]);
        }

        return counts;
    }

    // Helper method to get shipment with validation
    private Shipment getShipmentByIdForUpdate(UUID id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + id));
    }
}