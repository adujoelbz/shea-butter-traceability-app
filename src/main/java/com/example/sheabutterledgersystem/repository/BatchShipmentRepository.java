package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.BatchShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchShipmentRepository extends JpaRepository<BatchShipment, UUID> {
    List<BatchShipment> findByShipmentId(UUID shipmentId);

    List<BatchShipment> findByBatchId(UUID batchId);

    Optional<BatchShipment> findByBatchIdAndShipmentId(UUID batchId, UUID shipmentId);

    @Query("SELECT SUM(bs.quantityKg) FROM BatchShipment bs WHERE bs.shipment.id = :shipmentId")
    Double getTotalQuantityInShipment(@Param("shipmentId") UUID shipmentId);

    long countByShipmentId(UUID shipmentId);

    void deleteByShipmentId(UUID shipmentId);

    void deleteByBatchIdAndShipmentId(UUID batchId, UUID shipmentId);
}