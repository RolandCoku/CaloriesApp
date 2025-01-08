package org.springboot.caloriesapp.controller;

import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/food-entries")
public class FoodEntryController {

    private final FoodEntryService foodEntryService;

    public FoodEntryController(FoodEntryService foodEntryService) {
        this.foodEntryService = foodEntryService;
    }

    @GetMapping
    public ResponseEntity<List<FoodEntryDTO>> getAllFoodEntries() {
        return ResponseEntity.ok(foodEntryService.getAllFoodEntries());
    }

    @GetMapping("/total-calories")
    public ResponseEntity<Integer> getTotalCalories() {
        return ResponseEntity.ok(foodEntryService.getTotalCalories());
    }

    @PostMapping("/add")
    public ResponseEntity<FoodEntryDTO> addFoodEntry(FoodEntryDTO foodEntryDTO) {
        return ResponseEntity.ok(foodEntryService.addFoodEntry(foodEntryDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<FoodEntryDTO> updateFoodEntry(Long id, FoodEntryDTO foodEntryDTO) {
        return ResponseEntity.ok(foodEntryService.updateFoodEntry(id, foodEntryDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFoodEntry(Long id) {
        foodEntryService.deleteFoodEntry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodEntryDTO>> getFoodEntriesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodEntryDTO> getFoodEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(foodEntryService.getFoodEntryById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<FoodEntryDTO>> getFoodEntriesByDate(@PathVariable String date) {
        return ResponseEntity.ok(foodEntryService.getFoodEntriesByDate(LocalDate.parse(date)));
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<FoodEntryDTO>> getFoodEntriesByUserIdAndDate(@PathVariable Long userId, @PathVariable String date) {
        return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserIdAndDate(userId, LocalDate.parse(date)));
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<FoodEntryDTO>> getFoodEntriesByUserIdAndDateRange(@PathVariable Long userId, @RequestParam String startDate, @RequestParam String endDate) {
        return ResponseEntity.ok(foodEntryService.getFoodEntriesByUserIdAndDateRange(userId, LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

}
