package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidAmountException extends AppException {
    public InvalidAmountException() {
        super("Amount must be positive", "INVALID_AMOUNT", HttpStatus.BAD_REQUEST);
    }

    public InvalidAmountException(String message) {
        super(message, "INVALID_AMOUNT", HttpStatus.BAD_REQUEST);
    }
}
