package org.example.backend.dto;

public record UserResponse(
        String id,
        String username,
        Long balance
) {
}
