package com.example.demo.seeder;

import com.example.demo.model.*;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.model.enums.PaymentStatus;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final TerrainRepository terrainRepository;
    private final TerrainImageRepository terrainImageRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;

    public DatabaseSeeder(UserRepository userRepository,
                          TerrainRepository terrainRepository,
                          TerrainImageRepository terrainImageRepository,
                          BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          ReviewRepository reviewRepository,
                          FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.terrainRepository = terrainRepository;
        this.terrainImageRepository = terrainImageRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping.");
            return;
        }
        log.info("Seeding database...");

        // Users
        User owner1 = userRepository.save(new User("owner1", "password123", "owner1@example.com"));
        User owner2 = userRepository.save(new User("owner2", "password123", "owner2@example.com"));
        User renter1 = userRepository.save(new User("renter1", "password123", "renter1@example.com"));
        User renter2 = userRepository.save(new User("renter2", "password123", "renter2@example.com"));
        log.info("Seeded {} users", 4);

        // Terrains
        Terrain terrain1 = new Terrain();
        terrain1.setOwner(owner1);
        terrain1.setTitle("Green Field Arena");
        terrain1.setDescription("Beautiful natural grass field with floodlights, suitable for 11-a-side matches.");
        terrain1.setLocation("Phnom Penh");
        terrain1.setAreaSize(new BigDecimal("500.00"));
        terrain1.setPricePerDay(new BigDecimal("150.00"));
        terrain1.setAvailableFrom(LocalDateTime.now().minusDays(10));
        terrain1.setAvailableTo(LocalDateTime.now().plusMonths(6));
        terrain1.setIsAvailable(true);
        terrain1 = terrainRepository.save(terrain1);

        Terrain terrain2 = new Terrain();
        terrain2.setOwner(owner1);
        terrain2.setTitle("Stadium X Pitch");
        terrain2.setDescription("Artificial turf, perfect for 7-a-side games. Changing rooms available.");
        terrain2.setLocation("Siem Reap");
        terrain2.setAreaSize(new BigDecimal("300.00"));
        terrain2.setPricePerDay(new BigDecimal("100.00"));
        terrain2.setAvailableFrom(LocalDateTime.now().minusDays(5));
        terrain2.setAvailableTo(LocalDateTime.now().plusMonths(4));
        terrain2.setIsAvailable(true);
        terrain2 = terrainRepository.save(terrain2);

        Terrain terrain3 = new Terrain();
        terrain3.setOwner(owner2);
        terrain3.setTitle("Riverside Football Ground");
        terrain3.setDescription("Scenic riverside location with standard 11-a-side pitch. Parking available.");
        terrain3.setLocation("Phnom Penh");
        terrain3.setAreaSize(new BigDecimal("600.00"));
        terrain3.setPricePerDay(new BigDecimal("200.00"));
        terrain3.setAvailableFrom(LocalDateTime.now().minusDays(3));
        terrain3.setAvailableTo(LocalDateTime.now().plusMonths(3));
        terrain3.setIsAvailable(true);
        terrain3 = terrainRepository.save(terrain3);
        log.info("Seeded {} terrains", 3);

        // Terrain Images
        TerrainImage image1 = terrainImageRepository.save(new TerrainImage(terrain1, "/uploads/terrain1_main.jpg"));
        TerrainImage image2 = terrainImageRepository.save(new TerrainImage(terrain1, "/uploads/terrain1_side.jpg"));
        TerrainImage image3 = terrainImageRepository.save(new TerrainImage(terrain2, "/uploads/terrain2_main.jpg"));
        TerrainImage image4 = terrainImageRepository.save(new TerrainImage(terrain3, "/uploads/terrain3_main.jpg"));
        log.info("Seeded {} terrain images", 4);

        // Set main images
        terrain1.setMainImage(image1);
        terrain2.setMainImage(image3);
        terrain3.setMainImage(image4);
        terrainRepository.save(terrain1);
        terrainRepository.save(terrain2);
        terrainRepository.save(terrain3);

        // Bookings
        Booking booking1 = new Booking();
        booking1.setTerrain(terrain1);
        booking1.setRenter(renter1);
        booking1.setStartDate(LocalDateTime.now().plusDays(2));
        booking1.setEndDate(LocalDateTime.now().plusDays(3));
        booking1.setTotalPrice(new BigDecimal("150.00"));
        booking1.setStatus(BookingStatus.approved);
        booking1 = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setTerrain(terrain2);
        booking2.setRenter(renter1);
        booking2.setStartDate(LocalDateTime.now().plusDays(5));
        booking2.setEndDate(LocalDateTime.now().plusDays(6));
        booking2.setTotalPrice(new BigDecimal("100.00"));
        booking2.setStatus(BookingStatus.pending);
        booking2 = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setTerrain(terrain3);
        booking3.setRenter(renter2);
        booking3.setStartDate(LocalDateTime.now().plusDays(10));
        booking3.setEndDate(LocalDateTime.now().plusDays(12));
        booking3.setTotalPrice(new BigDecimal("400.00"));
        booking3.setStatus(BookingStatus.approved);
        booking3 = bookingRepository.save(booking3);
        log.info("Seeded {} bookings", 3);

        // Payments
        Payment payment1 = new Payment();
        payment1.setBooking(booking1);
        payment1.setPaymentMethod("credit_card");
        payment1.setAmountPaid(new BigDecimal("150.00"));
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setStatus(PaymentStatus.paid);
        payment1.setTransactionId("TXN-001");
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setBooking(booking3);
        payment2.setPaymentMethod("aba_pay");
        payment2.setAmountPaid(new BigDecimal("400.00"));
        payment2.setPaymentDate(LocalDateTime.now());
        payment2.setStatus(PaymentStatus.paid);
        payment2.setTransactionId("TXN-002");
        paymentRepository.save(payment2);
        log.info("Seeded {} payments", 2);

        // Reviews
        Review review1 = new Review();
        review1.setTerrain(terrain1);
        review1.setUser(renter1);
        review1.setRating(5);
        review1.setComment("Amazing field! Well maintained and great facilities.");
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setTerrain(terrain2);
        review2.setUser(renter1);
        review2.setRating(4);
        review2.setComment("Good pitch, but changing rooms could be cleaner.");
        reviewRepository.save(review2);

        Review review3 = new Review();
        review3.setTerrain(terrain3);
        review3.setUser(renter2);
        review3.setRating(5);
        review3.setComment("Beautiful location right by the river. Will book again!");
        reviewRepository.save(review3);
        log.info("Seeded {} reviews", 3);

        // Favorites
        Favorite fav1 = new Favorite();
        fav1.setUser(renter1);
        fav1.setTerrain(terrain1);
        favoriteRepository.save(fav1);

        Favorite fav2 = new Favorite();
        fav2.setUser(renter1);
        fav2.setTerrain(terrain3);
        favoriteRepository.save(fav2);

        Favorite fav3 = new Favorite();
        fav3.setUser(renter2);
        fav3.setTerrain(terrain2);
        favoriteRepository.save(fav3);
        log.info("Seeded {} favorites", 3);

        log.info("Database seeding completed successfully!");
    }
}
