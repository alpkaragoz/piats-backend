package com.piats.backend.controllers;

import com.piats.backend.dto.ConfessionRequestDto;
import com.piats.backend.dto.ConfessionResponseDto;
import com.piats.backend.services.ConfessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/confessions")
@RequiredArgsConstructor
public class ConfessionController {

    private final ConfessionService confessionService;

    @GetMapping
    public ResponseEntity<List<ConfessionResponseDto>> getAllConfessions() {
        return ResponseEntity.ok().body(confessionService.getAllConfessions());
    }

    @PostMapping
    public ResponseEntity<ConfessionRequestDto> createConfession(@Valid @RequestBody ConfessionRequestDto confessionRequestDto) {
        return ResponseEntity.ok().body(confessionService.createConfession(confessionRequestDto));
    }
}
