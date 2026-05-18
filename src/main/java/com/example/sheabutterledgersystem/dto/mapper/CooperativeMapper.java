package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.CooperativeRequest;
import com.example.sheabutterledgersystem.dto.response.CooperativeResponse;
import com.example.sheabutterledgersystem.model.Cooperative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface CooperativeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "collectors", ignore = true)
    Cooperative toEntity(CooperativeRequest request);

    @Mapping(target = "collectorCount", expression = "java(cooperative.getCollectors() != null ? (long) cooperative.getCollectors().size() : 0L)")
    CooperativeResponse toResponse(Cooperative cooperative);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "collectors", ignore = true)
    void updateEntity(CooperativeRequest request, @MappingTarget Cooperative cooperative);
}