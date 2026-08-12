package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class NotGameOwnerException extends AppException {
    public NotGameOwnerException() {
        super("Unauthorized access to game", "NOT_GAME_OWNER", HttpStatus.FORBIDDEN);
    }

    public NotGameOwnerException(String message) {
        super(message, "NOT_GAME_OWNER", HttpStatus.FORBIDDEN);
    }
}
