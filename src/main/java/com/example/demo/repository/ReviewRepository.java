package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTerrainId(Long terrainId);
    List<Review> findByUserId(Long userId);
    boolean existsByTerrainIdAndUserId(Long terrainId, Long userId);
}
