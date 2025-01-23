package org.springboot.caloriesapp.controller;

import jakarta.validation.Valid;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springboot.caloriesapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
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
    @PostMapping("/user/add")
    public String addFoodEntry(
            @Valid @ModelAttribute FoodEntryDTO foodEntryDTO,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            foodEntryService.addFoodEntry(userId, foodEntryDTO);

            redirectAttributes.addFlashAttribute("successMessage", "Food entry added successfully!");
            return "redirect:/home";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding food entry: " + e.getMessage());
            return "redirect:/home";
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

    // Get the total spending for the authenticated user
    @GetMapping("/user/total-spending")
    public ResponseEntity<?> getTotalSpendingForUser(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getTotalSpendingForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total spending: " + e.getMessage());
        }
    }

    // Get total spending for the authenticated user by date
    @GetMapping("/user/total-spending/date/{date}")
    public ResponseEntity<?> getTotalSpendingForUserByDate(
            @PathVariable String date,
            Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(foodEntryService.getTotalSpendingForUserByDate(userId, parsedDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving total spending: " + e.getMessage());
        }
    }

    //Get the last week's spending total for the authenticated user
    @GetMapping("/user/weekly-average/spending")
    public ResponseEntity<?> getWeeklyAverageSpendingForUser(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getWeeklyAverageSpendingForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving weekly average spending: " + e.getMessage());
        }
    }

    // Get the calories for every day of the week for the authenticated user
    @GetMapping("/user/weekly-calories")
    public ResponseEntity<?> getWeeklyCaloriesForUser(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getWeeklyCaloriesForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving weekly calories: " + e.getMessage());
        }
    }

    // Get the spending for every day of the week for the authenticated user
    @GetMapping("/user/weekly-spending")
    public ResponseEntity<?> getWeeklySpendingForUser(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getWeeklySpendingForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving weekly spending: " + e.getMessage());
        }
    }

    // Get the days when the user exceeded the daily calorie limit
    @GetMapping("/user/days-over-limit")
    public ResponseEntity<?> getDaysOverCalorieLimit(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getDaysOverCalorieLimit(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving days over calorie limit: " + e.getMessage());
        }
    }

    // Weekly Summary
    @GetMapping("/user/weekly-summary")
    public ResponseEntity<?> getWeeklySummary(Authentication authentication) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(foodEntryService.getWeeklySummary(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error retrieving weekly summary: " + e.getMessage());
        }
    }

}
