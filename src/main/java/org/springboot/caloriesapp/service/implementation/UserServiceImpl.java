package org.springboot.caloriesapp.service.implementation;

import org.springboot.caloriesapp.dto.RegisterUserDTO;
import org.springboot.caloriesapp.dto.UserDTO;
import org.springboot.caloriesapp.model.Role;
import org.springboot.caloriesapp.model.User;
import org.springboot.caloriesapp.repository.RoleRepository;
import org.springboot.caloriesapp.repository.UserRepository;
import org.springboot.caloriesapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO registerUser(RegisterUserDTO registerUserDTO) {

        //Check if the password and password confirmation match
        if (!registerUserDTO.getPassword().equals(registerUserDTO.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        //Check if the username already exists
        if (userRepository.existsByUsername(registerUserDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        //Check if the email already exists
        if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        //Find the role by ID
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setName(registerUserDTO.getFirstName() + " " + registerUserDTO.getLastName());
        user.setUsername(registerUserDTO.getUsername());
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        user.setRole(role);
        user = userRepository.save(user);

        return mapToDTO(user);
    }


    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFirstName() != null && userDTO.getLastName() != null) {
            user.setName(userDTO.getFirstName() + " " + userDTO.getLastName());

        } else if (userDTO.getFirstName() != null && userDTO.getLastName() == null) {
            user.setName(userDTO.getFirstName() + " " + user.getName().split(" ")[1]);

        }else if (userDTO.getLastName() != null && userDTO.getFirstName() == null) {
            user.setName(user.getName().split(" ")[0] + " " + userDTO.getLastName());
        }

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAllUserWhoExceededMonthlyPriceLimit(){
        double monthlyLimit = 1000.0;
        return userRepository.findUsersExceedingMonthlyLimit(monthlyLimit).stream().map(this::mapToDTO).toList();
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getName().split(" ")[0]);
        userDTO.setLastName(user.getName().split(" ")[1]);
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoleId(user.getRole().getId());
        userDTO.setRoleName(user.getRole().getName());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        return userDTO;
    }
}
