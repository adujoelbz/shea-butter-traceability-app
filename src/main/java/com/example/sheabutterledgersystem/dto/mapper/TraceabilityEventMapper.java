package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.response.TraceabilityEventResponse;
import com.example.sheabutterledgersystem.model.TraceabilityEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = {BatchMapper.class, UserMapper.class})
public interface TraceabilityEventMapper {

    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "batchNumber", source = "batch.batchNumber")
    @Mapping(target = "performedBy", source = "performedBy.username")
    TraceabilityEventResponse toResponse(TraceabilityEvent event);
}