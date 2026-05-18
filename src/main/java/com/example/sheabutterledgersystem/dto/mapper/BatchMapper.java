package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.BatchRequest;
import com.example.sheabutterledgersystem.dto.response.BatchPhotoResponse;
import com.example.sheabutterledgersystem.dto.response.BatchResponse;
import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.BatchPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Mapper(config = BaseMapper.class)
public interface BatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "collector", ignore = true)
    @Mapping(target = "cooperative", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "batchPhotos", ignore = true)
    @Mapping(target = "batchShipments", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "qualityChecks", ignore = true)
    @Mapping(target = "traceabilityEvents", ignore = true)
    @Mapping(target = "collectionDate", source = "collectionDate")
    @Mapping(target = "processingDate", source = "processingDate", qualifiedByName = "localDateTimeToInstant")
    Batch toEntity(BatchRequest request);

    @Mapping(target = "collectorId", source = "collector.id")
    @Mapping(target = "collectorName", expression = "java(batch.getCollector() != null ? batch.getCollector().getFirstName() + \" \" + batch.getCollector().getLastName() : null)")
    @Mapping(target = "collectorPhone", source = "collector.phoneNumber")
    @Mapping(target = "cooperativeId", source = "cooperative.id")
    @Mapping(target = "cooperativeName", source = "cooperative.name")
    @Mapping(target = "photos", source = "batchPhotos")
    @Mapping(target = "primaryPhoto", source = "batchPhotos", qualifiedByName = "getPrimaryPhoto")
    @Mapping(target = "photosCount", expression = "java(batch.getBatchPhotos() != null ? batch.getBatchPhotos().size() : 0)")
    @Mapping(target = "eventsCount", expression = "java(batch.getTraceabilityEvents() != null ? batch.getTraceabilityEvents().size() : 0)")
    @Mapping(target = "processingDate", source = "processingDate", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "createdBy", source = "createdBy.id")
    BatchResponse toResponse(Batch batch);

    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant(ZoneOffset.UTC) : null;
    }

    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    @Named("getPrimaryPhoto")
    default BatchPhotoResponse getPrimaryPhoto(Set<BatchPhoto> photos) {
        if (photos == null) return null;
        return photos.stream()
                .filter(BatchPhoto::getIsPrimary)
                .findFirst()
                .map(this::toPhotoResponse)
                .orElse(null);
    }

    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "uploadedBy", source = "uploadedBy.username")  // Map User to username String
    BatchPhotoResponse toPhotoResponse(BatchPhoto photo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "collector", ignore = true)
    @Mapping(target = "cooperative", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "batchPhotos", ignore = true)
    @Mapping(target = "batchShipments", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "qualityChecks", ignore = true)
    @Mapping(target = "traceabilityEvents", ignore = true)
    @Mapping(target = "collectionDate", source = "collectionDate")
    @Mapping(target = "processingDate", source = "processingDate", qualifiedByName = "localDateTimeToInstant")
    void updateEntity(BatchRequest request, @MappingTarget Batch batch);
}