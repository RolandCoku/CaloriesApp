package org.springboot.caloriesapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springboot.caloriesapp.dto.RegisterUserDTO;
import org.springboot.caloriesapp.dto.UserDTO;
import org.springboot.caloriesapp.model.Role;
import org.springboot.caloriesapp.model.User;
import org.springboot.caloriesapp.repository.RoleRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Role userRole;

    @BeforeEach
    void setUp() {
        // Clear the database
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Insert the role we expect to use
        userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);
    }

    @Test
    void registerUser_Successful() throws Exception {
        // given
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("testName");
        registerUserDTO.setLastName("testSurname");
        registerUserDTO.setUsername("testUsername");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("password");
        registerUserDTO.setConfirmPassword("password");

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUsername"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registerUser_UsernameExists_ReturnsBadRequest() throws Exception {
        // given
        User user = new User("testName testSurname", "testUsername", "password", "test@example.com", userRole);
        userRepository.save(user);

        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("testName");
        registerUserDTO.setLastName("testSurname");
        registerUserDTO.setUsername("testUsername");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("password");
        registerUserDTO.setConfirmPassword("password");

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmailExists_ReturnsBadRequest() throws Exception {
        // given
        User user = new User("testName testSurname", "testUsername", "password", "test@example.com", userRole);
        userRepository.save(user);

        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("testName");
        registerUserDTO.setLastName("testSurname");
        registerUserDTO.setUsername("newTestUsername");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("password");
        registerUserDTO.setConfirmPassword("password");

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_PasswordMismatch_ReturnsBadRequest() throws Exception {
        // given
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("testName");
        registerUserDTO.setLastName("testSurname");
        registerUserDTO.setUsername("testUsername");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("password1");
        registerUserDTO.setConfirmPassword("password2");

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_Successful() throws Exception {
        // given
        User user = new User("testName testSurname", "testUsername", "password", "test@example.com", userRole);
        user = userRepository.save(user);

        // when & then
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUsername"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    void getAllUsers_Successful() throws Exception {
        // given
        User user1 = new User("testName1 testSurname1", "testUsername1", "password", "test1@example.com", userRole);
        User user2 = new User("testName2 testSurname2", "testUsername2", "password", "test2@example.com", userRole);
        userRepository.save(user1);
        userRepository.save(user2);

        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("testUsername1"))
                .andExpect(jsonPath("$[0].email").value("test1@example.com"))
                .andExpect(jsonPath("$[1].username").value("testUsername2"))
                .andExpect(jsonPath("$[1].email").value("test2@example.com"));
    }

    @Test
    void getAllUsers_EmptyList() throws Exception {
        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void updateUser_Successful() throws Exception {
        // given
        User user = new User("newTestName newTestSurname", "newTestUsername", "password", "newTest@example.com", userRole);
        user = userRepository.save(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("newTestName");
        userDTO.setLastName("newTestSurname");
        userDTO.setUsername("newTestUsername");
        userDTO.setEmail("newTest@example.com");

        // when & then
        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newTestUsername"))
                .andExpect(jsonPath("$.email").value("newTest@example.com"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        // given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("newTestName");
        userDTO.setLastName("newTestSurname");
        userDTO.setUsername("newTestUsername");
        userDTO.setEmail("newTest@example.com");

        // when & then
        mockMvc.perform(put("/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_Successful() throws Exception {
        // given
        User user = new User("testName testSurname", "testUsername", "password", "test@example.com", userRole);
        user = userRepository.save(user);

        // when & then
        mockMvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/users/9999"))
                .andExpect(status().isNotFound());
    }


}