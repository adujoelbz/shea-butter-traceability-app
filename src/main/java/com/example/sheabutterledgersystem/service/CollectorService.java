package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.model.Collector;
import com.example.sheabutterledgersystem.repository.CollectorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectorService {

    private final CollectorRepository collectorRepository;

    @Transactional
    public Collector createCollector(Collector collector) {
        // Check if phone number already exists
        if (collectorRepository.findByPhoneNumber(collector.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists: " + collector.getPhoneNumber());
        }

        // Check if national ID already exists
        if (collector.getNationalId() != null &&
                collectorRepository.findByNationalId(collector.getNationalId()).isPresent()) {
            throw new IllegalArgumentException("National ID already exists: " + collector.getNationalId());
        }

        return collectorRepository.save(collector);
    }

    public Collector getCollectorById(UUID id) {
        return collectorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Collector not found with id: " + id));
    }

    public Collector getCollectorByPhoneNumber(String phoneNumber) {
        return collectorRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("Collector not found with phone: " + phoneNumber));
    }

    public List<Collector> getCollectorsByVillage(String village) {
        return collectorRepository.findByVillage(village);
    }

    public List<Collector> getCollectorsByRegion(String region) {
        return collectorRepository.findByRegion(region);
    }

    public List<Collector> getCollectorsByCooperative(UUID cooperativeId) {
        return collectorRepository.findByCooperativeId(cooperativeId);
    }

    public Page<Collector> getCollectorsByCooperative(UUID cooperativeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        return collectorRepository.findByCooperativeId(cooperativeId, pageable);
    }

    public Page<Collector> getAllCollectors(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return collectorRepository.findAll(pageable);
    }

    public Page<Collector> searchCollectors(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        return collectorRepository.searchCollectors(searchTerm, pageable);
    }

    @Transactional
    public Collector updateCollector(UUID id, Collector collectorDetails) {
        Collector collector = getCollectorById(id);

        // Update fields
        collector.setFirstName(collectorDetails.getFirstName());
        collector.setLastName(collectorDetails.getLastName());
        collector.setPhoneNumber(collectorDetails.getPhoneNumber());
        collector.setVillage(collectorDetails.getVillage());
        collector.setDistrict(collectorDetails.getDistrict());
        collector.setRegion(collectorDetails.getRegion());
        collector.setGpsCoordinates(collectorDetails.getGpsCoordinates());
        collector.setYearsOfExperience(collectorDetails.getYearsOfExperience());
        collector.setBankName(collectorDetails.getBankName());
        collector.setBankAccountNumber(collectorDetails.getBankAccountNumber());
        collector.setMobileMoneyNumber(collectorDetails.getMobileMoneyNumber());
        collector.setEmergencyContactName(collectorDetails.getEmergencyContactName());
        collector.setEmergencyContactPhone(collectorDetails.getEmergencyContactPhone());
        collector.setNotes(collectorDetails.getNotes());

        return collectorRepository.save(collector);
    }

    @Transactional
    public Collector updateCollectorStatus(UUID id, String status) {
        Collector collector = getCollectorById(id);
        collector.setStatus(status);
        return collectorRepository.save(collector);
    }

    @Transactional
    public void updateLastCollectionDate(UUID id) {
        Collector collector = getCollectorById(id);
        collector.setLastCollectionDate(Instant.from(LocalDateTime.now()));
        collectorRepository.save(collector);
    }

    @Transactional
    public void deleteCollector(UUID id) {
        if (!collectorRepository.existsById(id)) {
            throw new EntityNotFoundException("Collector not found with id: " + id);
        }
        collectorRepository.deleteById(id);
    }

    public Map<String, Object> getCollectorStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCollectors", collectorRepository.count());
        stats.put("activeCollectors", collectorRepository.countActiveCollectors());
        stats.put("collectorsByRegion", collectorRepository.getCollectorsByRegion());
        stats.put("dailyRegistrations", collectorRepository.getDailyRegistrations());

        return stats;
    }

    public long getActiveCollectorCount() {
        return collectorRepository.countActiveCollectors();
    }

    public List<Collector> getActiveCollectors(LocalDateTime since) {
        return collectorRepository.findActiveCollectors(since);
    }
}