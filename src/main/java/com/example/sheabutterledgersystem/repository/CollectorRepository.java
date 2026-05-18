package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.Collector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectorRepository extends JpaRepository<Collector, UUID> {
    Optional<Collector> findByNationalId(String nationalId);
    Optional<Collector> findByPhoneNumber(String phoneNumber);

    List<Collector> findByVillage(String village);
    List<Collector> findByDistrict(String district);
    List<Collector> findByRegion(String region);

    List<Collector> findByCooperativeId(UUID cooperativeId);
    Page<Collector> findByCooperativeId(UUID cooperativeId, Pageable pageable);

    List<Collector> findByStatus(String status);
    long countByStatus(String status);

    @Query("SELECT c FROM Collector c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.village) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.nationalId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Collector> searchCollectors(@Param("search") String search, Pageable pageable);

    List<Collector> findByRegistrationDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM Collector c WHERE c.lastCollectionDate >= :since")
    List<Collector> findActiveCollectors(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(c) FROM Collector c WHERE c.status = 'ACTIVE'")
    long countActiveCollectors();

    @Query("SELECT c.region, COUNT(c) FROM Collector c GROUP BY c.region")
    List<Object[]> getCollectorsByRegion();

    @Query("SELECT FUNCTION('DATE', c.registrationDate), COUNT(c) " +
            "FROM Collector c " +
            "GROUP BY FUNCTION('DATE', c.registrationDate) " +
            "ORDER BY FUNCTION('DATE', c.registrationDate) DESC")
    List<Object[]> getDailyRegistrations();


}