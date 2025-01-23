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
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        // Insert users
        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword("admin");
        adminUser.setRole(adminRole);
        userRepository.save(adminUser);

        regularUser = new User();
        regularUser.setName("Regular User");
        regularUser.setUsername("user");
        regularUser.setEmail("user@user.com");
        regularUser.setPassword("user");
        regularUser.setRole(userRole);
        userRepository.save(regularUser);

        // Insert food entries
        foodEntry1 = new FoodEntry();
        foodEntry1.setName("Apple");
        foodEntry1.setCalories(95.0);
        foodEntry1.setPrice(1.5);
        foodEntry1.setUser(regularUser);
        foodEntryRepository.save(foodEntry1);

        FoodEntry foodEntry2 = new FoodEntry();
        foodEntry2.setName("Banana");
        foodEntry2.setCalories(105.0);
        foodEntry2.setPrice(0.75);
        foodEntry2.setUser(regularUser);
        foodEntryRepository.save(foodEntry2);

    }

    @Test
    void getAllUsers_Successful() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.username == 'admin')].email").value(hasItem("admin@admin.com")))
                .andExpect(jsonPath("$[?(@.username == 'user')].email").value(hasItem("user@user.com")));

    }

    @Test
    void getAllUsers_Empty() throws Exception {
        userRepository.deleteAll();
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
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
                .andExpect(content().string("100.0"));

    }
}
