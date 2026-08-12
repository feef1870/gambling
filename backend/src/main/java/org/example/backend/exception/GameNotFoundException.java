package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class GameNotFoundException extends AppException {
    public GameNotFoundException() {
        super("Game not found", "GAME_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public GameNotFoundException(String message) {
        super(message, "GAME_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
