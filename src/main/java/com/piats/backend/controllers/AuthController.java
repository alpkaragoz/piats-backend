package com.piats.backend.controllers;

import com.piats.backend.dto.TokenResponseDto;
import com.piats.backend.models.User;
import com.piats.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<TokenResponseDto> loginUser(@RequestBody User requestUser) {
        return ResponseEntity.ok().body(userService.authenticateUser(requestUser));
    }
}