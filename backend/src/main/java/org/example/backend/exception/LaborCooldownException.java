package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public class LaborCooldownException extends AppException {
    private final long secondsRemaining;

    public LaborCooldownException(long secondsRemaining) {
        super("You must wait " + secondsRemaining + " seconds before claiming again",
                "LABOR_COOLDOWN", HttpStatus.TOO_MANY_REQUESTS);
        this.secondsRemaining = secondsRemaining;
    }

    public long getSecondsRemaining() {
        return secondsRemaining;
    }
}
