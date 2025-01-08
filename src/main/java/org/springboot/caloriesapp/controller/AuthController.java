package org.springboot.caloriesapp.controller;

import org.springboot.caloriesapp.dto.LoginDTO;
import org.springboot.caloriesapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Implement the login method here
    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    // Implement the logout method here


}
