package org.springboot.caloriesapp.service.implementation;

import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.model.FoodEntry;
import org.springboot.caloriesapp.repository.FoodEntryRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FoodEntryServiceImpl implements FoodEntryService {

    private final FoodEntryRepository foodEntryRepository;
    private final UserRepository userRepository;

    public FoodEntryServiceImpl(FoodEntryRepository foodEntryRepository, UserRepository userRepository) {
        this.foodEntryRepository = foodEntryRepository;
        this.userRepository = userRepository;
    }

    // ----------------------- User-Specific Methods -----------------------

    @Override
    public FoodEntryDTO addFoodEntry(Long userId, FoodEntryDTO foodEntryDTO) {
        FoodEntry foodEntry = new FoodEntry();
        foodEntry.setName(foodEntryDTO.getName());
        foodEntry.setCalories(foodEntryDTO.getCalories());
        foodEntry.setPrice(foodEntryDTO.getPrice());
        foodEntry.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        foodEntry.setCreatedAt(LocalDateTime.now());
        foodEntry.setUpdatedAt(LocalDateTime.now());

        return mapToFoodEntryDTO(foodEntryRepository.save(foodEntry));
    }

    @Override
    public FoodEntryDTO updateFoodEntry(Long userId, Long id, FoodEntryDTO foodEntryDTO) {
        FoodEntry foodEntry = foodEntryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Food entry not found or access denied"));

        if (foodEntryDTO.getName() != null) {
            foodEntry.setName(foodEntryDTO.getName());
        }
        if (foodEntryDTO.getCalories() != null) {
            foodEntry.setCalories(foodEntryDTO.getCalories());
        }
        if (foodEntryDTO.getPrice() != null) {
            foodEntry.setPrice(foodEntryDTO.getPrice());
        }
        foodEntry.setUpdatedAt(LocalDateTime.now());

        return mapToFoodEntryDTO(foodEntryRepository.save(foodEntry));
    }

    @Override
    public void deleteFoodEntry(Long userId, Long id) {
        FoodEntry foodEntry = foodEntryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Food entry not found or access denied"));

        foodEntryRepository.delete(foodEntry);
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByUserId(Long userId) {
        return foodEntryRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToFoodEntryDTO)
                .toList();
    }

    @Override
    public FoodEntryDTO getFoodEntryById(Long userId, Long id) {
        return foodEntryRepository.findByIdAndUserId(id, userId)
                .map(this::mapToFoodEntryDTO)
                .orElseThrow(() -> new RuntimeException("Food entry not found or access denied"));
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByUserIdAndDate(Long userId, LocalDate date) {
        return foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                        userId,
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX))
                .stream()
                .map(this::mapToFoodEntryDTO)
                .toList();
    }

    @Override
    public List<FoodEntryDTO> getFoodEntriesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                        userId,
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX))
                .stream()
                .map(this::mapToFoodEntryDTO)
                .toList();
    }

    @Override
    public Double getTotalCaloriesForUser(Long userId) {
        return foodEntryRepository.findAllByUserId(userId)
                .stream()
                .mapToDouble(FoodEntry::getCalories)
                .sum();
    }

    @Override
    public Double getTotalCaloriesForUserByDate(Long userId, LocalDate date) {
        return foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                        userId,
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX))
                .stream()
                .mapToDouble(FoodEntry::getCalories)
                .sum();
    }

    @Override
    public Double getTotalCaloriesForUserByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                        userId,
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX))
                .stream()
                .mapToDouble(FoodEntry::getCalories)
                .sum();
    }

    @Override
    public Double getWeaklyAverageCaloriesByUser(Long userId) {
        List<FoodEntry> foodEntriesOnTheLastWeek = foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                userId,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());
        double totalCalories = foodEntriesOnTheLastWeek.stream()
                .mapToDouble(FoodEntry::getCalories)
                .sum();

        return totalCalories / (foodEntriesOnTheLastWeek.isEmpty() ? 1 : foodEntriesOnTheLastWeek.size());
    }

    // ----------------------- Utility Method -----------------------

    private FoodEntryDTO mapToFoodEntryDTO(FoodEntry foodEntry) {
        FoodEntryDTO foodEntryDTO = new FoodEntryDTO();
        foodEntryDTO.setId(foodEntry.getId());
        foodEntryDTO.setName(foodEntry.getName());
        foodEntryDTO.setCalories(foodEntry.getCalories());
        foodEntryDTO.setPrice(foodEntry.getPrice());
        foodEntryDTO.setCreatedAt(foodEntry.getCreatedAt());
        foodEntryDTO.setUpdatedAt(foodEntry.getUpdatedAt());
        return foodEntryDTO;
    }
}