package com.piats.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserInfoResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String role;
}
