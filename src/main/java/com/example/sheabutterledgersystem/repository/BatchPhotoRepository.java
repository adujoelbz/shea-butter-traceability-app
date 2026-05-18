package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.BatchPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchPhotoRepository extends JpaRepository<BatchPhoto, UUID> {
    List<BatchPhoto> findByBatchIdOrderByUploadedAtDesc(UUID batchId);

    Optional<BatchPhoto> findByBatchIdAndIsPrimaryTrue(UUID batchId);

    List<BatchPhoto> findByUploadedById(UUID userId);

    long countByBatchId(UUID batchId);

    @Modifying
    @Transactional
    @Query("UPDATE BatchPhoto p SET p.isPrimary = false WHERE p.batch.id = :batchId")
    void resetPrimaryForBatch(@Param("batchId") UUID batchId);

    @Modifying
    @Transactional
    void deleteByBatchId(UUID batchId);
}