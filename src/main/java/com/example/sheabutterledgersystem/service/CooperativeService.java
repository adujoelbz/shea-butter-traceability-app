package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.model.Cooperative;
import com.example.sheabutterledgersystem.repository.CooperativeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CooperativeService {

    private final CooperativeRepository cooperativeRepository;

    @Transactional
    public Cooperative createCooperative(Cooperative cooperative) {
        // Check if registration number already exists
        if (cooperative.getRegistrationNumber() != null &&
                cooperativeRepository.findByRegistrationNumber(cooperative.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("Registration number already exists: " + cooperative.getRegistrationNumber());
        }

        return cooperativeRepository.save(cooperative);
    }

    public Cooperative getCooperativeById(UUID id) {
        return cooperativeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cooperative not found with id: " + id));
    }

    public List<Cooperative> getCooperativesByRegion(String region) {
        return cooperativeRepository.findByRegion(region);
    }

    public Page<Cooperative> getAllCooperatives(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return cooperativeRepository.findAll(pageable);
    }

    public Page<Cooperative> searchCooperatives(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return cooperativeRepository.searchCooperatives(searchTerm, pageable);
    }

    @Transactional
    public Cooperative updateCooperative(UUID id, Cooperative cooperativeDetails) {
        Cooperative cooperative = getCooperativeById(id);

        // Update fields
        cooperative.setName(cooperativeDetails.getName());
        cooperative.setRegion(cooperativeDetails.getRegion());
        cooperative.setDistrict(cooperativeDetails.getDistrict());
        cooperative.setPhoneNumber(cooperativeDetails.getPhoneNumber());
        cooperative.setEmail(cooperativeDetails.getEmail());
        cooperative.setContactPerson(cooperativeDetails.getContactPerson());

        return cooperativeRepository.save(cooperative);
    }

    @Transactional
    public void deleteCooperative(UUID id) {
        if (!cooperativeRepository.existsById(id)) {
            throw new EntityNotFoundException("Cooperative not found with id: " + id);
        }
        cooperativeRepository.deleteById(id);
    }

    @Transactional
    public Cooperative updateCooperativeStatus(UUID id, boolean isActive) {
        Cooperative cooperative = getCooperativeById(id);
        cooperative.setIsActive(isActive);
        return cooperativeRepository.save(cooperative);
    }

    public List<Object[]> getCooperativesByRegionStats() {
        return cooperativeRepository.countByRegion();
    }
}