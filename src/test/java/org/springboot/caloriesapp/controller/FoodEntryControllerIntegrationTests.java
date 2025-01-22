package org.springboot.caloriesapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.model.FoodEntry;
import org.springboot.caloriesapp.model.Role;
import org.springboot.caloriesapp.model.User;
import org.springboot.caloriesapp.repository.FoodEntryRepository;
import org.springboot.caloriesapp.repository.RoleRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FoodEntryControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FoodEntryRepository foodEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Role userRole;
    private User user;
    private FoodEntry foodEntry1;
    private FoodEntry foodEntry2;

    @BeforeEach
    void setUp() {
        // Clear the database
        foodEntryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create and save a role
        userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        // Create and save a user
        user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password"); // Assuming password is pre-encoded
        user.setRole(userRole);
        userRepository.save(user);

        // Create and save food entries for the user
        foodEntry1 = new FoodEntry();
        foodEntry1.setName("Apple");
        foodEntry1.setCalories(95.0);
        foodEntry1.setPrice(1.5);
        foodEntry1.setUser(user);
        foodEntry1.setCreatedAt(LocalDateTime.now().minusDays(2));
        foodEntry1.setUpdatedAt(LocalDateTime.now().minusDays(2));
        foodEntryRepository.save(foodEntry1);

        foodEntry2 = new FoodEntry();
        foodEntry2.setName("Banana");
        foodEntry2.setCalories(105.0);
        foodEntry2.setPrice(0.75);
        foodEntry2.setUser(user);
        foodEntry2.setCreatedAt(LocalDateTime.now().minusDays(1));
        foodEntry2.setUpdatedAt(LocalDateTime.now().minusDays(1));
        foodEntryRepository.save(foodEntry2);
    }

    // Utility method to provide authentication headers for the mock user
    private String getAuthHeader() {
        // Mock user authentication token (JWT or Basic Auth as per your implementation)
        return "Bearer mock-token-for-testuser";
    }

    // ------------------ User-Specific Tests ------------------

    @Test
    void getAllFoodEntriesForAuthenticatedUser_Successful() throws Exception {
        mockMvc.perform(get("/food-entries/user/entries")
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Apple')].calories").value(hasItem(95.0)))
                .andExpect(jsonPath("$[?(@.name == 'Banana')].calories").value(hasItem(105.0)));
    }

    @Test
    void addFoodEntryForAuthenticatedUser_Successful() throws Exception {
        FoodEntryDTO newFoodEntry = new FoodEntryDTO();
        newFoodEntry.setName("Orange");
        newFoodEntry.setCalories(62.0);
        newFoodEntry.setPrice(0.80);

        mockMvc.perform(post("/food-entries/user/add")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFoodEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Orange"))
                .andExpect(jsonPath("$.calories").value(62.0))
                .andExpect(jsonPath("$.price").value(0.80));
    }

    @Test
    void updateFoodEntryForAuthenticatedUser_Successful() throws Exception {
        FoodEntryDTO updateDTO = new FoodEntryDTO();
        updateDTO.setName("Green Apple");
        updateDTO.setCalories(100.0);
        updateDTO.setPrice(1.75);

        mockMvc.perform(put("/food-entries/user/update/" + foodEntry1.getId())
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Green Apple"))
                .andExpect(jsonPath("$.calories").value(100.0))
                .andExpect(jsonPath("$.price").value(1.75));
    }

    @Test
    void deleteFoodEntryForAuthenticatedUser_Successful() throws Exception {
        mockMvc.perform(delete("/food-entries/user/delete/" + foodEntry1.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/food-entries/user/" + foodEntry1.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFoodEntriesByDateForAuthenticatedUser_Successful() throws Exception {
        String dateString = LocalDate.now().minusDays(2).toString();

        mockMvc.perform(get("/food-entries/user/date/" + dateString)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Apple"));
    }

    // ------------------ Admin-Specific Tests ------------------

    @Test
    void getAllFoodEntriesAsAdmin_Successful() throws Exception {
        mockMvc.perform(get("/food-entries/admin/all")
                        .header("Authorization", getAuthHeader())) // Replace with admin credentials
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Apple')].calories").value(hasItem(95.0)))
                .andExpect(jsonPath("$[?(@.name == 'Banana')].calories").value(hasItem(105.0)));
    }

    @Test
    void getFoodEntryByIdAsAdmin_Successful() throws Exception {
        mockMvc.perform(get("/food-entries/admin/all/" + foodEntry1.getId())
                        .header("Authorization", getAuthHeader())) // Replace with admin credentials
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.calories").value(95.0));
    }
}

