package com.piats.backend.controllers;

import com.piats.backend.dto.MessageResponseDto;
import com.piats.backend.dto.RegisterUserRequestDto;
import com.piats.backend.dto.UserInfoResponseDto;
import com.piats.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
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

    @GetMapping
    public ResponseEntity<Page<UserInfoResponseDto>> getAllUsers(
            @RequestParam(required = false) String role,
            Pageable pageable) {
        return ResponseEntity.ok().body(userService.getAllUsers(pageable, role));
    }
}

