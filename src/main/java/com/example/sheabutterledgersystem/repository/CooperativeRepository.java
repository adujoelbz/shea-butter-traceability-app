package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.Cooperative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CooperativeRepository extends JpaRepository<Cooperative, UUID> {
    Optional<Cooperative> findByRegistrationNumber(String registrationNumber);

    List<Cooperative> findByNameContainingIgnoreCase(String name);

    List<Cooperative> findByRegion(String region);

    List<Cooperative> findByDistrict(String district);

    List<Cooperative> findByIsActiveTrue();

    @Query("SELECT c FROM Cooperative c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.region) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.district) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Cooperative> searchCooperatives(@Param("search") String search, Pageable pageable);

    @Query("SELECT c.region, COUNT(c) FROM Cooperative c GROUP BY c.region")
    List<Object[]> countByRegion();
}