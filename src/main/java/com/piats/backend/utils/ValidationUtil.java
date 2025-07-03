package com.piats.backend.utils;

import com.piats.backend.exceptions.BadRequestException;
import com.piats.backend.models.User;

public class ValidationUtil {

    public static void validateRegister(User requestUser) {
        validateEmail(requestUser.getEmail());
        validatePassword(requestUser.getPassword());
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
}
