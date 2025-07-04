package com.piats.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequestDto {
    private String email;
    private String password;
}
