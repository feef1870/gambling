package org.example.backend.dto;

import org.example.backend.entities.User;

import java.time.Instant;

public record UserResponse(
        String id,
        String username,
        Long balance,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getBalance(),
                user.getCreatedAt()
        );
    }
}
