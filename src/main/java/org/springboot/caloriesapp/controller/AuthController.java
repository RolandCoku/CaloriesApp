package org.springboot.caloriesapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @GetMapping("/home")
    public String home() {
        return "user/index";
    }

    @GetMapping("/user/days-over-limit")
    public String daysOverLimit() {
        return "/user/list-days";
    }
}
