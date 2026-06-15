package com.example.demo.controller;

import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.TerrainRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final TerrainRepository terrainRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            TerrainRepository terrainRepository,
                            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.terrainRepository = terrainRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/terrain/{terrainId}")
    public List<Review> getReviewsByTerrain(@PathVariable Long terrainId) {
        return reviewRepository.findByTerrainId(terrainId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUser(@PathVariable Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        if (review.getTerrain() == null || review.getTerrain().getId() == null) {
            return ResponseEntity.badRequest().body("Terrain ID is required");
        }
        if (review.getUser() == null || review.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        var terrainOpt = terrainRepository.findById(review.getTerrain().getId());
        var userOpt = userRepository.findById(review.getUser().getId());

        if (terrainOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Terrain not found");
        }
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Check if user already reviewed this terrain
        if (reviewRepository.existsByTerrainIdAndUserId(
                review.getTerrain().getId(), review.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User has already reviewed this terrain");
        }

        review.setTerrain(terrainOpt.get());
        review.setUser(userOpt.get());
        Review saved = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review reviewUpdate) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    if (reviewUpdate.getRating() != null) existing.setRating(reviewUpdate.getRating());
                    if (reviewUpdate.getComment() != null) existing.setComment(reviewUpdate.getComment());
                    return ResponseEntity.ok(reviewRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
