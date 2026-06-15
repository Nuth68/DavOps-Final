package com.example.demo.repository;

import com.example.demo.model.TerrainImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerrainImageRepository extends JpaRepository<TerrainImage, Long> {
    List<TerrainImage> findByTerrainId(Long terrainId);
}
