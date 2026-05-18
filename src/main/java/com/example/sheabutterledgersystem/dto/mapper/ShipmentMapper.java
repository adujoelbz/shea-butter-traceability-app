package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.ShipmentRequest;
import com.example.sheabutterledgersystem.dto.response.ShipmentResponse;
import com.example.sheabutterledgersystem.model.BatchShipment;
import com.example.sheabutterledgersystem.model.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(config = BaseMapper.class, uses = {UserMapper.class})
public interface ShipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "batchShipments", ignore = true)
    Shipment toEntity(ShipmentRequest request);

    ShipmentResponse toResponse(Shipment shipment);

    @Mapping(target = "batchCount", expression = "java(batchShipments != null ? batchShipments.size() : 0)")
    @Mapping(target = "totalQuantityKg", source = "batchShipments", qualifiedByName = "totalQuantity")
    @Mapping(target = "batches", source = "batchShipments", qualifiedByName = "toBatchItems")
    ShipmentResponse toResponseWithBatches(Shipment shipment, List<BatchShipment> batchShipments);

    @Named("totalQuantity")
    default Double calculateTotalQuantity(List<BatchShipment> batchShipments) {
        if (batchShipments == null) return 0.0;
        return batchShipments.stream()
                .mapToDouble(bs -> bs.getQuantityKg().doubleValue())
                .sum();
    }

    @Named("toBatchItems")
    default List<ShipmentResponse.ShipmentBatchItem> toBatchItems(List<BatchShipment> batchShipments) {
        if (batchShipments == null) return null;
        return batchShipments.stream()
                .map(this::toBatchItem)
                .toList();
    }

    ShipmentResponse.ShipmentBatchItem toBatchItem(BatchShipment batchShipment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "batchShipments", ignore = true)
    void updateEntity(ShipmentRequest request, @MappingTarget Shipment shipment);
}