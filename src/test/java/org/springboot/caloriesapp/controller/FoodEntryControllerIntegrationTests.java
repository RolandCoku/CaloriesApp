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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
        user.setPassword("password");
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

        // Mock authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null, List.of())
        );
    }

    // Utility method to provide authentication headers for the mock user
    private String getAuthHeader() {
        return "Bearer mock-token-for-testuser";
    }

    // ------------------ User-Specific Tests ------------------

    @Test
    void getAllFoodEntriesByAuthenticatedUser_Successful() throws Exception {
        mockMvc.perform(get("/food-entries/user/entries")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Apple')].calories").value(hasItem(95.0)))
                .andExpect(jsonPath("$[?(@.name == 'Banana')].calories").value(hasItem(105.0)));

    }

    @Test
    void updateFoodEntryForAuthenticatedUser_Successful() throws Exception {
        FoodEntryDTO updateDTO = new FoodEntryDTO();
        updateDTO.setName("Green Apple");
        updateDTO.setCalories(100.0);
        updateDTO.setPrice(1.75);

        mockMvc.perform(put("/food-entries/user/update/" + foodEntry1.getId())
                        .with(user("testuser").roles("USER"))
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
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/food-entries/user/" + foodEntry1.getId())
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFoodEntriesByDateForAuthenticatedUser_Successful() throws Exception {
        String dateString = LocalDate.now().toString();

        mockMvc.perform(get("/food-entries/user/date/" + dateString)
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Apple"));
    }

    @Test
    void getFoodEntriesByDateRangeForAuthenticatedUser_Successful() throws Exception {
        String startDate = LocalDate.now().minusDays(2).toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(get("/food-entries/user/date-range")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Banana"));
    }

    @Test
    void getTotalCaloriesForAuthenticatedUser_Successful() throws Exception {
        mockMvc.perform(get("/food-entries/user/total-calories")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void getTotalCaloriesForAuthenticatedUserByDate_Successful() throws Exception {
        String dateString = LocalDate.now().toString();

        mockMvc.perform(get("/food-entries/user/total-calories/date/" + dateString)
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void getTotalCaloriesForAuthenticatedUserByDateRange_Successful() throws Exception {
        String startDate = LocalDate.now().minusDays(2).toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(get("/food-entries/user/total-calories/date-range")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }
}



