package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends AppException {
    public InsufficientFundsException(String message) {
        super(message, "INSUFFICIENT_FUNDS", HttpStatus.BAD_REQUEST);
    }
}
