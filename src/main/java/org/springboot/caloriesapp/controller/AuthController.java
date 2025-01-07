package org.springboot.caloriesapp.controller;

import org.springboot.caloriesapp.service.AuthService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Implement the login method here


    // Implement the logout method here


}
