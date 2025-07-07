package com.piats.backend.controllers;

import com.piats.backend.dto.LoginUserRequestDto;
import com.piats.backend.dto.TokenResponseDto;
import com.piats.backend.models.User;
import com.piats.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/login")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<TokenResponseDto> loginUser(@RequestBody LoginUserRequestDto requestUser) {
        return ResponseEntity.ok().body(userService.authenticateUser(requestUser));
    }
}