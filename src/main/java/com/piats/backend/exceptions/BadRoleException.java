package com.piats.backend.exceptions;

public class BadRoleException extends RuntimeException{
    public BadRoleException(String message) {
        super(message);
    }
}
