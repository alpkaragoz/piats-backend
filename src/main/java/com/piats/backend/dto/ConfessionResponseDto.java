package com.piats.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfessionResponseDto {
    private String nickname;
    private String confessionText;
    private String department;
    private ZonedDateTime createdAt;
}
