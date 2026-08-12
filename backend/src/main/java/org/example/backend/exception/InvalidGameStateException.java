package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidGameStateException extends AppException {
    public InvalidGameStateException() {
        super("Game is already finished", "INVALID_STATE", HttpStatus.BAD_REQUEST);
    }

    public InvalidGameStateException(String message) {
        super(message, "INVALID_STATE", HttpStatus.BAD_REQUEST);
    }
}
