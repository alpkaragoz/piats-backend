package com.piats.backend.utils;

import com.piats.backend.enums.Role;
import com.piats.backend.exceptions.InvalidTokenException;
import com.piats.backend.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final JwtService jwtService;

    @Autowired
    public AuthUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public boolean isRoleFromHeaderValid(Role role, String header) {
        String token = extractTokenFromHeader(header);
        String extractedRole = jwtService.extractRole(token);
        if (role.name().equalsIgnoreCase(extractedRole)) {
            return true;
        }
        return false;
    }

    public String extractTokenFromHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new InvalidTokenException("Invalid authorization header.");
        }

        final String BEARER_PREFIX = "Bearer ";
        if (!header.startsWith(BEARER_PREFIX) || header.length() < BEARER_PREFIX.length() + 1) {
            throw new InvalidTokenException("Invalid authorization header.");
        }

        return header.substring(BEARER_PREFIX.length());
    }
}
