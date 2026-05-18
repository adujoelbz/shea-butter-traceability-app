package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.BatchPhoto;
import com.example.sheabutterledgersystem.model.User;
import com.example.sheabutterledgersystem.repository.BatchPhotoRepository;
import com.example.sheabutterledgersystem.repository.BatchRepository;
import com.example.sheabutterledgersystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchPhotoService {

    private final BatchPhotoRepository photoRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;

    @Transactional
    public BatchPhoto addPhoto(UUID batchId, BatchPhoto photo, UUID uploadedBy) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));

        photo.setBatch(batch);

        // Set uploaded by user if provided
        if (uploadedBy != null) {
            User user = userRepository.findById(uploadedBy).orElse(null);
            photo.setUploadedBy(user);
        }

        // If this is the first photo, make it primary
        if (photoRepository.countByBatchId(batchId) == 0) {
            photo.setIsPrimary(true);
        }

        return photoRepository.save(photo);
    }

    @Transactional
    public List<BatchPhoto> addMultiplePhotos(UUID batchId, List<BatchPhoto> photos, UUID uploadedBy) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));

        boolean isFirstPhoto = photoRepository.countByBatchId(batchId) == 0;

        for (BatchPhoto photo : photos) {
            photo.setBatch(batch);
            if (uploadedBy != null) {
                User user = userRepository.findById(uploadedBy).orElse(null);
                photo.setUploadedBy(user);
            }
            if (isFirstPhoto) {
                photo.setIsPrimary(true);
                isFirstPhoto = false;
            }
        }

        return photoRepository.saveAll(photos);
    }

    public List<BatchPhoto> getPhotosForBatch(UUID batchId) {
        return photoRepository.findByBatchIdOrderByUploadedAtDesc(batchId);
    }

    public BatchPhoto getPrimaryPhotoForBatch(UUID batchId) {
        return photoRepository.findByBatchIdAndIsPrimaryTrue(batchId)
                .orElse(null);
    }

    public BatchPhoto getPhotoById(UUID id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + id));
    }

    @Transactional
    public BatchPhoto setAsPrimary(UUID id) {
        BatchPhoto photo = getPhotoById(id);

        // Reset all photos for this batch to not primary
        photoRepository.resetPrimaryForBatch(photo.getBatch().getId());

        // Set this photo as primary
        photo.setIsPrimary(true);

        return photoRepository.save(photo);
    }

    @Transactional
    public void deletePhoto(UUID id) {
        if (!photoRepository.existsById(id)) {
            throw new EntityNotFoundException("Photo not found with id: " + id);
        }
        photoRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllPhotosForBatch(UUID batchId) {
        photoRepository.deleteByBatchId(batchId);
    }

    public long getPhotoCountForBatch(UUID batchId) {
        return photoRepository.countByBatchId(batchId);
    }
}