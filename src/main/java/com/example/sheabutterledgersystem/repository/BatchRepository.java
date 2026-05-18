package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.Batch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {

    Optional<Batch> findByBatchNumber(String batchNumber);
    Optional<Batch> findByQrCodeHash(String qrCodeHash);

    List<Batch> findByCollectorId(UUID collectorId);
    Page<Batch> findByCollectorId(UUID collectorId, Pageable pageable);

    List<Batch> findByCooperativeId(UUID cooperativeId);
    Page<Batch> findByCooperativeId(UUID cooperativeId, Pageable pageable);

    List<Batch> findByStatus(String status);
    Page<Batch> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);

    List<Batch> findByCollectionDateBetween(LocalDateTime start, LocalDateTime end);

    List<Batch> findByIsPaidFalse();

    @Query("SELECT b FROM Batch b WHERE " +
            "LOWER(b.batchNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.collector.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.collector.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.collectionZone) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Batch> searchBatches(@Param("search") String search, Pageable pageable);

    @Query("SELECT SUM(b.quantityKg) FROM Batch b WHERE b.collectionDate BETWEEN :start AND :end")
    Double getTotalQuantityBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(b.totalPrice) FROM Batch b WHERE b.paymentDate BETWEEN :start AND :end")
    Double getTotalPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT b.status, COUNT(b) FROM Batch b GROUP BY b.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT b.qualityGrade, COUNT(b), AVG(b.quantityKg) " +
            "FROM Batch b GROUP BY b.qualityGrade")
    List<Object[]> getQualityGradeStats();

    @Query("SELECT FUNCTION('DATE', b.collectionDate), COUNT(b), SUM(b.quantityKg) " +
            "FROM Batch b " +
            "WHERE b.collectionDate BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', b.collectionDate)")
    List<Object[]> getDailyBatchSummary(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('YEAR', b.collectionDate), FUNCTION('MONTH', b.collectionDate), " +
            "COUNT(b), SUM(b.quantityKg), SUM(b.totalPrice) " +
            "FROM Batch b " +
            "GROUP BY FUNCTION('YEAR', b.collectionDate), FUNCTION('MONTH', b.collectionDate) " +
            "ORDER BY FUNCTION('YEAR', b.collectionDate) DESC, FUNCTION('MONTH', b.collectionDate) DESC")
    List<Object[]> getMonthlyTrends();

    Page<Batch> findAllByOrderByCollectionDateDesc(Pageable pageable);

}