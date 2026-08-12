package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class UserCreationConflictException extends AppException {
    public UserCreationConflictException() {
        super("User creation conflict", "USER_CONFLICT", HttpStatus.CONFLICT);
    }

    public UserCreationConflictException(String message) {
        super(message, "USER_CONFLICT", HttpStatus.CONFLICT);
    }
}
