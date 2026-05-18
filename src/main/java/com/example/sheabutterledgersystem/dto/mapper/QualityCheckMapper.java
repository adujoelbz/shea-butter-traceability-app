package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.QualityCheckRequest;
import com.example.sheabutterledgersystem.dto.response.QualityCheckResponse;
import com.example.sheabutterledgersystem.model.QualityCheck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(config = BaseMapper.class, uses = {BatchMapper.class, UserMapper.class})
public interface QualityCheckMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "inspector", ignore = true)
    @Mapping(target = "checkDate", expression = "java(java.time.Instant.now())")  // Set current time
    QualityCheck toEntity(QualityCheckRequest request);

    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "batchNumber", source = "batch.batchNumber")
    @Mapping(target = "inspectorId", source = "inspector.id")
    @Mapping(target = "inspectorName", expression = "java(check.getInspector() != null ? check.getInspector().getFirstName() + \" \" + check.getInspector().getLastName() : null)")
    @Mapping(target = "checkDate", source = "checkDate", qualifiedByName = "instantToLocalDateTime")
    QualityCheckResponse toResponse(QualityCheck check);

    @Named("qualityCheckInstantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant(ZoneOffset.UTC) : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "inspector", ignore = true)
    @Mapping(target = "checkDate", ignore = true)
    void updateEntity(QualityCheckRequest request, @MappingTarget QualityCheck check);
}