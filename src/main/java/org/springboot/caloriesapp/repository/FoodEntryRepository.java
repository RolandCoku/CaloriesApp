package org.springboot.caloriesapp.repository;

import org.springboot.caloriesapp.model.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
    @Query("SELECT f FROM FoodEntry f WHERE f.user.id = :userId")
    List<FoodEntry> findAllByUserId(Long userId);

    @Query("SELECT f FROM FoodEntry f WHERE f.createdAt BETWEEN :startOfTheDay AND :endOfTheDay")
    List<FoodEntry> findAllByCreatedAtBetween(LocalDateTime startOfTheDay, LocalDateTime endOfTheDay);

    @Query("SELECT f FROM FoodEntry f WHERE f.user.id = :userId AND f.createdAt BETWEEN :startOfTheDay AND :endOfTheDay")
    List<FoodEntry> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfTheDay, LocalDateTime endOfTheDay);

    @Query("SELECT f FROM FoodEntry f WHERE f.user.id = :userId AND f.id = :id")
    Optional<FoodEntry> findByIdAndUserId(Long id, Long userId);
}
