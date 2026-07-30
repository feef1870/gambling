package org.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GameCreateRequest(
        @NotNull @Positive(message = "Bet must be greater than zero") Long betAmount
) {
}
