package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.QualityCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QualityCheckRepository extends JpaRepository<QualityCheck, UUID> {
    List<QualityCheck> findByBatchIdOrderByCheckDateDesc(UUID batchId);
    Page<QualityCheck> findByBatchId(UUID batchId, Pageable pageable);

    Optional<QualityCheck> findFirstByBatchIdOrderByCheckDateDesc(UUID batchId);

    List<QualityCheck> findByInspectorId(UUID inspectorId);

    List<QualityCheck> findByIsPassedTrue();
    List<QualityCheck> findByIsPassedFalse();

    List<QualityCheck> findByQualityGrade(String qualityGrade);

    @Query("SELECT q.qualityGrade, COUNT(q) FROM QualityCheck q GROUP BY q.qualityGrade")
    List<Object[]> getQualityGradeDistribution();

    @Query("SELECT AVG(q.moistureContent) FROM QualityCheck q WHERE q.batch.id = :batchId")
    Double getAverageMoistureForBatch(@Param("batchId") UUID batchId);

    @Query("SELECT COUNT(q) FROM QualityCheck q WHERE q.isPassed = true")
    long countPassedChecks();

    @Query("SELECT COUNT(q) FROM QualityCheck q WHERE q.isPassed = false")
    long countFailedChecks();
}