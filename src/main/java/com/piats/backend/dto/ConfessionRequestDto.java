package com.piats.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfessionRequestDto {
    private String nickname;
    private String confessionText;
    private String department;
}
