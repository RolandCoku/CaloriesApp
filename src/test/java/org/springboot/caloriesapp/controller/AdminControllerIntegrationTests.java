package org.springboot.caloriesapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springboot.caloriesapp.dto.FoodEntryAdminDTO;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FoodEntryRepository foodEntryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User regularUser;
    private FoodEntry foodEntry1;

    @BeforeEach
    void setUp() {
        // Clear database
        foodEntryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Insert roles
        Role userRole = new Role("USER");
        roleRepository.save(userRole);

        Role adminRole = new Role("ADMIN");
        roleRepository.save(adminRole);

        // Insert users
        User adminUser = new User("Admin User", "admin", "admin@example.com", "adminpass", adminRole);
        userRepository.save(adminUser);

        regularUser = new User("Regular User", "user", "user@example.com", "userpass", userRole);
        userRepository.save(regularUser);

        // Insert food entries
        foodEntry1 = new FoodEntry(1L, "Apple", 95.0, 0.50, regularUser);
        foodEntryRepository.save(foodEntry1);

        FoodEntry foodEntry2 = new FoodEntry(2L, "Banana", 105.0, 0.75, regularUser);
        foodEntryRepository.save(foodEntry2);
    }

    @Test
    void getAllUsers_Successful() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.username == 'admin')].email").value(hasItem("admin@example.com")))
                .andExpect(jsonPath("$[?(@.username == 'user')].email").value(hasItem("user@example.com")));
    }

    @Test
    void deleteUser_Successful() throws Exception {
        mockMvc.perform(delete("/admin/users/" + regularUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void getAllFoodEntries_Successful() throws Exception {
        mockMvc.perform(get("/admin/food-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Apple')].calories").value(hasItem(95.0)))
                .andExpect(jsonPath("$[?(@.name == 'Banana')].calories").value(hasItem(105.0)));
    }

    @Test
    void createFoodEntry_Successful() throws Exception {
        FoodEntryAdminDTO newFoodEntry = new FoodEntryAdminDTO();
        newFoodEntry.setUserId(regularUser.getId());
        newFoodEntry.setName("Orange");
        newFoodEntry.setCalories(62.0);
        newFoodEntry.setPrice(0.80);

        mockMvc.perform(post("/admin/food-entries/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFoodEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Orange"))
                .andExpect(jsonPath("$.calories").value(62.0))
                .andExpect(jsonPath("$.price").value(0.80))
                .andExpect(jsonPath("$.userId").value(regularUser.getId()));
    }

    @Test
    void updateFoodEntry_Successful() throws Exception {
        FoodEntryAdminDTO updatedFoodEntry = new FoodEntryAdminDTO();
        updatedFoodEntry.setUserId(regularUser.getId());
        updatedFoodEntry.setName("Green Apple");
        updatedFoodEntry.setCalories(100.0);
        updatedFoodEntry.setPrice(1.75);

        mockMvc.perform(put("/admin/" + foodEntry1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFoodEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Green Apple"))
                .andExpect(jsonPath("$.calories").value(100.0))
                .andExpect(jsonPath("$.price").value(1.75))
                .andExpect(jsonPath("$.userId").value(regularUser.getId()));
    }

    @Test
    void deleteFoodEntry_Successful() throws Exception {
        mockMvc.perform(delete("/admin/food-entries/" + foodEntry1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admin/food-entries/" + foodEntry1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWeeklyAverageCaloriesByUser_Successful() throws Exception {
        mockMvc.perform(get("/admin/user/" + regularUser.getId() + "/average-calories"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(isEmptyOrNullString())));
    }
}
