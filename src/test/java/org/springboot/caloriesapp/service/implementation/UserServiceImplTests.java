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
class UserServiceImplTests {

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

    @Test
    void getUserById_Successful() {
        // given
        User user = new User("TestName TestSurname", "testUser", "password", "test@example.com", new Role(2L, "USER"));
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserDTO result = userService.getUserById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getAllUsers_Successful() {
        // given
        when(userRepository.findAll()).thenReturn(
                java.util.List.of(
                        new User("TestName1 TestSurname1", "testUser1", "password1", "test1@example.com", new Role(2L, "USER")),
                        new User("TestName2 TestSurname2", "testUser2", "password2", "test2@example.com", new Role(2L, "USER"))
                )
        );

        // when
        java.util.List<UserDTO> result = userService.getAllUsers();

        // then
        // Check the first user
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser1", result.getFirst().getUsername());
        assertEquals("TestName1", result.getFirst().getFirstName());
        assertEquals("TestSurname1", result.getFirst().getLastName());
        assertEquals("test1@example.com", result.getFirst().getEmail());
        assertEquals(2L, result.getFirst().getRoleId());

        // Check the second user
        assertEquals("testUser2", result.get(1).getUsername());
        assertEquals("TestName2", result.get(1).getFirstName());
        assertEquals("TestSurname2", result.get(1).getLastName());
        assertEquals("test2@example.com", result.get(1).getEmail());
        assertEquals(2L, result.get(1).getRoleId());
    }


    @Test
    void updateUser_Successful() {
        // given
        User existingUser = new User("oldName oldSurname", "oldUser", "password", "old@example.com", new Role(2L, "USER"));
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("newTestName");
        updateDTO.setLastName("newTestSurname");
        updateDTO.setUsername("newTestUser");
        updateDTO.setEmail("newTest@example.com");

        // Simulate the save method returning the updated user
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.<User>getArgument(0));

        // when
        UserDTO updatedUserDTO = userService.updateUser(1L, updateDTO);

        // then
        verify(userRepository, times(1)).save(ArgumentMatchers.any(User.class));
        assertEquals("newTestUser", updatedUserDTO.getUsername());
        assertEquals("newTest@example.com", updatedUserDTO.getEmail());
        assertEquals("newTestName", updatedUserDTO.getFirstName());
        assertEquals("newTestSurname", updatedUserDTO.getLastName());
    }


    @Test
    void updateUser_FirstName_null() {
        // given
        User existingUser = new User("testName testSurname", "testUser", "password", "test@example.com", new Role(2L, "USER"));
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserDTO updateDTO = new UserDTO();
        updateDTO.setLastName("newTestSurname");
        updateDTO.setUsername("newTestUser");
        updateDTO.setEmail("newTest@example.com");

        // Simulate the save method returning the updated user
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.<User>getArgument(0));

        // when
        UserDTO updatedUserDTO = userService.updateUser(1L, updateDTO);

        // then
        verify(userRepository, times(1)).save(ArgumentMatchers.any(User.class));
        assertEquals("newTestUser", updatedUserDTO.getUsername());
        assertEquals("newTest@example.com", updatedUserDTO.getEmail());
        assertEquals("testName", updatedUserDTO.getFirstName());
        assertEquals("newTestSurname", updatedUserDTO.getLastName());
    }

    @Test
    void updateUser_LastName_null() {
        // given
        User existingUser = new User("testName testSurname", "testUser", "password", "test@example.com", new Role(2L, "USER"));
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("newTestName");
        updateDTO.setUsername("newTestUser");
        updateDTO.setEmail("newTest@example.com");

        // Simulate the save method returning the updated user
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.<User>getArgument(0));

        // when
        UserDTO updatedUserDTO = userService.updateUser(1L, updateDTO);

        // then
        verify(userRepository, times(1)).save(ArgumentMatchers.any(User.class));
        assertEquals("newTestUser", updatedUserDTO.getUsername());
        assertEquals("newTest@example.com", updatedUserDTO.getEmail());
        assertEquals("newTestName", updatedUserDTO.getFirstName());
        assertEquals("testSurname", updatedUserDTO.getLastName());
    }

    @Test
    void updateUser_Username_null() {
        // given
        User existingUser = new User("testName testSurname", "testUser", "password", "test@example.com", new Role(2L, "USER"));
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("newTestName");
        updateDTO.setLastName("newTestSurname");
        updateDTO.setEmail("newTest@example.com");

        // Simulate the save method returning the updated user
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.<User>getArgument(0));

        // when
        UserDTO updatedUserDTO = userService.updateUser(1L, updateDTO);

        // then
        verify(userRepository, times(1)).save(ArgumentMatchers.any(User.class));
        assertEquals("testUser", updatedUserDTO.getUsername());
        assertEquals("newTest@example.com", updatedUserDTO.getEmail());
        assertEquals("newTestName", updatedUserDTO.getFirstName());
        assertEquals("newTestSurname", updatedUserDTO.getLastName());
    }

    @Test
    void updateUser_Email_null() {
        // given
        User existingUser = new User("testName testSurname", "testUser", "password", "test@example.com", new Role(2L, "USER"));
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("newTestName");
        updateDTO.setLastName("newTestSurname");
        updateDTO.setUsername("newTestUser");

        // Simulate the save method returning the updated user
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.<User>getArgument(0));

        // when
        UserDTO updatedUserDTO = userService.updateUser(1L, updateDTO);

        // then
        verify(userRepository, times(1)).save(ArgumentMatchers.any(User.class));
        assertEquals("newTestUser", updatedUserDTO.getUsername());
        assertEquals("test@example.com", updatedUserDTO.getEmail());
        assertEquals("newTestName", updatedUserDTO.getFirstName());
        assertEquals("newTestSurname", updatedUserDTO.getLastName());
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("newTestName");
        updateDTO.setLastName("newTestSurname");
        updateDTO.setUsername("newTestUser");
        updateDTO.setEmail("newTest@example.com");

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(99L, updateDTO));
        assertTrue(ex.getMessage().contains("User not found"));

        verify(userRepository, times(0)).save(ArgumentMatchers.any(User.class)); // Ensure save is never called
    }

    @Test
    void deleteUser_Successful() {
        // given
        when(userRepository.existsById(1L)).thenReturn(true);

        // when
        userService.deleteUser(1L);

        // then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // given
        when(userRepository.existsById(99L)).thenReturn(false);

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
        assertTrue(ex.getMessage().contains("User not found"));

        verify(userRepository, times(0)).deleteById(99L); // Ensure deleteById is never called
    }
}