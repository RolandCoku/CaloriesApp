package org.springboot.caloriesapp.service;

import org.springboot.caloriesapp.dto.RegisterUserDTO;
import org.springboot.caloriesapp.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDTO registerUser(RegisterUserDTO registerUserDTO);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
