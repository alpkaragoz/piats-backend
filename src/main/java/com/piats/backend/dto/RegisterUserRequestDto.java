package com.piats.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
}
