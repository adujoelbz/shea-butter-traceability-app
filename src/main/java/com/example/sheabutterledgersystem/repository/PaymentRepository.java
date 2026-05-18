package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Find by transaction reference
    Optional<Payment> findByTransactionReference(String transactionReference);

    // Find payments for a collector
    List<Payment> findByCollectorIdOrderByPaymentDateDesc(UUID collectorId);
    Page<Payment> findByCollectorId(UUID collectorId, Pageable pageable);

    // Find payments for a batch
    Optional<Payment> findByBatchId(UUID batchId);

    // Find payments by method
    List<Payment> findByPaymentMethod(String paymentMethod);

    // Find payments within date range
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    Page<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Get payment summary for a collector
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.collector.id = :collectorId")
    Double getTotalPaymentsForCollector(@Param("collectorId") UUID collectorId);

    // Get payment summary by method
    @Query("SELECT p.paymentMethod, SUM(p.amount), COUNT(p) " +
            "FROM Payment p " +
            "WHERE p.paymentDate BETWEEN :start AND :end " +
            "GROUP BY p.paymentMethod")
    List<Object[]> getPaymentSummaryByMethod(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    // Get daily payment totals
    @Query("SELECT FUNCTION('DATE', p.paymentDate), SUM(p.amount), COUNT(p) " +
            "FROM Payment p " +
            "WHERE p.paymentDate BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', p.paymentDate)")
    List<Object[]> getDailyPaymentTotals(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    // Get monthly payment trends
    @Query("SELECT FUNCTION('YEAR', p.paymentDate), FUNCTION('MONTH', p.paymentDate), " +
            "SUM(p.amount), COUNT(p) " +
            "FROM Payment p " +
            "GROUP BY FUNCTION('YEAR', p.paymentDate), FUNCTION('MONTH', p.paymentDate) " +
            "ORDER BY FUNCTION('YEAR', p.paymentDate) DESC, FUNCTION('MONTH', p.paymentDate) DESC")
    List<Object[]> getMonthlyPaymentTrends();

    // Count payments by status/method (for stats)
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p GROUP BY p.paymentMethod")
    List<Object[]> countByPaymentMethod();

    // Get total payments amount
    @Query("SELECT SUM(p.amount) FROM Payment p")
    Double getTotalPaymentsAmount();

    // Get total payments amount for a period
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    Double getTotalPaymentsAmountBetween(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);
}