package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.TerrainRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final TerrainRepository terrainRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository,
                             TerrainRepository terrainRepository,
                             UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.terrainRepository = terrainRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/terrain/{terrainId}")
    public List<Booking> getBookingsByTerrain(@PathVariable Long terrainId) {
        return bookingRepository.findByTerrainId(terrainId);
    }

    @GetMapping("/renter/{renterId}")
    public List<Booking> getBookingsByRenter(@PathVariable Long renterId) {
        return bookingRepository.findByRenterId(renterId);
    }

    @GetMapping("/status/{status}")
    public List<Booking> getBookingsByStatus(@PathVariable BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        if (booking.getTerrain() == null || booking.getTerrain().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (booking.getRenter() == null || booking.getRenter().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        var terrainOpt = terrainRepository.findById(booking.getTerrain().getId());
        var renterOpt = userRepository.findById(booking.getRenter().getId());

        if (terrainOpt.isEmpty() || renterOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        booking.setTerrain(terrainOpt.get());
        booking.setRenter(renterOpt.get());
        booking.setStatus(BookingStatus.pending);
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id,
                                                        @RequestParam BookingStatus status) {
        return bookingRepository.findById(id)
                .map(booking -> {
                    booking.setStatus(status);
                    return ResponseEntity.ok(bookingRepository.save(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (!bookingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
