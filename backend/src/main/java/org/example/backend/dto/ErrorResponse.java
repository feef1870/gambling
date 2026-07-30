package org.example.backend.dto;

import java.time.Instant;

public record ErrorResponse(
        String errorCode,
        String message,
        int status,
        Instant timestamp,
        String path
) {
}