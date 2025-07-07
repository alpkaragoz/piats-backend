package com.piats.backend.utils;

import com.piats.backend.dto.RegisterUserRequestDto;
import com.piats.backend.enums.Role;
import com.piats.backend.exceptions.BadRequestException;
import com.piats.backend.exceptions.BadRoleException;

public class ValidationUtil {

    public static void validateRegister(RegisterUserRequestDto requestUser) {
        validateEmail(requestUser.getEmail());
        validatePassword(requestUser.getPassword());
        doesUserRoleExist(requestUser.getRole());
    }

    // Method to validate email
    private static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email cannot be empty.");
        }
        if (!email.contains("@")) {
            throw new BadRequestException("Email must contain '@'.");
        }
    }

    // Method to validate password
    private static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException("Password cannot be empty.");
        }
        if (password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long.");
        }
    }

    // Method to validate user role
    public static boolean doesUserRoleExist(String role) {
        try {
            if (role == null || role.trim().isEmpty()) {
                throw new BadRoleException("Role cannot be null or empty.");
            }
            Role.valueOf(role.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            throw new BadRoleException("Given role for user is not correct.");
        }
    }
}
