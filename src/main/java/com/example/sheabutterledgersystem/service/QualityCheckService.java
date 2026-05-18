package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.QualityCheck;
import com.example.sheabutterledgersystem.repository.BatchRepository;
import com.example.sheabutterledgersystem.repository.QualityCheckRepository;
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
public class QualityCheckService {

    private final QualityCheckRepository qualityCheckRepository;
    private final BatchRepository batchRepository;
    private final BatchService batchService;
    private final TraceabilityEventService eventService;

    @Transactional
    public QualityCheck performQualityCheck(UUID batchId, QualityCheck check) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));

        check.setBatch(batch);
        QualityCheck savedCheck = qualityCheckRepository.save(check);

        // Update batch quality grade if check passed
        if (check.getIsPassed()) {
            batch.setQualityGrade(check.getQualityGrade());
            batch.setMoistureContent(check.getMoistureContent());
            batch.setImpurityPercentage(check.getImpurityPercentage());
            batch.setQualityNotes(check.getNotes());
            batchRepository.save(batch);

            // Update batch status
            batchService.updateBatchStatus(batchId, "QUALITY_CHECKED", "Quality check passed");
        } else {
            // Create event for failed check
            eventService.createEvent(batchId, "QUALITY_FAILED", batch.getCollectionZone(),
                    "Quality check failed: " + check.getNotes());
        }

        return savedCheck;
    }

    public QualityCheck getQualityCheckById(UUID id) {
        return qualityCheckRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quality check not found with id: " + id));
    }

    public List<QualityCheck> getQualityChecksForBatch(UUID batchId) {
        return qualityCheckRepository.findByBatchIdOrderByCheckDateDesc(batchId);
    }

    public Page<QualityCheck> getQualityChecksForBatch(UUID batchId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("checkDate").descending());
        return qualityCheckRepository.findByBatchId(batchId, pageable);
    }

    public QualityCheck getLatestQualityCheckForBatch(UUID batchId) {
        return qualityCheckRepository.findFirstByBatchIdOrderByCheckDateDesc(batchId)
                .orElse(null);
    }

    public List<QualityCheck> getChecksByInspector(UUID inspectorId) {
        return qualityCheckRepository.findByInspectorId(inspectorId);
    }

    public List<QualityCheck> getPassedChecks() {
        return qualityCheckRepository.findByIsPassedTrue();
    }

    public List<QualityCheck> getFailedChecks() {
        return qualityCheckRepository.findByIsPassedFalse();
    }

    public List<QualityCheck> getChecksByGrade(String qualityGrade) {
        return qualityCheckRepository.findByQualityGrade(qualityGrade);
    }

    public List<Object[]> getQualityGradeDistribution() {
        return qualityCheckRepository.getQualityGradeDistribution();
    }

    public Double getAverageMoistureForBatch(UUID batchId) {
        return qualityCheckRepository.getAverageMoistureForBatch(batchId);
    }

    public long getPassedChecksCount() {
        return qualityCheckRepository.countPassedChecks();
    }

    public long getFailedChecksCount() {
        return qualityCheckRepository.countFailedChecks();
    }

    public double getPassRate() {
        long total = qualityCheckRepository.count();
        if (total == 0) return 0;
        long passed = qualityCheckRepository.countPassedChecks();
        return (passed * 100.0) / total;
    }

    @Transactional
    public QualityCheck updateQualityCheck(UUID id, QualityCheck checkDetails) {
        QualityCheck check = getQualityCheckById(id);

        check.setMoistureContent(checkDetails.getMoistureContent());
        check.setImpurityPercentage(checkDetails.getImpurityPercentage());
        check.setQualityGrade(checkDetails.getQualityGrade());
        check.setIsPassed(checkDetails.getIsPassed());
        check.setNotes(checkDetails.getNotes());

        return qualityCheckRepository.save(check);
    }

    @Transactional
    public void deleteQualityCheck(UUID id) {
        if (!qualityCheckRepository.existsById(id)) {
            throw new EntityNotFoundException("Quality check not found with id: " + id);
        }
        qualityCheckRepository.deleteById(id);
    }
}