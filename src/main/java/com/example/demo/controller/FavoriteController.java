package com.example.demo.controller;

import com.example.demo.model.Favorite;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.TerrainRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TerrainRepository terrainRepository;

    public FavoriteController(FavoriteRepository favoriteRepository,
                              UserRepository userRepository,
                              TerrainRepository terrainRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.terrainRepository = terrainRepository;
    }

    @GetMapping
    public List<Favorite> getAllFavorites() {
        return favoriteRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Favorite> getFavoritesByUser(@PathVariable Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @GetMapping("/terrain/{terrainId}")
    public List<Favorite> getFavoritesByTerrain(@PathVariable Long terrainId) {
        return favoriteRepository.findByTerrainId(terrainId);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(@RequestParam Long userId, @RequestParam Long terrainId) {
        boolean exists = favoriteRepository.existsByUserIdAndTerrainId(userId, terrainId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody Favorite favorite) {
        if (favorite.getUser() == null || favorite.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        if (favorite.getTerrain() == null || favorite.getTerrain().getId() == null) {
            return ResponseEntity.badRequest().body("Terrain ID is required");
        }

        if (favoriteRepository.existsByUserIdAndTerrainId(
                favorite.getUser().getId(), favorite.getTerrain().getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Already in favorites");
        }

        var userOpt = userRepository.findById(favorite.getUser().getId());
        var terrainOpt = terrainRepository.findById(favorite.getTerrain().getId());

        if (userOpt.isEmpty() || terrainOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User or Terrain not found");
        }

        favorite.setUser(userOpt.get());
        favorite.setTerrain(terrainOpt.get());
        Favorite saved = favoriteRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long terrainId) {
        if (!favoriteRepository.existsByUserIdAndTerrainId(userId, terrainId)) {
            return ResponseEntity.notFound().build();
        }
        favoriteRepository.deleteByUserIdAndTerrainId(userId, terrainId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavoriteById(@PathVariable Long id) {
        if (!favoriteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        favoriteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
