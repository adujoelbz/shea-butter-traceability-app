package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.PaymentRequest;
import com.example.sheabutterledgersystem.dto.response.PaymentResponse;
import com.example.sheabutterledgersystem.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "collector", ignore = true)
    @Mapping(target = "processedBy", ignore = true)  // Ignore - will be set in service
    @Mapping(target = "createdAt", ignore = true)    // Ignore - will be set in service
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "batchNumber", source = "batch.batchNumber")
    @Mapping(target = "collectorId", source = "collector.id")
    @Mapping(target = "collectorName", expression = "java(payment.getCollector() != null ? payment.getCollector().getFirstName() + \" \" + payment.getCollector().getLastName() : null)")
    @Mapping(target = "processedBy", source = "processedBy.id")  // Map User to UUID
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "collector", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(PaymentRequest request, @MappingTarget Payment payment);
}