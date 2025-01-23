package org.springboot.caloriesapp.service.implementation;

import org.springboot.caloriesapp.dto.FoodEntryAdminDTO;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.model.FoodEntry;
import org.springboot.caloriesapp.repository.FoodEntryRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodEntryServiceImpl implements FoodEntryService {

    private final FoodEntryRepository foodEntryRepository;
    private final UserRepository userRepository;
    private final int CALORIE_LIMIT = 2500;
    private final double SPENDING_LIMIT = 1000.0;

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

    // ----------------------- Admin-Specific Methods -----------------------

    @Override
    public List<FoodEntryAdminDTO> getAllFoodEntries() {
        return foodEntryRepository.findAll()
                .stream()
                .map(this::mapToFoodEntryAdminDTO)
                .toList();
    }

    @Override
    public FoodEntryAdminDTO getFoodEntryById(Long id) {
        return foodEntryRepository.findById(id)
                .map(this::mapToFoodEntryAdminDTO)
                .orElseThrow(() -> new RuntimeException("Food entry not found"));
    }

    @Override
    public FoodEntryAdminDTO addFoodEntryForUser(FoodEntryAdminDTO foodEntryAdminDTO) {
        FoodEntry foodEntry = new FoodEntry();
        foodEntry.setName(foodEntryAdminDTO.getName());
        foodEntry.setCalories(foodEntryAdminDTO.getCalories());
        foodEntry.setPrice(foodEntryAdminDTO.getPrice());
        foodEntry.setUser(userRepository.findById(foodEntryAdminDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        foodEntry.setCreatedAt(LocalDateTime.now());
        foodEntry.setUpdatedAt(LocalDateTime.now());

        return mapToFoodEntryAdminDTO(foodEntryRepository.save(foodEntry));
    }

    @Override
    public void deleteFoodEntryById(Long id) {
        FoodEntry foodEntry = foodEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food entry not found"));
        foodEntryRepository.delete(foodEntry);
    }

    @Override
    public FoodEntryAdminDTO updateFoodEntryById(Long id, FoodEntryDTO foodEntryDTO) {
        FoodEntry foodEntry = foodEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food entry not found"));

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

        return mapToFoodEntryAdminDTO(foodEntryRepository.save(foodEntry));
    }

    @Override
    public Double getTotalSpendingForUser(Long userId) {
        return foodEntryRepository.findAllByUserId(userId)
                .stream()
                .mapToDouble(FoodEntry::getPrice)
                .sum();
    }

    @Override
    public Double getWeeklyAverageSpendingForUser(Long userId) {
        List<FoodEntry> foodEntriesOnTheLastWeek = foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                userId,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());
        double totalSpending = foodEntriesOnTheLastWeek.stream()
                .mapToDouble(FoodEntry::getPrice)
                .sum();

        return totalSpending / (foodEntriesOnTheLastWeek.isEmpty() ? 1 : foodEntriesOnTheLastWeek.size());
    }

    @Override
    public List<?> getWeeklyCaloriesForUser(Long userId) {
        //Get last 7 days food entries
        List<FoodEntry> foodEntriesOnTheLastWeek = foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                userId,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());

        //Get total calories for each day
        return foodEntriesOnTheLastWeek.stream()
                // Group by date (ignoring time)
                .collect(Collectors.groupingBy(entry -> entry.getCreatedAt().toLocalDate()))
                .entrySet()
                .stream()
                .map(entry -> {
                    // Sum the calories for all entries on the same date
                    double totalCalories = entry.getValue().stream()
                            .mapToDouble(FoodEntry::getCalories)
                            .sum();
                    // Map the result with day of the week and total calories
                    return Map.of(
                            "day", entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            "totalCalories", totalCalories
                    );
                })
                .toList();

    }

    @Override
    public List<?> getDaysOverCalorieLimit(Long userId) {
        //Get last 7-day food entries
        List<FoodEntry> foodEntriesOnTheLastWeek = foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                userId,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());

        //Get total calories for each day
        return foodEntriesOnTheLastWeek.stream()
                // Group by date (ignoring time)
                .collect(Collectors.groupingBy(entry -> entry.getCreatedAt().toLocalDate()))
                .entrySet()
                .stream()
                .map(entry -> {
                    // Sum the calories for all entries on the same date
                    double totalCalories = entry.getValue().stream()
                            .mapToDouble(FoodEntry::getCalories)
                            .sum();
                    double totalSpending = entry.getValue().stream()
                            .mapToDouble(FoodEntry::getPrice)
                            .sum();
                    // Map the result with day of the week and total calories and total spending
                    return Map.of(
                            "date", entry.getKey(),
                            "day", entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            "totalCalories", totalCalories,
                            "totalSpending", totalSpending
                    );
                }).toList();
    }

    @Override
    public Double getTotalSpendingForUserByDate(Long userId, LocalDate parsedDate) {
        return foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                        userId,
                        parsedDate.atStartOfDay(),
                        parsedDate.atTime(LocalTime.MAX))
                .stream()
                .mapToDouble(FoodEntry::getPrice)
                .sum();
    }

    @Override
    public List<?> getWeeklySpendingForUser(Long userId) {
        //Get last 7-day food entries
        List<FoodEntry> foodEntriesOnTheLastWeek = foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                userId,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());

        //Get total calories for each day
        return foodEntriesOnTheLastWeek.stream()
                // Group by date (ignoring time)
                .collect(Collectors.groupingBy(entry -> entry.getCreatedAt().toLocalDate()))
                .entrySet()
                .stream()
                .map(entry -> {
                    // Sum the calories for all entries on the same date
                    double totalSpending = entry.getValue().stream()
                            .mapToDouble(FoodEntry::getPrice)
                            .sum();
                    // Map the result with day of the week and total calories
                    return Map.of(
                            "day", entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            "totalSpending", totalSpending
                    );
                })
                .toList();
    }

    @Override
    public List<?> getWeeklySummary(Long userId) {
        //Get last 7-day food entries
        List<FoodEntry> foodEntriesOnTheLastWeek = foodEntryRepository.findAllByUserIdAndCreatedAtBetween(
                userId,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());

        // Get the total calories, total spending's and days that the total calorie was above the threshold for the week
        double totalCalories = foodEntriesOnTheLastWeek.stream()
                .mapToDouble(FoodEntry::getCalories)
                .sum();
        double totalSpending = foodEntriesOnTheLastWeek.stream()
                .mapToDouble(FoodEntry::getPrice)
                .sum();
        long daysOverCalorieLimit = foodEntriesOnTheLastWeek.stream()
                .collect(Collectors.groupingBy(entry -> entry.getCreatedAt().toLocalDate()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().stream()
                        .mapToDouble(FoodEntry::getCalories)
                        .sum() > CALORIE_LIMIT)
                .count();

        return List.of(Map.of(
                "totalCaloriesConsumed", totalCalories,
                "totalSpendings", totalSpending,
                "daysOverCalorieLimit", daysOverCalorieLimit
        ));

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

    private FoodEntryAdminDTO mapToFoodEntryAdminDTO(FoodEntry foodEntry) {
        FoodEntryAdminDTO foodEntryAdminDTO = new FoodEntryAdminDTO();
        foodEntryAdminDTO.setId(foodEntry.getId());
        foodEntryAdminDTO.setName(foodEntry.getName());
        foodEntryAdminDTO.setCalories(foodEntry.getCalories());
        foodEntryAdminDTO.setPrice(foodEntry.getPrice());
        foodEntryAdminDTO.setCreatedAt(foodEntry.getCreatedAt());
        foodEntryAdminDTO.setUpdatedAt(foodEntry.getUpdatedAt());
        if (foodEntry.getUser() != null) {
            foodEntryAdminDTO.setUserId(foodEntry.getUser().getId());
        }
        return foodEntryAdminDTO;
    }
}