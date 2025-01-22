package org.springboot.caloriesapp.controller;

import jakarta.validation.Valid;
import org.springboot.caloriesapp.dto.FoodEntryAdminDTO;
import org.springboot.caloriesapp.service.FoodEntryService;
import org.springboot.caloriesapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final FoodEntryService foodEntryService;
    public AdminController(UserService userService, FoodEntryService foodEntryService){
        this.userService = userService;
        this.foodEntryService = foodEntryService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting user: " + e.getMessage());
        }
    }

    //Manage food entries endpoints
    @GetMapping("/food-entries")
    public ResponseEntity<?> getAllFoodEntries() {
        try {
            return ResponseEntity.ok(foodEntryService.getAllFoodEntries());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //Get food entry by id
    @GetMapping("/food-entries/{id}")
    public ResponseEntity<?> getFoodEntryById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(foodEntryService.getFoodEntryById(id));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Add a new food entry
    @PostMapping("/food-entries/create")
    public ResponseEntity<?> createFoodEntry(@Valid @RequestBody FoodEntryAdminDTO foodEntryAdminDTO) {
        try {
            return ResponseEntity.ok(foodEntryService.addFoodEntryForUser(foodEntryAdminDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error adding food entry: " + e.getMessage());
        }
    }

    // Update an existing food entry
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFoodEntry(
            @PathVariable Long id,
            @Valid @RequestBody FoodEntryAdminDTO foodEntryAdminDTO) {
        try {
            return ResponseEntity.ok(foodEntryService.updateFoodEntryById(id, foodEntryAdminDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error updating food entry: " + e.getMessage());
        }
    }

    // Delete a food entry
    @DeleteMapping("/food-entries/{id}")
    public ResponseEntity<?> deleteFoodEntry(@PathVariable Long id) {
        try {
            foodEntryService.deleteFoodEntryById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting food entry: " + e.getMessage());
        }
    }

    @GetMapping("/user/{id}/average-calories")
    public ResponseEntity<?> getWeaklyAverageCaloriesByUser(@PathVariable Long id){
        try {
            return ResponseEntity.ok(foodEntryService.getWeaklyAverageCaloriesByUser(id));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error getting average calories for the user: " + e.getMessage());
        }
    }

    //Get all users that exceeded the monthly spending limit
    @GetMapping("/users/above-price-limit")
    public ResponseEntity<?> getAllUserWhoExceededMonthlyPriceLimit(){
        return ResponseEntity.ok(userService.getAllUserWhoExceededMonthlyPriceLimit());
    }
}
