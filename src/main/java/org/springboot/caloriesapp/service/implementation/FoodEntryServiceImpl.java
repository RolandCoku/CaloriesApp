package org.springboot.caloriesapp.service.implementation;

import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FoodEntryServiceImpl implements FoodEntryService {

    @Override
    public FoodEntryDTO addFoodEntry(FoodEntryDTO foodEntryDTO) {
        return null;
    }

    @Override
    public FoodEntryDTO updateFoodEntry(Long id, FoodEntryDTO foodEntryDTO) {
        return null;
    }

    @Override
    public int getTotalCalories() {
        return 0;
    }

    @Override
    public void deleteFoodEntry(Long id) {

    }

    @Override
    public List<FoodEntryDTO> getAllFoodEntries() {
        return List.of();
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByUserId(Long userId) {
        return List.of();
    }

    @Override
    public FoodEntryDTO getFoodEntryById(Long id) {
        return null;
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByUserIdAndDate(Long userId, LocalDate date) {
        return List.of();
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }
}
