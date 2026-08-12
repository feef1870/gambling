package org.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddBalanceRequest(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        Long amount) {
}
