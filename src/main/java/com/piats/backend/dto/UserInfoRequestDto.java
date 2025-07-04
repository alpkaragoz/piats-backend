package com.piats.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoRequestDto {
    private String firstName;
    private String lastName;
    private String role;
}
