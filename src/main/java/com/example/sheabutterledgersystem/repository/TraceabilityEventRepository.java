package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.TraceabilityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TraceabilityEventRepository extends JpaRepository<TraceabilityEvent, UUID> {
    List<TraceabilityEvent> findByBatchIdOrderByEventDateDesc(UUID batchId);
    Page<TraceabilityEvent> findByBatchId(UUID batchId, Pageable pageable);

    List<TraceabilityEvent> findByEventTypeOrderByEventDateDesc(String eventType);

    List<TraceabilityEvent> findByPerformedById(UUID userId);

    List<TraceabilityEvent> findByEventDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM TraceabilityEvent e WHERE e.batch.id = :batchId ORDER BY e.eventDate ASC")
    List<TraceabilityEvent> getBatchTimeline(@Param("batchId") UUID batchId);

    @Query("SELECT e FROM TraceabilityEvent e WHERE e.batch.id = :batchId ORDER BY e.eventDate DESC")
    List<TraceabilityEvent> findLatestEvent(@Param("batchId") UUID batchId, Pageable pageable);


    @Query("SELECT e.eventType, COUNT(e) FROM TraceabilityEvent e GROUP BY e.eventType")
    List<Object[]> countByEventType();
}