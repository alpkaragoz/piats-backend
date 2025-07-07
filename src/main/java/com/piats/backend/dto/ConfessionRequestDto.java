package com.piats.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfessionRequestDto {

    @NotBlank(message = "Nickname cannot be empty or just spaces.")
    @Size(max = 50, message = "Nickname should be shorter than 50 characters.")
    private String nickname;

    @NotBlank(message = "Confession text cannot be empty or just spaces.")
    @Size(max = 256, message = "Confession should be shorter than 256 characters.")
    private String confessionText;

    @NotBlank(message = "Department cannot be empty or just spaces.")
    @Size(max = 50, message = "Department should be shorter than 50 characters.")
    private String department;
}
