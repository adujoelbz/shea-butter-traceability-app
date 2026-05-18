package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.CollectorRequest;
import com.example.sheabutterledgersystem.dto.response.CollectorResponse;
import com.example.sheabutterledgersystem.model.Collector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = BaseMapper.class, uses = {CooperativeMapper.class})
public interface CollectorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "lastCollectionDate", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "cooperative", ignore = true)
    @Mapping(target = "batches", ignore = true)
    @Mapping(target = "payments", ignore = true)
    Collector toEntity(CollectorRequest request);

    @Mapping(target = "fullName", expression = "java(collector.getFirstName() + \" \" + collector.getLastName())")
    @Mapping(target = "cooperativeId", source = "cooperative.id")
    @Mapping(target = "cooperativeName", source = "cooperative.name")
    @Mapping(target = "totalBatches", expression = "java(collector.getBatches() != null ? (long) collector.getBatches().size() : 0L)")
    @Mapping(target = "totalCollectionsKg", expression = "java(collector.getBatches() != null ? collector.getBatches().stream().mapToDouble(b -> b.getQuantityKg().doubleValue()).sum() : 0.0)")
    @Mapping(target = "totalEarnings", expression = "java(collector.getPayments() != null ? collector.getPayments().stream().mapToDouble(p -> p.getAmount().doubleValue()).sum() : 0.0)")
    CollectorResponse toResponse(Collector collector);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "cooperative", ignore = true)
    @Mapping(target = "batches", ignore = true)
    @Mapping(target = "payments", ignore = true)
    void updateEntity(CollectorRequest request, @MappingTarget Collector collector);
}