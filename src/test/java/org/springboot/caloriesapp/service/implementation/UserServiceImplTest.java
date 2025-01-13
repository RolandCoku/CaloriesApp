package org.springboot.caloriesapp.service.implementation;

import org.springboot.caloriesapp.dto.RegisterUserDTO;
import org.springboot.caloriesapp.dto.UserDTO;
import org.springboot.caloriesapp.model.Role;
import org.springboot.caloriesapp.model.User;
import org.springboot.caloriesapp.repository.RoleRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterUserDTO registerUserDTO;

    @BeforeEach
    void setUp() {
        registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("TestName");
        registerUserDTO.setLastName("TestSurname");
        registerUserDTO.setUsername("testUser");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setConfirmPassword("password");
    }

    @Test
    void registerUser_Successful() {
        // given
        registerUserDTO.setPassword("password");
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        Role mockRole = new Role(2L, "USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(mockRole));

        User savedUser = new User("TestName TestSurname", "testUser", "encoded_password", "test@example.com", mockRole);
        savedUser.setId(1L);

        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserDTO result = userService.registerUser(registerUserDTO);

        // then
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(2L, result.getRoleId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_PasswordsMismatch_ThrowsException() {
        // given
        registerUserDTO.setPassword("abc123");
        registerUserDTO.setConfirmPassword("def456");

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(registerUserDTO));
        assertTrue(ex.getMessage().contains("Passwords do not match"));
    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsException() {
        // given
        registerUserDTO.setPassword("password");
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(registerUserDTO));
        assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // given
        registerUserDTO.setPassword("password");
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(registerUserDTO));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }



}