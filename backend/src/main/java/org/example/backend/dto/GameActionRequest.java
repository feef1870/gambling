package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record GameActionRequest(
        @NotBlank(message = "Action cannot be empty") String action
) {
}
