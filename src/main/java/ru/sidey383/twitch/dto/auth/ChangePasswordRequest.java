package ru.sidey383.twitch.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        String oldPassword,
        @NotBlank
        String newPassword
) {
}
