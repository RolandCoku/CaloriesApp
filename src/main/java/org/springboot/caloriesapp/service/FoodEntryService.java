package org.springboot.caloriesapp.service;

import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface FoodEntryService {
    FoodEntryDTO addFoodEntry(FoodEntryDTO foodEntryDTO);
    FoodEntryDTO updateFoodEntry(Long id, FoodEntryDTO foodEntryDTO);
    Double getTotalCaloriesForUser(Long userId);
    void deleteFoodEntry(Long id);
    List<FoodEntryDTO> getAllFoodEntries();
    List<FoodEntryDTO> getFoodEntriesByUserId(Long userId);
    FoodEntryDTO getFoodEntryById(Long id);
    List<FoodEntryDTO> getFoodEntriesByDate(LocalDate date);
    List<FoodEntryDTO> getFoodEntriesByUserIdAndDate(Long userId, LocalDate date);
    List<FoodEntryDTO> getFoodEntriesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}
