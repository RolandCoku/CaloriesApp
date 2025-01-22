package org.springboot.caloriesapp.controller;

import jakarta.validation.Valid;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springboot.caloriesapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/food-entries")
public class FoodEntryController {

    private final FoodEntryService foodEntryService;
    private final UserService userService;

    public FoodEntryController(FoodEntryService foodEntryService, UserService userService) {
        this.foodEntryService = foodEntryService;
        this.userService = userService;
    }

    // Utility method to retrieve userId from Authentication
    private Long getAuthenticatedUserId(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserIdByUsername(username);
    }

    // Get all food entries for the authenticated user
    @GetMapping("/user/entries")
    public ResponseEntity<?> getFoodEntriesByAuthenticatedUser(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entries: " + e.getMessage());
        }
    }

    // Add a new food entry for the authenticated user
    @PostMapping("/user/add")
    public ResponseEntity<?> addFoodEntry(
            @Valid @RequestBody FoodEntryDTO foodEntryDTO,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.addFoodEntry(userId, foodEntryDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error adding food entry: " + e.getMessage());
        }
    }

    // Update an existing food entry for the authenticated user
    @PutMapping("/user/update/{id}")
    public ResponseEntity<?> updateFoodEntry(
            @PathVariable Long id,
            @Valid @RequestBody FoodEntryDTO foodEntryDTO,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.updateFoodEntry(userId, id, foodEntryDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error updating food entry: " + e.getMessage());
        }
    }

    // Delete a food entry for the authenticated user
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteFoodEntry(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            foodEntryService.deleteFoodEntry(userId, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting food entry: " + e.getMessage());
        }
    }

    // Get a specific food entry by ID for the authenticated user
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getFoodEntryById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getFoodEntryById(userId, id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entry: " + e.getMessage());
        }
    }

    // Get all food entries by date for the authenticated user
    @GetMapping("/user/date/{date}")
    public ResponseEntity<?> getFoodEntriesByDate(
            @PathVariable String date,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserIdAndDate(userId, parsedDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving food entries: " + e.getMessage());
        }
    }

    // Get food entries by date range for the authenticated user
    @GetMapping("/user/date-range")
    public ResponseEntity<?> getFoodEntriesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
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

    // Get total calories for the authenticated user
    @GetMapping("/user/total-calories")
    public ResponseEntity<?> getTotalCaloriesForUser(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getTotalCaloriesForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total calories: " + e.getMessage());
        }
    }

    // Get total calories for a specific date for the authenticated user
    @GetMapping("/user/total-calories/date/{date}")
    public ResponseEntity<?> getTotalCaloriesForUserByDate(
            @PathVariable String date,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(foodEntryService.getTotalCaloriesForUserByDate(userId, parsedDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total calories: " + e.getMessage());
        }
    }

    // Get total calories for a date range for the authenticated user
    @GetMapping("/user/total-calories/date-range")
    public ResponseEntity<?> getTotalCaloriesForUserByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
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

    // Get all food entries (Optional: Admin-only access)
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllFoodEntries(Authentication authentication) {
        // Optionally, restrict this endpoint to admin roles
        if (userService.isAdmin(authentication)) { // Implement isAdmin in UserService
            return ResponseEntity.ok(foodEntryService.getAllFoodEntries());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
    }
}
