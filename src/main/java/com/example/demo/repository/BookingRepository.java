package com.example.demo.repository;

import com.example.demo.model.Booking;
import com.example.demo.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTerrainId(Long terrainId);
    List<Booking> findByRenterId(Long renterId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByTerrainIdAndStatus(Long terrainId, BookingStatus status);
}
