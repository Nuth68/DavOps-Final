package com.example.demo.controller;

import com.example.demo.model.Terrain;
import com.example.demo.model.TerrainImage;
import com.example.demo.repository.TerrainImageRepository;
import com.example.demo.repository.TerrainRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrains")
public class TerrainController {

    private final TerrainRepository terrainRepository;
    private final UserRepository userRepository;
    private final TerrainImageRepository terrainImageRepository;

    public TerrainController(TerrainRepository terrainRepository,
                             UserRepository userRepository,
                             TerrainImageRepository terrainImageRepository) {
        this.terrainRepository = terrainRepository;
        this.userRepository = userRepository;
        this.terrainImageRepository = terrainImageRepository;
    }

    @GetMapping
    public List<Terrain> getAllTerrains() {
        return terrainRepository.findAll();
    }

    @GetMapping("/available")
    public List<Terrain> getAvailableTerrains() {
        return terrainRepository.findByIsAvailableTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terrain> getTerrainById(@PathVariable Long id) {
        return terrainRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Terrain> searchByLocation(@RequestParam String location) {
        return terrainRepository.findByLocationContainingIgnoreCase(location);
    }

    @PostMapping
    public ResponseEntity<Terrain> createTerrain(@RequestBody Terrain terrain) {
        if (terrain.getOwner() == null || terrain.getOwner().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(terrain.getOwner().getId())
                .map(owner -> {
                    terrain.setOwner(owner);
                    Terrain saved = terrainRepository.save(terrain);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Terrain> updateTerrain(@PathVariable Long id, @RequestBody Terrain terrainUpdate) {
        return terrainRepository.findById(id)
                .map(existing -> {
                    if (terrainUpdate.getTitle() != null) existing.setTitle(terrainUpdate.getTitle());
                    if (terrainUpdate.getDescription() != null) existing.setDescription(terrainUpdate.getDescription());
                    if (terrainUpdate.getLocation() != null) existing.setLocation(terrainUpdate.getLocation());
                    if (terrainUpdate.getAreaSize() != null) existing.setAreaSize(terrainUpdate.getAreaSize());
                    if (terrainUpdate.getPricePerDay() != null) existing.setPricePerDay(terrainUpdate.getPricePerDay());
                    if (terrainUpdate.getAvailableFrom() != null) existing.setAvailableFrom(terrainUpdate.getAvailableFrom());
                    if (terrainUpdate.getAvailableTo() != null) existing.setAvailableTo(terrainUpdate.getAvailableTo());
                    if (terrainUpdate.getIsAvailable() != null) existing.setIsAvailable(terrainUpdate.getIsAvailable());
                    return ResponseEntity.ok(terrainRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerrain(@PathVariable Long id) {
        if (!terrainRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        terrainRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{terrainId}/images")
    public ResponseEntity<List<TerrainImage>> getTerrainImages(@PathVariable Long terrainId) {
        if (!terrainRepository.existsById(terrainId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(terrainImageRepository.findByTerrainId(terrainId));
    }

    @PostMapping("/{terrainId}/images")
    public ResponseEntity<TerrainImage> addTerrainImage(@PathVariable Long terrainId,
                                                         @RequestBody TerrainImage image) {
        return terrainRepository.findById(terrainId)
                .map(terrain -> {
                    image.setTerrain(terrain);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(terrainImageRepository.save(image));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
