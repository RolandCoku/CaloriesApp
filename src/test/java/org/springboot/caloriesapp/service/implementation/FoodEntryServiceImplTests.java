package org.springboot.caloriesapp.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springboot.caloriesapp.dto.FoodEntryAdminDTO;
import org.springboot.caloriesapp.dto.FoodEntryDTO;
import org.springboot.caloriesapp.model.FoodEntry;
import org.springboot.caloriesapp.model.User;
import org.springboot.caloriesapp.repository.FoodEntryRepository;
import org.springboot.caloriesapp.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodEntryServiceImplTests {

    @Mock
    private FoodEntryRepository foodEntryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FoodEntryServiceImpl foodEntryService;

    private FoodEntryDTO foodEntryDTO;
    private FoodEntryAdminDTO foodEntryAdminDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Initialize common test data
        foodEntryDTO = new FoodEntryDTO();
        foodEntryDTO.setName("Test Food");
        foodEntryDTO.setCalories(250.0);
        foodEntryDTO.setPrice(5.0);

        foodEntryAdminDTO = new FoodEntryAdminDTO();
        foodEntryAdminDTO.setUserId(1L);
        foodEntryAdminDTO.setName("Admin Food");
        foodEntryAdminDTO.setCalories(300.0);
        foodEntryAdminDTO.setPrice(10.0);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setUsername("johndoe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setPassword("password");
    }

    // ------------------ User-Specific Tests ------------------

    @Test
    void addFoodEntryForAuthenticatedUser_Successful() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        FoodEntry savedFoodEntry = new FoodEntry();
        savedFoodEntry.setId(100L);
        savedFoodEntry.setName("Test Food");
        savedFoodEntry.setCalories(250.0);
        savedFoodEntry.setPrice(5.0);
        savedFoodEntry.setUser(mockUser);

        when(foodEntryRepository.save(any(FoodEntry.class))).thenReturn(savedFoodEntry);

        // When
        FoodEntryDTO result = foodEntryService.addFoodEntry(1L, foodEntryDTO);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Test Food", result.getName());
        assertEquals(250.0, result.getCalories());
        assertEquals(5.0, result.getPrice());

        verify(userRepository, times(1)).findById(1L);
        verify(foodEntryRepository, times(1)).save(any(FoodEntry.class));
    }

    // ------------------ Admin-Specific Tests ------------------

    @Test
    void addFoodEntryForUserByAdmin_Successful() {
        // Given
        FoodEntryAdminDTO foodEntryAdminDTO = new FoodEntryAdminDTO();
        foodEntryAdminDTO.setName("Admin Food");
        foodEntryAdminDTO.setCalories(300.0);
        foodEntryAdminDTO.setPrice(10.0);
        foodEntryAdminDTO.setUserEmail(mockUser.getEmail()); // Updated to use email

        FoodEntry savedFoodEntry = new FoodEntry();
        savedFoodEntry.setId(200L);
        savedFoodEntry.setName("Admin Food");
        savedFoodEntry.setCalories(300.0);
        savedFoodEntry.setPrice(10.0);
        savedFoodEntry.setUser(mockUser);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser)); // Mocking email lookup
        when(foodEntryRepository.save(any(FoodEntry.class))).thenReturn(savedFoodEntry);

        // When
        FoodEntryAdminDTO result = foodEntryService.addFoodEntryForUser(foodEntryAdminDTO);

        // Then
        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals("Admin Food", result.getName());
        assertEquals(300.0, result.getCalories());
        assertEquals(10.0, result.getPrice());
        assertEquals(mockUser.getId(), result.getUserId());
    }

}


