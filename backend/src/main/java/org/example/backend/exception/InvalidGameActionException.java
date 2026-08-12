package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidGameActionException extends AppException{
    public InvalidGameActionException() {
        super("Invalid action", "INVALID_ACTION", HttpStatus.BAD_REQUEST);
    }

    public InvalidGameActionException(String message) {
        super(message, "INVALID_ACTION", HttpStatus.BAD_REQUEST);
    }
}
