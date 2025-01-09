package org.springboot.caloriesapp.service.implementation;

import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.model.FoodEntry;
import org.springboot.caloriesapp.repository.FoodEntryRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FoodEntryServiceImpl implements FoodEntryService {

    private final FoodEntryRepository foodEntryRepository;
    private final UserRepository userRepository;

    public FoodEntryServiceImpl(FoodEntryRepository foodEntryRepository, UserRepository userRepository) {
        this.foodEntryRepository = foodEntryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FoodEntryDTO addFoodEntry(FoodEntryDTO foodEntryDTO) {
        FoodEntry foodEntry = new FoodEntry();

        foodEntry.setName(foodEntryDTO.getName());
        foodEntry.setCalories(foodEntryDTO.getCalories());
        foodEntry.setPrice(foodEntryDTO.getPrice());
        foodEntry.setUser(userRepository.findById(foodEntryDTO.getUserId()).orElseThrow());

        return mapToDTO(foodEntryRepository.save(foodEntry));
    }

    @Override
    public FoodEntryDTO updateFoodEntry(Long id, FoodEntryDTO foodEntryDTO) {
        FoodEntry foodEntry = foodEntryRepository.findById(id).orElseThrow(() -> new RuntimeException("Food entry not found"));

        if (foodEntryDTO.getName() != null) {
            foodEntry.setName(foodEntryDTO.getName());
        }

        if (foodEntryDTO.getCalories() != null) {
            foodEntry.setCalories(foodEntryDTO.getCalories());
        }

        if (foodEntryDTO.getPrice() != null) {
            foodEntry.setPrice(foodEntryDTO.getPrice());
        }

        if (foodEntryDTO.getUserId() != null) {
            foodEntry.setUser(userRepository.findById(foodEntryDTO.getUserId()).orElseThrow());
        }

        return mapToDTO(foodEntryRepository.save(foodEntry));
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
        return foodEntryRepository.findAll().stream().map(this::mapToDTO).toList();
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

    private FoodEntryDTO mapToDTO(FoodEntry foodEntry) {
        FoodEntryDTO foodEntryDTO = new FoodEntryDTO();
        foodEntryDTO.setId(foodEntry.getId());
        foodEntryDTO.setName(foodEntry.getName());
        foodEntryDTO.setCalories(foodEntry.getCalories());
        foodEntryDTO.setPrice(foodEntry.getPrice());
        foodEntryDTO.setUserId(foodEntry.getUser().getId());
        return foodEntryDTO;
    }
}
