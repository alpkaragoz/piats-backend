package com.piats.backend.controllers;

import com.piats.backend.dto.MessageResponseDto;
import com.piats.backend.dto.RegisterUserRequestDto;
import com.piats.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.piats.backend.models.User;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200") //TODO
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<MessageResponseDto> registerUser(@RequestBody RegisterUserRequestDto requestUser) {
        return ResponseEntity.ok().body(userService.saveUser(requestUser));
    }

}

