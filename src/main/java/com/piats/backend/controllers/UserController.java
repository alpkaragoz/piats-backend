package com.piats.backend.controllers;

import com.piats.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }
}

