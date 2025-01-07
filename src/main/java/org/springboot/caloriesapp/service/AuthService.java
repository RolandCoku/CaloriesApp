package org.springboot.caloriesapp.service;

import org.springboot.caloriesapp.dto.LoginDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> login(LoginDTO loginDTO);
}
