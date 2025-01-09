package org.springboot.caloriesapp.controller;

import jakarta.validation.Valid;
import org.springboot.caloriesapp.dto.RegisterUserDTO;
import org.springboot.caloriesapp.dto.UserDTO;
import org.springboot.caloriesapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register a new user
    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            return ResponseEntity.ok(userService.registerUser(registerUserDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        }
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, userDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error updating user: " + e.getMessage());
        }
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting user: " + e.getMessage());
        }
    }

//    // Search users by name
//    @GetMapping("/search")
//    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
//        try {
//            List<UserDTO> users = userService.searchUsersByName(name);
//            if (users.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found with the given name.");
//            }
//            return ResponseEntity.ok(users);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Error searching users: " + e.getMessage());
//        }
//    }
//
//    // Get users by role
//    @GetMapping("/role/{roleId}")
//    public ResponseEntity<?> getUsersByRole(@PathVariable Long roleId) {
//        try {
//            List<UserDTO> users = userService.getUsersByRole(roleId);
//            if (users.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found for the given role.");
//            }
//            return ResponseEntity.ok(users);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Error retrieving users by role: " + e.getMessage());
//        }
//    }
}