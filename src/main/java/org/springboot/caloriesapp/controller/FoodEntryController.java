package org.springboot.caloriesapp.controller;

import jakarta.validation.Valid;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/food-entries")
public class FoodEntryController {

    private final FoodEntryService foodEntryService;

    public FoodEntryController(FoodEntryService foodEntryService) {
        this.foodEntryService = foodEntryService;
    }

    // Get all food entries
    @GetMapping
    public ResponseEntity<List<FoodEntryDTO>> getAllFoodEntries() {
        return ResponseEntity.ok(foodEntryService.getAllFoodEntries());
    }

    // Add a new food entry
    @PostMapping("/add")
    public ResponseEntity<?> addFoodEntry(@Valid @RequestBody FoodEntryDTO foodEntryDTO) {
        try {
            return ResponseEntity.ok(foodEntryService.addFoodEntry(foodEntryDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error adding food entry: " + e.getMessage());
        }
    }

    // Update an existing food entry
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFoodEntry(
            @PathVariable Long id,
            @Valid @RequestBody FoodEntryDTO foodEntryDTO) {
        try {
            return ResponseEntity.ok(foodEntryService.updateFoodEntry(id, foodEntryDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error updating food entry: " + e.getMessage());
        }
    }

    // Delete a food entry
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFoodEntry(@PathVariable Long id) {
        try {
            foodEntryService.deleteFoodEntry(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting food entry: " + e.getMessage());
        }
    }

    // Get all food entries by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFoodEntriesByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entries: " + e.getMessage());
        }
    }

    // Get a specific food entry by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodEntryById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(foodEntryService.getFoodEntryById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entry: " + e.getMessage());
        }
    }

    //Get all food entries by date
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getFoodEntriesByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(foodEntryService.getFoodEntriesByDate(parsedDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entries: " + e.getMessage());
        }
    }

    // Get food entries by user ID and date
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<?> getFoodEntriesByUserIdAndDate(
            @PathVariable Long userId,
            @PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserIdAndDate(userId, parsedDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entries: " + e.getMessage());
        }
    }

    // Get food entries by user ID and date range
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<?> getFoodEntriesByUserIdAndDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);

            if (parsedStartDate.isAfter(parsedEndDate)) {
                return ResponseEntity.badRequest().body("Start date must be before or equal to end date.");
            }

            return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserIdAndDateRange(userId, parsedStartDate, parsedEndDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entries: " + e.getMessage());
        }
    }

    // Get total calories for the food entries by USER ID
    @GetMapping("/user/{userId}/total-calories")
    public ResponseEntity<?> getTotalCaloriesForUser(
            @PathVariable Long userId) {
        try {
            return ResponseEntity.ok(foodEntryService.getTotalCaloriesForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total calories: " + e.getMessage());
        }
    }

    // Get total calories for the food entries by USER ID and DATE
    @GetMapping("/user/{userId}/total-calories/date/{date}")
    public ResponseEntity<?> getTotalCaloriesForUserByDate(
            @PathVariable Long userId,
            @PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(foodEntryService.getTotalCaloriesForUserByDate(userId, parsedDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total calories: " + e.getMessage());
        }
    }

    // Get total calories for the food entries by USER ID and DATE RANGE
    @GetMapping("/user/{userId}/total-calories/date-range")
    public ResponseEntity<?> getTotalCaloriesForUserByDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);

            if (parsedStartDate.isAfter(parsedEndDate)) {
                return ResponseEntity.badRequest().body("Start date must be before or equal to end date.");
            }

            return ResponseEntity.ok(foodEntryService.getTotalCaloriesForUserByDateRange(userId, parsedStartDate, parsedEndDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total calories: " + e.getMessage());
        }
    }
}
