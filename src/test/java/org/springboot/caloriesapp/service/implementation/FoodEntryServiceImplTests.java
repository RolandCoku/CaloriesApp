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
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        FoodEntry savedFoodEntry = new FoodEntry();
        savedFoodEntry.setId(200L);
        savedFoodEntry.setName("Admin Food");
        savedFoodEntry.setCalories(300.0);
        savedFoodEntry.setPrice(10.0);
        savedFoodEntry.setUser(mockUser);

        when(foodEntryRepository.save(any(FoodEntry.class))).thenReturn(savedFoodEntry);

        // When
        FoodEntryAdminDTO result = (FoodEntryAdminDTO) foodEntryService.addFoodEntryForUser(foodEntryAdminDTO);

        // Then
        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals("Admin Food", result.getName());
        assertEquals(300.0, result.getCalories());
        assertEquals(10.0, result.getPrice());
        assertEquals(1L, result.getUserId());

        verify(userRepository, times(1)).findById(1L);
        verify(foodEntryRepository, times(1)).save(any(FoodEntry.class));
    }

    @Test
    void updateFoodEntryForUserByAdmin_Successful() {
        // Given
        Long foodEntryId = 100L;
        FoodEntry existingFoodEntry = new FoodEntry();
        existingFoodEntry.setId(foodEntryId);
        existingFoodEntry.setName("Old Food");
        existingFoodEntry.setCalories(200.0);
        existingFoodEntry.setPrice(4.0);
        existingFoodEntry.setUser(mockUser);

        when(foodEntryRepository.findById(foodEntryId)).thenReturn(Optional.of(existingFoodEntry));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        FoodEntry updatedFoodEntry = new FoodEntry();
        updatedFoodEntry.setId(foodEntryId);
        updatedFoodEntry.setName("Updated Admin Food");
        updatedFoodEntry.setCalories(400.0);
        updatedFoodEntry.setPrice(15.0);
        updatedFoodEntry.setUser(mockUser);

        when(foodEntryRepository.save(any(FoodEntry.class))).thenReturn(updatedFoodEntry);

        FoodEntryAdminDTO updateDTO = new FoodEntryAdminDTO();
        updateDTO.setName("Updated Admin Food");
        updateDTO.setCalories(400.0);
        updateDTO.setPrice(15.0);
        updateDTO.setUserId(1L);

        // When
        FoodEntryAdminDTO result = (FoodEntryAdminDTO) foodEntryService.updateFoodEntryById(foodEntryId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(foodEntryId, result.getId());
        assertEquals("Updated Admin Food", result.getName());
        assertEquals(400.0, result.getCalories());
        assertEquals(15.0, result.getPrice());
        assertEquals(1L, result.getUserId());

        verify(foodEntryRepository, times(1)).findById(foodEntryId);
        verify(userRepository, times(1)).findById(1L);
        verify(foodEntryRepository, times(1)).save(any(FoodEntry.class));
    }

    @Test
    void deleteFoodEntryByAdmin_Successful() {
        // Given
        Long foodEntryId = 300L;
        doNothing().when(foodEntryRepository).deleteById(foodEntryId);

        // When
        foodEntryService.deleteFoodEntryById(foodEntryId);

        // Then
        verify(foodEntryRepository, times(1)).deleteById(foodEntryId);
    }
}


