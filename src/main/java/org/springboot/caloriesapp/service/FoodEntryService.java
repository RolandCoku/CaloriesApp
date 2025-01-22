package org.springboot.caloriesapp.service;

import org.springboot.caloriesapp.dto.FoodEntryAdminDTO;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface FoodEntryService {

    // User-specific methods
    FoodEntryDTO addFoodEntry(Long userId, FoodEntryDTO foodEntryDTO);
    FoodEntryDTO updateFoodEntry(Long userId, Long id, FoodEntryDTO foodEntryDTO);
    void deleteFoodEntry(Long userId, Long id);
    List<FoodEntryDTO> getFoodEntriesByUserId(Long userId);
    FoodEntryDTO getFoodEntryById(Long userId, Long id);
    List<FoodEntryDTO> getFoodEntriesByUserIdAndDate(Long userId, LocalDate date);
    List<FoodEntryDTO> getFoodEntriesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    Double getTotalCaloriesForUser(Long userId);
    Double getTotalCaloriesForUserByDate(Long userId, LocalDate date);
    Double getTotalCaloriesForUserByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    Double getWeaklyAverageCaloriesByUser(Long userId);

    // Admin-specific methods
    List<FoodEntryAdminDTO> getAllFoodEntries(); // Fetch all entries
    FoodEntryAdminDTO getFoodEntryById(Long id); // Fetch entry by ID without filtering
    FoodEntryAdminDTO addFoodEntryForUser(FoodEntryAdminDTO foodEntryAdminDTO); // Includes userId in DTO
    void deleteFoodEntryById(Long id); // Delete entry by ID without filtering
    FoodEntryAdminDTO updateFoodEntryById(Long id, FoodEntryDTO foodEntryDTO);
}

