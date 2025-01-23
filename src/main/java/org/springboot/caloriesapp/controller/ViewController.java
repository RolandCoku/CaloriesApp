package org.springboot.caloriesapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

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

    @GetMapping("/admin")
    public String admin() {
        return "admin/index";
    }
}
