package com.example.sheabutterledgersystem.repository;

import com.example.sheabutterledgersystem.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    Optional<Shipment> findByShipmentNumber(String shipmentNumber);

    List<Shipment> findByBuyerNameContainingIgnoreCase(String buyerName);
    Page<Shipment> findByBuyerNameContainingIgnoreCase(String buyerName, Pageable pageable);

    List<Shipment> findByDestinationCountry(String country);

    List<Shipment> findByStatus(String status);
    Page<Shipment> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);

    List<Shipment> findByShipmentDateBetween(LocalDate start, LocalDate end);

    List<Shipment> findByShipmentDateAfterOrderByShipmentDateAsc(LocalDate date);

    // Search shipments
    @Query("SELECT s FROM Shipment s WHERE " +
            "LOWER(s.shipmentNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.destinationCountry) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.containerNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Shipment> searchShipments(@Param("search") String search, Pageable pageable);

    @Query("SELECT s.status, COUNT(s) FROM Shipment s GROUP BY s.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT s.destinationCountry, COUNT(s) FROM Shipment s GROUP BY s.destinationCountry")
    List<Object[]> countByDestination();
}